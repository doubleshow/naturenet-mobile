package net.nature.mobile.model;

import java.io.IOException;
import java.util.Date;

import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetRestAdapter;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class BaseModel extends Model {

	public BaseModel(){
		super();
		uID = -1L;
		created_at = new Date().getTime();
	}

	@Expose
	@Column(name = "UID")
	@SerializedName("id")
	public Long uID = -1L;
	
	@Expose
	@Column(name = "created")
	private Long created_at;
	
	public String toString(){
		return Objects.toStringHelper(this).		
				add("id", getId()).
				add("uid", uID).
				add("created_at", getTimeCreated()).
				toString();
	}
	
	protected String TAG = "NatureNetModel";

	public boolean existsLocally() {
		Model ret = (new Select()).from(getClass()).where("uid = ?", getUId()).executeSingle();
		// BUG: "exist()" does not work initially when database is empty
		//		new Select().from(getClass()).where("tid = ?", getId()).exist();		
		return ret != null;
	}

	public boolean existsRemotely(){
		return uID > 0;
	}


	public void sync(){
		// if it does not exist locally
		if (!existsLocally()){
			save();
			Log.d(TAG , "pulled " + this);
		}else if (!existsRemotely()){			
			NatureNetAPI api = NatureNetRestAdapter.get();
			if (api != null){
				saveRemotely(api);				
			}
			Log.d(TAG , "pushed " + this);
		}
	}
	


	protected void saveRemotely(NatureNetAPI api) {		
	}
	
	

	public void setUId(Long uid){
		uID = uid;
	}
	
	public Long getUId() {
		return uID;
	}

	public static int count(Class clazz){
		return new Select().from(clazz).count();
	}


	public static <T extends BaseModel> T find_by_uid(Class clazz, Long uid) {
		return new Select().from(clazz).where("uid = ?", uid).executeSingle();		
	}

	public Long getTimeCreated() {
		return created_at;
	}

}
