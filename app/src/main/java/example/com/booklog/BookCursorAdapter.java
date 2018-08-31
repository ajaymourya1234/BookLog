package example.com.booklog;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static example.com.booklog.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.BookContract.BookEntry._ID;
import static example.com.booklog.BookContract.LOG_TAG;

public class BookCursorAdapter extends CursorAdapter {

    OnQuantityChangeListener listener;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        listener = (OnQuantityChangeListener) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView authorTextView = view.findViewById(R.id.author);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        ImageView imageView = view.findViewById(R.id.image);
        Button button = view.findViewById(R.id.button);

        int rowIndex = cursor.getColumnIndex(_ID);
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int authorColumnIndex = cursor.getColumnIndex(COLUMN_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);

        final long rowId = cursor.getLong(rowIndex);
        String name = cursor.getString(nameColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int[] quantity = {cursor.getInt(quantityColumnIndex)};
        String imageUri = cursor.getString(imageColumnIndex);

        nameTextView.setText(name);
        authorTextView.setText(author);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity[0]));
        imageView.setImageURI(Uri.parse(imageUri));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "Sell Button has been clicked");
                if (listener != null ) {
                    if (quantity[0] > 0) {
                        quantity[0]--;
                        listener.updateQuantity(rowId, quantity[0]);
                    } else {
                        Toast.makeText(context, "No more books to sell", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
