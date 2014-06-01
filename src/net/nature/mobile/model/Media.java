package net.nature.mobile.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.cloudinary.Cloudinary;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

public class Media extends NNModel{

	@Override
	protected String getModelName() {
		return "Media";
	}
	
	@Column(name="Note_ID", notNull=true)
	private Long note_id;

	@Expose
	@SerializedName("link")
	@Column(name="URL")
	private String url;	

	@Column(name="PATH")
	private String local;

	@Expose
	@Column(name="title")
	private String title = "";

	public String getPath(){
		if (local != null){
			return local;				
		}else{
			return getURL();
		}
	}

	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("note_id", note_id).
				add("title", getTitle()).
				add("url", getURL()).
				add("local", local).
				toString();
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getLocal(){
		return local;
	}

	public void setNote(Note note) {
		this.note_id = note.getId();		
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	String uploadToCloudinary(){
		Map config = new HashMap();
		config.put("cloud_name", "university-of-colorado");
		config.put("api_key", "893246586645466");
		config.put("api_secret", "8Liy-YcDCvHZpokYZ8z3cUxCtyk");
		Cloudinary cloudinary = new Cloudinary(config);

		// Hack to get around this problem
		// Caused by: java.io.FileNotFoundException: /file:/storage/emulated/0/Pictures/JPEG_20140504_114444_559339952.jpg: open failed: ENOENT (No such file or directory)
		String local = getLocal();
		local = local.replaceAll("file:", "");

		JSONObject ret;
		try {
			ret = cloudinary.uploader().upload(new File(local), Cloudinary.emptyMap());
			String public_id = ret.getString("public_id");
			String url = ret.getString("url");
			Log.d(TAG, "uploaded to cloudinary: " + ret);
			return public_id;		
		} catch (IOException e) {				

		} catch (JSONException e) {

		}
		return null;
	}
	
	
	@Override
	protected <T extends NNModel> T doPullByUID(NatureNetAPI api, long uID){
		return (T) api.getMedia(uID).data;
	}	

	@Override
	protected <T extends NNModel> T doPushNew(NatureNetAPI api){
		if (getNote() != null){			
			String public_id = uploadToCloudinary();
			if (public_id != null){
				Result<Media> m = api.createMedia(getNote().getUId(), getTitle(), public_id);
				setUId(m.data.getUId()); 
				setURL(m.data.getURL());
				save();
			}
		}
		return (T) this;
	}

	Note getNote() {
		return Model.load(Note.class,  note_id);
	}	
}
