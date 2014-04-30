package net.nature.mobile.tests;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.nature.mobile.CreateAccountActivity;
import net.nature.mobile.EditNoteActivity;
import net.nature.mobile.R;
import net.nature.mobile.model.Account;
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

	private String newUsername = "testUser123";
	private Note note;

	public EditNoteActivityTest() {
		super(EditNoteActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();		
		new Delete().from(Note.class).execute();
		
		note = new Note();
		note.id = 1L;
		note.content = "Mock content";
		note.save();
		
		Intent intent = new Intent();
		intent.putExtra(EditNoteActivity.Extras.NOTE_ID, 1L);
	    setActivityIntent(intent);
		getActivity();
	}

	@MediumTest
	public void test_display_note(){		
		onView(withId(R.id.note_content)).
			check(matches(isDisplayed())).
			check(matches(withText(note.content)));
				
		onView(withId(R.id.note_save)).
			check(matches(not(isDisplayed())));

		onView(withId(R.id.note_cancel)).
			check(matches(not(isDisplayed())));
		
		onView(withId(R.id.note_content)).
			perform(click());
		
		onView(withId(R.id.note_save)).
			check(matches(isDisplayed()));

		onView(withId(R.id.note_cancel)).
			check(matches(isDisplayed()));
		
		onView(withId(R.id.note_cancel)).
			perform(click());
		
		onView(withId(R.id.note_cancel)).
			check(matches(not(isDisplayed())));		
	}

	@MediumTest
	public void test_click_to_edit_content_and_cancel(){
		onView(withId(R.id.note_content)).
			perform(click());
		
		onView(withId(R.id.note_save)).
			check(matches(isDisplayed()));
	
		onView(withId(R.id.note_cancel)).
			check(matches(isDisplayed()));
		
		onView(withId(R.id.note_cancel)).
			perform(click());
		
		onView(withId(R.id.note_cancel)).
			check(matches(not(isDisplayed())));
		
		Note note = Note.find(1L);
		assertThat("content should stay the same", 
				note.content, equalTo(note.content));
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
				
		Note note = Note.find(1L);
		assertThat("content should have few more characters",
				note.content, equalTo(note.content + "more"));
	}
}
