package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name="CONTEXT", id="tID")
public class Context extends SyncableModel {
	
	@Expose
	@Column(name="Description")
	public String description;
	
	@Expose
	@Column(name="Kind")
	public String kind;
	
	@Expose
	@Column(name="Name")
	private String name;
	
	@Expose
	@Column(name="Site_ID")
	public Long site_id;
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("name", getName()).
				add("description", description).				
				add("site_id" , site_id).
				toString();
	}

	public static Context find_by_uid(Long uid) {
		return new Select().from(Context.class).where("uid = ?", uid).executeSingle();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
