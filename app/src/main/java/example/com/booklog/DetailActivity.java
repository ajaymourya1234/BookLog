package example.com.booklog;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.CONTENT_URI;
import static example.com.booklog.BookContract.BookEntry._ID;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.priceEditText)
    EditText priceEditText;
    @BindView(R.id.quantity)
    TextView quantityEditText;
    @BindView(R.id.supplierNameEditText)
    EditText supplierNameEditText;
    @BindView(R.id.supplierPhoneNoEditText)
    EditText supplierPhoneEditText;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                savePet();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePet() {

        String name = nameEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        if (uri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(price) && TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_QUANTITY, quantity);
        contentValues.put(COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(COLUMN_SUPPLIER_PHONE, supplierPhone);

        if (uri == null) {
            Uri newUri = getContentResolver().insert(CONTENT_URI, contentValues);

            if (newUri == null) {
                Toast.makeText(this, "Error saving book", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Book saved", Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(uri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating book", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Book updated", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setMessage("Delete this book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deletePet() {
        if (uri != null) {
            int rowsDeleted = getContentResolver().delete(uri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Error deleting book", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Book deleted", Toast.LENGTH_LONG).show();
            }
            finish();
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

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }

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
