package net.nature.mobile.tests;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.nature.mobile.CreateAccountActivity;
import net.nature.mobile.EditNoteActivity;
import net.nature.mobile.R;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.*;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;

public class EditNoteActivityTest extends ActivityInstrumentationTestCase2<EditNoteActivity> {
	
	private Note note;
	private Context context1;
	private Context context2;
	private Media media;

	public EditNoteActivityTest() {
		super(EditNoteActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();		
		new Delete().from(Note.class).execute();
		new Delete().from(Context.class).execute();
		
		context1 = new Context();
		//context1.(1L);
		context1.kind = "Activity";
		context1.name = "Ask an expert";
		context1.save();
		
		context2 = new Context();
//		context2.setuID(2L);
		context2.kind = "Activity";
		context2.name = "Take a picture";
		context2.save();		
		
		note = new Note();
		//note.uID = 1L;
		note.content = "Mock content";
		note.context_id = context2.getId();
		note.save();
		
		media = new Media();
		media.setNote(note); 
		media.setURL("http://i.imgur.com/DvpvklR.png");
		media.save();

		
		Intent intent = new Intent();
		intent.putExtra(EditNoteActivity.Extras.NOTE_ID, 1L);
	    setActivityIntent(intent);
		getActivity();
	}

	@MediumTest
	public void test_view_note(){		
		onView(withId(R.id.note_content)).
			check(matches(isDisplayed())).
			check(matches(withText(note.content)));
				
		onView(withId(R.id.note_save)).
			check(matches(not(isDisplayed())));

		onView(withId(R.id.note_cancel)).
			check(matches(not(isDisplayed())));
		
		onView(withId(R.id.note_context)).
			check(matches(hasDescendant(withText(context2.name))));		
	}	
	
	@MediumTest
	public void test_click_to_change_context(){
		onView(withId(R.id.note_context)).
			perform(click());
		
		onView(withText(context1.name)).
			perform(click());
		
		Note note = Model.load(Note.class, 1L);
		assertThat("context should change to 1", 
				note.context_id, equalTo(context1.getUId()));
	}
	
	@MediumTest
	public void test_click_to_edit_content_and_save(){
		onView(withId(R.id.note_content)).
			perform(click());
		
		onView(withId(R.id.note_save)).
			check(matches(isDisplayed()));
	
		onView(withId(R.id.note_cancel)).
			check(matches(isDisplayed()));
		
		onView(withId(R.id.note_content)).
			perform(typeText("more"));

		onView(withId(R.id.note_save)).
			perform(click());
		
		onView(withId(R.id.note_save)).
			check(matches(not(isDisplayed())));
				
		Note updatedNote = Model.load(Note.class, 1L);
		assertThat("content should have few more characters",
				updatedNote.content, equalTo(note.content + "more"));
	}
}
