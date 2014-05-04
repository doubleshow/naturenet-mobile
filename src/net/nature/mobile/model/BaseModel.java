package net.nature.mobile.model;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class BaseModel extends Model {

	public BaseModel(){
		super();
	}
	
	@Expose
	@Column(name = "UID")
	@SerializedName("id")
	public Long uID;
	
	protected String TAG = "NatureNetModel";

	public boolean existsLocally() {
		Model ret = (new Select()).from(getClass()).where("uid = ?", getUId()).executeSingle();
		// BUG: "exist()" does not work initially when database is empty
		//		new Select().from(getClass()).where("tid = ?", getId()).exist();		
		return ret != null;
	}
	
	
	public void sync(){
		// if it does not exist locally
		if (!existsLocally()){
			save();
			Log.d(TAG , "saved " + this);
		}
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


}
