package net.nature.mobile;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.List;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Feedback;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.Site;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
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

	public static final String EXTRA_SITE_ID = "site.id";

	private Button mSave;
	private Button mCancel;
	private ImageView mImage;
	private EditText mContent;
	private Spinner mContext;
	private Note mNote;
	private View mButtonBar;
	private MapFragment mMap;

	private Site mSite;

	private Spinner mLandmark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
				
		Long site_id = getIntent().getExtras().getLong(EXTRA_SITE_ID);
		mSite = Model.load(Site.class, site_id);
		checkNotNull(mSite);
		
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
		
		
		
		// Title 
		Date now = new Date();
		String str = DateUtils.getRelativeDateTimeString(

		        this, // Suppose you are in an activity or other Context subclass

		        mNote.getTimeCreated(), // The time to display

		        DateUtils.MINUTE_IN_MILLIS, // The resolution. This will display only 
		                                        // minutes (no "3 seconds ago") 


		        DateUtils.WEEK_IN_MILLIS, // The maximum resolution at which the time will switch 
		                         // to default date instead of spans. This will not 
		                         // display "3 weeks ago" but a full date instead

		        0).toString();
		ActionBar ab = getActionBar();
	    ab.setTitle("Observation " + str); 

		
 		
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
			mMap.setCurrentLocationCameraMarker(mNote.getLatitude(), mNote.getLongitude(), true);
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
		
		//
		// Note context
		//
		
		mContext = (Spinner) findViewById(R.id.note_context);			
		
		SiteActivitiesAdapter adapter = new SiteActivitiesAdapter(this, mSite);
		mContext.setAdapter(adapter);
		int position = adapter.getPositionByName(mNote.getContext().getName());
		Log.d(TAG,"position: " + position);		
		mContext.setSelection(position);
		mContext.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Context context = (Context) parentView.getItemAtPosition(position);		    	
		    	checkNotNull(context);
		    	mNote.setContext(context);
				mNote.commit();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }

		});
		
		//
		// Note landmark
		//		
		mLandmark = (Spinner) findViewById(R.id.note_landmark);
		SiteLandmarkAdapter landmarkAdapter = new SiteLandmarkAdapter(this, mSite);
		mLandmark.setAdapter(landmarkAdapter);
		
		
		Feedback landmarkFeedback = mNote.getLandmarkFeedback();
		if (landmarkFeedback == null){
			position = 0;
		}else{
			position = landmarkAdapter.getPositionByName(landmarkFeedback.getContent());
		}
		mLandmark.setSelection(position);
		Log.d(TAG,"position: " + position);	
		mLandmark.setOnItemSelectedListener(new OnItemSelectedListener() {		
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Context context = (Context) parentView.getItemAtPosition(position);		    	
		    	checkNotNull(context);
		    	mNote.setLandmarkFeedback(context);
				mNote.commit();
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }

		});
	}
	
    static class SiteLandmarkAdapter extends ArrayAdapter<Context> {
    	
    	private List<Context> activities;

		public int getPositionByName(String name){    	
    		final List<String> context_names = Lists.newArrayList();
    		for (Context c : activities){
    			context_names.add(c.getName());
    		}
    		return context_names.indexOf(name);
    	}

        public SiteLandmarkAdapter(android.content.Context context, Site site){
            super(context, android.R.layout.simple_list_item_2);            
            activities = site.getLandmarks();
            addAll(activities);
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
                                           R.layout.item_landmark_short,
                                           null);
            TextView tvText1 = (TextView)convertView.findViewById(R.id.context_name);
            tvText1.setText(getItem(position).getTitle());
            TextView tvText2 = (TextView)convertView.findViewById(R.id.site_name);
            tvText2.setText(getItem(position).getSite().getName());
            return convertView;
        }
    }	
	

    static class SiteActivitiesAdapter extends ArrayAdapter<Context> {
    	
    	private List<Context> activities;

		public int getPositionByName(String name){    	
    		final List<String> context_names = Lists.newArrayList();
    		for (Context c : activities){
    			context_names.add(c.getName());
    		}
    		return context_names.indexOf(name);
    	}

        public SiteActivitiesAdapter(android.content.Context context, Site site){
            super(context, android.R.layout.simple_list_item_2);            
            activities = site.getActivities();
            addAll(activities);
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
            tvText1.setText(getItem(position).getTitle());
            TextView tvText2 = (TextView)convertView.findViewById(R.id.site_name);
            tvText2.setText(getItem(position).getSite().getName());
            TextView tvText3 = (TextView)convertView.findViewById(R.id.context_description);
            tvText3.setText(getItem(position).getDescription());            
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
