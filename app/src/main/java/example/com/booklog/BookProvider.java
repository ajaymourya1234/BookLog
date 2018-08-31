package example.com.booklog;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.BookContract.BookEntry.CONTENT_ITEM_TYPE;
import static example.com.booklog.BookContract.BookEntry.CONTENT_LIST_TYPE;
import static example.com.booklog.BookContract.BookEntry.TABLE_NAME;
import static example.com.booklog.BookContract.BookEntry._ID;
import static example.com.booklog.BookContract.CONTENT_AUTHORITY;
import static example.com.booklog.BookContract.PATH_BOOKS;

public class BookProvider extends ContentProvider {

    public static final int CODE_BOOK = 100;
    public static final int CODE_BOOK_WITH_ID = 101;

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, CODE_BOOK);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", CODE_BOOK_WITH_ID);
    }

    private BookDbHelper helper;

    @Override
    public boolean onCreate() {
        helper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case CODE_BOOK:
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CODE_BOOK_WITH_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

                default:
                    throw new IllegalArgumentException("Cannot query unknown URI : " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)) {
            case CODE_BOOK:
                return CONTENT_LIST_TYPE;
            case CODE_BOOK_WITH_ID:
                return CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri + " with match " + uriMatcher.match(uri));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_BOOK:
                validateInsertData(contentValues);
                long id = database.insert(TABLE_NAME, null, contentValues);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return ContentUris.withAppendedId(uri, id);
                default:
                    throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private void validateInsertData(ContentValues contentValues) {
        String name = contentValues.getAsString(COLUMN_NAME);
        if (name == null || TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Book requires a title");
        }

        String priceInString = contentValues.getAsString(COLUMN_PRICE);
        double price = priceInString != null && !TextUtils.isEmpty(priceInString) ? Double.parseDouble(priceInString) : 0;
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative or zero");
        }

        String quantityInString = contentValues.getAsString(COLUMN_QUANTITY);
        int quantity = quantityInString != null && !TextUtils.isEmpty(quantityInString) ? Integer.parseInt(quantityInString) : 0;
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        String supplierName = contentValues.getAsString(COLUMN_SUPPLIER_NAME);
        if (supplierName == null || TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("Supplier name is required");
        }

        String supplierPhone = contentValues.getAsString(COLUMN_SUPPLIER_PHONE);
        String supplierEmail = contentValues.getAsString(COLUMN_SUPPLIER_EMAIL);

        if (supplierPhone == null && supplierEmail == null) {
            throw new IllegalArgumentException("Either one of supplier's phone or email is required");
        } else if (TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierEmail)) {
            throw new IllegalArgumentException("Either one of supplier's phone or email is required");
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();

        int rowsDeleted;

        switch (uriMatcher.match(uri)) {

            case CODE_BOOK:
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;

            case CODE_BOOK_WITH_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;

                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = helper.getWritableDatabase();
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case CODE_BOOK:
                if (contentValues.size() == 0) {
                    return 0;
                }
                validateUpdateData(contentValues);
                rowsUpdated = database.update(TABLE_NAME, contentValues, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;

            case CODE_BOOK_WITH_ID:
                if (contentValues.size() == 0) {
                    return 0;
                }
                validateUpdateData(contentValues);
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = database.update(TABLE_NAME, contentValues, selection, selectionArgs);
                if (rowsUpdated != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdated;

                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private void validateUpdateData(ContentValues contentValues) {

        if (contentValues.containsKey(COLUMN_NAME)) {
            String name = contentValues.getAsString(COLUMN_NAME);
            if (name == null || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Book requires a title");
            }
        }

        if (contentValues.containsKey(COLUMN_PRICE) && contentValues.getAsString(COLUMN_PRICE).length() > 0) {
            double price = contentValues.getAsDouble(COLUMN_PRICE);
            if (price < 0) {
                throw new IllegalArgumentException("Price cannot be zero or negative");
            }
        }

        if (contentValues.containsKey(COLUMN_QUANTITY) && contentValues.getAsString(COLUMN_QUANTITY).length() > 0) {
            int quantity = contentValues.getAsInteger(COLUMN_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }
        }


        if (contentValues.containsKey(COLUMN_SUPPLIER_NAME)) {
            String supplierName = contentValues.getAsString(COLUMN_SUPPLIER_NAME);
            if (supplierName == null || TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("Supplier name is required");
            }
        }

        if (!contentValues.containsKey(COLUMN_SUPPLIER_PHONE) && !contentValues.containsKey(COLUMN_SUPPLIER_EMAIL)) {
            throw new IllegalArgumentException("Either one of supplier's phone or email is required");
        }

        String supplierPhone = "";
        String supplierEmail = "";

        if (contentValues.containsKey(COLUMN_SUPPLIER_PHONE)) {
            supplierPhone = contentValues.getAsString(COLUMN_SUPPLIER_PHONE);
        }

        if (contentValues.containsKey(COLUMN_SUPPLIER_EMAIL)) {
            supplierEmail = contentValues.getAsString(COLUMN_SUPPLIER_EMAIL);
        }

        if (TextUtils.isEmpty(supplierPhone) && TextUtils.isEmpty(supplierEmail)) {
            throw new IllegalArgumentException("Either one of supplier's phone or email is required");
        }
    }
}
