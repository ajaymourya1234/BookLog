package example.com.booklog;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static example.com.booklog.BookContract.BookEntry.CODE_BOOK;
import static example.com.booklog.BookContract.BookEntry.CODE_BOOK_WITH_ID;
import static example.com.booklog.BookContract.BookEntry.TABLE_NAME;
import static example.com.booklog.BookContract.CONTENT_AUTHORITY;

public class BookProvider extends ContentProvider {

    BookDbHelper helper;
    private static UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, TABLE_NAME, CODE_BOOK);
        uriMatcher.addURI(CONTENT_AUTHORITY, TABLE_NAME + "/#", CODE_BOOK_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        helper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase database = helper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_BOOK:
                long id = database.insert(TABLE_NAME, null, contentValues);
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return BookContract.BookEntry.buildUriWithId(id);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
