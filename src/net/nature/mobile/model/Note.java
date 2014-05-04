package net.nature.mobile.model;

import java.io.File;
import java.util.List;

import retrofit.mime.TypedFile;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.*;
@Table(name="NOTE", id="tID")
public class Note extends BaseModel {

	// Local

	@Expose
	@Column(name="content")
	public String content;

	//	@Expose
	//	@SerializedName("id")
	//	@Column(name="uID")
	//	public Long uID;

	@Column(name="Context_ID", notNull=true)
	public Long context_id;

	@Column(name="Account_ID", notNull=true)
	public Long account_id;

	// Remote Json

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

	public static Note find(Long id){		
		return new Select().from(Note.class).where("uid = ?", id).executeSingle();
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
		
		Result<Note> r = api.createNote(getAccount().username, "FieldNote",  content, getContext().name);
		setUId(r.data.getUId());
		save();
		
		for (Media media : getMedias()){			
			TypedFile file = new TypedFile("image/png", new File(media.getLocal())); 
			Result<Media> m = api.createMedia(getUId(), media.getTitle(), file);
			media.uID = m.data.getUId(); 
			media.save();
			Log.d(TAG, "pushed " +  media);
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
