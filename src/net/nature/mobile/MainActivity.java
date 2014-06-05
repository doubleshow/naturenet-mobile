package net.nature.mobile;


import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import net.nature.mobile.EditNoteActivity.SiteActivitiesAdapter;
import net.nature.mobile.EditNoteActivity.SiteLandmarkAdapter;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Feedback;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.Session;
import net.nature.mobile.model.Site;
import retrofit.RetrofitError;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.android.gms.location.LocationListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity 
implements LocationListener {

	private static final int REQUEST_CREATE_NOTE = 3;
	private static final int REQUEST_EDIT_NOTE = 4;
	private static final int REQUEST_SELECT_CONTEXT = 5;
	private static final int REQUEST_SELECT_ACCOUNT = 6;
	private static final int REQUEST_SURVEY = 7;

	private static final String TAG = "MainActivity";

	private SyncTask mSyncTask;
	private Account mAccount;
	private Context mContext;

	private View mButtonGallery;
	private ImageView mLastImage1st;
	private ImageView mLastImage2nd;
	private ImageView mLastImage3rd;
	private Button mButtonCreateNote;
	private MapFragment mMapFragment;
	private Location mCurrentLocation;
	private Site mSite;
	private Spinner mContextSpinner;
	private SiteActivitiesAdapter mContextAdapter;
	private Spinner mLandmarkSpinner;
	protected Context mLandmark;
	private SiteLandmarkAdapter mLandmarkAdapter;

	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	boolean isInitialized(){
		return NNModel.countLocal(Site.class) == 4;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		mAccount = Session.getAccount();
		mSite = Session.getSite();
		doSetupActivityMainLayout();
		if (mAccount == null || mSite == null){
			Intent intent = new Intent(getBaseContext(), SelectAccountActivity.class);
			startActivityForResult(intent, REQUEST_SELECT_ACCOUNT);
		}else{        	
			onSignedIn(mAccount, mSite);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SELECT_ACCOUNT){
			if (resultCode == RESULT_OK) {
				Long account_id = data.getLongExtra(SigninActivity.EXTRA_ACCOUNT_ID,-1);
				Account account = Model.load(Account.class,  account_id);
				String site_name = data.getStringExtra(SelectAccountActivity.EXTRA_SITE_NAME);
				Site site = NNModel.findByName(Site.class, site_name.toLowerCase());

				checkNotNull(account);
				checkNotNull(site);
				onSignedIn(account,site);
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
		}else if (requestCode == REQUEST_SURVEY){
			if (resultCode == RESULT_OK) {
				String surveyText = data.getStringExtra(SurveyActivity.OUTPUT_SURVEY_TEXT);

				Feedback f = new Feedback();
				f.setKind("Survey");
				f.setAccount(mAccount);
				f.setContent(surveyText);
				f.setTarget(mAccount);
				f.commit();				

				//				mSyncTask = new SyncTask();
				//				mSyncTask.execute(mAccount);

				mAccount = null;
				mContext = null;		
				Session.signOut();

				Intent intent = new Intent(getBaseContext(), SelectAccountActivity.class);
				startActivityForResult(intent, REQUEST_SELECT_ACCOUNT);
			}
		}


	}

	private void onContextSelected(Context context){
		checkNotNull(context);
		int position = mContextAdapter.getPositionByName(context.getName());			
		mContextSpinner.setSelection(position);
	}

	private void launchEditNoteActivity(Long note_id){
		Intent intent = new Intent(getBaseContext(), EditNoteActivity.class);
		intent.putExtra(EditNoteActivity.Extras.NOTE_ID, note_id);
		intent.putExtra(EditNoteActivity.EXTRA_SITE_ID, mSite.getId());
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

	private void doSetupActivityMainLayout(){
		setContentView(R.layout.activity_main);
		mButtonCreateNote = (Button) findViewById(R.id.main_button_create_note);
		mButtonGallery = (View) findViewById(R.id.main_image_right_arrow);
		mLastImage1st = (ImageView) findViewById(R.id.main_image_last_1st);
		mLastImage2nd = (ImageView) findViewById(R.id.main_image_last_2nd);
		mLastImage3rd = (ImageView) findViewById(R.id.main_image_last_3rd);
		mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mContextSpinner = (Spinner) findViewById(R.id.note_context);
		mLandmarkSpinner = (Spinner) findViewById(R.id.note_landmark);


		//
		// Context Spinner
		//		
		mContextSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Context context = (Context) parentView.getItemAtPosition(position);		    	
				checkNotNull(context);
				mContext = context;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});

		// Landmark Spinner
		mLandmarkSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Context context = (Context) parentView.getItemAtPosition(position);		    	
				checkNotNull(context);
				mLandmark = context;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});






		mButtonGallery.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ListNoteActivity.class);
				intent.putExtra(ListNoteActivity.EXTRA_ACCOUNT_ID, mAccount.getId());
				intent.putExtra(ListNoteActivity.EXTRA_SITE_ID, mSite.getId());
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
				intent.putExtra(CreateNoteActivity.Extras.INPUT_LANDMARK_ID, mLandmark.getId());
				if (mCurrentLocation != null){
					intent.putExtra(CreateNoteActivity.Extras.INPUT_LONGITUDE,  mCurrentLocation.getLongitude());
					intent.putExtra(CreateNoteActivity.Extras.INPUT_LATITUDE,  mCurrentLocation.getLatitude());
				}
				startActivityForResult(intent, REQUEST_CREATE_NOTE);
			}        	
		});

		if (mContext == null){

		}else{
			onContextSelected(mContext);
		}
	}



	private void onSignedIn(Account account, Site site){
		checkNotNull(account);
		checkNotNull(site);

		Session.signIn(account,site);
		mAccount = account;
		mSite = site;		

		// set title text
		ActionBar ab = getActionBar();
		ab.setTitle(account.getUsername() + "@" + site.getName().toUpperCase()); 

		// depends on site
		mContextAdapter = new SiteActivitiesAdapter(this, mSite);
		mContextSpinner.setAdapter(mContextAdapter);
		mContextSpinner.setSelection(0);

		mLandmarkAdapter = new SiteLandmarkAdapter(this, mSite);
		mLandmarkSpinner.setAdapter(mLandmarkAdapter);
		mLandmarkSpinner.setSelection(0);

		mMapFragment.setSite(mSite);

		for (Context landmark : site.getLandmarks()){
			Double longitude = (Double) landmark.getExtras().get("longitude");
			Double latitude = (Double) landmark.getExtras().get("latitude");
			if (longitude != null && latitude != null)
				mMapFragment.addLandmarkMarker(latitude, longitude, landmark.getTitle());
		}

		loadRecentNotes(account);
	}

	private void loadRecentNotes(Account account){
		checkNotNull(account);

		// display the last two recent notes
		List<Note> notes = mAccount.getNotesOrderedByRecencyAtSite(mSite);
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
			//		}else if (id == R.id.action_sync){        	
			//			doSync();
		}else if (id == R.id.action_signout){        	
			doSignout();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.action_signout);
		item.setEnabled(mAccount != null);
		//		menu.findItem(R.id.action_sync).setEnabled(mSyncTask == null);
		return super.onPrepareOptionsMenu(menu);
	}

	private void doSignout() {
		Intent intent = new Intent(getBaseContext(), SurveyActivity.class);
		startActivityForResult(intent, REQUEST_SURVEY);
	}

	@Override
	public void onStop() {
		if (mSyncTask != null)
			mSyncTask.cancel(true);
		super.onStop();
	}


	@Override
	public void onStart() {
		super.onStart();
		mSyncTask = new SyncTask();
		mSyncTask.execute();
	}

	private static final int SYNC_INTERVAL = 60000;
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class SyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {				
				while (!isCancelled()){
					List<Account> accounts = new Select().from(Account.class).execute();
					for (Account account : accounts){
						try {
							account.pushNotes();
							account.pushFeedbacks();
						}
						catch (RetrofitError e){
							e.printStackTrace();
						}
					}
					Thread.sleep(SYNC_INTERVAL);
				}
			}catch (InterruptedException e) {
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
		}

		@Override
		protected void onCancelled() {
		}
	}

	private static final String PROVIDER = "flp";
	private static final double LAT = 39.195324;
	private static final double LNG = -106.821839;
	private static final float ACCURACY = 3.0f;
	/*
	 * From input arguments, create a single Location with provider set to
	 * "flp"
	 */
	public Location createLocation(double lat, double lng, float accuracy) {
		// Create a new Location
		Location newLocation = new Location(PROVIDER);
		newLocation.setLatitude(lat);
		newLocation.setLongitude(lng);
		newLocation.setAccuracy(accuracy);
		return newLocation;
	}

	// Example of creating a new Location from test data
	Location testLocation = createLocation(LAT, LNG, ACCURACY);	

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "location update :" + location);
		mCurrentLocation = location;
		mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		if (mMapFragment != null){
			//			mMapFragment.setCurrentLocation(testLocation);
			mMapFragment.setCurrentLocation(location);
		}

	}

}
