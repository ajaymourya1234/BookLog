package example.com.booklog;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import static example.com.booklog.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_ISBN;
import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.CONTENT_URI;
import static example.com.booklog.BookContract.BookEntry._ID;
import static example.com.booklog.BookContract.LOG_TAG;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, TextWatcher {

    private static final int PICK_IMAGE_REQUEST = 0;

    @BindView(R.id.nameEditText)
    EditText nameEditText;
    @BindView(R.id.authorEditText)
    EditText authorEditText;
    @BindView(R.id.isbnEditText)
    EditText isbnEditText;
    @BindView(R.id.priceEditText)
    EditText priceEditText;
    @BindView(R.id.pieces)
    EditText quantityEditText;
    @BindView(R.id.supplierNameEditText)
    EditText supplierNameEditText;
    @BindView(R.id.supplierPhoneNoEditText)
    EditText supplierPhoneEditText;
    @BindView(R.id.supplierEmailEditText)
    EditText supplierEmailEditText;
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
    private Toast toast;
    private boolean saveSuccess = true;
    private boolean unsavedChanges;

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
                if (saveSuccess) {
                    finish();
                    return true;
                }
                return false;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
        unsavedChanges = false;
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
        String author = authorEditText.getText().toString().trim();
        String isbn = isbnEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        String supplierEmail = supplierEmailEditText.getText().toString().trim();

        if (imageUri == null || TextUtils.isEmpty(imageUri.toString())) {
            imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
            "://" + getResources().getResourcePackageName(R.drawable.no_img_available) +
            '/' + getResources().getResourceTypeName(R.drawable.no_img_available) +
            '/' + getResources().getResourceEntryName(R.drawable.no_img_available));
        }

        if (uri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(author) && TextUtils.isEmpty(isbn) &&
                TextUtils.isEmpty(price) && TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierEmail)) {
            return;
        }

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Title cannot be empty");
            saveSuccess = false;
            return;
        } else {
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(author)) {
            author = getString(R.string.unknown_author);
        }

        if (TextUtils.isEmpty(isbn)) {
            isbn = getString(R.string.isbn_not_available);
        }

        if (TextUtils.isEmpty(price)) {
            priceEditText.setError("Valid price required");
            saveSuccess = false;
            return;
        } else {
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(quantity)) {
            quantityEditText.setError("Quantity required");
            saveSuccess = false;
            return;
        } else {
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(supplierName)) {
            supplierNameEditText.setError("Supplier name is required");
            saveSuccess = false;
            return;
        } else {
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierEmail)) {
            displayError("Either one of supplier's phone or email is required");
            saveSuccess = false;
            return;
        } else {
            saveSuccess = true;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_AUTHOR, author);
        contentValues.put(COLUMN_ISBN, isbn);
        contentValues.put(COLUMN_PRICE, price);
        contentValues.put(COLUMN_QUANTITY, quantity);
        contentValues.put(COLUMN_SUPPLIER_NAME, supplierName);
        contentValues.put(COLUMN_SUPPLIER_PHONE, supplierPhone);
        contentValues.put(COLUMN_SUPPLIER_EMAIL, supplierEmail);
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

    private void displayError(String error) {
        cancelToast();
        toast = Toast.makeText(this, error, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void cancelToast() {
        if (toast != null) {
            toast.cancel();
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
                COLUMN_AUTHOR,
                COLUMN_ISBN,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
                COLUMN_SUPPLIER_NAME,
                COLUMN_SUPPLIER_PHONE,
                COLUMN_SUPPLIER_EMAIL,
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
            int authorColumnIndex = cursor.getColumnIndex(COLUMN_AUTHOR);
            int isbnColumnIndex = cursor.getColumnIndex(COLUMN_ISBN);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String isbn = cursor.getString(isbnColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            imageUri = Uri.parse(cursor.getString(imageColumnIndex));

            nameEditText.setText(name);
            nameEditText.setSelection(nameEditText.getText().length());
            authorEditText.setText(author);
            isbnEditText.setText(isbn);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
            supplierEmailEditText.setText(supplierEmail);
            image.setImageURI(imageUri);

            registerTextChangedListeners();
        }

    }

    private void registerTextChangedListeners() {
        nameEditText.addTextChangedListener(this);
        authorEditText.addTextChangedListener(this);
        isbnEditText.addTextChangedListener(this);
        priceEditText.addTextChangedListener(this);
        quantityEditText.addTextChangedListener(this);
        supplierNameEditText.addTextChangedListener(this);
        supplierPhoneEditText.addTextChangedListener(this);
        supplierEmailEditText.addTextChangedListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        authorEditText.setText("");
        isbnEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
        supplierEmailEditText.setText("");
        image.setImageResource(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(LOG_TAG, "Unsaved changes exist ? " + unsavedChanges);
        if (unsavedChanges) {
            showUnsavedChangesAlert();
            return false;
        }
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        int quantity = 0;
        if (quantityEditText.getText().length() > 0){
            quantity = Integer.parseInt(quantityEditText.getText().toString());
        }
        switch (view.getId()) {
            case R.id.increaseQuantity:
                quantity++;
                quantityEditText.setText(String.valueOf(quantity));
                quantityEditText.setSelection(quantityEditText.getText().length());
                quantityEditText.setError(null);
                break;
            case R.id.decreaseQuantity:
                if (quantity > 0) {
                    quantity--;
                    quantityEditText.setText(String.valueOf(quantity));
                    quantityEditText.setSelection(quantityEditText.getText().length());
                    quantityEditText.setError(null);
                } else {
                    Toast.makeText(this, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
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
            unsavedChanges = true;
            if (data != null) {
                imageUri = data.getData();
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

    @Override
    public void onBackPressed() {
        if (unsavedChanges) {
            showUnsavedChangesAlert();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        unsavedChanges = true;
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
