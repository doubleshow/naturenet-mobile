package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;

public class Context extends Model {
	
	@Column(name="Description")
	public String description;
	
	@Column(name="Kind")
	public String kind;
	
	@Column(name="Name")
	public String name;
	
	@Column(name="Site_ID")
	public Long site_id;
	
	@Column(name="UID")
	public Long id;
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("name", name).
				add("description", description).				
				add("site_id", site_id).
				toString();
	}
	
	// TODO: pull-up to base class
	public boolean exists() {
		return new Select().from(this.getClass())
			.where("uid = ?", id).exists();
	}

}
