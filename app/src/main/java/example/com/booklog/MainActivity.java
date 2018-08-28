package example.com.booklog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;

import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private BookDbHelper dbHelper;
    private SQLiteDatabase database;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BookDbHelper(this);

        cursor = fetchData();

        displayData(cursor);
    }

    private void displayData(Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            while (cursor.moveToNext()) {
                stringBuilder.append("ID : ").append("\nBook Name: ").append(cursor.getString(cursor.getColumnIndex(COLUMN_NAME))).append("\nPrice : ").append(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE))).append("\nQuantity : ").append(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY))).append("\n\n");
            }

        } else {

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private Cursor fetchData() {

        database = dbHelper.getReadableDatabase();

        String[] columns = {COLUMN_NAME, COLUMN_PRICE, COLUMN_QUANTITY};
        return database.query(TABLE_NAME, columns, null, null, null, null, null);
    }

    private void insertDummyData() {
        database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, "How to kill a mocking bird");
        contentValues.put(COLUMN_PRICE, 399);
        contentValues.put(COLUMN_QUANTITY, 3);
        contentValues.put(COLUMN_SUPPLIER_NAME, "Arrow books");
        contentValues.put(COLUMN_SUPPLIER_PHONE, "998-(243)-3217");

        database.insert(TABLE_NAME, null, contentValues);
    }

    private void deleteData() {
        database = dbHelper.getWritableDatabase();

        database.delete(TABLE_NAME, null, null);
    }

    private void refresh() {
        cursor = fetchData();
        displayData(cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert:
                insertDummyData();
                break;
            case R.id.action_delete:
                deleteData();
                break;
            case R.id.action_refresh:
                if (cursor != null){
                    refresh();
                }
                break;
        }
        return true;
    }
}
