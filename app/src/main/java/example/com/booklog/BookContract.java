package example.com.booklog;

import android.provider.BaseColumns;

public class BookContract {
    
    public class BookEntry implements BaseColumns {
        
        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;
        public static final String BASE_URI = "content://" + CONTENT_AUTHORITY;
        public static final String CONTENT_URI = BASE_URI + "/" + TABLE_NAME;
        public static final String CONTENT_URI_ID = BASE_URI + "/" + TABLE_NAME + "/#";
        
    }
}
