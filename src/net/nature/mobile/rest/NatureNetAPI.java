package net.nature.mobile.rest;

import java.util.List;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import retrofit.http.GET;
import retrofit.http.Path;

public interface NatureNetAPI {

	public class Result<T>{	
		public T data;
		public int status_code;
		public String status_txt;
	}

	@GET("/accounts")
	Result<List<Account>> listAccounts();
	
	@GET("/accounts/count")
	Result<Integer> countAccounts();
	
	@GET("/account/{username}")
	Result<Account> getAccount(@Path("username") String username);
	
	@GET("/contexts")
	Result<List<Context>> listContexts();

	@GET("/account/{username}/notes")
	Result<List<Note>> listNotes(@Path("username") String username);

	@GET("/notes")
	Result<List<Note>> listNotes();
	
	@GET("/note/{id}")
	Result<Note> getNote(@Path("id") long id);

	@GET("/medias")
	Result<List<Media>> listMedias();

}

