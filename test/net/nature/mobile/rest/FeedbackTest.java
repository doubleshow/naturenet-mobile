package net.nature.mobile.rest;

import java.util.Date;

import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Feedback;
import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.NNModel.STATE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
public class FeedbackTest {

	private Feedback newFeedback;

	@Before
	public void setUp(){
		Note note = NNModel.resolveByUID(Note.class, 1L);
		Account account  = NNModel.resolveByUID(Account.class, 1L);
		
		newFeedback = new Feedback();
		newFeedback.setContent("new content of a feedback");
		newFeedback.setKind("comment");
		newFeedback.setTarget(note);	
		newFeedback.setAccount(account);
		ShadowLog.stream = System.out;
	}
	
	@Test
	public void resolve_feedback_by_uid(){
		Feedback feedback = NNModel.resolveByUID(Feedback.class, 10L);
		System.out.println(feedback);
		assertThat(feedback, notNullValue());
		assertThat(NNModel.countLocal(Feedback.class, STATE.SYNCED), equalTo(1));
	}
		
	@Test
	public void create_new_and_sync(){
		newFeedback.commit();
		assertThat(NNModel.countLocal(Feedback.class, STATE.SAVED), equalTo(1));
		
		newFeedback.push();
		assertThat(NNModel.countLocal(Feedback.class, STATE.SYNCED), equalTo(1));
	}	
	
	@Test
	public void pull_modify_push(){
		Feedback f = NNModel.pullByUID(Feedback.class, 1L);				
		f.commit();
		assertThat(f.getSyncState(), equalTo(STATE.SYNCED));
		
		String newContent = "new content" + new Date().toString();
		f.setContent(newContent);
		f.commit();
		
		assertThat(f.getSyncState(), equalTo(STATE.MODIFIED));
		
		f.push();
		Feedback remote = NNModel.pullByUID(Feedback.class, 1L);	
		
		assertThat(remote.getContent(), equalTo(newContent));
	}	
}
