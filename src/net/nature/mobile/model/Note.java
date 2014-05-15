package net.nature.mobile.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.mime.TypedFile;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.cloudinary.Cloudinary;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.*;
@Table(name="NOTE", id="tID")
public class Note extends BaseModel {

	// Local

	@Expose
	@Column(name="content")
	public String content = "";

	@Column(name="Context_ID", notNull=true)
	public Long context_id;

	@Column(name="Account_ID", notNull=true)
	public Long account_id;
	
	@Expose
	@Column(name="longitude")
	public Double longitude;
	
	@Expose
	@Column(name="latitude")
	public Double latitude;

	// Remote Json
	
	public boolean isGeoTagged(){
		return longitude != null && longitude != 0 && latitude != null && latitude != 0;
	}

	@Expose
	@SerializedName("account")
	private Account account;

	@Expose
	@SerializedName("context")
	private Context context;

	@Expose
	private Media[] medias;

	public List<Media> getMedias(){
		return new Select().from(Media.class).where("note_id = ?", getId()).execute();
	}

	public Media getMediaSingle(){
		return new Select().from(Media.class).where("note_id = ?", getId()).executeSingle();
	}

	public void sync(){
		// if it does not exist locally
		if (!existsLocally()){

			if (account != null && context != null){

				// resolve relationships
				BaseModel local_account = BaseModel.find_by_uid(Account.class, account.getUId());			
				account_id = local_account.getId();			

				Context local_context = BaseModel.find_by_uid(Context.class, context.getUId());			
				context_id = local_context.getId();			

				save();
				
				//			
				for (Media media : medias){	
					media.setNote(this);					
					media.save();
					Log.d(TAG, "pulled " +  media);
				}

				Log.d(TAG, "pulled " + this);
			}
		}else{			
			super.sync();
		}
	}
	
	protected void saveRemotely(NatureNetAPI api){
		checkNotNull(api);
		checkNotNull(getAccount());
		checkNotNull(getContext());
		
		Result<Note> r = api.createNote(getAccount().username, "FieldNote",  content, getContext().name, latitude, longitude);
		setUId(r.data.getUId());
		save();
		
		Map config = new HashMap();
		config.put("cloud_name", "university-of-colorado");
		config.put("api_key", "893246586645466");
		config.put("api_secret", "8Liy-YcDCvHZpokYZ8z3cUxCtyk");
		Cloudinary cloudinary = new Cloudinary(config);


		for (Media media : getMedias()){
			// Hack to get around this problem
			// Caused by: java.io.FileNotFoundException: /file:/storage/emulated/0/Pictures/JPEG_20140504_114444_559339952.jpg: open failed: ENOENT (No such file or directory)
			String local = media.getLocal();
			local = local.replaceAll("file:", "");
			
			JSONObject ret;
			try {
				ret = cloudinary.uploader().upload(new File(local), Cloudinary.emptyMap());
				String public_id = ret.getString("public_id");
				String url = ret.getString("url");
				Log.d(TAG, "uploaded to cloudinary: " + ret);
				
				Result<Media> m = api.createMedia(getUId(), media.getTitle(), url);
				media.setUId(m.data.getUId()); 
				media.setURL(m.data.getURL());
				media.save();
				Log.d(TAG, "pushed " +  media);
				
			} catch (IOException e) {				
				
			} catch (JSONException e) {
				
			}
			
			
//			
//			TypedFile file = new TypedFile("image/jpeg", new File(local)); 
			

		}		
	}


	public Context getContext() {
		if (context == null && context_id != null){
			return Model.load(Context.class, context_id);
		}else{
			return context;
		}

	}

	public Account getAccount() {
		if (account == null && account_id != null){
			return Model.load(Account.class,  account_id);
		}else{
			return account;
		}
	}

	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("content", content).
				add("lat/lng", latitude + "," + longitude).
				add("account", getAccount()).
				add("context", getContext()).
				//add("medias", getMedias()).
				toString();
	}

	public void setAccount(Account account) {
		checkNotNull(account);
		account_id = account.getId();
	}

	public void setContext(Context context) {
		checkNotNull(context);
		context_id = context.getId();
	}
	
	

	//	public int count(){
	//		return new Select().from(Note.class).count();
	//	}
}
