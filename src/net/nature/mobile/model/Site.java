package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

public class Site extends Model{

	@Column(name="UID")
	public Long id;
	
	@Column(name="Name")
	public String name;
	
	@Column(name="Description")
	public String description;
}
