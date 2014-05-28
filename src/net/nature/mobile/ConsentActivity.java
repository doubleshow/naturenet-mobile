package net.nature.mobile;

import static com.google.common.base.Preconditions.checkNotNull;

import com.activeandroid.Model;
import com.google.common.collect.Lists;

import retrofit.RetrofitError;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import net.nature.mobile.rest.NatureNetRestAdapter;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class ConsentActivity extends Activity {
	//	/**
	//	 * A dummy authentication store containing known user names and passwords.
	//	 * TODO: remove after connecting to a real authentication system.
	//	 */
	//	private static final String[] DUMMY_CREDENTIALS = new String[] {
	//			"foo@example.com:hello", "bar@example.com:world" };
	//
	//	/**
	//	 * The default email to populate the email field with.
	//	 */
	//	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	//	public static final String EXTRA_NAME = "com.example.android.authenticatordemo.extra.NAME";
	//
	//	/**
	//	 * Keep track of the login task to ensure we can cancel it if requested.
	//	 */
	////	private UserLoginTask mAuthTask = null;
	//
	//	// Values for email and password at the time of the login attempt.
	//	private String mUsername;
	//	private String mName;
	//	private String mEmail;
	//	private String mPassword;
	//
	//	// UI references.
	//	private EditText mUsernameView;
	//	private EditText mNameView;
	//	private EditText mEmailView;
	//	private EditText mPasswordView;
	//	private View mLoginFormView;
	//	private View mLoginStatusView;
	//	private TextView mLoginStatusMessageView;

	protected static final int REQUEST_NEXT_PAGE = 0;
	private static final int REQUEST_CREATE_ACCOUNT = 1;
	protected static final String TAG = "ConsentActivity";
	private CheckBox checkbox1;
	private CheckBox checkbox2;
	private int page;
	private Button next;
	private String consentText;
	private CheckBox checkbox3;
	private CheckBox checkbox4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		page = getIntent().getIntExtra("page", 1);
		consentText = getIntent().getStringExtra("consent.text");

		if (page == 1){
			setContentView(R.layout.activity_consent_page1);	
		}else if (page == 2){
			setContentView(R.layout.activity_consent_page2);


			checkbox1 = (CheckBox) findViewById(R.id.checkBox1);
			checkbox2 = (CheckBox) findViewById(R.id.checkBox2);
			checkbox3 = (CheckBox) findViewById(R.id.checkBox3);
			checkbox4 = (CheckBox) findViewById(R.id.checkBox4);			

			OnClickListener setNextButton = new OnClickListener(){
				@Override
				public void onClick(View v) {
					boolean required = checkbox1.isChecked() && checkbox2.isChecked();
					next.setEnabled(required);					
				}		
			};
			checkbox1.setOnClickListener(setNextButton);
			checkbox2.setOnClickListener(setNextButton);
		}else if (page == 3){
//			Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
			Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
			intent.putExtra("consent.text", consentText);
			startActivityForResult(intent, REQUEST_CREATE_ACCOUNT);
			return;
		}		

		next = (Button) findViewById(R.id.next_button);		
		next.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ConsentActivity.class);
				intent.putExtra("page", page + 1);
				if (page == 2){
					consentText = "";
					for (CheckBox ch : Lists.newArrayList(checkbox1,checkbox2,checkbox3,checkbox4)){
						if (ch.isChecked()){
							consentText += ch.getText().toString();
						}
					}
					intent.putExtra("consent.text", consentText);
					Log.d(TAG,"consentText=" + consentText);
				}
				startActivityForResult(intent, REQUEST_NEXT_PAGE);
			}
		});
	}

//	void doFinish(){
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_NEXT_PAGE){
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
			}
		}else if (requestCode == REQUEST_CREATE_ACCOUNT){
			setResult(resultCode, data);
			finish();			
		}
	}
}
