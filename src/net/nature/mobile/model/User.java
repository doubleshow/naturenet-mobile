package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

public class User extends Model {

	@Column(name="Name")
	public String name;
	
	@Column(name="Username")
	public String username;
	
}
