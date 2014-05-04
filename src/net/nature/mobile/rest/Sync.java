package net.nature.mobile.rest;

import java.util.List;

import retrofit.RestAdapter;
import android.util.Log;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.nature.mobile.model.BaseModel;
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
		 Gson gson = new GsonBuilder()
		 .excludeFieldsWithoutExposeAnnotation()
//	     .registerTypeAdapter(Id.class, new IdTypeAdapter())
//	     .enableComplexMapKeySerialization()
//	     .serializeNulls()
//	     .setDateFormat(DateFormat.LONG)
//	     .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
//	     .setPrettyPrinting()
//	     .setVersion(1.0)
	     .create();
		 
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://naturenet.herokuapp.com/api")
		.build();		
		api = restAdapter.create(NatureNetAPI.class);
	}
	
	public int countRemoteAccounts(){
		return api.countAccounts().data;
	}

	public void sync(Account account){
		checkNotNull(account);
		account.sync();
	}

	public void sync(Context context){
		checkNotNull(context);
		context.sync();
	}	

	public void sync(Note note){
		checkNotNull(note);	
		note.sync();
	}

	public void syncNotesForUsers(String username){
		checkNotNull(api);	
		Result<List<Note>> r = api.listNotes(username);
		if (r.status_code == 200){
			for (Note u : r.data){
				u.sync();
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
		if (BaseModel.count(Account.class) == 0){
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
