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

public class SyncTest extends AndroidTestCase {
	
	private Sync sync;
	
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		sync = new Sync();
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
	
	public void testSyncAccounts(){		
		(new Delete()).from(Account.class).execute();
		
		assertThat("after deleting all accounts, the number of account should be zero",
				Account.count(), equalTo(0));
								
		sync.syncAccounts();
		
		int countAfterFirstSync = Account.count();
		
		assertThat("after the first sync, the number of accounts should be at least 5",
				countAfterFirstSync, greaterThan(5));
				
		sync.syncAccounts();
		int countAfterSecondSync = Account.count();
		
		assertThat("after the second sync, the number of accounts should not change",
				countAfterSecondSync, equalTo(countAfterFirstSync));
		
	}
	
//	public void testPullAllActivities(){
//		
//		(new Delete()).from(Context.class).execute();
//		int countBefore = (new Select()).from(Context.class).execute().size();
//				
//		sync.pullAllActivities();
//		
//		int countAfter = (new Select()).from(Context.class).execute().size();
//	
//		assertThat(countAfter, greaterThan(countBefore + 3));
//		
//		sync.pullAllActivities();
//		
//		int countAfterAgain = (new Select()).from(Context.class).execute().size();
//		
//		assertThat(countAfterAgain, equalTo(countAfter));
//	}
}
