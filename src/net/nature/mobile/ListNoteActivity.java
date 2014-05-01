package net.nature.mobile;

import java.util.List;

import net.nature.mobile.ListNoteActivity.UserArrayAdapter.ViewHolder;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
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

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import static com.google.common.base.Preconditions.checkNotNull;

public class ListNoteActivity extends Activity {

	static public final String EXTRA_ACCOUNT_ID = "account.id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

					
		Long account_id = getIntent().getExtras().getLong(EXTRA_ACCOUNT_ID);
		Account mAccount = Account.find(account_id);
		checkNotNull(mAccount);
		
		List<Note> notes = mAccount.notes();
		
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
				intent.putExtra(EditNoteActivity.Extras.NOTE_ID, holder.note.id);				
				startActivity(intent);
			}
		});
	}

	static public class UserArrayAdapter extends ArrayAdapter<Note> {
		private final Context context;
		private final Note[] notes;

		static public class ViewHolder {
			public TextView text;
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
				viewHolder.text = (TextView) rowView.findViewById(R.id.textViewUsername);
				viewHolder.image = (ImageView) rowView
						.findViewById(R.id.imageViewAvatar);
				rowView.setTag(viewHolder);
				
			}
			
//			SquaredImageView view = (SquaredImageView) convertView;
//			  if (view == null) {
//			    view = new SquaredImageView(context);
//			  }
//			  String url = getItem(position);

			

			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.note = notes[position];
			
			String s = notes[position].content;
			holder.text.setText(s);

			
			Media media = notes[position].getMediaSingle();
			if (media != null){
				String path = media.getPath();
				Picasso.with(getContext()).load(path).resize(400,300).centerCrop().into(holder.image);				
			}

//			String avatarName = users[position].getAvatarName();
//			int id = context.getResources().getIdentifier(avatarName.toLowerCase(), "drawable", context.getPackageName());
//			holder.image.setImageResource(id);
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
