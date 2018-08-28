package example.com.booklog;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.text.DecimalFormat;

import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry._ID;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText nameEditText;
    EditText priceEditText;
    EditText quantityEditText;
    EditText supplierNameEditText;
    EditText supplierPhoneEditText;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        if (intent.getData() == null) {
            setTitle("Add a new book");
        } else {
            uri = intent.getData();
            setTitle("Edit book");

            getLoaderManager().initLoader(1, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                _ID,
                COLUMN_NAME,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
                COLUMN_SUPPLIER_NAME,
                COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE);

        String name = cursor.getString(nameColumnIndex);
        double price = cursor.getInt(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        String supplierName = cursor.getString(supplierNameColumnIndex);
        String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

        nameEditText.setText(name);
        priceEditText.setText(DecimalFormat.getNumberInstance().format(price));
        quantityEditText.setText(quantity);
        supplierNameEditText.setText(supplierName);
        supplierPhoneEditText.setText(supplierPhone);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
