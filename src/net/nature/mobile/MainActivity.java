package net.nature.mobile;


import java.util.List;

import com.squareup.picasso.Picasso;

import net.nature.mobile.R;
import net.nature.mobile.CreateAccountActivity.UserLoginTask;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.rest.Sync;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import static com.google.common.base.Preconditions.checkNotNull;

public class MainActivity extends Activity {

	private static final int CREATE_ACCOUNT = 2;
	private static final int REQUEST_SIGNIN = 1;
	private static final int REQUEST_CREATE_NOTE = 3;
	private static final int REQUEST_EDIT_NOTE = 4;
	private static final int REQUEST_SELECT_CONTEXT = 5;
	
	static final String EXTRA_MESSAGE = "net.nature.mobile.MESSAGE";
	
	
	private SyncTask mAuthTask;
	private Account mAccount;
	private Context mContext;

	private Button mButtonSignin;
	private TextView mUsername;
	private Button mButtonCreateAccount;
	private View mUserContainer;
	private View mSigninContainer;
	private Button mButtonGallery;
	private ImageView mLastImage1st;
	private ImageView mLastImage2nd;
	private TextView mContextName;
	private Button mButtonCreateNote;
	private Button mButtonSelectContext;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			mAccount = null;
			//          getFragmentManager().beginTransaction()
			//                  .add(R.id.container, new PlaceholderFragment())
			//                  .commit();
		}else{
			mAccount = new Account();
			mAccount.username = "Tom Yeh";
		}

		mAccount =  Account.find(2L);
		mContext = Context.find(1L);

		mSigninContainer = findViewById(R.id.main_signin_container);        

		mButtonSignin = (Button) findViewById(R.id.main_signin);
		mButtonCreateAccount = (Button) findViewById(R.id.main_create_account);
		mButtonCreateNote = (Button) findViewById(R.id.main_button_create_note);

		mUserContainer = findViewById(R.id.main_user_container);
		mUsername = (TextView) findViewById(R.id.main_username);
		mButtonGallery = (Button) findViewById(R.id.main_gallery);
		mLastImage1st = (ImageView) findViewById(R.id.main_image_last_1st);
		mLastImage2nd = (ImageView) findViewById(R.id.main_image_last_2nd);
		
		
		mContextName = (TextView) findViewById(R.id.main_context);
		mButtonSelectContext = (Button) findViewById(R.id.main_button_select_activity);

		if (mAccount == null){
			mUserContainer.setVisibility(View.INVISIBLE);
			mSigninContainer.setVisibility(View.VISIBLE);
		}else{        	
			onAccountSelected(mAccount);
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
				startActivityForResult(intent, CREATE_ACCOUNT);
			}        	
		});

		mButtonGallery.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), ListNoteActivity.class);
				intent.putExtra(ListNoteActivity.EXTRA_ACCOUNT_ID, mAccount.id);
				startActivity(intent);
			}        	
		});

		mButtonCreateNote.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				checkNotNull(mAccount); checkNotNull(mContext);

				Intent intent = new Intent(getBaseContext(), CreateNoteActivity.class);
				intent.putExtra(CreateNoteActivity.Extras.INPUT_ACCOUNT_ID, mAccount.id);
				intent.putExtra(CreateNoteActivity.Extras.INPUT_CONTEXT_ID, mContext.id);
				startActivityForResult(intent, REQUEST_CREATE_NOTE);
			}        	
		});
		
		mButtonSelectContext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				checkNotNull(mContext);
				
				Intent intent = new Intent(getBaseContext(), SelectContextActivity.class);				
				intent.putExtra(SelectContextActivity.EXTRA_INPUT_CONTEXT_ID, mContext.id);
				startActivityForResult(intent, REQUEST_SELECT_CONTEXT);
			}        	
		});

		if (mContext == null){

		}else{
			onContextSelected(mContext);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIGNIN) {
			if (resultCode == RESULT_OK) {
				Long account_id = data.getLongExtra(SigninActivity.EXTRA_ACCOUNT_ID,-1);
				Account account = Account.find(account_id);
				checkNotNull(account);	        	
				onAccountSelected(account);
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
				Context context = Context.find(context_id);
				checkNotNull(context);				
				onContextSelected(context);
			}
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

	private void onAccountSelected(Account account){
		checkNotNull(account);		
		mUserContainer.setVisibility(View.VISIBLE);
		mSigninContainer.setVisibility(View.INVISIBLE);
		mUsername.setText(account.username);
		
		loadRecentNotes(account);
	}

	private void loadRecentNotes(Account account){
		checkNotNull(account);
		
		// display the last two recent notes
		List<Note> notes = mAccount.getRecentNotes(2);
		if (notes.size() >= 1){			
			showNoteImageHelper(notes.get(0), mLastImage1st);
		}

		if (notes.size() >= 2){
			showNoteImageHelper(notes.get(1), mLastImage2nd);
		}		
	}

	private void showNoteImageHelper(Note note, ImageView view){
		checkNotNull(note);

		view.setOnClickListener(new OnClickToLaunchEditNoteActivity(note.id));

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
		}else if (id == R.id.action_gallery){
			Intent intent = new Intent(this, ListNoteActivity.class);
			startActivity(intent);
		}else if (id == R.id.action_camera){        
			Intent intent = new Intent(this, SelectAccountActivity.class);
			startActivity(intent);        
		}else if (id == R.id.action_sync){        	
			doSync();
		}
		return super.onOptionsItemSelected(item);
	}

	private void doSync() {
		mAuthTask = new SyncTask();
		mAuthTask.execute((Void) null);
	}


	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class SyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			//			try {				
			Sync sync = new Sync();
			sync.syncAll();

			//				Thread.sleep(1000);
			//			} catch (InterruptedException e) {
			//				return false;
			//			}

			//			for (String credential : DUMMY_CREDENTIALS) {
			//				String[] pieces = credential.split(":");
			//				if (pieces[0].equals(mEmail)) {
			//					// Account exists, return true if the password matches.
			//					return pieces[1].equals(mPassword);
			//				}
			//			}
			//
			//			// register the new account here.
			//			Account user = new Account();
			//			user.username = mUsername;
			//			user.name = mName;			
			//			user.save();			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			//			mAuthTask = null;
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
			//			mAuthTask = null;
			//			showProgress(false);
		}
	}
}
