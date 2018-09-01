package example.com.booklog.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.booklog.R;

import static example.com.booklog.data.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_ISBN;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.data.BookContract.LOG_TAG;
import static java.lang.String.format;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.isbn)
    TextView isbn;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.quantity)
    TextView quantity;
    @BindView(R.id.supplierName)
    TextView supplierName;
    @BindView(R.id.phone)
    TextView supplierPhone;
    @BindView(R.id.email)
    TextView supplierEmail;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            uri = intent.getData();
            if (uri != null) {
                Log.d(LOG_TAG, "Uri is : " + uri.toString());
                getLoaderManager().initLoader(1, null, this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                COLUMN_NAME,
                COLUMN_AUTHOR,
                COLUMN_ISBN,
                COLUMN_IMAGE,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
                COLUMN_SUPPLIER_NAME,
                COLUMN_SUPPLIER_PHONE,
                COLUMN_SUPPLIER_EMAIL
        };
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            title.setText(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            author.setText(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR)));
            isbn.setText(cursor.getString(cursor.getColumnIndex(COLUMN_ISBN)));
            image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE))));
            price.setText(cursor.getString(cursor.getColumnIndex(COLUMN_PRICE)));
            quantity.setText(String.format(getString(R.string.items_count), String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)))));
            supplierName.setText(cursor.getString(cursor.getColumnIndex(COLUMN_SUPPLIER_NAME)));
            supplierPhone.setText(cursor.getString(cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE)));
            if (supplierPhone.getText().length() == 0) {
                supplierPhone.setVisibility(View.GONE);
            } else {
                supplierPhone.setVisibility(View.VISIBLE);
            }
            supplierEmail.setText(cursor.getString(cursor.getColumnIndex(COLUMN_SUPPLIER_EMAIL)));
            if (supplierEmail.getText().length() == 0) {
                supplierEmail.setVisibility(View.GONE);
            } else {
                supplierEmail.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        title.setText("");
        author.setText("");
        isbn.setText("");
        price.setText("");
        quantity.setText("");
        supplierName.setText("");
        supplierPhone.setText("");
        supplierEmail.setText("");
    }
}
