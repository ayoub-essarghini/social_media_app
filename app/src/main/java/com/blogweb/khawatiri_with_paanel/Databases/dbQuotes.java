package com.blogweb.khawatiri_with_paanel.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbQuotes extends SQLiteOpenHelper {


    private Context context;
    private static final String DATABASE_NAME = "quotes.db";
    private static final int DATABASE_VERSION = 1;

    //table quotes
    private static final String TABLE_QT = "myData";
    private static final String COLUMN_ID = "id_qt";
    private static final String COLUMN_QT = "qt_item";
    private static final String COLUMN_QT_CT = "ct_qt_item";

    //table categories
    private static final String TABLE_CT = "myCategory";
    private static final String COLUMN_CT_ID = "id_ct";
    private static final String COLUMN_CT = "ct_item";

    public dbQuotes(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_QT +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QT + " TEXT, " + COLUMN_QT_CT + " TEXT)";

        db.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_CT +
                " (" + COLUMN_CT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CT + " TEXT)";

        db.execSQL(query2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CT);
        onCreate(db);
    }


    //function for add quote
    public boolean addCateg(String ct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CT, ct);
        long result = db.insert(TABLE_CT, null, cv);
        if (result == -1) {
            Log.e("add","add failed");
            //  Toast.makeText(context, "فشلت الاضافة!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.d("add","add successfully");
              //Toast.makeText(context, "تم الاضافة بنجاح !", Toast.LENGTH_SHORT).show();
            return true;
        }

    }

    //function for add quote
    public boolean addQuotes(String quote, String ct) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QT, quote);
        cv.put(COLUMN_QT_CT, ct);
        long result = db.insert(TABLE_QT, null, cv);
        if (result == -1) {
         //   Toast.makeText(context, "فشلت الاضافة!", Toast.LENGTH_SHORT).show();
            Log.e("add","add failed");
            return false;
        } else {
            Log.d("add","add successfully");
          //  Toast.makeText(context, "تم الاضافة بنجاح الى المفضلة!", Toast.LENGTH_SHORT).show();
            return true;
        }

    }



    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_QT);
        db.execSQL("DELETE FROM " + TABLE_CT);
    }

    //get all data from database
    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_CT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
    public Cursor readQuotesData() {
        String query = "SELECT * FROM " + TABLE_QT + " ORDER BY id_qt DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }
}
