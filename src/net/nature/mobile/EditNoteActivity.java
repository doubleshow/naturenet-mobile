package net.nature.mobile;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

public class EditNoteActivity extends Activity {

	public static class Extras{
		public static final String NOTE_ID = "note.id";
	}

	private static final String TAG = "EditNoteActivity";

	private Button mSave;
	private Button mCancel;
	private ImageView mImage;
	private EditText mContent;
	private Spinner mContext;
	private Note mNote;
	private View mButtonBar;
	private MapFragment mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);

		//		if (savedInstanceState == null) {
		////			getFragmentManager().beginTransaction()
		////					.add(R.id.container, new PlaceholderFragment()).commit();
		//			
		//			
		//		}	
		mImage = (ImageView) findViewById(R.id.note_image);
		mContent = (EditText) findViewById(R.id.note_content);
		
		mMap = (MapFragment) getFragmentManager().findFragmentById(R.id.note_map);
		
		mButtonBar =findViewById(R.id.note_save_cancel_bar);
		mSave = (Button) findViewById(R.id.note_save);
		mCancel = (Button) findViewById(R.id.note_cancel);

		Bundle bundle = getIntent().getExtras();
		checkNotNull(bundle);

		Long id = bundle.getLong(Extras.NOTE_ID);
		checkNotNull(id);

		mNote = Model.load(Note.class, id);
		checkNotNull(mNote);
 		
		//
		// Note Image
		//
		Media media = mNote.getMediaSingle();
		if (media != null){						
			Picasso.with(this).load(media.getPath()).resize(600, 400).centerCrop().into(mImage);							
		}

		//
		// Note Content
		//
		mContent.setText(mNote.getContent());
		
		OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mButtonBar.setVisibility(View.VISIBLE);					
				}else {
					mButtonBar.setVisibility(View.INVISIBLE);
				}
			}
		};
		mContent.setOnFocusChangeListener(focusChangeListener);
		mContent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mButtonBar.setVisibility(View.VISIBLE);
			}			
		});		
		
		// Note Map 	
		if (mNote.isGeoTagged()){
			mMap.setCurrentLocationCameraMarker(mNote.latitude, mNote.longitude, true);
		}else{
			mMap.getView().setVisibility(View.INVISIBLE);
		}
		mMap.setHomeButtonEnabled(false);		
		
		//
		// Note Save/Cancel Buttons
		// 
		mButtonBar.setVisibility(View.INVISIBLE);
		
		mCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				checkNotNull(mNote);
				mContent.setText(mNote.getContent());		
				mContent.invalidate();
				editFinished();
			}
		});
		
		mSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				checkNotNull(mNote);
				mNote.setContent(mContent.getText().toString());
				mNote.commit();
				editFinished();
			}
		});
		
		mContext = (Spinner) findViewById(R.id.note_context);
				
		List<Context> contexts = new Select().from(Context.class).execute();
		final List<String> context_names = Lists.newArrayList();
		for (Context c : contexts){
			context_names.add(c.getName());
		}
		
		ContextAdapter adapter = new ContextAdapter(this, contexts);
		mContext.setAdapter(adapter);		
		int position = context_names.indexOf(mNote.getContext().getName());
		Log.d(TAG, "position: " + position);
		mContext.setSelection(position);
		mContext.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Context context = (Context) parentView.getItemAtPosition(position);		    	
		    	checkNotNull(context);
		    	mNote.context_id = context.getId();
				mNote.commit();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }

		});
	}
	

    class ContextAdapter extends ArrayAdapter<Context> {

        public ContextAdapter(android.content.Context context, List<Context> objects) {
            super(context, android.R.layout.simple_list_item_2, objects);
        }

        @Override //don't override if you don't want the default spinner to be a two line view
        public View getView(int position, View convertView, ViewGroup parent) {
            return initView(position, convertView);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return initView(position, convertView);
        }

        private View initView(int position, View convertView) {
            if(convertView == null)
                convertView = View.inflate(getContext(),
                                           R.layout.item_context_short,
                                           null);
            TextView tvText1 = (TextView)convertView.findViewById(R.id.context_name);
            tvText1.setText(getItem(position).title);
            TextView tvText2 = (TextView)convertView.findViewById(R.id.site_name);
            tvText2.setText(getItem(position).getSite().name);
            return convertView;
        }
    }
	
	private void editFinished(){
		
		// close the soft keyboard
		InputMethodManager inputManager = (InputMethodManager)
                getSystemService(android.content.Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                   InputMethodManager.HIDE_NOT_ALWAYS);
		
		// hide save/cancel buttons
		mButtonBar.setVisibility(View.INVISIBLE);

		// give the focus to parent, forcing all edit components to lose focus
		findViewById(R.id.note_parent_layout).requestFocus();
	}


//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.edit_note, menu);
//		return true;
//	}

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
