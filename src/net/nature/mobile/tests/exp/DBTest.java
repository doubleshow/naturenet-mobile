package net.nature.mobile.tests.exp;

import android.test.AndroidTestCase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBTest extends AndroidTestCase {

	private DbStore dbStore;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dbStore = new DbStore(getContext());
	}

	public void testStoreOneName() throws Exception {
		dbStore.insert("MyName");
		assertEquals(1, dbStore.allNames().length);
	}

	public void testStoreTwoName() throws Exception {
		dbStore.insert("MyName");
		dbStore.insert("YourName");
		assertEquals(2, dbStore.allNames().length);
	}

}



class DbStore {

	private SQLiteDatabase db;

	public DbStore(Context context) {
		MyOpenHelper openHelper = new MyOpenHelper(context);
		this.db = openHelper.getWritableDatabase();
	}

	public long insert(String name) {
		ContentValues values = new ContentValues();
		values.put(MyOpenHelper.COLUMN_NAME, name);
		return db.insert(MyOpenHelper.TABLE_NAME, null, values);
	}

	public String[] allNames(){
		List<String> results = new ArrayList<String>();
		Cursor query = db.query(MyOpenHelper.TABLE_NAME, null, null, null, null, null, null);
		while (query.moveToNext()) {
			results.add(query.getString(query.getColumnIndexOrThrow(MyOpenHelper.COLUMN_NAME)));
		}
		query.close();
		return results.toArray(new String[0]);
	}

	private class MyOpenHelper extends SQLiteOpenHelper {

		private static final String TABLE_NAME = "NAMES";
		private static final String COLUMN_NAME = "NAME";
		private static final String DATABASE_CREATE = "create table "
				+ TABLE_NAME + " ("
				+ " ID integer primary key autoincrement, "
				+ COLUMN_NAME + " text not null"
				+ ");";

		public MyOpenHelper(Context context) {
			super(context, null, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase) {
			sqLiteDatabase.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
			sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		}

	}

}