package net.nature.mobile;

import java.util.List;

import net.nature.mobile.ListNoteActivity.UserArrayAdapter.ViewHolder;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.Site;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.common.collect.Lists;
import com.squareup.picasso.Picasso;

import static com.google.common.base.Preconditions.checkNotNull;

public class ListNoteActivity extends Activity {

	static public final String EXTRA_ACCOUNT_ID = "account.id";
	static public final String EXTRA_SITE_ID = "site.id";
	private Site mSite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

					
		Long account_id = getIntent().getExtras().getLong(EXTRA_ACCOUNT_ID);
		Account mAccount = Model.load(Account.class, account_id);
		checkNotNull(mAccount);
		
		Long site_id = getIntent().getExtras().getLong(EXTRA_SITE_ID);
		mSite = Model.load(Site.class, site_id);
		checkNotNull(mSite);
		
		List<Note> notes = mAccount.getNotesOrderedByRecencyAtSite(mSite);
		
		final ListView listview = new ListView(this);
		Note[] values = notes.toArray(new Note[]{});
		UserArrayAdapter adapter = new UserArrayAdapter(this, values);
		listview.setLayoutParams(new LayoutParams(200,200));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setAdapter(adapter);
		setContentView(listview);
		
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
				ViewHolder holder = (UserArrayAdapter.ViewHolder) v.getTag();
				Intent intent = new Intent(getBaseContext(), EditNoteActivity.class);
				intent.putExtra(EditNoteActivity.Extras.NOTE_ID, holder.note.getId());	
				intent.putExtra(EditNoteActivity.EXTRA_SITE_ID, mSite.getId());
				startActivity(intent);
			}
		});
	}

	static public class UserArrayAdapter extends ArrayAdapter<Note> {
		private final Context context;
		private final Note[] notes;

		static public class ViewHolder {
			public TextView textContent;
			public TextView textContext;
			public ImageView image;
			public Note note;
		}

		public UserArrayAdapter(Context context, Note[] users) {
			super(context, R.layout.item_user, users);
			this.context = context;
			this.notes = users;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				rowView = inflater.inflate(R.layout.item_note, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.textContent = (TextView) rowView.findViewById(R.id.note_content);
				viewHolder.textContext = (TextView) rowView.findViewById(R.id.note_context);
				viewHolder.image = (ImageView) rowView.findViewById(R.id.note_image);
				rowView.setTag(viewHolder);
				
			}
			
			ViewHolder holder = (ViewHolder) rowView.getTag();			
			holder.note = notes[position];
			
			Note note = notes[position];
			
			holder.textContent.setText(note.getContent());
			holder.textContext.setText(note.getContext().getTitle());

			
			Media media = notes[position].getMediaSingle();
			if (media != null){
				String path = media.getPath();
				Picasso.with(getContext()).load(path).resize(250,150).centerCrop().into(holder.image);				
			}

			return rowView;
		}
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
//			View rootView = inflater.inflate(R.layout.fragment_select_account,
//					container, false);
//			return rootView;
//		}
//	}

}
