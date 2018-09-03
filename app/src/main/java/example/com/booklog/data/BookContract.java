package example.com.booklog.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import example.com.booklog.BuildConfig;
import example.com.booklog.activity.MainActivity;


//mark the class final as it will not be required to inherit this
public final class BookContract {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    //private constructor so that the BookContract object cannot be instantiated outside this class
    private BookContract() {
    }

    //define content authority and base uri
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

    //define Book Table constants
    public static final class BookEntry implements BaseColumns {

        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_ISBN = "isbn";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_IMAGE = "image";

        //define content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_BOOKS);

        //define MIME types for the URIs
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

    }
}
