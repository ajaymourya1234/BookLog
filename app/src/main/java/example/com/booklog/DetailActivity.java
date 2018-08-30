package example.com.booklog;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static example.com.booklog.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.CONTENT_URI;
import static example.com.booklog.BookContract.BookEntry._ID;
import static example.com.booklog.BookContract.LOG_TAG;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 0;
    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.priceEditText)
    EditText priceEditText;
    @BindView(R.id.pieces)
    TextView quantityTextView;
    @BindView(R.id.supplierNameEditText)
    EditText supplierNameEditText;
    @BindView(R.id.supplierPhoneNoEditText)
    EditText supplierPhoneEditText;
    @BindView(R.id.increaseQuantity)
    ImageButton increaseQuantity;
    @BindView(R.id.decreaseQuantity)
    ImageButton decreaseQuantity;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.select_image_text)
    TextView selectImageTextView;

    private Uri uri;
    private Uri imageUri;

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
            invalidateOptionsMenu();
        } else {
            uri = intent.getData();
            setTitle("Edit book");

            getLoaderManager().initLoader(1, null, this);
        }

        increaseQuantity.setOnClickListener(this);
        decreaseQuantity.setOnClickListener(this);
        image.setOnClickListener(this);
        selectImageTextView.setOnClickListener(this);
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void savePet() {

        String name = nameEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String quantity = quantityTextView.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        if (uri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(price) && TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(imageUri.toString())) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_QUANTITY, quantity);
        contentValues.put(COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(COLUMN_SUPPLIER_PHONE, supplierPhone);
        contentValues.put(COLUMN_IMAGE, imageUri.toString());

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
                COLUMN_SUPPLIER_PHONE,
                COLUMN_IMAGE
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
            int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String imageUri = cursor.getString(imageColumnIndex);

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(price));
            quantityTextView.setText(String.valueOf(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
            image.setImageURI(Uri.parse(imageUri));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityTextView.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
        image.setImageResource(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        int quantity = Integer.parseInt(quantityTextView.getText().toString());
        switch (view.getId()) {
            case R.id.increaseQuantity:
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
                break;
            case R.id.decreaseQuantity:
                if (quantity > 1) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                } else {
                    Toast.makeText(this, "Quantity has to be at least 1", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_image_text:
            case R.id.image:
                Intent intent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                Log.d(LOG_TAG, " URI is : " + imageUri.toString());
                image.setImageBitmap(getBitmapFromUri(imageUri));
            }

        }

    }

    private Bitmap getBitmapFromUri(Uri uri) {

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        InputStream inputStream = null;

        try {
            inputStream = getContentResolver().openInputStream(uri);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            int targetWidth = options.outWidth;
            int targetHeight = options.outHeight;

            int scaleFactor = Math.min(imageWidth/targetWidth, imageHeight/targetHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;

            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
