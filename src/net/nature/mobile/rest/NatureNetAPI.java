package net.nature.mobile.rest;

import java.util.List;

import net.nature.mobile.model.Activity;
import net.nature.mobile.model.User;
import retrofit.http.GET;
import retrofit.http.Path;

public interface NatureNetAPI {

	public class Result<T>{	
		public T data;
		public int status_code;
		public String status_txt;
	}

	@GET("/accounts")
	Result<List<User>> listAccounts();
	
	@GET("/accounts/count")
	Result<Integer> countAccounts();
	
	@GET("/account/{username}")
	Result<User> getAccount(@Path("username") String username);
	
	@GET("/context/activities")
	Result<List<Activity>> listActivities();
	
}

