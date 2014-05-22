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
public class MediaTest {
	
	private Note note;
	private Media media;

	@Before
	public void setUp(){
		ShadowLog.stream = System.out;
		note = NNModel.resolve(Note.class,  1L);
		
		media = new Media();
		media.setNote(note);
		media.setLocal("test.png");
	}
	
	
	@Test
	public void download_media(){
		Media m = NNModel.download(Media.class,  1L);
		assertThat(m, notNullValue());
	}
	
	@Test
	public void new_media(){
		assertThat(media.getSyncState(), equalTo(STATE.NEW));
	}
	
	@Test
	public void new_media_upload(){
		media.commit();
		media.push();
		assertThat(media.getSyncState(), equalTo(STATE.SYNCED));
		
		Media r = NNModel.download(Media.class,  media.getUId());
		assertThat(r, notNullValue());
	}
		

	
}
