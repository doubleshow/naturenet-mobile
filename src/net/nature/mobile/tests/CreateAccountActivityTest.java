package net.nature.mobile.tests;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.nature.mobile.CreateAccountActivity;
import net.nature.mobile.R;
import net.nature.mobile.model.User;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.*;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.*;

public class CreateAccountActivityTest extends ActivityInstrumentationTestCase2<CreateAccountActivity> {

	private String newUsername = "testUser123";

	public CreateAccountActivityTest() {
		super(CreateAccountActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getActivity();
		new Delete().from(User.class).where("name = ?", newUsername).execute();
	}

	@MediumTest
	public void test_create_account(){
		
		int n = new Select().from(User.class).execute().size();
		
		onView(withId(R.id.username))
			.perform(typeText(newUsername));

		onView(withId(R.id.name))
			.perform(typeText("Test User"));

		onView(withId(R.id.email))
			.perform(typeText("test@email.com"));

		onView(withId(R.id.password))
			.perform(typeText("testpassword"));

		
		onView(withId(R.id.sign_in_button))
			.perform(click());

		int m = new Select().from(User.class).execute().size();
		assertEquals(n+1, m);
		
		User newUser = new Select().from(User.class).where("username = ?", newUsername).executeSingle();
		assertNotNull(newUser);
		assertEquals(newUsername, newUser.username);

	}

}
