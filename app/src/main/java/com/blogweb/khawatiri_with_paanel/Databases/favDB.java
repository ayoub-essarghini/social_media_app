package com.blogweb.khawatiri_with_paanel.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;


public class favDB extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "fav.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_QT_fav = "my_quotes";
    private static final String COLUMN_ID_fav = "id_fav";
    private static final String COLUMN_QUOTES = "quotes_fav";


    public favDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_QT_fav +
                " (" + COLUMN_ID_fav + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUOTES + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QT_fav);
        onCreate(db);
    }

    //function for add product
    public boolean favQuotes(String quote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUOTES, quote);
        long result = db.insert(TABLE_QT_fav, null, cv);
        if (result == -1) {
            Toasty.error(context, "فشلت الاضافة", Toast.LENGTH_SHORT, true).show();
            return false;
        } else {
            Toasty.success(context, "تمت الاضافة الى المفضلة بنجاح", Toast.LENGTH_SHORT, true).show();
            return true;
        }

    }

    //get all data from database for products
    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_QT_fav;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Boolean checkExistData(String qrr) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_QT_fav + " where " + COLUMN_QUOTES + " =? ", new String[]{qrr});
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }


    //fucntion for delete adata of product
    public void deleteFav(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_QT_fav, COLUMN_QUOTES + "=?", new String[]{row_id});
        if (result == -1) {
            Toasty.error(context, "فشل الحذف من المفضلةالمرجو المحاولة مرة اخرى", Toast.LENGTH_SHORT, true).show();
        } else {
            Toasty.success(context, "تم الحذف من المفضلة بنجاح", Toast.LENGTH_SHORT, true).show();
        }
    }
}
