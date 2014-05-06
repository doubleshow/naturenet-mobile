package net.nature.mobile.model;

import android.location.Location;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name="SESSION", id="tID")
public class Session extends Model{

	private static final String TAG = "Session";
	@Column(name="Account")
	public Long account_id = -1L;	
	
	static Session getSingleton(){
		Session session = (new Select()).from(Session.class).executeSingle();
		if (session == null){
			session = new Session();			
			session.save();
		}
		return session;
	}
	
	static public boolean isSignedIn(){
		return getSingleton().account_id > 0;
	}
	
	static public Account getAccount(){
		return Model.load(Account.class, getSingleton().account_id);
	}
	
	static public void signIn(Account account){
		Log.d(TAG,"sign in: " + account);
		Session session = getSingleton();
		session.account_id = account.getId();		
		session.save();
	}

	public static void signOut() {
		Session session = getSingleton();
		session.account_id = -1L;		
		session.save();
	}
	
}
