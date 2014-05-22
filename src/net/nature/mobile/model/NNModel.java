package net.nature.mobile.model;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetRestAdapter;
import net.nature.mobile.rest.NatureNetAPI.Result;
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

public abstract class NNModel extends Model {

	public NNModel(){
		super();
		uID = -1L;
		created_at = new Date().getTime();
		state = STATE.NEW;
	}

	@Column(name="syncState")
	public Integer state;	
	public static class STATE{
		static public int NEW = 1;
		static public int SAVED = 2;
		static public int MODIFIED = 3;
		static public int SYNCED = 4;
		static public int DOWNLOADED = 5;
	};

	public Integer getSyncState() {
		return state;
	}	
	
	public void commit() {
		if (state == STATE.NEW){
			state = STATE.SAVED;
			save();
		}else if (state == STATE.SYNCED){
			state = STATE.MODIFIED;
			save();			
		}else if (state == STATE.DOWNLOADED){
			Model ret = (new Select()).from(getClass()).where("uid = ?", getUId()).executeSingle();
			if (ret == null){
				state = STATE.SYNCED;
				save();
			}else{
				// compare time stamps to figure out who is newer
				// if remote is newer, copy over, or the opposite
			}
		}		
		doCommitChildren();
	}

	protected void doCommitChildren() {
	}
	
	protected void doSyncChildren(NatureNetAPI api) {		
	}

	public void push(){
		NatureNetAPI api = NatureNetRestAdapter.get();
		if (state == STATE.SAVED){
			NNModel m = doUploadNew(api);
			state = STATE.SYNCED;
			uID = m.getUId();
			save();
			
			doSyncChildren(api);			
		}else if (state == STATE.MODIFIED){
			NNModel m = doUploadChanges(api);
			state = STATE.SYNCED;
			save();
		}
	}

	protected <T extends NNModel> T doUploadNew(NatureNetAPI api){
		return null;
	}
	
	protected <T extends NNModel> T doUploadChanges(NatureNetAPI api){
		return null;
	}	
	
	protected <T extends NNModel> T doDownload(NatureNetAPI api, long uID){
		return null;
	}

	//	public <T extends SyncableModel> T download(){
	//		
	//		try{
	//			r = doDownload(api);
	//			data.syncState = STATE.DOWNLOADED;
	//			return r.data;
	//		}catch(RetrofitError r){
	//			return null;
	//		}
	//	}
	
	public static <T extends NNModel> T resolve(Class klass, long uID) {
		T model = findByUID(klass, uID);
		if (model == null){
			model = download(klass, uID);
			if (model != null){
				model.commit();
			}
		}
		return model;
	}

	public static <T extends NNModel> T download(Class klass, long uID) {
		try {
			NatureNetAPI api = NatureNetRestAdapter.get();
			try{
				T obj = (T) klass.getDeclaredConstructor().newInstance();
				obj = obj.doDownload(api,uID);
				if (obj != null){
					obj.state = STATE.DOWNLOADED;
				}
				return obj;				
			}catch(RetrofitError r){
				return null;
			}

		} catch (IllegalArgumentException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;

		//		NatureNetAPI api = NatureNetRestAdapter.get();
		//		try{
		//			doDownload(api);
		//			r.data.syncState = STATE.DOWNLOADED;
		//			return r.data;
		//		}catch(RetrofitError r){
		//			return null;
		//		}
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

	public boolean isRemoteOnly(){
		Model ret = (new Select()).from(getClass()).where("uid = ?", getUId()).executeSingle();
		return ret == null;
	}

	public boolean isLocalOnly() {
		return uID == -1L;
	}	

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

	public static int countLocal(Class clazz){
		return new Select().from(clazz).count();
	}

	public static int countLocal(Class clazz, int state){
		return new Select().from(clazz).where("syncState = ?", state).count();
	}

	public static <T extends NNModel> T findByUID(Class clazz, Long uid) {
		return new Select().from(clazz).where("uid = ?", uid).executeSingle();		
	}

	public Long getTimeCreated() {
		return created_at;
	}

}
