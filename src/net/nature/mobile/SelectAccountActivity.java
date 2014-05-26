package net.nature.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectAccountActivity extends Activity {

	protected static final int REQUEST_SIGNIN = 0;
	protected static final int REQUEST_CREATE_ACCOUNT = 1;
	
	private Button mButtonSignin;
	private Button mButtonCreateAccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_account);
		
		mButtonSignin = (Button) findViewById(R.id.main_signin);
		mButtonCreateAccount = (Button) findViewById(R.id.main_create_account);	
		
		mButtonSignin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), SigninActivity.class);
				startActivityForResult(intent, REQUEST_SIGNIN);
			}        	
		});

		mButtonCreateAccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ConsentActivity.class);
				startActivityForResult(intent, REQUEST_CREATE_ACCOUNT);
			}        	
		});		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIGNIN || requestCode == REQUEST_CREATE_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
//				Long account_id = data.getLongExtra(SigninActivity.EXTRA_ACCOUNT_ID,-1);
//				Account account = Model.load(Account.class,  account_id);				
//				checkNotNull(account);
//				onSignedIn(account);		
			}
		}
	}

}
