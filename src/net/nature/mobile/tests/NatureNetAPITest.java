package net.nature.mobile.tests;

import java.util.Date;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

import net.nature.mobile.model.Activity;
import net.nature.mobile.model.User;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.Sync;
import net.nature.mobile.rest.NatureNetAPI.Result;
import retrofit.RestAdapter;
import android.test.AndroidTestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NatureNetAPITest extends AndroidTestCase {
	
	private NatureNetAPI api;

	@Override
	protected void setUp() throws Exception{
		super.setUp();
		RestAdapter restAdapter = new RestAdapter.Builder()
	    .setEndpoint("http://naturenet.herokuapp.com/api")
	    .build();
		
		api = restAdapter.create(NatureNetAPI.class);
	}

	public void testGetAccounts(){		
		Result<List<User>> res = api.listAccounts();
		assertThat(res.data.size(), greaterThan(3));
		for (User user : res.data){
			Log.d("user", ""+user);
			System.out.println(user);
		}
	}
	
	public void testGetAccountTomYeh(){
		Result<User> res = api.getAccount("tomyeh");
		assertThat(res.data.username, equalTo("tomyeh"));	
	}
	
	public void testGetActivities(){		
		Result<List<Activity>> res = api.listActivities();
		assertThat(res.data.size(), greaterThan(3));
		for (Activity activity : res.data){			
			System.out.println(activity);
		}
	}
}
