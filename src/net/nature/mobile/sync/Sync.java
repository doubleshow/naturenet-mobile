package net.nature.mobile.sync;

import java.util.List;

import retrofit.RestAdapter;

import com.activeandroid.query.Select;

import net.nature.mobile.model.User;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NatureNetAPI.Result;

public class Sync {

	private NatureNetAPI api;

	public Sync(){
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://naturenet.herokuapp.com/api")
		.build();		
		api = restAdapter.create(NatureNetAPI.class);
	}

	public void pull(User remoteUser){
		User localUser = (new Select()).from(User.class).where("username = ?", remoteUser.username).executeSingle();
		if (localUser == null){
			remoteUser.save();
		}
	}

	public void pullAllUsers(){
		if (api != null){
			Result<List<User>> r = api.listAccounts();
			if (r.status_code == 200){
				for (User u : r.data){
					pull(u);
				}
			}
		}
	}
}
