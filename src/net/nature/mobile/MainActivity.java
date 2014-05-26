package net.nature.mobile;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.Session;
import net.nature.mobile.model.Site;
import retrofit.RetrofitError;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.Model;
import com.google.android.gms.location.LocationListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity 
implements LocationListener {

	private static final int REQUEST_CREATE_NOTE = 3;
	private static final int REQUEST_EDIT_NOTE = 4;
	private static final int REQUEST_SELECT_CONTEXT = 5;
	private static final int REQUEST_SELECT_ACCOUNT = 6;

	private static final String TAG = "MainActivity";

	private SyncTask mSyncTask;
	private Account mAccount;
	private Context mContext;

	private TextView mUsername;
	private View mButtonGallery;
	private ImageView mLastImage1st;
	private ImageView mLastImage2nd;
	private ImageView mLastImage3rd;
	private TextView mContextName;
	private Button mButtonCreateNote;
	private View mButtonSelectContext;
	private MapFragment mMapFragment;
	private Location mCurrentLocation;
	private MyTask download;


	private class MyTask extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {	    
			if (NNModel.countLocal(Site.class) == 0){
				NNModel.resolveByName(Site.class,  "aces");
				NNModel.resolveByName(Site.class,  "cu");
				NNModel.resolveByName(Site.class,  "umd");
				NNModel.resolveByName(Site.class,  "uncc");
			}						
			return NNModel.countLocal(Site.class) == 4;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result){
				doCreate();
			}else{
//				Toast.makeText(getApplicationContext(),
//						"Unable to load data from the server. Please check your internet connection.", Toast.LENGTH_LONG).show();
//				finish();
				setContentView(R.layout.activity_retry);
				findViewById(R.id.buttonRetry).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						setContentView(R.layout.activity_loading);
						download = new MyTask();
						download.execute((Void) null);
					}					
				});
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_loading);
		download = new MyTask();
		download.execute((Void) null);
		
	}
	
	private void doCreate(){
		setContentView(R.layout.activity_main);

		mButtonCreateNote = (Button) findViewById(R.id.main_button_create_note);

		mUsername = (TextView) findViewById(R.id.main_username);
		mButtonGallery = (View) findViewById(R.id.main_image_right_arrow);
		mLastImage1st = (ImageView) findViewById(R.id.main_image_last_1st);
		mLastImage2nd = (ImageView) findViewById(R.id.main_image_last_2nd);
		mLastImage3rd = (ImageView) findViewById(R.id.main_image_last_3rd);

		mContextName = (TextView) findViewById(R.id.main_context);
		mButtonSelectContext = (View) findViewById(R.id.main_button_select_activity);
		
		mAccount = Session.getAccount();
		Log.d("main", ""+mAccount);
		if (mAccount == null){
			Intent intent = new Intent(getBaseContext(), SelectAccountActivity.class);
			startActivityForResult(intent, REQUEST_SELECT_ACCOUNT);
		}else{        	
			onSignedIn(mAccount);
		}

		mButtonGallery.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ListNoteActivity.class);
				intent.putExtra(ListNoteActivity.EXTRA_ACCOUNT_ID, mAccount.getId());
				startActivity(intent);
			}        	
		});

		mButtonCreateNote.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				checkNotNull(mAccount); checkNotNull(mContext);

				Intent intent = new Intent(getBaseContext(), CreateNoteActivity.class);
				intent.putExtra(CreateNoteActivity.Extras.INPUT_ACCOUNT_ID, mAccount.getId());
				intent.putExtra(CreateNoteActivity.Extras.INPUT_CONTEXT_ID, mContext.getId());
				if (mCurrentLocation != null){
					intent.putExtra(CreateNoteActivity.Extras.INPUT_LONGITUDE,  mCurrentLocation.getLongitude());
					intent.putExtra(CreateNoteActivity.Extras.INPUT_LATITUDE,  mCurrentLocation.getLatitude());
				}
				startActivityForResult(intent, REQUEST_CREATE_NOTE);
			}        	
		});

		mButtonSelectContext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				checkNotNull(mContext);

				Intent intent = new Intent(getBaseContext(), SelectContextActivity.class);				
				intent.putExtra(SelectContextActivity.EXTRA_INPUT_CONTEXT_ID, mContext.getId());
				startActivityForResult(intent, REQUEST_SELECT_CONTEXT);
			}        	
		});

		if (mContext == null){

		}else{
			onContextSelected(mContext);
		}


		mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);


	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_ACCOUNT){
			if (resultCode == RESULT_OK) {
				Long account_id = data.getLongExtra(SigninActivity.EXTRA_ACCOUNT_ID,-1);
				Account account = Model.load(Account.class,  account_id);				
				checkNotNull(account);
				onSignedIn(account);
			}else {
				finish();
			}
		}else if (requestCode == REQUEST_CREATE_NOTE){
			if (resultCode == RESULT_OK) {	    	
				loadRecentNotes(mAccount);
				Long note_id = data.getLongExtra(CreateNoteActivity.Extras.OUTPUT_NOTE_ID,-1);
				launchEditNoteActivity(note_id);
			}
		}else if (requestCode == REQUEST_EDIT_NOTE){

		}else if (requestCode == REQUEST_SELECT_CONTEXT){
			if (resultCode == RESULT_OK) {	    	
				loadRecentNotes(mAccount);
				Long context_id = data.getLongExtra(SelectContextActivity.EXTRA_OUTPUT_CONTEXT_ID, -1);
				Context context = Model.load(Context.class,  context_id);
				checkNotNull(context);				
				onContextSelected(context);
			}
		}


	}

	private void onContextSelected(Context context){
		checkNotNull(context);
		mContext = context;
		mContextName.setText(context.title);
	}

	private void launchEditNoteActivity(Long note_id){
		Intent intent = new Intent(getBaseContext(), EditNoteActivity.class);
		intent.putExtra(EditNoteActivity.Extras.NOTE_ID, note_id);
		startActivityForResult(intent, REQUEST_EDIT_NOTE);
	}

	class OnClickToLaunchEditNoteActivity implements OnClickListener{
		private final Long note_id;
		public OnClickToLaunchEditNoteActivity(Long note_id) {
			super();
			this.note_id = note_id;
		}

		@Override
		public void onClick(View arg0) {
			launchEditNoteActivity(note_id);
		}
	}

	private void onSignedIn(Account account){
		checkNotNull(account);		
		Session.signIn(account);
		mAccount = account;
		mUsername.setText(account.getUsername());

		// select the default  context
		Context context = Model.load(Context.class, 1L);
		onContextSelected(context);

		loadRecentNotes(account);
	}

	private void loadRecentNotes(Account account){
		checkNotNull(account);

		// display the last two recent notes
		List<Note> notes = mAccount.getRecentNotes(4);
		if (notes.size() >= 1){			
			showNoteImageHelper(notes.get(0), mLastImage1st);
		}else{
			showPlaceHolderImageHelper(mLastImage1st);
		}

		if (notes.size() >= 2){
			showNoteImageHelper(notes.get(1), mLastImage2nd);
		}else{
			showPlaceHolderImageHelper(mLastImage2nd);
		}		

		if (notes.size() >= 3){			
			showNoteImageHelper(notes.get(2), mLastImage3rd);
		}else{
			showPlaceHolderImageHelper(mLastImage3rd);
		}

		if (notes.size() > 3){			
			findViewById(R.id.main_image_right_arrow).setVisibility(View.VISIBLE);			
		}else{
			findViewById(R.id.main_image_right_arrow).setVisibility(View.INVISIBLE);
		}

	}

	private void showPlaceHolderImageHelper(ImageView view){
		view.setVisibility(View.INVISIBLE);
		//		view.setImageResource(R.drawable.ic_place_holder);
		view.setOnClickListener(null);
	}

	private void showNoteImageHelper(Note note, ImageView view){
		checkNotNull(note);

		view.setVisibility(View.VISIBLE);
		view.setOnClickListener(new OnClickToLaunchEditNoteActivity(note.getId()));

		Media media = note.getMediaSingle();
		if (media != null){
			String path = media.getPath();
			Picasso.with(this).load(path).resize(150,150).centerCrop().into(view);				
		}		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}else if (id == R.id.action_sync){        	
			doSync();
		}else if (id == R.id.action_signout){        	
			doSignout();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_signout);
		item.setEnabled(mAccount != null);
		menu.findItem(R.id.action_sync).setEnabled(mSyncTask == null);
		return super.onPrepareOptionsMenu(menu);
	}

	private void doSignout() {
		mAccount = null;
		mContext = null;		
		Session.signOut();
		Intent intent = new Intent(getBaseContext(), SelectAccountActivity.class);
		startActivityForResult(intent, REQUEST_SELECT_ACCOUNT);
	}

	private void doSync() {
		((TextView) findViewById(R.id.label_notes)).setText("Your Notes (syncing...)");
		mSyncTask = new SyncTask();
		mSyncTask.execute((Void) null);
	}


	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class SyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {					
			try {
				checkNotNull(mAccount);
				mAccount.pushNotes();
			}catch (RetrofitError e){
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mSyncTask = null;
			((TextView) findViewById(R.id.label_notes)).setText("Your Notes");
		}

		@Override
		protected void onCancelled() {
			mSyncTask = null;
			((TextView) findViewById(R.id.label_notes)).setText("Your Notes");
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "location update :" + location);
		mCurrentLocation = location;
		mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		if (mMapFragment != null){
			mMapFragment.setCurrentLocation(location);
		}

	}

}
