package example.com.booklog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static example.com.booklog.data.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_ISBN;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.data.BookContract.BookEntry.TABLE_NAME;
import static example.com.booklog.data.BookContract.BookEntry._ID;

public class BookDbHelper extends SQLiteOpenHelper {

    //define constants for db name and version
    //version will be changed anytime the db schema changes (upgrade is required)
    private static final String DB_NAME = "books.db";
    private static final int DB_VERSION = 5;

    //define create table query
    private static final String CREATE_ENTRIES = "CREATE TABLE " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_AUTHOR + " TEXT NOT NULL, " +
            COLUMN_ISBN + " TEXT NOT NULL, " +
            COLUMN_PRICE + " REAL NOT NULL, " +
            COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
            COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, " +
            COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
            COLUMN_IMAGE + " TEXT NOT NULL" +
            ")";

    //define drop table query
    private static final String DROP_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    BookDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * creates the table based on the create query
     *
     * @param sqLiteDatabase the database in which the table is created
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTRIES);
    }

    /**
     * called whenever the table needs to be upgraded (ex: schema update)
     *
     * @param sqLiteDatabase database associated with the table
     * @param i              old version of the database
     * @param i1             new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
