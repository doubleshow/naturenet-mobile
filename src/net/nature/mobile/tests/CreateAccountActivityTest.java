package net.nature.mobile.tests;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import net.nature.mobile.CreateAccountActivity;
import net.nature.mobile.R;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.BaseModel;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.*;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;
public class CreateAccountActivityTest extends ActivityInstrumentationTestCase2<CreateAccountActivity> {

	private String newUsername = "testUser123";

	public CreateAccountActivityTest() {
		super(CreateAccountActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		getActivity();
		new Delete().from(Account.class).where("name = ?", newUsername).execute();
	}

	@MediumTest
	public void test_create_account(){
		
		int n = new Select().from(Account.class).execute().size();
		
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

		int m = new Select().from(Account.class).execute().size();
		assertEquals(n+1, m);
		
		Account newUser = new Select().from(Account.class).where("username = ?", newUsername).executeSingle();
		assertNotNull(newUser);
		assertEquals(newUsername, newUser.username);

	}
	
	@MediumTest
	public void test_username_already_taken_error(){
		
		int n = BaseModel.count(Account.class);
		
		onView(withId(R.id.username))
			.perform(typeText("tomyeh"));

		onView(withId(R.id.name))
			.perform(typeText("First lastname"));

		onView(withId(R.id.email))
			.perform(typeText("test@email.com"));

		onView(withId(R.id.password))
			.perform(typeText("testpassword"));

		
		onView(withId(R.id.sign_in_button))
			.perform(click());

		assertThat(BaseModel.count(Account.class), equalTo(n));
	}

}
