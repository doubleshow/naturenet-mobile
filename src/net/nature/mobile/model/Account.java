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
	
	public int countNotes(){		
		 return new Select().from(Note.class).where("account_id = ?", id).count();
	}

	public List<Note> notes() {
		return new Select().from(Note.class).where("account_id = ?", id).execute();		
	}
	
}
