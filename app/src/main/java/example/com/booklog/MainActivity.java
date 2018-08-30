package example.com.booklog;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import static example.com.booklog.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.CONTENT_URI;
import static example.com.booklog.BookContract.BookEntry.TABLE_NAME;
import static example.com.booklog.BookContract.BookEntry._ID;
import static example.com.booklog.BookContract.LOG_TAG;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnQuantityChangeListener {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private BookCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        listView.setEmptyView(emptyTextView);
        adapter = new BookCursorAdapter(this, null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(CONTENT_URI, id));
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(1, null, this);
    }

    private void insertDummyData() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, "To kill a mocking bird");
        contentValues.put(COLUMN_AUTHOR, "Harper Lee");
        contentValues.put(COLUMN_PRICE, 399);
        contentValues.put(COLUMN_QUANTITY, 3);
        contentValues.put(COLUMN_SUPPLIER_NAME, "Arrow books");
        contentValues.put(COLUMN_SUPPLIER_PHONE, "9982433217");
        contentValues.put(COLUMN_IMAGE, 0);

        getContentResolver().insert(CONTENT_URI, contentValues);
    }

    private void deleteData() {
        getContentResolver().delete(CONTENT_URI, null, null);
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
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                _ID,
                COLUMN_NAME,
                COLUMN_AUTHOR,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
                COLUMN_IMAGE
        };
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void updateQuantity(long rowId, int newQuantity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_QUANTITY, newQuantity);
        Uri updateUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
        Log.d(LOG_TAG, "Update URI is : " + updateUri);
        getContentResolver().update(updateUri, contentValues, null, null);
    }
}
