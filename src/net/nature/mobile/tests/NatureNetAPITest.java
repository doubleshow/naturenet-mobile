package net.nature.mobile.tests;

import java.util.Date;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import net.nature.mobile.model.User;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;
import net.nature.mobile.sync.Sync;
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
	}
	
	public void testGetAccountTomYeh(){
		Result<User> res = api.getAccount("tomyeh");
		assertThat(res.data.username, equalTo("tomyeh"));	
	}
	
}
