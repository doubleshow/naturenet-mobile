package net.nature.mobile.tests.exp;

import net.nature.mobile.R;
import net.nature.mobile.exp.DisplayMessageActivity;
import net.nature.mobile.exp.MainActivity;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import static org.fest.assertions.api.ANDROID.assertThat;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.*;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.*;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mFirstTestActivity;
	private TextView mFirstTestText;
	private Button mSendButton;
	private TextView mInfoView;


	public MainActivityTest() {
		super(MainActivity.class);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(true);

		mFirstTestActivity = getActivity();
		mFirstTestText =
				(TextView) mFirstTestActivity
				.findViewById(R.id.edit_message);



		mSendButton = (Button) mFirstTestActivity.findViewById(R.id.button_send);
		mInfoView = (TextView) mFirstTestActivity.findViewById(R.id.view_info);

	}

	@MediumTest
	public void testSendButton_layout(){
		final View decorView = mFirstTestActivity.getWindow().getDecorView();

		//		ViewAsserts.assertOnScreen(decorView,  mSendButton);

		// Set up an ActivityMonitor
//		ActivityMonitor receiverActivityMonitor =
//				getInstrumentation().addMonitor(DisplayMessageActivity.class.getName(),
//						null, false);

		
		onView(withId(R.id.edit_message))
			.perform(typeText("Hello"));
		
		onView(withId(R.id.button_send))
			.perform(click());
		
//		TouchUtils.clickView(this,  mSendButton);

		//		assertTrue(View.INVISIBLE == mInfoView.getVisibility());
//		assertThat(mInfoView).isInvisible();//isGone();
//
//		DisplayMessageActivity receiverActivity = (DisplayMessageActivity) 
//				receiverActivityMonitor.waitForActivityWithTimeout(1000);
//		assertNotNull("ReceiverActivity is null", receiverActivity);
//		assertEquals("Monitor for ReceiverActivity has not been called",
//				1, receiverActivityMonitor.getHits());
//		assertEquals("Activity is of wrong type",
//				DisplayMessageActivity.class, receiverActivity.getClass());
		
//		final Intent launchIntent = getStartedActivityIntent();


		onView(withId(R.id.view_hello))
			.check(matches(withText("Hello")));

		
//		// Remove the ActivityMonitor
//		getInstrumentation().removeMonitor(receiverActivityMonitor);

	}

	public void testTextView(){
		final String expected = "Hello World";//mFirstTestText.getText().toString();
		final String actual = mFirstTestText.getText().toString();
		assertEquals(expected, actual);
	}

	@MediumTest
	public void testTypeHello(){
		onView(withId(R.id.edit_message))
		.perform(typeText("Hello"))
		.check(matches(withText("Hello")));
	}

	@MediumTest
	public void testTypeSomething(){

		getInstrumentation().runOnMainSync(new Runnable(){
			@Override
			public void run(){
				mFirstTestText.requestFocus();
			}			
		});

		getInstrumentation().waitForIdleSync();
		getInstrumentation().sendStringSync("Hello");
		getInstrumentation().waitForIdleSync();

		//	    final String expected = mFirstTestText.getText().toString();
		//	    final String actual = mFirstTestText.getText().toString();
		//	    assertEquals(expected, actual);
	}
}
