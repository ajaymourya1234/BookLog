package example.com.booklog.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.booklog.R;
import example.com.booklog.adapter.BookCursorAdapter;
import example.com.booklog.listener.OnDialogButtonClick;
import example.com.booklog.listener.OnQuantityChangeListener;

import static example.com.booklog.activity.CustomDialog.EMAIL;
import static example.com.booklog.activity.CustomDialog.PHONE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.data.BookContract.BookEntry.CONTENT_URI;
import static example.com.booklog.data.BookContract.BookEntry._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnQuantityChangeListener, OnDialogButtonClick {

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

        //open add new product activity
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        //set empty view for listview
        listView.setEmptyView(emptyTextView);
        //initialize cursor adapter
        adapter = new BookCursorAdapter(this, null);
        listView.setAdapter(adapter);

        //register listview item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //open edit product activity
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                //send uri to identify specific product data
                intent.setData(ContentUris.withAppendedId(CONTENT_URI, id));
                startActivity(intent);
            }
        });

        //initialize loader
        getLoaderManager().initLoader(1, null, this);
    }

    /**
     * creates a loader if one with specified ID doesn't exist when initloader or restartloader is called
     *
     * @param i      ID that indicates whether a loader needs to be created or one exists already
     * @param bundle bundle of arguments to be passed to the loader when initializing
     * @return a new cursor loader reference with the given ID
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        //state the list of columns to be fetched
        String[] projection = {
                _ID,
                COLUMN_NAME,
                COLUMN_AUTHOR,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
                COLUMN_IMAGE,
                COLUMN_SUPPLIER_NAME,
                COLUMN_SUPPLIER_EMAIL,
                COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
    }

    /**
     * called when a previously created loader has finished it's load
     *
     * @param loader reference to the cursor loader that has finished loading of the required data
     * @param cursor reference to the cursor that contains the data from the database
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //swap in a new cursor, return the old cursor
        adapter.swapCursor(cursor);
    }

    /**
     * called when a previously created loader is being reset - makes the data associated with the loader unavailable
     *
     * @param loader reference to previously created cursor loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //swap in a new cursor, return the old cursor
        adapter.swapCursor(null);
    }

    /**
     * called when the sale button is clicked on the list view item to update the new quantity in the database
     *
     * @param rowId       row id of the database corresponding to the individual list item product
     * @param newQuantity new quantity of the product
     */
    @Override
    public void updateQuantity(long rowId, int newQuantity) {
        //set up a content values object and set the quantity value
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_QUANTITY, newQuantity);
        //define the specific product uri based on the row id
        Uri updateUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
        //invoke the update action via the content resolver
        getContentResolver().update(updateUri, contentValues, null, null);
    }

    /**
     * called when the user chooses a contact - email or phone to contact a supplier
     *
     * @param contactDetails - email address or phone number of the given supplier
     * @param type           - defines the contact type - email or phone to decide the intent type
     */
    @Override
    public void onChooseContactType(String contactDetails, String type) {
        Intent intent = new Intent();
        switch (type) {
            case EMAIL:
                //set email intent
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + contactDetails));
                break;
            case PHONE:
                //set phone dial intent
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contactDetails));
                break;
        }
        startActivity(intent);
    }
}
