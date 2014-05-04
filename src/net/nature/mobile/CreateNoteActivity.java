package net.nature.mobile;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

public class CreateNoteActivity extends Activity {

	public static class Extras{
		public static final String INPUT_ACCOUNT_ID = "account.id";
		public static final String INPUT_CONTEXT_ID = "context.id";
		public static final String OUTPUT_NOTE_ID = "note.id";
	}

	private static final int REQUEST_IMAGE_CAPTURE = 0;

	private static final String TAG = "CreateNoteActivity";

	private Account mAccount;
	private Context mContext;

	private String mCurrentPhotoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);


		Bundle bundle = getIntent().getExtras();
		checkNotNull(bundle);

		Long account_id = bundle.getLong(Extras.INPUT_ACCOUNT_ID);
		Long context_id = bundle.getLong(Extras.INPUT_CONTEXT_ID);
		checkNotNull(account_id);
		checkNotNull(context_id);

		mAccount = Model.load(Account.class, account_id);
		mContext = Model.load(Context.class, context_id);

		checkNotNull(mAccount);
		checkNotNull(mContext);

		dispatchTakePictureIntent();

	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
				);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE) {


			if (resultCode == RESULT_OK){
				Note note = new Note();
				note.account_id = mAccount.getId();
				note.context_id = mContext.getId();
				note.save();
				Log.d(TAG, "save " + note);

				Media media = new Media();
				media.setNote(note);
				media.setLocal(mCurrentPhotoPath);
				media.save();
				Log.d(TAG, "save " + media);


				galleryAddPic();


				Intent result = new Intent();
				result.putExtra(Extras.OUTPUT_NOTE_ID, note.getId());
				setResult(RESULT_OK, result);
				finish();
			}
			else if (resultCode == RESULT_CANCELED){
				finish();
			}
		}
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_note, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}	
}
