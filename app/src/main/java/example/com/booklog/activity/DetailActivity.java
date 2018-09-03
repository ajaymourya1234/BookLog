package example.com.booklog.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.booklog.R;
import example.com.booklog.utils.CustomDialog;
import example.com.booklog.utils.Utils;

import static example.com.booklog.data.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_ISBN;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.data.BookContract.BookEntry.CONTENT_URI;
import static example.com.booklog.data.BookContract.BookEntry._ID;

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
    @BindView(R.id.order)
    Button orderButton;

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

        Utils.disableButton(this, orderButton);

        //define default image URI that would be displayed if no image is uploaded by the user
        imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.no_img_available) +
                '/' + getResources().getResourceTypeName(R.drawable.no_img_available) +
                '/' + getResources().getResourceEntryName(R.drawable.no_img_available));

        //get intent to identify if this is to add a new product or edit an existing one
        Intent intent = getIntent();
        if (intent.getData() == null) {
            setTitle(getString(R.string.add_book));
            //call invalidate options menu to
            invalidateOptionsMenu();
            //set default image
            image.setImageURI(imageUri);

            //register text changed listeners for all edit texts to track any changes made so that
            //the user can be alerted when there are any unsaved changes on exiting the activity
            registerTextChangedListeners();
        } else {
            //fetch the specific uri from the intent
            uri = intent.getData();
            setTitle(getString(R.string.edit_book));

            //initialize the loader to fetch data for the current product
            getLoaderManager().initLoader(1, null, this);
        }

        //register listeners for increase and decrease quantity buttons
        increaseQuantity.setOnClickListener(this);
        decreaseQuantity.setOnClickListener(this);

        //register listeners to upload new image
        image.setOnClickListener(this);
        selectImageTextView.setOnClickListener(this);

    }

    /**
     * called to set up action bar menu for the activity
     *
     * @param menu menu reference to which the custom menu will be inflated
     * @return flag indicating whether menu set up was handled
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    /**
     * handle items clicked on the options menu
     *
     * @param item item selected from the options menu
     * @return flag indicating whether clicked menu item was handled
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            //handle save option
            case R.id.action_done:
                savePet();
                //close the edit screen on successfully saving the data to the database
                if (saveSuccess) {
                    finish();
                    return true;
                }
                return false;

            //handle delete option
            case R.id.action_delete:
                //display delete confirmation dialog to the user
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * set up any menu validations
     *
     * @param menu reference to action bar menu
     * @return boolean flag indicating whether the menu option preparations are handled
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //hide delete option if the intent to this activity is for adding a new product
        //instead of editing an existing product
        if (uri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * displays an alert dialog to confirm discarding any unsaved changes
     * on pressing back
     */
    private void showUnsavedChangesAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            //dismiss the dialog if the user chooses cancel
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            //close the editing activity if the user chooses to discard changes
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        //display the alert dialog
        builder.create().show();
        //reset unsaved changes flag
        unsavedChanges = false;
    }

    /**
     * called when the save button is clicked
     */
    private void savePet() {

        //verify if there are any changes made that need saving, return early if there are no changes to be saved
        if (!unsavedChanges) {
            return;
        }

        //fetch all the edit text data, trim any leading or following white spaces
        String name = nameEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String isbn = isbnEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        String supplierEmail = supplierEmailEditText.getText().toString().trim();


        //if the user chose to add a new product but did not enter any fields, exit early
        if (uri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(author) && TextUtils.isEmpty(isbn) &&
                TextUtils.isEmpty(price) && TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierEmail)) {
            return;
        }

        //validate that the book title isn't an empty string
        if (TextUtils.isEmpty(name)) {
            //set error accordingly
            nameEditText.requestFocus();
            nameEditText.setError(getString(R.string.title_empty_error));
            displayToastAlert(getString(R.string.title_empty_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            Utils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(author)) {
            //set default author text if the user has not entered author name
            author = getString(R.string.unknown_author);
        }

        if (TextUtils.isEmpty(isbn)) {
            //set default isbn text if the user has not entered isbn details
            isbn = getString(R.string.isbn_not_available);
        }

        //validate price field isn't empty
        if (TextUtils.isEmpty(price)) {
            //set error accordingly
            priceEditText.setError("");
            displayToastAlert(getString(R.string.price_invalid_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            Utils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(quantity)) {
            //set error accordingly
            quantityEditText.setError("");
            displayToastAlert(getString(R.string.quantity_missing_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            Utils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(supplierName)) {
            //set error accordingly
            supplierNameEditText.setError("");
            displayToastAlert(getString(R.string.supplier_name_error));
            //indicate save wasn't successful
            saveSuccess = false;
            //hide soft keyboard to indicate to the user that field validation has failed
            Utils.hideSoftKeyboard(this);
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        if (TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierEmail)) {
            //display error accordingly
            displayToastAlert(getString(R.string.supplier_details_missing_error));
            //indicate save wasn't successful
            saveSuccess = false;
            return;
        } else {
            //update save success flag if validation succeeds
            saveSuccess = true;
        }

        //set up content values object to store all fields
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

        //verify if this is a new product insert request
        if (uri == null) {
            //invoke insert method via content resolver
            Uri newUri = getContentResolver().insert(CONTENT_URI, contentValues);

            //display error/success alert to the user
            if (newUri == null) {
                displayToastAlert(getString(R.string.error_save_book));
            } else {
                displayToastAlert(getString(R.string.success_save_book));
            }
        } else {
            //invoke update method via content resolver
            int rowsAffected = getContentResolver().update(uri, contentValues, null, null);

            //display error/success alert to the user
            if (rowsAffected == 0) {
                displayToastAlert(getString(R.string.error_update_book));
            } else {
                displayToastAlert(getString(R.string.success_update_book));
            }
        }
    }

    /**
     * displays alert message in the form of a toast
     *
     * @param message message to be displayed to the user
     */
    private void displayToastAlert(String message) {
        //cancel any outstanding toast before displaying a new one
        cancelToast();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * cancel any existing toasts
     */
    private void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    /**
     * display delete confirm dialog to the user before deleting a product
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirm);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete pet on confirmation
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dismiss dialog if denied
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        //display alert dialog
        builder.create().show();
    }

    /**
     * deletes an existing pet by the id specific uri
     */
    private void deletePet() {
        if (uri != null) {
            //invoke delete action via the content resolver
            int rowsDeleted = getContentResolver().delete(uri, null, null);

            //display error/success message accordingly
            if (rowsDeleted == 0) {
                displayToastAlert(getString(R.string.error_delete_book));
            } else {
                displayToastAlert(getString(R.string.success_delete_book));
            }
            finish();
        }
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

    /**
     * called when a previously created loader has finished it's load
     *
     * @param loader reference to the cursor loader that has finished loading of the required data
     * @param cursor reference to the cursor that contains the data from the database
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //exit early if there is no valid data loaded in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        //verify that the cursor contains data
        if (cursor.moveToFirst()) {

            //get column indices for the required fields
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
            int authorColumnIndex = cursor.getColumnIndex(COLUMN_AUTHOR);
            int isbnColumnIndex = cursor.getColumnIndex(COLUMN_ISBN);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);

            //fetch the data obtained from the cursor
            String name = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String isbn = cursor.getString(isbnColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            imageUri = Uri.parse(cursor.getString(imageColumnIndex));

            //set the data in the edit texts and the image view
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

            //register text changed listeners for all edit texts to track any changes made so that
            //the user can be alerted when there are any unsaved changes on exiting the activity
            registerTextChangedListeners();

            //enable order button to allow the user to contact the supplier
            if (!TextUtils.isEmpty(supplierEmail) || !TextUtils.isEmpty(supplierPhone)) {
                Utils.enableButton(this, orderButton);
            }

            //set click functionality for order button to contact supplier via phone/email intent
            if (orderButton.isEnabled()) {
                orderButton.setOnClickListener(this);
            }
        }

    }

    /**
     * registers text changed listeners for all editable fields
     */
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

    /**
     * called when a previously created loader is being reset - makes the data associated with the loader unavailable
     *
     * @param loader reference to previously created cursor loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //reset the content of all edit texts and image view's resource
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

    /**
     * handle action bar back navigation
     *
     * @return boolean flag indicating whether the action was handled
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (unsavedChanges) {
            //display dialog to alert the user of the unsaved changes, if any
            showUnsavedChangesAlert();
            return false;
        }
        //close the edit/add activity if there are no unsaved changes to be handled
        finish();
        return true;
    }

    /**
     * handle click listeners for views
     *
     * @param view reference to the view that receives the click event
     */
    @Override
    public void onClick(View view) {

        //declare and initialize variable to hold and update quantity
        int quantity = 0;
        if (quantityEditText.getText().length() > 0) {
            //fetch the quantity from the edit text
            quantity = Integer.parseInt(quantityEditText.getText().toString());
        }

        switch (view.getId()) {
            //handle incrementing the quantity
            case R.id.increaseQuantity:
                quantity++;
                updateQuantity(quantity);
                break;
            //handle decrementing the quantity
            case R.id.decreaseQuantity:
                if (quantity > 0) {
                    quantity--;
                    updateQuantity(quantity);
                } else {
                    //ensure that the quantity doesn't go negative
                    displayToastAlert(getString(R.string.negative_quantity_error));
                }
                break;

            //handle updating product image on the image view and "Select new image" textview associated with it
            case R.id.select_image_text:
            case R.id.image:
                Intent intent;
                //create new intent to fetch an image from device storage
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                } else {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                }
                //set intent type to filter images
                intent.setType("image/*");
                //start activity and get the resulting URI if a new image is selected
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;

            case R.id.order:
                CustomDialog dialog = new CustomDialog(this, nameEditText.getText().toString().trim(),supplierNameEditText.getText().toString().trim(), supplierEmailEditText.getText().toString().trim(), supplierPhoneEditText.getText().toString().trim());
                dialog.show();
                break;
        }
    }

    /**
     * displays the updated quantity value in the edit text field
     *
     * @param quantity new quantity value
     */
    private void updateQuantity(int quantity) {
        //update the edit text with the new value, clear error alert if any
        quantityEditText.setText(String.valueOf(quantity));
        quantityEditText.setSelection(quantityEditText.getText().length());
        quantityEditText.setError(null);
    }

    /**
     * called once startActivityForResult returns
     *
     * @param requestCode integer code to identify the type of intent request completed
     * @param resultCode  status of the intent action
     * @param data        data fetched on successful intent action
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //hold reference to the current image uri
        //if the user picks an image same as the existing one, do not update the uri
        Uri currentImageUri = imageUri;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                //get the uri on successful intent action
                imageUri = data.getData();
                if (!currentImageUri.toString().equals(imageUri.toString())) {
                    //if a different image is picked, indicate that the image uri is to be updated in the database
                    unsavedChanges = true;
                }
                //update the image view with the newly chosen image after scaling the image size
                image.setImageBitmap(Utils.getBitmapFromUri(this, image, imageUri));
            }

        }

    }

    /**
     * verify and display unsaved changes alert dialog to the user on back pressed
     */
    @Override
    public void onBackPressed() {
        if (unsavedChanges) {
            showUnsavedChangesAlert();
        } else {
            //exit if there are no unsaved changes
            super.onBackPressed();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //override onTextChanged method for EditText fields to track any changes
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        unsavedChanges = true;
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
