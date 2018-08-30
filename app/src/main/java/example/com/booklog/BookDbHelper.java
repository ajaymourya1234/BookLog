package example.com.booklog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static example.com.booklog.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_ISBN;
import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.TABLE_NAME;
import static example.com.booklog.BookContract.BookEntry._ID;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "books.db";
    public static final int DB_VERSION = 5;

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

    private static final String DROP_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public BookDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
