package net.nature.mobile;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.RetrofitError;

import com.activeandroid.Model;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.nutiteq.MapView;
import com.nutiteq.components.Bounds;
import com.nutiteq.components.Color;
import com.nutiteq.components.Components;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.Range;
import com.nutiteq.geometry.Line;
import com.nutiteq.geometry.Marker;
import com.nutiteq.geometry.VectorElement;
import com.nutiteq.layers.Layer;
import com.nutiteq.projections.EPSG3857;
import com.nutiteq.rasterdatasources.HTTPRasterDataSource;
import com.nutiteq.rasterdatasources.RasterDataSource;
import com.nutiteq.rasterlayers.RasterLayer;
import com.nutiteq.style.LineStyle;
import com.nutiteq.style.MarkerStyle;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.Label;
import com.nutiteq.ui.MapListener;
import com.nutiteq.utils.Const;
import com.nutiteq.utils.UnscaledBitmapLoader;
import com.nutiteq.vectorlayers.GeometryLayer;
import com.nutiteq.vectorlayers.MarkerLayer;
import com.squareup.picasso.Picasso;

import net.nature.mobile.R;
import net.nature.mobile.CreateAccountActivity.UserLoginTask;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.Session;
import net.nature.mobile.rest.Sync;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainActivity extends Activity implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	private static final int REQUEST_CREATE_ACCOUNT = 2;
	private static final int REQUEST_SIGNIN = 1;
	private static final int REQUEST_CREATE_NOTE = 3;
	private static final int REQUEST_EDIT_NOTE = 4;
	private static final int REQUEST_SELECT_CONTEXT = 5;

	static final String EXTRA_MESSAGE = "net.nature.mobile.MESSAGE";
	private static final String TAG = "MainActivity";


	private SyncTask mSyncTask;
	private Account mAccount;
	private Context mContext;

	private Button mButtonSignin;
	private TextView mUsername;
	private Button mButtonCreateAccount;
	private View mUserContainer;
	private View mSigninContainer;
	private View mButtonGallery;
	private ImageView mLastImage1st;
	private ImageView mLastImage2nd;
	private ImageView mLastImage3rd;
	private TextView mContextName;
	private Button mButtonCreateNote;
	private View mButtonSelectContext;
	private MapView mMapView;
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private LocationRequest mLocationRequest;
	private LocationRequest setInterval;
	// Handle to SharedPreferences for this app
	SharedPreferences mPrefs;

	// Handle to a SharedPreferences editor
	SharedPreferences.Editor mEditor;
	private boolean mUpdatesRequested;
	private TextView mConnectionStatus;
	private TextView mConnectionState;
	private MarkerLayer mMarkerLayer;
	private Marker mMarker;
	private View mButtonCurrentPosition;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSigninContainer = findViewById(R.id.main_signin_container);        
		mButtonSignin = (Button) findViewById(R.id.main_signin);
		mButtonCreateAccount = (Button) findViewById(R.id.main_create_account);
		mButtonCreateNote = (Button) findViewById(R.id.main_button_create_note);

		mUserContainer = findViewById(R.id.main_user_container);
		mUsername = (TextView) findViewById(R.id.main_username);
		mButtonGallery = (View) findViewById(R.id.main_image_right_arrow);
		mLastImage1st = (ImageView) findViewById(R.id.main_image_last_1st);
		mLastImage2nd = (ImageView) findViewById(R.id.main_image_last_2nd);
		mLastImage3rd = (ImageView) findViewById(R.id.main_image_last_3rd);

		mMapView = (MapView) findViewById(R.id.mapView);
		mButtonCurrentPosition = (View) findViewById(R.id.main_button_mylocation);

		mContextName = (TextView) findViewById(R.id.main_context);
		mButtonSelectContext = (View) findViewById(R.id.main_button_select_activity);

		mAccount = Session.getAccount();
		Log.d("main", ""+mAccount);
		if (mAccount == null){
			mUserContainer.setVisibility(View.INVISIBLE);
			mSigninContainer.setVisibility(View.VISIBLE);
		}else{        	
			onSignedIn(mAccount);
		}

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
				Intent intent = new Intent(getBaseContext(), CreateAccountActivity.class);
				startActivityForResult(intent, REQUEST_CREATE_ACCOUNT);
			}        	
		});

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





		//		new Marker(mMarker);

		// Create a new global location parameters object
		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		setInterval = mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		// Note that location updates are off until the user turns them on
		//mUpdatesRequested = false;
		mUpdatesRequested = true;

		// Open Shared Preferences
		mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, android.content.Context.MODE_PRIVATE);

		// Get an editor
		mEditor = mPrefs.edit();

		/*
		 * Create a new location client, using the enclosing class to
		 * handle callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);









		// define new configuration holder object
		mMapView.setComponents(new Components());

		// Define base layer. Here we use MapQuest open tiles which are free to use
		// Almost all online maps use EPSG3857 projection.
		// We use online data source for the tiles and the URL is given as template. 
		RasterDataSource dataSource = new HTTPRasterDataSource(new EPSG3857(), 0, 19, "http://otile1.mqcdn.com/tiles/1.0.0/osm/{zoom}/{x}/{y}.png");
		RasterLayer mapLayer = new RasterLayer(dataSource, 0);
		mMapView.getLayers().setBaseLayer(mapLayer);

		// do not allow to rotate map with two-finger touch. There is no rotation constraints by degrees, just on/off
		mMapView.getConstraints().setRotatable(false);

		// Set allowed zoom range and bounding box. 
		// Bounding box must be in layer projection units, so conversion is needed.
		mMapView.getConstraints().setZoomRange(new Range(12, 19));

		//		MapPos topLeft = mMapView.getLayers().getBaseProjection().fromWgs84(-82.5724 , 23.1999);
		//		MapPos bottomRight = mMapView.getLayers().getBaseProjection().fromWgs84(-82.1351, 22.8785);
		//		mMapView.getConstraints().setMapBounds(new Bounds(topLeft.x, topLeft.y,bottomRight.x, bottomRight.y));

		// Location: San Francisco 
		// NB! it must be in base layer projection (EPSG3857), so we convert it from lat and long
		//		mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(-122.41666666667f, 37.76666666666f));
		// rotation - 0 = north-up
		mMapView.setMapRotation(0f);
		// zoom - 0 = world, like on most web maps
		mMapView.setZoom(18.0f);

		//		mMapView.getConstraints().setZoomRange(new Range(10, 16));

		// tilt means perspective view. Default is 90 degrees for "normal" 2D map view, minimum allowed is 30 degrees.
		//mMapView.setTilt(35.0f);

		//		GeometryLayer locationLayer = new GeometryLayer(mMapView.getLayers().getBaseProjection());
		//		mMapView.getComponents().layers.addLayer(locationLayer);

		MapEventListener mapListener = new MapEventListener(this);
		mMapView.getOptions().setMapListener(mapListener);
	
		mMarkerLayer = new MarkerLayer(mapLayer.getProjection());
		mMapView.getLayers().addLayer(mMarkerLayer);


		mButtonCurrentPosition.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//				checkNotNull(mContext);
				if (mCurrentLocation != null){
					double latitude = mCurrentLocation.getLatitude();
					double longitude = mCurrentLocation.getLongitude();
					mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));
				}
			}        	
		});

	}

	// helper to draw a circle to given layer

	private void circle(float lat, float lon, float circleRadius, GeometryLayer layer){
		// number of circle line points
		int NR_OF_CIRCLE_VERTS = 18;
		List<MapPos> circleVerts = new ArrayList<MapPos>(NR_OF_CIRCLE_VERTS);

		MapPos circlePos = layer.getProjection().fromWgs84(lat, lon);
		// width of map to scale circle
		float projectionScale = (float) layer.getProjection().getBounds().getWidth();
		// convert radius from meters to map units
		float circleScale = circleRadius / 7500000f * projectionScale;

		for (float tsj = 0; tsj <= 360; tsj += 360 / NR_OF_CIRCLE_VERTS) {
			MapPos mapPos = new MapPos(circleScale * Math.cos(tsj * Const.DEG_TO_RAD) + circlePos.x, circleScale *    Math.sin(tsj * Const.DEG_TO_RAD) + circlePos.y);
			circleVerts.add(mapPos);
		}
		LineStyle style = LineStyle.builder().setWidth(0.1f).setColor(Color.argb(192, 255, 255, 0)).build();
		Line circle = new Line(circleVerts, null, style, null);
		layer.add(circle);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIGNIN || requestCode == REQUEST_CREATE_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				Long account_id = data.getLongExtra(SigninActivity.EXTRA_ACCOUNT_ID,-1);
				Account account = Model.load(Account.class,  account_id);				
				checkNotNull(account);
				onSignedIn(account);		
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




		// Choose what to do based on the request code
		switch (requestCode) {

		// If the request code matches the code sent in onConnectionFailed
		case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:

				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

				// Display the result
				//				mConnectionState.setText(R.string.connected);
				//				mConnectionStatus.setText(R.string.resolved);
				break;

				// If any other result was returned by Google Play services
			default:
				// Log the result
				Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

				// Display the result
				//				mConnectionState.setText(R.string.disconnected);
				//				mConnectionStatus.setText(R.string.no_resolution);

				break;
			}

			// If any other request code was received
		default:
			// Report that this Activity received an unknown requestCode
			Log.d(LocationUtils.APPTAG,
					getString(R.string.unknown_activity_request_code, requestCode));

			break;
		}
	}

	private void onContextSelected(Context context){
		checkNotNull(context);
		mContext = context;
		mContextName.setText(context.name);
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
		mUserContainer.setVisibility(View.VISIBLE);
		mSigninContainer.setVisibility(View.INVISIBLE);
		mUsername.setText(account.username);

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
		mSigninContainer.setVisibility(View.VISIBLE);
		mUserContainer.setVisibility(View.INVISIBLE);
		Session.signOut();
	}

	private void doSync() {
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
			Sync sync = new Sync();
			try {
				sync.syncAll();
			}catch (RetrofitError e){
				e.printStackTrace();
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mSyncTask = null;
			//			showProgress(false);
			//
			//			if (success) {
			//				finish();
			//			} else {
			//				mPasswordView
			//						.setError(getString(R.string.error_incorrect_password));
			//				mPasswordView.requestFocus();
			//			}
		}

		@Override
		protected void onCancelled() {
			mSyncTask = null;
			//			showProgress(false);
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
		//Start the map - mandatory
		mMapView.startMapping();


		/*
		 * Connect the client. Don't re-start any requests here;
		 * instead, wait for onResume()
		 */
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is going into the background.
	 * Parts of the UI may be visible, but the Activity is inactive.
	 */
	@Override
	public void onPause() {

		// Save the current setting for updates
		mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
		mEditor.commit();

		super.onPause();
	}


	/*
	 * Called when the system detects that this Activity is now visible.
	 */
	@Override
	public void onResume() {
		super.onResume();

		// If the app already has a setting for getting location updates, get it
		if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
			mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

			// Otherwise, turn off location updates until requested
		} else {
			mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
			mEditor.commit();
		}

	}


	@Override
	protected void onStop() {
		//Stop the map - mandatory to avoid problems with app restart
		mMapView.stopMapping();

		// If the client is connected
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();
		super.onStop();
	}



	// Global constants
	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int
	CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/**
	 * Verify that Google Play services is available before making a request.
	 *
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
			}
			return false;
		}
	}


	/**
	 * Invoked by the "Start Updates" button
	 * Sends a request to start location updates
	 *
	 * @param v The view object associated with this method, in this case a Button.
	 */
	public void startUpdates(View v) {
		mUpdatesRequested = true;

		if (servicesConnected()) {
			startPeriodicUpdates();
		}
	}

	/**
	 * Invoked by the "Stop Updates" button
	 * Sends a request to remove location updates
	 * request them.
	 *
	 * @param v The view object associated with this method, in this case a Button.
	 */
	public void stopUpdates(View v) {
		mUpdatesRequested = false;

		if (servicesConnected()) {
			stopPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services when the request to connect the
	 * client finishes successfully. At this point, you can
	 * request the current location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle bundle) {
		//        mConnectionStatus.setText(R.string.connected);

		mCurrentLocation = mLocationClient.getLastLocation();
		MapPos markerLocation;
		if (mCurrentLocation != null){
			double latitude = mCurrentLocation.getLatitude();
			double longitude = mCurrentLocation.getLongitude();
			mMapView.setFocusPoint(mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude));

			markerLocation = mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude);					
		}else{
			markerLocation = mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(37.766667f, -122.416667f);
		}


		//		// define marker style (image, size, color)
		Bitmap pointMarker = UnscaledBitmapLoader.decodeResource(getResources(), R.drawable.cur_position);
		MarkerStyle markerStyle = MarkerStyle.builder().setBitmap(pointMarker).setSize(1.0f).setColor(Color.WHITE).build();
		// define label what is shown when you click on marker
		Label markerLabel = new DefaultLabel("You are here");//, "Here is a marker");

		mMarker = new Marker(markerLocation, markerLabel, markerStyle, mMarkerLayer);
		mMarkerLayer.clear();		
		mMarkerLayer.add(new Marker(markerLocation, markerLabel, markerStyle, mMarkerLayer));
		if (mUpdatesRequested) {
			startPeriodicUpdates();
		}
	}

	/*
	 * Called by Location Services if the connection to the
	 * location client drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		//        mConnectionStatus.setText(R.string.disconnected);
	}

	/*
	 * Called by Location Services if the attempt to
	 * Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {

				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */

			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// If no resolution is available, display a dialog to the user with the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	/**
	 * Report location updates to the UI.
	 *
	 * @param location The updated location.
	 */
	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		MapPos markerLocation = mMapView.getLayers().getBaseLayer().getProjection().fromWgs84(longitude, latitude);
		mMarker.setMapPos(markerLocation);		
		mMarkerLayer.clear();
		mMarkerLayer.add(mMarker);
		mMapView.invalidate();
	}

	/**
	 * In response to a request to start updates, send a request
	 * to Location Services
	 */
	private void startPeriodicUpdates() {

		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		//        mConnectionState.setText(R.string.location_requested);
	}

	/**
	 * In response to a request to stop updates, send a request to
	 * Location Services
	 */
	private void stopPeriodicUpdates() {
		mLocationClient.removeLocationUpdates(this);
		//        mConnectionState.setText(R.string.location_updates_stopped);
	}

	/**
	 * An AsyncTask that calls getFromLocation() in the background.
	 * The class uses the following generic types:
	 * Location - A {@link android.location.Location} object containing the current location,
	 *            passed as the input parameter to doInBackground()
	 * Void     - indicates that progress units are not used by this subclass
	 * String   - An address passed to onPostExecute()
	 */
	protected class GetAddressTask extends AsyncTask<Location, Void, String> {

		// Store the context passed to the AsyncTask when the system instantiates it.
		android.content.Context localContext;

		// Constructor called by the system to instantiate the task
		public GetAddressTask(android.content.Context context) {

			// Required by the semantics of AsyncTask
			super();

			// Set a Context for the background task
			localContext = context;
		}

		/**
		 * Get a geocoding service instance, pass latitude and longitude to it, format the returned
		 * address, and return the address to the UI thread.
		 */
		@Override
		protected String doInBackground(Location... params) {
			/*
			 * Get a new geocoding service instance, set for localized addresses. This example uses
			 * android.location.Geocoder, but other geocoders that conform to address standards
			 * can also be used.
			 */
			Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

			// Get the current location from the input parameter list
			Location location = params[0];

			// Create a list to contain the result address
			List <Address> addresses = null;

			// Try to get an address for the current location. Catch IO or network problems.
			try {

				/*
				 * Call the synchronous getFromLocation() method with the latitude and
				 * longitude of the current location. Return at most 1 address.
				 */
				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1
						);

				// Catch network or other I/O problems.
			} catch (IOException exception1) {

				// Log an error and return an error message
				Log.e(LocationUtils.APPTAG, getString(R.string.IO_Exception_getFromLocation));

				// print the stack trace
				exception1.printStackTrace();

				// Return an error message
				return (getString(R.string.IO_Exception_getFromLocation));

				// Catch incorrect latitude or longitude values
			} catch (IllegalArgumentException exception2) {

				// Construct a message containing the invalid arguments
				String errorString = getString(
						R.string.illegal_argument_exception,
						location.getLatitude(),
						location.getLongitude()
						);
				// Log the error and print the stack trace
				Log.e(LocationUtils.APPTAG, errorString);
				exception2.printStackTrace();

				//
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {

				// Get the first address
				Address address = addresses.get(0);

				// Format the first line of address
				String addressText = getString(R.string.address_output_string,

						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ?
								address.getAddressLine(0) : "",

								// Locality is usually a city
								address.getLocality(),

								// The country of the address
								address.getCountryName()
						);

				// Return the text
				return addressText;

				// If there aren't any addresses, post a message
			} else {
				return getString(R.string.no_address_found);
			}
		}

		/**
		 * A method that's called once doInBackground() completes. Set the text of the
		 * UI element that displays the address. This method runs on the UI thread.
		 */
		@Override
		protected void onPostExecute(String address) {

			// Turn off the progress bar
			//            mActivityIndicator.setVisibility(View.GONE);

			// Set the address in the UI
			//            mAddress.setText(address);
		}
	}

	/**
	 * Show a dialog returned by Google Play services for the
	 * connection error code
	 *
	 * @param errorCode An error code returned from onConnectionFailed
	 */
	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
				errorCode,
				this,
				LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
		}
	}

	/**
	 * Define a DialogFragment to display the error dialog generated in
	 * showErrorDialog.
	 */
	public static class ErrorDialogFragment extends android.app.DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		/**
		 * Default constructor. Sets the dialog field to null
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Set the dialog to display
		 *
		 * @param dialog An error dialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * This method must return a Dialog to the DialogFragment.
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}






	class MapEventListener extends MapListener {

		private Activity activity;

		// activity is often useful to handle click events 
		public MapEventListener(Activity activity) {
			this.activity = activity;
		}

		// Vector element (touch) handlers
		@Override
		public void onLabelClicked(VectorElement vectorElement, boolean longClick) {
			Toast.makeText(activity, "onLabelClicked "+((DefaultLabel) vectorElement.getLabel()).getTitle()+" longClick: "+longClick, Toast.LENGTH_SHORT).show();
		}

		//		@Override
		//		public void onVectorElementClicked(VectorElement vectorElement, boolean longClick) {
		//			Toast.makeText(activity, "onVectorElementClicked "+((DefaultLabel) vectorElement.getLabel()).getTitle()+" longClick: "+longClick, Toast.LENGTH_SHORT).show();
		//		}

		//		// Map View manipulation handlers
		//		@Override
		//		public void onMapClicked(final float x, final float y, final boolean longClick) {
		//			Toast.makeText(activity, "onMapClicked "+(new EPSG3857()).toWgs84(x, y).x+" "+(new EPSG3857()).toWgs84(x, y).y+" longClick: "+longClick, Toast.LENGTH_SHORT).show();
		//		}

		@Override
		public void onMapMoved() {
			//			stopPeriodicUpdates();
		}

		// Progress indication handlers
		@Override
		public void onBackgroundTasksStarted() {
			// This method is called when mapping library is performing relatively long lasting operations.
			// This is good place to show some progress indicator.
			// NOTE: in order to make title progress bar work, place
			// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
			// just before setContentView call in your Activity's onCreate method.
			activity.setProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onBackgroundTasksFinished() {
			// This method is called when mapping library has finished all long lasting operations.
			activity.setProgressBarIndeterminateVisibility(false);
		}

		@Override
		public void onMapClicked(double arg0, double arg1, boolean arg2) {
			// TODO Auto-generated method stub
//			stopPeriodicUpdates();
		}

		@Override
		public void onVectorElementClicked(VectorElement arg0, double arg1,
				double arg2, boolean arg3) {
			// TODO Auto-generated method stub

		}
	}

}
