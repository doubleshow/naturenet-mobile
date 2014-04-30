package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.common.base.Objects;

public class Activity extends Model {
	
	@Column(name="Description")
	public String description;
	
	@Column(name="Name")
	public String name;
	
	@Column(name="Site")
	public Site site;
	
	@Column(name="UID")
	public Long id;
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("name", name).
				add("description", description).
				add("site", site.name).
				add("site.id", site.id).
				toString();
	}	
}
