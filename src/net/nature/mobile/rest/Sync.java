package net.nature.mobile.rest;

import java.util.List;

import retrofit.RestAdapter;
import android.util.Log;

import com.activeandroid.query.Select;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.Account;
import net.nature.mobile.rest.NatureNetAPI.Result;
import static com.google.common.base.Preconditions.*;

public class Sync {

	private static final String TAG = "Sync";
	private NatureNetAPI api;

	public Sync(){
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://naturenet.herokuapp.com/api")
		.build();		
		api = restAdapter.create(NatureNetAPI.class);
	}

	public void pull(Account remoteUser){
		Account localUser = (new Select()).from(Account.class).where("username = ?", remoteUser.username).executeSingle();
		if (localUser == null){
			remoteUser.save();
		}
	}
	
	public void pull(Context remote){
		Context local = (new Select()).from(Context.class).where("uid = ?", remote.id).executeSingle();
		if (local == null){
			remote.save();
		}
	}	
	
	public void sync(Note note){
		checkNotNull(note);	
		if (!note.exists()){
			Log.d(TAG, "saving " + note);
			note.syncForeignKeysAndSave();
		}
	}
	
	public void syncNotesForUsers(String username){
		checkNotNull(api);	
		Result<List<Note>> r = api.listNotes(username);
		if (r.status_code == 200){
			for (Note u : r.data){
				sync(u);
			}
		}
	}
	

	public void pullAllUsers(){
		if (api != null){
			Result<List<Account>> r = api.listAccounts();
			if (r.status_code == 200){
				for (Account u : r.data){
					pull(u);
				}
			}
		}
	}
	

	
	public void pullAllActivities(){
		if (api != null){
			Result<List<Context>> r = api.listActivities();
			if (r.status_code == 200){
				for (Context u : r.data){
					pull(u);
				}
			}
		}
	}
}
