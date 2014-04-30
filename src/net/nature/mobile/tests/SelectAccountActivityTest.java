package net.nature.mobile.tests;

import java.lang.reflect.Field;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.google.android.apps.common.testing.ui.espresso.matcher.BoundedMatcher;

import net.nature.mobile.SelectAccountActivity;
import net.nature.mobile.model.Account;
import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.*;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;

public class SelectAccountActivityTest extends ActivityInstrumentationTestCase2<SelectAccountActivity> {

	private Account mockUser;

	public SelectAccountActivityTest() {
		super(SelectAccountActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mockUser = new Account();
		mockUser.username = ""+(new Date()).getTime();
		mockUser.save();
		
		getActivity();
	}
	
	@Override
	protected void tearDown() throws Exception {
		mockUser.delete();
		super.tearDown();
	}
	
	public static Matcher<Object> withUsername(final Matcher<String> itemTextMatcher){
//		  checkNotNull(itemTextMatcher);
		  return new BoundedMatcher<Object, Account>(Account.class) {
		    @Override
		    public boolean matchesSafely(Account user) {
		      return itemTextMatcher.matches(user.username);
		    }

		    @Override
		    public void describeTo(Description description) {
		      description.appendText("with item content: ");
		      itemTextMatcher.describeTo(description);
		    }
		  };
		}
	
	
	protected Intent assertFinishCalledWithResult(int resultCode) {
		  assertThat(isFinishCalled(), is(true));
		  try {
		    Field f = Activity.class.getDeclaredField("mResultCode");
		    f.setAccessible(true);
		    int actualResultCode = (Integer)f.get(getActivity());
		    assertThat(actualResultCode, is(resultCode));
		    f = Activity.class.getDeclaredField("mResultData");
		    f.setAccessible(true);
		    return (Intent)f.get(getActivity());
		  } catch (NoSuchFieldException e) {
		    throw new RuntimeException("Looks like the Android Activity class has changed it's   private fields for mResultCode or mResultData.  Time to update the reflection code.", e);
		  } catch (Exception e) {
		    throw new RuntimeException(e);
		  }
		}
	
	
	private boolean isFinishCalled() {
		return getActivity().isFinishing();
	}

	@MediumTest
	public void test_click_an_item_to_select(){
		
		onData(withUsername(equalTo(mockUser.username))).perform(click());
		
		Intent intent = assertFinishCalledWithResult(Activity.RESULT_OK);
		assertThat(intent, notNullValue());
		assertThat(intent.getCharSequenceExtra("username").toString(),equalTo(mockUser.username));
	}

//	@MediumTest
//	public void test_press_back_to_cancel(){
// got: com.google.android.apps.common.testing.ui.espresso.NoActivityResumedException: Pressed back and killed the app
//		Espresso.pressBack();
//	}
}
