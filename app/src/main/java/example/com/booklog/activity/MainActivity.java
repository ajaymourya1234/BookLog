package example.com.booklog.activity;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.booklog.R;
import example.com.booklog.adapter.BookCursorAdapter;
import example.com.booklog.listener.OnDialogButtonClick;
import example.com.booklog.listener.OnQuantityChangeListener;

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
                ImageView imageView = view.findViewById(R.id.image);
                ActivityOptions options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, imageView, imageView.getTransitionName());
                }
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.setData(ContentUris.withAppendedId(CONTENT_URI, id));
                startActivity(intent, options.toBundle());
            }
        });

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
        getContentResolver().update(updateUri, contentValues, null, null);
    }

    @Override
    public void onChooseEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        startActivity(intent);
    }

    @Override
    public void onChoosePhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
