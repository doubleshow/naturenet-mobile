package net.nature.mobile.rest;

import java.util.List;

import retrofit.RestAdapter;
import android.util.Log;

import com.activeandroid.query.Select;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
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

	public void sync(Account account){
		checkNotNull(account);
		if (!account.exists()){
			account.save();
		}
	}

	public void sync(Context context){
		checkNotNull(context);
		if (!context.exists()){
			context.save();
		}
	}	

	public void sync(Note note){
		checkNotNull(note);	
		if (!note.exists()){			
			note.syncForeignKeysAndSave();
			Log.d(TAG, "saved " + note);
			
			for (Media media : note.medias){	
				media.note_id = note.id;
				media.save();
				Log.d(TAG, "    saved " + media);
			}
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
	
	public void syncNotes(){
		checkNotNull(api);	
		Result<List<Note>> r = api.listNotes();
		if (r.status_code == 200){
			for (Note u : r.data){
				sync(u);
			}
		}
	}


	public void syncAccounts(){
		checkNotNull(api);
		Result<List<Account>> r = api.listAccounts();
		if (r.status_code == 200){
			for (Account u : r.data){
				sync(u);
			}
		}
	}
	
	public void syncContexts(){
		checkNotNull(api);
		Result<List<Context>> r = api.listContexts();
		if (r.status_code == 200){
			for (Context u : r.data){
				sync(u);
			}
		}
	}

	public void syncAll() {
		syncAccounts();
		syncContexts();
		syncNotes();
	}


//	public void pullAllActivities(){
//		if (api != null){
//			Result<List<Context>> r = api.listActivities();
//			if (r.status_code == 200){
//				for (Context u : r.data){
//					pull(u);
//				}
//			}
//		}
//	}
}
