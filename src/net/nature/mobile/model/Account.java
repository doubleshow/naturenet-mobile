package net.nature.mobile.model;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;

public class Account extends Model {

	@Column(name="Name")
	public String name;
	
	@Column(name="Username")
	public String username;
	
	@Column(name="Email")
	public String email;
	
	@Column(name="UID")
	public Long id;
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("username", username).
				add("name", name).
				add("email", email).
				add("id", id).toString();
	}
	
	public static int count(){
		 return new Select().from(Account.class).count();
	}
	
	public int countNotes(){		
		 return new Select().from(Note.class).where("account_id = ?", id).count();
	}

	public List<Note> notes() {
		return new Select().from(Note.class).where("account_id = ?", id).execute();		
	}
	
	public boolean exists() {
		return new Select().from(Account.class)
			.where("uid = ?", id).exists();
	}

	public static Account find(Long id) {
		return new Select().from(Account.class).where("uid = ?", id).executeSingle();
	}

	public List<Note> getRecentNotes(int n) {
		return new Select().from(Note.class).where("account_id = ?", id).orderBy("uid DESC").limit(n).execute();		
	}
	
	public List<Note> getNotesOrderedByRecency() {
		return new Select().from(Note.class).where("account_id = ?", id).orderBy("uid DESC").execute();		
	}

}
