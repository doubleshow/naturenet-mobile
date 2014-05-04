package net.nature.mobile.rest;

//import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import retrofit.RestAdapter;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import net.nature.mobile.rest.NoteJson;
import net.nature.mobile.rest.Sync;
import android.test.AndroidTestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static net.nature.mobile.model.BaseModel.*;

@RunWith(RobolectricTestRunner.class)
public class SyncTest {
	
	private Sync sync;
	
	@Before
	public void setUp(){
		ShadowLog.stream = System.out;
		sync = new Sync();
		// Roboletric would start an in-memory db so "delete" are not needed
//		new Delete().from(Note.class).execute();
//		new Delete().from(Context.class).execute();
//		new Delete().from(Account.class).execute();
//		new Delete().from(Media.class).execute();
	}
	
	@Test
	public void testSyncNotesForTomYeh(){
						
		sync.syncAccounts();
		sync.syncContexts();
		
		Account account = Account.find_by_username("tomyeh");
		
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
	
	@Test
	public void testSyncAccounts(){		
		
		assertThat("after deleting all accounts, the number of account should be zero",
				count(Account.class), equalTo(0));
								
		sync.syncAccounts();
		
		int countAfterFirstSync = count(Account.class);
		
		assertThat("after the first sync, the number of accounts should be at least 5",
				countAfterFirstSync, greaterThan(5));
				
		sync.syncAccounts();
		int countAfterSecondSync = count(Account.class);
		
		assertThat("after the second sync, the number of accounts should not change",
				countAfterSecondSync, equalTo(countAfterFirstSync));
	}
	
	@Test
	public void testSyncContexts(){		
		
		assertThat(count(Context.class), equalTo(0));
								
		sync.syncContexts();
		
		int countAfterFirstSync = count(Context.class);
		
		assertThat(count(Context.class), greaterThan(5));
				
		sync.syncContexts();
		
		assertThat(count(Context.class), equalTo(countAfterFirstSync));
		
	}
	
}
