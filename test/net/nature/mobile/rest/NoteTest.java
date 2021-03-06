package net.nature.mobile.rest;

import java.util.Date;
import java.util.List;

import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.NNModel.STATE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
public class NoteTest {
	
	private Account account;
	private Context context;
	private Note newNote;
	private Media media;

	@Before
	public void setUp(){
		ShadowLog.stream = System.out;
		account = NNModel.resolveByUID(Account.class, 1L);
		context = NNModel.resolveByUID(Context.class, 1L);
		
		newNote = createNewNote();
		
		media = new Media();
		media.setLocal("test.png");
		media.setTitle("this is a new media");		
	}
	
	Note createNewNote(){		
		Note note = new Note();
		note.setAccount(account);
		note.setContext(context);
		String newContent = "new content" + new Date().toString();
		note.setContent(newContent);
		return note;
	}
	
	@Test
	public void download(){
		Note note = NNModel.pullByUID(Note.class, 1L);
		assertThat(note, notNullValue());	
		assertThat(note.getUId(), equalTo(1L));
		assertThat(note.getSyncState(), equalTo(Note.STATE.DOWNLOADED));
	}	
	
	@Test
	public void download_with_medias(){
		Note note = NNModel.pullByUID(Note.class, 1L);
		List<Media> medias = note.getMedias();
		
		assertThat(medias.size(), greaterThan(0));
		for (Media media : medias){
			assertThat(media.getSyncState(), equalTo(STATE.DOWNLOADED));
		}
		
		assertThat(NNModel.countLocal(Media.class), equalTo(0));
	}
	
	@Test
	public void download_with_medias_and_commit(){
		Note note = NNModel.pullByUID(Note.class, 1L);
		note.commit();
		List<Media> medias = note.getMedias();
				
		for (Media media : medias){
			assertThat(media.getSyncState(), equalTo(STATE.SYNCED));
		}		
		assertThat(NNModel.countLocal(Media.class), greaterThan(0));
	}	
	
	@Test
	public void download_and_commit(){
		Note note = NNModel.pullByUID(Note.class, 1L);
		note.commit();
		assertThat(note.getSyncState(), equalTo(Note.STATE.SYNCED));
	}
	
	@Test
	public void download_commit_and_redownload(){
		Note note = NNModel.pullByUID(Note.class, 1L);
		note.commit();
		assertThat(NNModel.countLocal(Note.class), equalTo(1));
		
		note = NNModel.pullByUID(Note.class, 1L);
		note.commit();
		
		// should still be the same local record (count is still one)
		assertThat(NNModel.countLocal(Note.class), equalTo(1));
	}
	
	@Test
	public void create_new(){
		assertThat(newNote.getSyncState(), equalTo(Note.STATE.NEW));	
	}
	
	@Test
	public void create_new_and_save(){
		assertThat(NNModel.countLocal(Note.class), equalTo(0));
		
		newNote.commit();
		assertThat(newNote.getSyncState(), equalTo(Note.STATE.SAVED));
		
		assertThat(NNModel.countLocal(Note.class), equalTo(1));
	}
	
	@Test
	public void create_new_and_commit_3_times(){
		createNewNote().commit();
		createNewNote().commit();
		createNewNote().commit();
		
		assertThat(NNModel.countLocal(Note.class), equalTo(3));
		assertThat(NNModel.countLocal(Note.class, STATE.SAVED), equalTo(3));
		assertThat(NNModel.countLocal(Note.class, STATE.SYNCED), equalTo(0));
	}
	
	@Test
	public void create_new_and_sync(){
		assertThat(NNModel.countLocal(Note.class, STATE.SYNCED), equalTo(0));
		
		newNote.commit();
		assertThat(NNModel.countLocal(Note.class, STATE.SAVED), equalTo(1));
		
		newNote.push();
		assertThat(NNModel.countLocal(Note.class, STATE.SYNCED), equalTo(1));
		assertThat(NNModel.countLocal(Note.class, STATE.SAVED), equalTo(0));
		assertThat(NNModel.countLocal(Note.class), equalTo(1));		
	}
	
	@Test
	public void create_new_with_media(){
		newNote.addMedia(media);
		newNote.commit();
		
		assertThat(NNModel.countLocal(Note.class), equalTo(1));
		assertThat(NNModel.countLocal(Note.class, STATE.SAVED), equalTo(1));
		
		assertThat(NNModel.countLocal(Media.class), equalTo(1));
		assertThat(NNModel.countLocal(Media.class, STATE.SAVED), equalTo(1));
	}
	
	@Test
	public void create_new_with_media_and_sync(){		
		newNote.addMedia(media);
		newNote.commit();
		newNote.push();
		
		assertThat(NNModel.countLocal(Note.class), equalTo(1));
		assertThat(NNModel.countLocal(Note.class, STATE.SYNCED), equalTo(1));
		
		assertThat(NNModel.countLocal(Media.class), equalTo(1));
		assertThat(NNModel.countLocal(Media.class, STATE.SYNCED), equalTo(1));		
	}	
	
	@Test
	public void download_modify(){
		Note note = NNModel.pullByUID(Note.class, 1L);				
		note.commit();
		assertThat(note.getSyncState(), equalTo(Note.STATE.SYNCED));
		
		String newContent = "new content" + new Date().toString();
		note.setContent(newContent);
		note.commit();
		
		assertThat(note.getSyncState(), equalTo(Note.STATE.MODIFIED));
	}	
	
	
	@Test
	public void download_modify_and_sync(){
		Note note = NNModel.pullByUID(Note.class, 1L);				
		note.commit();
			
		String newContent = "new content" + new Date().toString();
		note.setContent(newContent);
		note.commit();
		
		note.push();
		
		assertThat(note.getSyncState(), equalTo(Note.STATE.SYNCED));
		
		Note syncedNote = NNModel.pullByUID(Note.class, 1L);	
		
		assertThat(syncedNote.getContent(), equalTo(newContent));
	}	

	
}
