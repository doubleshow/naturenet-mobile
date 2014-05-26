package net.nature.mobile.model;

import net.nature.mobile.rest.NatureNetAPI;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name="CONTEXT", id="tID")
public class Context extends NNModel {

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

	@Expose
	@Column(name="Title")
	public String title;

	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("name", getName()).
				add("description", title).	
				add("description", description).				
				add("site_id" , site_id).
				toString();
	}


	public Site getSite() {
		return Model.load(Site.class, site_id);
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

	public void setSite(Site site){
		this.site_id = site.getId();		
	}


	@Override
	protected <T extends NNModel> T doPullByUID(NatureNetAPI api, long uID){
		return (T) api.getContext(uID).data;
	}

}
