package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

public class Media extends Model{

	@Expose
	@Column(name="UID")
	public Long id;
	
	@Column(name="Note_ID")
	public Long note_id;
	
	@Expose
	@SerializedName("link")
	@Column(name="URL")
	public String url;	
	
	@Expose
	@Column(name="PATH")
	// TODO: change this to local
	public String path;
	
	@Expose
	@Column(name="title")
	public String title;
	
	public String getPath(){
		if (path != null){
			return path;				
		}else{
			return url;
		}
	}
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("id", id).
				add("title", title).
				add("url", url).
				add("path", path).
				add("note_id", note_id).
				toString();
	}
}
