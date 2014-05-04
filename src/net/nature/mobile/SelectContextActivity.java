package net.nature.mobile;

import java.util.List;

import net.nature.mobile.model.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class SelectContextActivity extends Activity {

	static final String EXTRA_INPUT_CONTEXT_ID = "context.id";
	static final String EXTRA_OUTPUT_CONTEXT_ID = "context.id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Context> users = new Select().from(Context.class).execute();
		Context[] values = users.toArray(new Context[]{});
		ContextArrayAdapter adapter = new ContextArrayAdapter(values);	


		final ListView listview = new ListView(this);
		listview.setLayoutParams(new LayoutParams(200,200));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setAdapter(adapter);
		setContentView(listview);

		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				ContextArrayAdapter.ViewHolder holder = (ContextArrayAdapter.ViewHolder) v.getTag();
				Context context = holder.context;

				Intent result = new Intent();
				result.putExtra(EXTRA_OUTPUT_CONTEXT_ID, context.getUId());
				setResult(Activity.RESULT_OK, result);
				finish();
			}
		});
	}

	public class ContextArrayAdapter extends ArrayAdapter<Context> {
		private final Context[] contexts;

		public class ViewHolder {
			public TextView textName;
			public TextView textDescription;
			public ImageView image;
			public Context context;
		}

		public ContextArrayAdapter(Context[] contexts) {
			super(getBaseContext(), R.layout.item_context, contexts);
			this.contexts = contexts;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = LayoutInflater.from(getBaseContext());
				rowView = inflater.inflate(R.layout.item_context, null);
				ViewHolder viewHolder = new ViewHolder();
				viewHolder.textName = (TextView) rowView.findViewById(R.id.context_name);
				viewHolder.textDescription = (TextView) rowView.findViewById(R.id.context_description);
				viewHolder.image = (ImageView) rowView.findViewById(R.id.imageViewAvatar);
				rowView.setTag(viewHolder);
			}

			ViewHolder holder = (ViewHolder) rowView.getTag();

			holder.context = contexts[position];
	
			Context context = contexts[position];
			holder.textName.setText(context.name);
			holder.textDescription.setText(context.description);

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

}
