package net.nature.mobile.model;

import java.io.Serializable;
import java.util.List;

import net.nature.mobile.rest.NatureNetAPI;
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
public class Account extends BaseModel {

	@Expose
	@Column(name="Name")
	public String name;
	
	@Expose
	@Column(name="Username")
	public String username;
	
	@Expose
	@Column(name="Email")
	public String email;
	
	public String toString(){
		return Objects.toStringHelper(this).
				//addValue(super.toString()).				
				add("base", super.toString()).
				add("username", username).
				add("name", name).
				add("email", email).
				toString();
	}
	
	protected void saveRemotely(NatureNetAPI api) {
		checkNotNull(api);
		Result<Account> r = api.createAccount(username, name, "", email, "I consent");
		this.uID = r.data.getUId();
		save();
	}
		
	public int countNotes(){		
		 return new Select().from(Note.class).where("account_id = ?", getId()).count();
	}

	public List<Note> notes() {
		return new Select().from(Note.class).where("account_id = ?", getId()).execute();		
	}
	
	public static Account find_by_username(String username) {
		return new Select().from(Account.class).where("username = ?", username).executeSingle();
	}

	public List<Note> getRecentNotes(int n) {
		return new Select().from(Note.class).where("account_id = ?", getUId()).orderBy("uid DESC").limit(n).execute();		
	}
	
	public List<Note> getNotesOrderedByRecency() {
		return new Select().from(Note.class).where("account_id = ?", getUId()).orderBy("uid DESC").execute();		
	}

	public static Account find_by_uid(Long uid) {
		return new Select().from(Account.class).where("uid = ?", uid).executeSingle();		
	}

}
