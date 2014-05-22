package net.nature.mobile.rest;

import java.util.List;

import retrofit.RestAdapter;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.nature.mobile.model.SyncableModel;
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
		api = NatureNetRestAdapter.get();
	}

	public int countRemoteAccounts(){
		return api.countAccounts().data;
	}

	public void sync(Account account){
		checkNotNull(account);
		// sync dependencies if necessary
		syncContexts();
		
		account.sync();
		syncNotesForAccount(account);
	}

	public void sync(Context context){
		checkNotNull(context);
		context.sync();
	}	

	public void sync(Note note){
		checkNotNull(note);	
		note.sync();
	}

	private void syncNotesForAccount(Account account){
		checkNotNull(api);

		// pull
		Result<List<Note>> r = api.listNotes(account.getUsername());
		for (Note u : r.data){
			u.sync();
		}

		// push
		List<Note> xs = account.getNotes();
		for (Note u : xs){
			u.sync();
		}
	}

	public void syncNotesForUser(String username){
		checkNotNull(api);
		// pull
		Result<List<Note>> r = api.listNotes(username);
		if (r.status_code == 200){
			for (Note u : r.data){
				u.sync();
			}
		}
	}

	public void syncNotes(){
		checkNotNull(api);	
		if (SyncableModel.countLocal(Note.class) == 0){
			Result<List<Note>> r = api.listNotes();
			if (r.status_code == 200){
				for (Note u : r.data){
					sync(u);
				}
			}
		}else{
			List<Note> xs = new Select().from(Note.class).execute();
			for (Note x : xs){
				sync(x);
			}

		}
	}


	public void syncAccounts(){
		checkNotNull(api);
		if (SyncableModel.countLocal(Account.class) == 0){
			Result<List<Account>> r = api.listAccounts();
			if (r.status_code == 200){
				for (Account u : r.data){
					sync(u);
				}
			}			
		}else{

			List<Account> xs = new Select().from(Account.class).execute();
			for (Account x : xs){
				sync(x);
			}

		}

	}

	public void syncContexts(){
		checkNotNull(api);
		if (SyncableModel.countLocal(Context.class) == 0){
			Result<List<Context>> r = api.listContexts();
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
