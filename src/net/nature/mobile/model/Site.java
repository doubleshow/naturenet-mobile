package net.nature.mobile.model;

import java.util.Arrays;
import java.util.List;

import net.nature.mobile.model.NNModel.STATE;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetRestAdapter;
import net.nature.mobile.rest.NatureNetAPI.Result;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

@Table(name="SITE", id="tID")
public class Site extends NNModel{

	@Expose
	@Column(name="Name")
	public String name;
	
	@Expose
	@Column(name="Description")
	public String description;

	@Expose
	@Column(name="Kind")
	public String kind;
	
	@Expose
	@Column(name="Image_URL")
	private String image_url;	
	
	@Expose
	private Context contexts[];
	
	
	protected void doCommitChildren(){
		for (Context context : contexts){
			context.setSite(this);
			context.commit();
		}
	}	
	
	protected void resolveDependencies(){				
		for (Context context : contexts){
			context.state = STATE.DOWNLOADED;
		}
	}
	
	@Override
	protected <T extends NNModel> T doPullByName(NatureNetAPI api, String name){
		Site site = api.getSite(name).data;
		site.resolveDependencies();
		return (T) site;
	}	
	
	public List<Context> getContexts(){
		if (contexts != null){
			return Arrays.asList(contexts);
		}else{
			return new Select().from(Context.class).where("site_id = ?", getId()).execute();
		}
	}
	
	public List<Context> getActivities(){
		return new Select().from(Context.class).where("site_id = ? and kind = ?", getId(), "Activity").execute();		
	}
		
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("name", name).
				add("description", description).
				add("image_url", image_url).
				toString();
	}

	public String getImageURL() {
		return image_url;
	}

	public void setImageURL(String image_url) {
		this.image_url = image_url;
	}	
}
