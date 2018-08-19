package example.com.booklog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    BookDbHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDbHelper(this);
        database = dbHelper.getWritableDatabase();

        insertDummyData();
    }

    private void insertDummyData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, "How to kill a mocking bird");
        contentValues.put(COLUMN_PRICE, 399);
        contentValues.put(COLUMN_QUANTITY, 3);
        contentValues.put(COLUMN_SUPPLIER_NAME, "Arrow books");
        contentValues.put(COLUMN_SUPPLIER_PHONE, "998-(243)-3217");
        database.insert(TABLE_NAME, null, contentValues);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_insert) {
            insertDummyData();
        }
        return true;
    }
}
