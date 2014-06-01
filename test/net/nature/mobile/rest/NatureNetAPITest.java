package net.nature.mobile.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.nature.mobile.MainActivity;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import net.nature.mobile.rest.NatureNetAPI;
import net.nature.mobile.rest.NoteJson;
import net.nature.mobile.rest.NatureNetAPI.Result;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import android.app.Activity;
import android.test.AndroidTestCase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
//@Config(manifest=Config.NONE)
public class NatureNetAPITest {

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

	@Before
	public void setUp() {
//		super.setUp();

		 Gson gson = new GsonBuilder()
		 .excludeFieldsWithoutExposeAnnotation()
//	     .registerTypeAdapter(Id.class, new IdTypeAdapter())
//	     .enableComplexMapKeySerialization()
//	     .serializeNulls()
//	     .setDateFormat(DateFormat.LONG)
//	     .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
	     .setPrettyPrinting()
//	     .setVersion(1.0)
	     .create();

		RestAdapter restAdapter = new RestAdapter.Builder()
		.setConverter(new GsonConverter(gson))
		.setEndpoint("http://naturenet.herokuapp.com/api")
		.setErrorHandler(new MyErrorHandler())
		.build();

		api = restAdapter.create(NatureNetAPI.class);
		
		
		 Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
	}

	@Test	
	public void testGetAccounts(){		
		Result<List<Account>> res = api.listAccounts();
		assertThat(res.data.size(), greaterThan(3));
		for (Account user : res.data){
			Log.d("user", ""+user);
			System.out.println(user);
		}
	}

	@Test
	public void testGetAccountTomYeh(){
		Result<Account> result = api.getAccount("tomyeh");
		System.out.println(result.data);
		assertThat(result.data.getUsername(), equalTo("tomyeh"));	
	}

	@Test
	public void testListNotesForTomYeh(){
		Result<List<Note>> res = api.listNotes("tomyeh");
		for (Note x : res.data){			
			System.out.println(x);
			assertThat(x.getAccount(), notNullValue());
			assertThat(x.getAccount().getUsername(), equalTo("tomyeh"));
		}
	}

	@Test
	public void testAddAccount(){
		String unique_username = "u" + (new Date()).getTime();		
		Result<Account> res = api.createAccount(unique_username, "First Last","password","someone@email.com","I consent");
		System.out.println(res.data);		
		assertThat(res.status_code, equalTo(200));
		assertThat(res.data, notNullValue());
		assertThat(res.data.getUsername(), equalTo(unique_username));	
	}
	
	@Test
	public void testGetContext(){
		Context c = api.getContext(1L).data;
		assertThat(c, notNullValue());
		assertThat(c.getUId(), equalTo(1L));
	}

	@Test
	public void testAddNote(){
		Account a = api.getAccount("jenny").data;
		Context c = api.getContext(1L).data;
		
		Result<Note> r = api.createNote(a.getUsername(), "FieldNote", "some content about this note", c.getName(), 0.0, 0.0);
		System.out.println(r.data);		
		assertThat(r.status_code, equalTo(200));
		assertThat(r.data.getContent(), equalTo("some content about this note"));			
	}
	
	@Test
	public void testAddMedia(){
		Account a = api.getAccount("jenny").data;
		Context c = api.getContext(1L).data;		
		Note n = api.listNotes(a.getUsername()).data.get(0);
		
		Media m = api.createMedia(n.getUId(), "some title", new TypedFile("image/png", new File("test.png"))).data;

		System.out.println(m);
	}
}
