package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.common.base.Objects;

public class User extends Model {

	@Column(name="Name")
	public String name;
	
	@Column(name="Username")
	public String username;
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("username", username).
				add("name", name).toString();
	}
	
}
