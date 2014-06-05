package net.nature.mobile;

import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.Site;
import retrofit.RetrofitError;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class InitializationActivity extends Activity {

	static final String OUTPUT_SURVEY_TEXT = "survey.text";	
	protected static final String TAG = InitializationActivity.class.getName();
	private static final int REQUEST_MAIN_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		if (!isInitialSyncCompleted()){
			setContentView(R.layout.activity_loading);
			new InitialDownloadTask().execute((Void) null);
		}else{
			onInitializationCompleted();
		}
	}
	
	void onInitializationCompleted(){
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		startActivityForResult(intent, REQUEST_MAIN_ACTIVITY);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Intent result = new Intent();
		setResult(RESULT_OK, result);
		finish();
	}
	
	boolean isInitialSyncCompleted(){
		 return NNModel.countLocal(Site.class) == 4;
	}

	private class InitialDownloadTask extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {	 
			Log.d(TAG,  "downloading initial data");
			try{
				if (NNModel.countLocal(Site.class) == 0){
					NNModel.resolveByName(Site.class,  "aces");
					NNModel.resolveByName(Site.class,  "cu");
					NNModel.resolveByName(Site.class,  "umd");
					NNModel.resolveByName(Site.class,  "uncc");
				}						
				return isInitialSyncCompleted();
			}catch(RetrofitError e){
				return false;
			}
		}

		@Override
		protected void onPreExecute() {
			setContentView(R.layout.activity_loading);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result){
				onInitializationCompleted();
			}else{
				Log.d(TAG,  "failed to load initial data");
				setContentView(R.layout.activity_retry);			
				findViewById(R.id.buttonRetry).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						new InitialDownloadTask().execute((Void) null);					
					}					
				});
			}
		}
	}	
}
