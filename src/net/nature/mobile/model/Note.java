package net.nature.mobile.model;

import java.util.List;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
	//	private AccountJson accountJson;
	private Account account;


	//private ContextJson contextJson;

	@Expose
	@SerializedName("context")
	private Context context;

	@Expose
	private Media[] medias;

	static public class AccountJson {
		@Expose
		public Long id;
		@Expose
		public String username;
	}

	//	static public class ContextJson{
	//		@Expose
	//		public String kind;
	//		@Expose
	//		public Long id;
	//	}

	public List<Media> getMedias(){
//		return getMany(Media.class, "note");
		return new Select().from(Media.class).where("note_id = ?", getId()).execute();
	}

	public Media getMediaSingle(){
		return new Select().from(Media.class).where("note_id = ?", getId()).executeSingle();
	}

	public Long syncForeignKeysAndSave(){
		//		checkNotNull(accountJson);
		//		checkNotNull(contextJson);
		//		
		//		if (accountJson != null && contextJson != null && medias != null){			
		//			BaseModel account = Account.find_by_uid(accountJson.id);			
		//			account_id = account.getId();			
		//				
		//			Context context = Context.find_by_uid(contextJson.id);
		//			context_id = context.getId();			
		//		
		//			save();
		//		
		//			for (Media media : medias){	
		//				media.setNote(this);
		//				media.save();
		////				Log.d(TAG, "    saved " + media);
		//			}
		//		}
		return getId();
	}

	//	void resolveForeignKeys(){
	//		if (accountJson != null && contextJson != null){
	//			System.out.println(accountJson.id);
	//			Account account = Account.find_by_uid(accountJson.id);			
	//			account_id = account.getId();			
	//			
	//			Context context = Context.find_by_uid(contextJson.id);
	//			context_id = context.getId();			
	//		}
	//	}


	public static Note find(Long id){		
		return new Select().from(Note.class).where("uid = ?", id).executeSingle();
	}

	//	public boolean exists() {
	//		return new Select().from(Note.class)
	//			.where("uid = ?", uID).exists();
	//	}

	//	Long getContextId(){
	//		if (context_id == null && contextJson != null){
	//			return  contextJson.id;
	//		}else{
	//			return context_id;
	//		}		
	//	}
	//	
	//	Long getAccountId(){
	//		if (account_id == null && accountJson != null){
	//			return  accountJson.id;
	//		}else{
	//			return account_id;
	//		}		
	//	}
	//	
	//	private void resolve(){
	//		if (accountJson != null && contextJson != null && medias != null){			
	//			BaseModel account = Account.find_by_uid(accountJson.id);			
	//			account_id = account.getId();			
	//
	//			Context context = Context.find_by_uid(contextJson.id);
	//			context_id = context.getId();			
	//
	//			save();
	//
	//			for (Media media : medias){	
	//				media.setNote(this);
	//				media.save();
	//				//			Log.d(TAG, "    saved " + media);
	//			}
	//		}
	//	}


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
					Log.d(TAG, "saved " +  media);
				}

				Log.d(TAG, "saved " + this);
			}
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
				add("medias", getMedias()).
				toString();
	}

	//	public int count(){
	//		return new Select().from(Note.class).count();
	//	}
}
