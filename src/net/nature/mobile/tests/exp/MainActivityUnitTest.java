package net.nature.mobile.tests.exp;

import net.nature.mobile.R;
import net.nature.mobile.exp.MainActivity;
import android.app.Fragment;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.TextView;

public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity>{

	public MainActivityUnitTest() {
		super(MainActivity.class);
	}

	private Intent mLaunchIntent;
	private Button sendButton;
	private MainActivity mActivity;

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(getInstrumentation()
                .getTargetContext(), MainActivity.class);
        startActivity(mLaunchIntent, null, null);
        mActivity = getActivity();
//        getInstrumentation().callActivityOnStart(mActivity);
////        Fragment v = mActivity.getFragmentManager().findFragmentById(R.id.view_info);
//        sendButton =
//                (Button) getActivity()
//                .findViewById(R.id.button_send);        
    }
	
//	@MediumTest
	public void testNextActivityWasLaunchedWithIntent() {
//		  mLaunchIntent = new Intent(getInstrumentation()
//	                .getTargetContext(), MainActivity.class);
//	        startActivity(mLaunchIntent, null, null);
//		TextView v = (TextView) getActivity().findViewById(R.id.view_info);
//		v.performClick();
//	        sendButton =
//	                (Button) getActivity()
//	                .findViewById(R.id.button_send);
//	    sendButton.performClick();
//		((MainActivity) getActivity()).sendMessage();

	    final Intent launchIntent = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchIntent);
	    assertTrue(isFinishCalled());

//	    final String payload =
//	            launchIntent.getStringExtra(NextActivity.EXTRAS_PAYLOAD_KEY);
//	    assertEquals("Payload is empty", LaunchActivity.STRING_PAYLOAD, payload);
	}
}
