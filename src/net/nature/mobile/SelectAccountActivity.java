package net.nature.mobile;

import java.util.List;

import net.nature.mobile.SelectAccountActivity.UserArrayAdapter.ViewHolder;
import net.nature.mobile.model.Account;
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

public class SelectAccountActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ListView listview = new ListView(this);
		List<Account> users = new Select().from(Account.class).execute();
		Account[] values = users.toArray(new Account[]{});
		UserArrayAdapter adapter = new UserArrayAdapter(this, values);
		listview.setLayoutParams(new LayoutParams(200,200));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setAdapter(adapter);
		setContentView(listview);
		
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				ViewHolder holder = (UserArrayAdapter.ViewHolder) v.getTag();
				Account user = holder.user;

				Intent result = new Intent();
				result.putExtra("username", user.getUsername());
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		});
	}

	static public class UserArrayAdapter extends ArrayAdapter<Account> {
		private final Context context;
		private final Account[] users;

		static public class ViewHolder {
			public TextView text;
			public ImageView image;
			public Account user;
		}

		public UserArrayAdapter(Context context, Account[] users) {
			super(context, R.layout.item_user, users);
			this.context = context;
			this.users = users;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				rowView = inflater.inflate(R.layout.item_user, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.text = (TextView) rowView.findViewById(R.id.textViewUsername);
				viewHolder.image = (ImageView) rowView
						.findViewById(R.id.imageViewAvatar);
				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();
			
			holder.user = users[position];
			
			String s = users[position].getUsername();
			holder.text.setText(s);

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
