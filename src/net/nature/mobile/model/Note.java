package net.nature.mobile.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetRestAdapter;
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
public class Note extends NNModel {

	// Local

	@Expose
	@Column(name="content")
	private String content = "";


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

	
	protected void resolveDependencies(){
		account = NNModel.resolveByUID(Account.class, account.getUId());
		context = NNModel.resolveByUID(Context.class, context.getUId());				
		account_id = account.getId();			
		context_id = context.getId();				
		for (Media media : medias){
			media.state = STATE.DOWNLOADED;
		}
	}
	
	protected void doCommitChildren(){
		for (Media media : getMedias()){
			media.setNote(this);
			media.commit();
		}				
	}
	protected void doPushChildren(NatureNetAPI api){
		for (Media media : getMedias()){
			media.push();
		}				
	}	
	
	@Override
	protected <T extends NNModel> T doPullByUID(NatureNetAPI api, long uID){
		Note d =  api.getNote(uID).data;
		d.resolveDependencies();
		return (T) d;
	}
	
	@Override
	protected <T extends NNModel> T doPushNew(NatureNetAPI api){
		return (T) api.createNote(getAccount().getUsername(), "FieldNote", content, getContext().getName(), latitude, longitude).data;
	}
	
	@Override
	protected <T extends NNModel> T doPushChanges(NatureNetAPI api){
		return (T) api.updateNote(getUId(), getAccount().getUsername(), "FieldNote", content, getContext().getName(), latitude, longitude).data;
	}	

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
		if (medias != null){
			return Arrays.asList(medias);
		}else{
			return new Select().from(Media.class).where("note_id = ?", getId()).execute();
		}
	}

	public Media getMediaSingle(){
		return new Select().from(Media.class).where("note_id = ?", getId()).executeSingle();
	}

	public void sync1(){
		if (state == STATE.DOWNLOADED){
			NNModel existingLocalRecord = NNModel.findByUID(Note.class, getUId());
			if (existingLocalRecord == null){
				state = STATE.SYNCED;
				save();
			}
		}
	}

	public void sync(){
		// if it does not exist locally
		if (!existsLocally()){

			if (account != null && context != null){

				// resolve relationships
				NNModel local_account = NNModel.findByUID(Account.class, account.getUId());			
				account_id = local_account.getId();			

				Context local_context = NNModel.findByUID(Context.class, context.getUId());			
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

		Result<Note> r = api.createNote(getAccount().getUsername(), "FieldNote",  getContent(), getContext().getName(), latitude, longitude);
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
				add("state", getSyncState()).
				add("content", getContent()).
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

	public static Note download(long uid) {
		NatureNetAPI api = NatureNetRestAdapter.get();
		try{
			Result<Note> r = api.getNote(uid);
			r.data.state = STATE.DOWNLOADED;
			return r.data;
		}catch(RetrofitError r){
			return null;
		}
	}

	public static List<Note> findByAccount(Account account){
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Note> loadFromRemoteForAccount(Account account) {
		NatureNetAPI api = NatureNetRestAdapter.get();
		Result<List<Note>> r = api.listNotes(account.getUsername());		
		for (Note item : r.data){
			if (item.isRemoteOnly());
			item.save();
		}
		return r.data;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void addMedia(Media media) {
		if (medias == null){
			medias = new Media[]{media};
		}
	}



	//	public int count(){
	//		return new Select().from(Note.class).count();
	//	}
}
