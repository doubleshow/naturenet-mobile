package net.nature.mobile.tests;

import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Note;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import net.nature.mobile.rest.NoteJson;
import net.nature.mobile.rest.Sync;
import android.test.AndroidTestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class SyncTest extends AndroidTestCase {
	
	private Sync sync;
	private NatureNetAPI api;
	
		@Override
	protected void setUp() throws Exception{
		super.setUp();
		sync = new Sync();
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint("http://naturenet.herokuapp.com/api")
	    .build();		
		api = restAdapter.create(NatureNetAPI.class);
	}

	public void testPullNewRemoteUser(){
		Account user = new Account();
		String newName = "remote" + (new Date()).getTime();
		user.username = newName;
		
		sync.pull(user);
		
		Account u = (new Select()).from(Account.class).where("username = ?", user.username).executeSingle();		
		assertThat(u, notNullValue());
		assertThat(u.username, equalTo(newName));
	}
	
	public void testSyncNotesForTomYeh(){
		new Delete().from(Note.class).execute();
		
		Account account = new Account();
		account.id = 1L;
		account.username = "tomyeh";
		
		int count = account.countNotes();
		assertThat(count, equalTo(0));
				
		sync.syncNotesForUsers("tomyeh");		
		
		int countAfterFirstSync = account.countNotes();

		assertThat("after the first sync, the number of notes should be more than four", 
				countAfterFirstSync, greaterThan(4));
		
		sync.syncNotesForUsers("tomyeh");
		
		int countAfterSecondSync = account.countNotes();
				
		assertThat("after the second sync, the number of notes should not change",
				countAfterSecondSync, equalTo(countAfterSecondSync));
		
		account.notes().get(0).delete();
	
		int countAfterDeleteOne = account.countNotes();
		assertThat("after deleting a note, the number of notes should be one fewer",
				countAfterDeleteOne, equalTo(countAfterSecondSync-1));
		
		sync.syncNotesForUsers("tomyeh");
		
		int countAfterThirdSync = account.countNotes();
		assertThat("after syncing again, the number of notes should be go back to just before",
				countAfterThirdSync, equalTo(countAfterSecondSync));
		
	}
	
	public void testPullExistingRemoteUser(){
		Account user = (new Select()).from(Account.class).executeSingle();
		
		int countBefore = (new Select()).from(Account.class).execute().size();
						
		sync.pull(user);
		
		int countAfter = (new Select()).from(Account.class).execute().size();
						
		assertThat(countBefore, equalTo(countAfter));
		
	}
	
	public void testPullAllUsers(){
		
		(new Delete()).from(Account.class).execute();
		int countBefore = (new Select()).from(Account.class).execute().size();
				
		sync.pullAllUsers();
		
		int countAfter = (new Select()).from(Account.class).execute().size();
		
		// assuming at least 5 accounts, this must hold
		assertThat(countAfter, greaterThan(countBefore + 5));
		
		sync.pullAllUsers();
		
		int countAfterAgain = (new Select()).from(Account.class).execute().size();
		
		assertThat(countAfterAgain, equalTo(countAfter));
	}
	
	public void testPullAllActivities(){
		
		(new Delete()).from(Context.class).execute();
		int countBefore = (new Select()).from(Context.class).execute().size();
				
		sync.pullAllActivities();
		
		int countAfter = (new Select()).from(Context.class).execute().size();
	
		assertThat(countAfter, greaterThan(countBefore + 3));
		
		sync.pullAllActivities();
		
		int countAfterAgain = (new Select()).from(Context.class).execute().size();
		
		assertThat(countAfterAgain, equalTo(countAfter));
	}
}
