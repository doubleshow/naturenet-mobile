package net.nature.mobile;

import net.nature.mobile.SelectContextActivity.SiteFragment;
import net.nature.mobile.SelectContextActivity.SitePagerAdapter;
import net.nature.mobile.model.Site;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SelectAccountActivity extends FragmentActivity {

	protected static final int REQUEST_SIGNIN = 0;
	protected static final int REQUEST_CREATE_ACCOUNT = 1;
	protected static final String TAG = "SelectAccountActivity";
	public static final String EXTRA_SITE_NAME = "site.name";
	
	private Button mButtonSignin;
	private Button mButtonCreateAccount;
	private String siteName;
		
	private SitePagerAdapter adapterViewPager;
	private ViewPager vpPager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_account);
		
		mButtonSignin = (Button) findViewById(R.id.main_signin);
		mButtonCreateAccount = (Button) findViewById(R.id.main_create_account);	
		
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
				Intent intent = new Intent(getBaseContext(), ConsentActivity.class);
				startActivityForResult(intent, REQUEST_CREATE_ACCOUNT);
			}        	
		});	
		
		
		vpPager = (ViewPager) findViewById(R.id.vpPager);
		adapterViewPager = new SitePagerAdapter(getSupportFragmentManager());
		vpPager.setAdapter(adapterViewPager);
		vpPager.setOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageSelected(int i) {
				siteName = ((SiteFragment) adapterViewPager.getItem(i)).getSiteName();	
				Log.d(TAG, "onPageSelected: " + siteName);
			}			
		});
		vpPager.setCurrentItem(0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SIGNIN || requestCode == REQUEST_CREATE_ACCOUNT) {
			if (resultCode == RESULT_OK) {									
				data.putExtra(EXTRA_SITE_NAME, siteName);
				setResult(RESULT_OK, data);
				finish();
				Toast.makeText(getApplicationContext(), siteName, Toast.LENGTH_LONG).show();
			}
		}
	}

}
