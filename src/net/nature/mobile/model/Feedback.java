package net.nature.mobile.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.cloudinary.Cloudinary;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

public class Feedback extends NNModel{

	@Override
	protected String getModelName() {
		return "Feedback";
	}
	
	static public class Target{
		@Expose
		String model;
		@Expose
		Long id;
	}
	
	@Expose
	private Account account;
	
	@Expose
	private Target target;
	
	private NNModel targetModel;
	
	@Column(name="target_id", notNull=true)
	private Long target_id;
	
	@Column(name="target_model", notNull=true)
	private String target_model;	

	@Expose
	@Column(name="Content")
	private String content;
	
	@Expose
	@Column(name="Kind")
	private String kind;

	@Column(name="account_id", notNull=true)
	private Long account_id;
	
//	public String getPath(){
//		if (local != null){
//			return local;				
//		}else{
//			return getURL();
//		}
//	}

	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("account_id", account_id).
				add("target_model", target_model).
				add("target_id", target_id).
				add("kind", getKind()).
				add("content", getContent()).
				toString();
	}
	
	@Override
	protected <T extends NNModel> T doPullByUID(NatureNetAPI api, long uID){
		Feedback fb =  api.getFeedback(uID).data;
		
		Account account = resolveByUID(Account.class, fb.account.uID);
		fb.account_id = account.getId();
		
		if (fb.target.model.equalsIgnoreCase("Note")){
			Note note = resolveByUID(Note.class, fb.target.id);
			if (note != null){
				fb.target_id = note.getId();
				fb.target_model = "Note";
			}
		}
		return (T) fb;
	}

	@Override
	protected <T extends NNModel> T doPushNew(NatureNetAPI api){
		if (getTarget() != null){			
			Account account = getAccount();
			NNModel target = getTarget();			
			Result<Feedback> m = api.createFeedback(kind, target.getModelName(), 
					target.getUId(), account.getUsername(), content);
			setUId(m.data.getUId());
		}
		return (T) this;
	}
	@Override
	protected <T extends NNModel> T doPushChanges(NatureNetAPI api){
		return (T) api.updateFeedback(getUId(), getAccount().getUsername(), content).data;
	}	
	

	public String getContent() {
		return content;
	}

	public Account getAccount(){
		return Model.load(Account.class, account_id);
	}

	public void setAccount(Account account){
		account_id = account.getId();
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public NNModel getTarget(){
		if (target_model.equalsIgnoreCase("Note")){
			return Model.load(Note.class, target_id);
		}
		return null;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public void setTarget(NNModel model) {
		targetModel = model;
		target_id = model.getId();
		target_model = model.getClass().getSimpleName();
	}	

}
