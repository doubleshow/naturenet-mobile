package net.nature.mobile;

import java.util.List;

import net.nature.mobile.MainActivity.SyncTask;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.Site;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

public class SelectContextActivity extends FragmentActivity {

    private static final String TAG = "SelectContextActivity";
	
	static final String EXTRA_INPUT_CONTEXT_ID = "context.id";
	static final String EXTRA_OUTPUT_CONTEXT_ID = "context.id";
	private MyPagerAdapter adapterViewPager;
	private MyTask download;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_context);
		download = new MyTask();
		download.execute((Void) null);
	}

	private class MyTask extends AsyncTask<Void, Integer, List<String>> {


		@Override
	    protected List<String> doInBackground(Void... params) {	    	
	    	NNModel.resolveByName(Site.class,  "aces");
	    	NNModel.resolveByName(Site.class,  "cu");
	    	NNModel.resolveByName(Site.class,  "umd");
	    	NNModel.resolveByName(Site.class,  "uncc");
	        return null;
	    }

	    @Override
	    protected void onPostExecute(List<String> result) {
	    	setContentView(R.layout.activity_select_context);
			ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
			adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
			vpPager.setAdapter(adapterViewPager);
			vpPager.invalidate();
		}
	    
	}

	public static class FirstFragment extends Fragment {
		// Store instance variables
		private String title;
		private int page;
		private Site site;

		// newInstance constructor for creating fragment with arguments
		public static FirstFragment newInstance(int page, String title) {
			FirstFragment fragmentFirst = new FirstFragment();
			Bundle args = new Bundle();
			args.putInt("someInt", page);
			args.putString("someTitle", title);
			fragmentFirst.setArguments(args);
			return fragmentFirst;
		}

		// Store instance variables based on arguments passed
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			page = getArguments().getInt("someInt", 0);
			title = getArguments().getString("someTitle");
			
			
			site = NNModel.findByName(Site.class, title.toLowerCase());
			
			//	        site = getArguments().getString("someTitle");
		}

		// Inflate the view for the fragment based on layout XML
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_site, container, false);
			TextView tvLabel = (TextView) view.findViewById(R.id.textViewDescription);
			tvLabel.setText(site.description);

			ListView listview = (ListView) view.findViewById(R.id.listView);
			
			List<Context> items = site.getContexts();
			Context[] values = items.toArray(new Context[]{});
			ContextArrayAdapter adapter = new ContextArrayAdapter(getActivity(),values);	
			listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View v, int arg2,
						long arg3) {
					ContextArrayAdapter.ViewHolder holder = (ContextArrayAdapter.ViewHolder) v.getTag();
					Context context = holder.context;

					Intent result = new Intent();
					result.putExtra(EXTRA_OUTPUT_CONTEXT_ID, context.getId());
					getActivity().setResult(Activity.RESULT_OK, result);
					getActivity().finish();
				}
			});
			return view;
		}
	}
	
	

	public static class MyPagerAdapter extends FragmentPagerAdapter {
		private static int NUM_ITEMS = 4;

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		// Returns total number of pages
		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		// Returns the fragment to display for that page
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0: // Fragment # 0 - This will show FirstFragment
				return FirstFragment.newInstance(0, "ACES");
			case 1: // Fragment # 0 - This will show FirstFragment different title
				return FirstFragment.newInstance(1, "CU");
			case 2: // Fragment # 1 - This will show SecondFragment
				return FirstFragment.newInstance(2, "UMD");
			case 3: // Fragment # 1 - This will show SecondFragment
				return FirstFragment.newInstance(3, "UNCC");				
			default:
				return null;
			}
		}

		// Returns the page title for the top indicator
		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0: 
				return "ACES";
			case 1: 
				return "CU";
			case 2: 
				return "UMD";
			case 3: 
				return "UNCC";				
			default:
				return null;
			}

		}

	}


	static public class ContextArrayAdapter extends ArrayAdapter<Context> {
		private final Context[] contexts;

		public class ViewHolder {
			public TextView textName;
			public TextView textDescription;
			public ImageView image;
			public Context context;
		}

		public ContextArrayAdapter(android.content.Context context, Context[] contexts) {
			super(context, R.layout.item_context, contexts);
			this.contexts = contexts;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = LayoutInflater.from(getContext());
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
			holder.textName.setText(context.title);
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
