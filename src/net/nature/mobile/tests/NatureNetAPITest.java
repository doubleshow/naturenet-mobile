package net.nature.mobile.tests;

import java.util.Date;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Note;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NoteJson;
import net.nature.mobile.rest.Sync;
import net.nature.mobile.rest.NatureNetAPI.Result;
import retrofit.RestAdapter;
import android.test.AndroidTestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NatureNetAPITest extends AndroidTestCase {
	
	private NatureNetAPI api;

	@Override
	protected void setUp() throws Exception{
		super.setUp();
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint("http://naturenet.herokuapp.com/api")
	    .build();
		
		api = restAdapter.create(NatureNetAPI.class);
	}

	public void testGetAccounts(){		
		Result<List<Account>> res = api.listAccounts();
		assertThat(res.data.size(), greaterThan(3));
		for (Account user : res.data){
			Log.d("user", ""+user);
			System.out.println(user);
		}
	}
	
	public void testGetAccountTomYeh(){
		Result<Account> res = api.getAccount("tomyeh");
		assertThat(res.data.username, equalTo("tomyeh"));	
	}
	
	public void testListNotesForTomYeh(){
		Result<List<Note>> res = api.listNotes("tomyeh");
		for (Note x : res.data){			
			System.out.println(x);
			assertThat(x.account.username, equalTo("tomyeh"));
		}	
	}
	
}
