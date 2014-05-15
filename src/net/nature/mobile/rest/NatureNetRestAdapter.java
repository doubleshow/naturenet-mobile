package net.nature.mobile.rest;

import java.io.IOException;
import java.util.Date;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedByteArray;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class NatureNetRestAdapter {
	public static final String TAG = "NNAPI";

	static class MyErrorHandler implements ErrorHandler {
		@Override public Throwable handleError(RetrofitError cause) {
			Log.d(TAG, "cause: " + cause);
//			Log.d(TAG, "cause: " + cause.getMessage());
			Response r = cause.getResponse();
			Log.d(TAG, "response:" + r);
//			TypedByteArray t = (TypedByteArray) r.getBody();
////			Log.d(TAG, ""+t);
//			try {
//				t.writeTo(System.out);
//			} catch (IOException e) {
//			}
//			cause.set
			return cause;
		}
	}
	
	static public NatureNetAPI get(){
		Gson gson = new GsonBuilder()
		.excludeFieldsWithoutExposeAnnotation()
		.registerTypeAdapter(Date.class, new DateTypeAdapter())		
		.create();

		RestAdapter restAdapter = new RestAdapter.Builder()
		.setConverter(new GsonConverter(gson))
		.setEndpoint("http://naturenet.herokuapp.com/api")
		.setErrorHandler(new MyErrorHandler())
		.build();		
		return restAdapter.create(NatureNetAPI.class);
	}
}
