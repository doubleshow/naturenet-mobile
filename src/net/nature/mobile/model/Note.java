package net.nature.mobile.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetRestAdapter;
import net.nature.mobile.rest.NatureNetAPI.Result;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.cloudinary.Cloudinary;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.google.common.base.Preconditions.*;
@Table(name="NOTE", id="tID")
public class Note extends NNModel {
	
	@Override
	protected String getModelName() {
		return "Note";
	}
	
	// Local

	@Expose
	@Column(name="content")
	private String content = "";


	@Column(name="Context_ID", notNull=true)
	private Long context_id;

	@Column(name="Account_ID", notNull=true)
	private Long account_id;

	@Expose
	@Column(name="longitude")
	private Double longitude;

	@Expose
	@Column(name="latitude")
	private Double latitude;
	
	@Expose
	@Column(name="Kind")
	private String kind;

	protected void resolveDependencies(){
		account = NNModel.resolveByUID(Account.class, account.getUId());
		context = NNModel.resolveByUID(Context.class, context.getUId());				
		account_id = account.getId();			
		context_id = context.getId();				
		for (Media media : medias){
			media.state = STATE.DOWNLOADED;
		}
		for (Feedback feedback : feedbacks){
			feedback.state = STATE.DOWNLOADED;
		}
	}
	
	protected void doCommitChildren(){
		for (Media media : getMedias()){
			media.setNote(this);
			media.commit();
		}
		for (Feedback feedback : getFeedbacks()){
			feedback.setTarget(this);
			feedback.resolveDependencies();
			feedback.commit();
		}						
	}
	protected void doPushChildren(NatureNetAPI api){
		for (Media media : getMedias()){
			media.push();
		}				
	}	
	
	@Override
	protected <T extends NNModel> T doPullByUID(NatureNetAPI api, long uID){
		Note d =  api.getNote(uID).data;
		d.resolveDependencies();
		return (T) d;
	}
	
	@Override
	protected <T extends NNModel> T doPushNew(NatureNetAPI api){
		return (T) api.createNote(getAccount().getUsername(), "FieldNote", content, getContext().getName(), getLatitude(), getLongitude()).data;
	}
	
	@Override
	protected <T extends NNModel> T doPushChanges(NatureNetAPI api){
		return (T) api.updateNote(getUId(), getAccount().getUsername(), "FieldNote", content, getContext().getName(), getLatitude(), getLongitude()).data;
	}	

	// Remote Json

	public boolean isGeoTagged(){
		return getLongitude() != null && getLongitude() != 0 && getLatitude() != null && getLatitude() != 0;
	}

	@Expose
	@SerializedName("account")
	private Account account;

	@Expose
	@SerializedName("context")
	private Context context;

	@Expose
	private Media[] medias;	

	@Expose
	private Feedback[] feedbacks;	

	
	public List<Media> getMedias(){
		if (medias != null){
			return Arrays.asList(medias);
		}else{
			return new Select().from(Media.class).where("note_id = ?", getId()).execute();
		}
	}
	
	public List<Feedback> getFeedbacks(){
		if (feedbacks != null){
			return Arrays.asList(feedbacks);
		}else{
			return new Select().from(Feedback.class).where("target_model = ? and target_id = ?", "Note", getId()).execute();
		}
	}	

	public Media getMediaSingle(){
		return new Select().from(Media.class).where("note_id = ?", getId()).executeSingle();
	}

	public Context getContext() {
		if (context == null && context_id != null){
			return Model.load(Context.class, context_id);
		}else{
			return context;
		}

	}

	public Account getAccount() {
		if (account == null && account_id != null){
			return Model.load(Account.class,  account_id);
		}else{
			return account;
		}
	}

	public String toString(){
		return Objects.toStringHelper(this).
				add("id", getId()).
				add("uid", getUId()).
				add("state", getSyncState()).
				add("content", getContent()).
				add("lat/lng", getLatitude() + "," + getLongitude()).
				add("account", getAccount()).
				add("context", getContext()).
				//add("medias", getMedias()).
				toString();
	}

	public void setAccount(Account account) {
		checkNotNull(account);
		account_id = account.getId();
	}

	public void setContext(Context context) {
		checkNotNull(context);
		context_id = context.getId();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void addMedia(Media media) {
		if (medias == null){
			medias = new Media[]{media};
		}
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLandmarkFeedback(Context context) {
		if (context != null){
			Feedback f = getLandmarkFeedback();
			if (f == null){
				f = new Feedback();
				f.setAccount(getAccount());
				f.setTarget(this);
				f.setKind("Landmark");
			}
			f.setContent(context.getName());
			f.commit();
		}
	}
	
	public Feedback getLandmarkFeedback(){
		return (new Select()).from(Feedback.class).where("kind = ? and target_model = ? and target_id = ?", "Landmark", "Note", this.getId()).executeSingle();		
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}
}
