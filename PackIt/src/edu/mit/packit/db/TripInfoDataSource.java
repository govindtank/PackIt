package edu.mit.packit.db;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.packit.PackItActivity;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class TripInfoDataSource {

	private SQLiteDatabase database;
	private TripSQLiteHelper dbHelper;
	private static final String TAG = "TripInfoDataSource";
	
	private String[] tripinfo_cols = { TripSQLiteHelper.COLUMN_ID,
			TripSQLiteHelper.TRIP_NAME,
			TripSQLiteHelper.LOCATION,
			TripSQLiteHelper.TRANSPORTATION,
			TripSQLiteHelper.GENDER,
			TripSQLiteHelper.FROM_DATE,
			TripSQLiteHelper.TO_DATE };
	
	public static void set(Context context) {
		if (PackItActivity.datasource == null) {
			PackItActivity.datasource = new TripInfoDataSource(context.getApplicationContext());
		}
		
	}
	private TripInfoDataSource(Context context) {
		dbHelper = new TripSQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		//database.setTransactionSuccessful();
		//database.endTransaction();
		dbHelper.close();
	}
	
	public void createTrip(HashMap<String, String> details) {
		ContentValues values = new ContentValues();
		for (String info : details.keySet()) {
			values.put(info, details.get(info));
		}
		long insertId = database.insert(TripSQLiteHelper.TABLE_TRIPINFO, null, values);
		//Cursor cursor = database.query(TripSQLiteHelper.TABLE_TRIPINFO, tripinfo_cols, TripSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		Cursor cursor = database.query(TripSQLiteHelper.TABLE_TRIPINFO, tripinfo_cols, TripSQLiteHelper.TRIP_NAME + " = \'" + details.get(TripSQLiteHelper.TRIP_NAME)+"\'", null, null, null, null);
		Log.i(TAG, ""+cursor.moveToFirst());
		// Log.i("TripInfoDataSource", cursor.getString(0));
		cursor.close();
	}
	
	public void deleteTrip(TripDetails trip) {
		long id = trip.getId();
		database.delete(TripSQLiteHelper.TABLE_TRIPINFO, TripSQLiteHelper.COLUMN_ID + " = " + id, null);
	}
	
	public void deleteAllTrips() {
		dbHelper.deleteAllTrips(database);
	}
	
	public List<String> getAllTripNames() {
		List<String> trip_names = new ArrayList<String>();

		Cursor cursor = database.query(TripSQLiteHelper.TABLE_TRIPINFO,
				tripinfo_cols, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TripDetails detail = cursorToComment(cursor);
			trip_names.add(detail.getTripName());
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return trip_names;
	}
	
	public List<TripDetails> getAllTrips() {
		List<TripDetails> details = new ArrayList<TripDetails>();

		Cursor cursor = database.query(TripSQLiteHelper.TABLE_TRIPINFO,
				tripinfo_cols, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TripDetails detail = cursorToComment(cursor);
			details.add(detail);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return details;
	}
	public TripDetails getTrip(String trip_name) {
		Cursor cursor = database.query(TripSQLiteHelper.TABLE_TRIPINFO, tripinfo_cols, TripSQLiteHelper.TRIP_NAME + " = \'" + trip_name +"\'", null, null, null, null);
		Log.i(TAG, ""+cursor.moveToFirst());
		if (cursor.moveToFirst()) {
			TripDetails details = new TripDetails();
			details.setId(cursor.getLong(0));
			details.setTripName(cursor.getString(1));
			details.setLocation(cursor.getString(2));
			details.setTransportation(cursor.getString(3));
			details.setFromDate(cursor.getString(4));
			details.setToDate(cursor.getString(5));
			
			return details;
		}
		
		return null;
	}
	
	private TripDetails cursorToComment(Cursor cursor) {
		TripDetails details = new TripDetails();
		details.setId(cursor.getLong(0));
		details.setTripName(cursor.getString(1));
		details.setLocation(cursor.getString(2));
		details.setTransportation(cursor.getString(3));
		details.setFromDate(cursor.getString(4));
		details.setToDate(cursor.getString(5));
		return details;
	}
}
