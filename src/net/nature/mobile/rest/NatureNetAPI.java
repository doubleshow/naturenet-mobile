package net.nature.mobile.rest;

import java.io.Serializable;
import java.util.List;

import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import net.nature.mobile.model.Context;
import net.nature.mobile.model.Account;
import net.nature.mobile.model.Media;
import net.nature.mobile.model.Note;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface NatureNetAPI {

	public class Result<T>{	
		@Expose
		public T data;
		@Expose
		public int status_code;
		@Expose
		public String status_txt;

	}
	
//	static abstract class Model {
//		private Long mId = null;
//
//		private final TableInfo mTableInfo;
//		private final String idName;
//		
//		public Model() {
//			mTableInfo = null;
//			idName = null;
//		}
//	}
//
//	@Table(name="ACCOUNT", id="tID")
//	static public class Account1 extends Model {
//
//		public Account1(){			
//		}
//
//		@Expose
//		@Column(name="Name")
//		public String name;
//
//		@Expose
//		@Column(name="Username")
//		public String username;
//
//		@Expose
//		@Column(name="Email")
//		public String email;
//
//		public String toString(){
//			return Objects.toStringHelper(this).
//					//						add("id", getId()).
//					//						add("uid", uID).
//					add("username", username).
//					add("name", name).
//					add("email", email).
//					toString();
//		}
//	}

	@GET("/accounts")
	Result<List<Account>> listAccounts();

	@GET("/accounts/count")
	Result<Integer> countAccounts();

	@GET("/account/{username}")
	Result<Account> getAccount(@Path("username") String username);

	@FormUrlEncoded
	@POST("/account/new/{username}")
	Result<Account> createAccount(@Path("username") String username, @Field("name") String name, @Field("password") String password, @Field("email") String email, @Field("consent") String consent); 	

	@GET("/account/{username}/notes")
	Result<List<Note>> listNotes(@Path("username") String username);

	@GET("/notes")
	Result<List<Note>> listNotes();

	@GET("/note/{id}")
	Result<Note> getNote(@Path("id") long id);


	@FormUrlEncoded
	@POST("/note/new/{username}")
	Result<Note> createNote(@Path("username") String username, @Field("kind") String kind, @Field("content") String content, @Field("context") String context);


	@GET("/medias")
	Result<List<Media>> listMedias();

	@GET("/contexts")
	Result<List<Context>> listContexts();

	@GET("/context/{id}")
	Result<Context> getContext(@Path("id") Long id);


}

