package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

public class Note extends Model {

	@Expose
	@Column(name="Content")
	public String content;
	
	@Expose
	@Column(name="UID")
	public Long id;
	
	@Column(name="Context_ID")
	public Long context_id;

	@Column(name="Account_ID")
	public Long account_id;
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("id", id).
				add("content", content).
				add("user_id", account_id).
				add("context_id", context_id).
//				add("email", email).
				toString();
	}
	
	@Expose
	public AccountJson account;
	
	@Expose
	public ContextJson context;
		
	public Long syncForeignKeysAndSave(){
		if (account_id == null && account != null)
			account_id = account.id;
		if (context_id == null && context != null)
			context_id = context.id;		
		return save();
	}
	
	static public class AccountJson {
		public Long id;
		public String username;
	}
	
	static public class ContextJson{
		public String kind;
		public Long id;
	}

	public static Note find(Long id){
		return new Select().from(Note.class).where("uid = ?", id).executeSingle();
	}
	
	public boolean exists() {
		return new Select().from(Note.class)
			.where("uid = ?", id).exists();
	}

	public Context getContext() {
		return new Select().from(Context.class).where("uid = ?", context_id).executeSingle();
	}
	
//	public int count(){
//		return new Select().from(Note.class).count();
//	}
}
