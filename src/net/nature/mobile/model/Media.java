package net.nature.mobile.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

public class Media extends SyncableModel{

//	@Expose
	@Column(name="Note_ID", notNull=true)
	private Long note_id;
	
	@Expose
	@SerializedName("link")
	@Column(name="URL")
	private String url;	
	
	@Column(name="PATH")
	private String local;
	
	@Expose
	@Column(name="title")
	private String title = "";
	
	public String getPath(){
		if (local != null){
			return local;				
		}else{
			return getURL();
		}
	}
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("title", getTitle()).
				add("url", getURL()).
				add("local", local).
//				add("note_id", note_id).
				toString();
	}

	public void setLocal(String local) {
		this.local = local;
	}
	
	public String getLocal(){
		return local;
	}

	public void setNote(Note note) {
		this.note_id = note.getId();		
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
