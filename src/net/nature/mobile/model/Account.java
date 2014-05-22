package net.nature.mobile.model;

import java.io.Serializable;
import java.util.List;

import retrofit.RetrofitError;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.Sync;
import net.nature.mobile.rest.NatureNetAPI.Result;
import net.nature.mobile.rest.NatureNetRestAdapter;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.*;

@Table(name="ACCOUNT", id="tID")
public class Account extends SyncableModel {

	@Expose
	@Column(name="Name")
	public String name;

	@Expose
	@Column(name="Username")
	private String username;

	@Expose
	@Column(name="Password")
	private String password;

	@Expose
	@Column(name="Email")
	private String email;

	public String toString(){
		return Objects.toStringHelper(this).
				addValue(super.toString()).				
				//				add("base", super.toString()).
				add("username", getUsername()).
				add("name", name).
				add("email", getEmail()).
				toString();
	}

	//	public void sync1(){
	//		NatureNetAPI api = NatureNetRestAdapter.get();
	//		Result<Account> r = api.createAccount(username, name, password, email, "I consent");
	//		this.uID = r.data.getUId();
	//		save();
	//	}
	//		
	////		// if it does not exist locally
	////		if (!existsLocally()){
	////			save();
	////			Log.d(TAG , "pulled " + this);
	////		}else if (!existsRemotely()){
	//			
	////		NatureNetAPI api = NatureNetRestAdapter.get();
	////		if (api != null){
	////			saveRemotely(api);				
	////		}
	////			Log.d(TAG , "pushed " + this);
	////		}
	//	}

	protected void saveRemotely(NatureNetAPI api) {
		checkNotNull(api);
		Result<Account> r = api.createAccount(getUsername(), name, "", email, "I consent");				
		this.uID = r.data.getUId();		
		save();
	}

	public void saveToRemote(){
		if (existsRemotely()){
			// TODO: Update existing	
		}else{
			// Creating new
			NatureNetAPI api = NatureNetRestAdapter.get();
			Result<Account> r = api.createAccount(getUsername(), name, "", email, "I consent");				
			this.uID = r.data.getUId();
			save();
		}
	}

	public static Account loadFromRemote(String username){		
		NatureNetAPI api = NatureNetRestAdapter.get();
		try{
			Result<Account> r = api.getAccount(username);
			if (findByUsernameLocally(username) == null){
				r.data.save();
			}
			return r.data;
		}catch(RetrofitError r){
			return null;
		}
	}

	public int countNotes(){		
		return new Select().from(Note.class).where("account_id = ?", getId()).count();
	}

	public List<Note> notes() {
		return new Select().from(Note.class).where("account_id = ?", getId()).execute();		
	}

	public static Account findByUsername(String username) {
		Account a = findByUsernameLocally(username);
		if (a != null){
			return a;
		}else{
			return loadFromRemote(username);
		}		
	}
	
	public static Account findByUsernameLocally(String username) {
		return new Select().from(Account.class).where("Username = ?", username).executeSingle();
	}

	public List<Note> getRecentNotes(int n) {
		return new Select().from(Note.class).where("account_id = ?", getId()).orderBy("tid DESC").limit(n).execute();		
	}

	public List<Note> getNotesOrderedByRecency() {
		return new Select().from(Note.class).where("account_id = ?", getId()).orderBy("tid DESC").execute();		
	}

	public List<Note> getNotes() {
		return new Select().from(Note.class).where("account_id = ?", getId()).execute();		
	}


	public static Account find_by_uid(Long uid) {
		return new Select().from(Account.class).where("uid = ?", uid).executeSingle();		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


}
