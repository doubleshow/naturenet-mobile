package net.nature.mobile.tests;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Note;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NoteJson;
import net.nature.mobile.rest.Sync;
import net.nature.mobile.rest.NatureNetAPI.Result;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import android.test.AndroidTestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NatureNetAPITest extends AndroidTestCase {

	private NatureNetAPI api;
	
	
	class MyErrorHandler implements ErrorHandler {
		@Override public Throwable handleError(RetrofitError cause) {
			Response r = cause.getResponse();
			System.out.println(cause);
			System.out.println(r.getReason());
			System.out.println(r.getBody().mimeType());
			System.out.println(r.getBody());
//			r.get
			TypedByteArray t = (TypedByteArray) r.getBody();
			try {
				t.writeTo(System.out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("body", ""+r.getBody());
			if (r != null && r.getStatus() == 401) {
//				return new UnauthorizedException(cause);
			}
			return cause;
		}
	}

	@Override
	protected void setUp() throws Exception{
		super.setUp();



		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://naturenet.herokuapp.com/api")
		.setErrorHandler(new MyErrorHandler())
		.build();

		api = restAdapter.create(NatureNetAPI.class);
	}

	public void testGetAccounts(){		
		Result<List<Account>> res = api.listAccounts();
		assertThat(res.data.size(), greaterThan(3));
		for (Account user : res.data){
			Log.d("user", ""+user);
			System.out.println(user);
		}
	}

	public void testGetAccountTomYeh(){
		Result<Account> res = api.getAccount("tomyeh");
		assertThat(res.data.username, equalTo("tomyeh"));	
	}

	public void testListNotesForTomYeh(){
		Result<List<Note>> res = api.listNotes("tomyeh");
		for (Note x : res.data){			
			System.out.println(x);
			assertThat(x.getAccount().username, equalTo("tomyeh"));
		}	
	}


	public void testAddAccount(){
		String unique_username = "u" + (new Date()).getTime();		
		Result<Account> res = api.createAccount(unique_username, "First Last","password","someone@email.com","I consent");
		System.out.println(res.data);		
		assertThat(res.status_code, equalTo(200));
		assertThat(res.data, notNullValue());
		assertThat(res.data.username, equalTo(unique_username));	
	}

	public void testAddNote(){
//		Note n = api.getNote(10L).data;
//		System.out.println(n);	
		
		Account a = api.getAccount("jenny").data;
		Context c = api.getContext(1L).data;

		Result<Note> r = api.createNote(a.username, "FieldNote", "some content about this note", c.name);
		System.out.println(r.data);		
		assertThat(r.status_code, equalTo(200));
		assertThat(r.data.content, equalTo("some content about this note"));			
	}
}
