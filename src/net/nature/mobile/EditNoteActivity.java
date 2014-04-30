package net.nature.mobile;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Note;
import android.app.Activity;
import android.os.Bundle;
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

import com.activeandroid.query.Select;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

public class EditNoteActivity extends Activity {

	public static class Extras{
		public static final String NOTE_ID = "note.id";
	}

	private Button mSave;
	private Button mCancel;
	private ImageView mImage;
	private EditText mContent;
	private Spinner mContext;

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
		mSave = (Button) findViewById(R.id.note_save);
		mCancel = (Button) findViewById(R.id.note_cancel);

		Bundle bundle = getIntent().getExtras();
		checkNotNull(bundle);

		Long id = bundle.getLong(Extras.NOTE_ID);
		checkNotNull(id);

		final Note note = Note.find(id);
		checkNotNull(note);

		mContent.setText(note.content);
		Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(mImage);

		
		OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					mSave.setVisibility(View.VISIBLE);
					mCancel.setVisibility(View.VISIBLE);					
				}else {
					mSave.setVisibility(View.INVISIBLE);
					mCancel.setVisibility(View.INVISIBLE);					
				}
			}
		};
		mContent.setOnFocusChangeListener(focusChangeListener);
		mContent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mSave.setVisibility(View.VISIBLE);
				mCancel.setVisibility(View.VISIBLE);						
			}			
		});
		mContent.requestFocus();
		
		mCancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mSave.setVisibility(View.INVISIBLE);
				mCancel.setVisibility(View.INVISIBLE);						
			}
		});
		
		mSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mSave.setVisibility(View.INVISIBLE);
				mCancel.setVisibility(View.INVISIBLE);
				
				note.content = mContent.getText().toString();
				note.save();				
			}
		});
		
		mContext = (Spinner) findViewById(R.id.note_context);
		
		List<Context> contexts = new Select().from(Context.class).execute();
		final List<String> context_names = Lists.newArrayList();
		for (Context c : contexts){
			context_names.add(c.name);
		}
		
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, context_names.toArray());		        
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mContext.setAdapter(adapter);
		
		int position = context_names.indexOf(note.getContext().name);		
		mContext.setSelection(position);
		
		
		mContext.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Context context = new Select().from(Context.class).where("name = ?", context_names.get(position)).executeSingle();
		    	checkNotNull(context);
		    	note.context_id = context.id;
				note.save();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
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

	//	/**
	//	 * A placeholder fragment containing a simple view.
	//	 */
	//	public static class PlaceholderFragment extends Fragment {
	//
	//		public PlaceholderFragment() {
	//		}
	//
	//		@Override
	//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
	//				Bundle savedInstanceState) {
	//			View rootView = inflater.inflate(R.layout.fragment_edit_note,
	//					container, false);
	//			return rootView;
	//		}
	//	}
}
