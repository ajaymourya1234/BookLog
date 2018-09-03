package example.com.booklog.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import example.com.booklog.R;
import example.com.booklog.utils.Utils;
import example.com.booklog.listener.OnQuantityChangeListener;

import static example.com.booklog.data.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.data.BookContract.BookEntry._ID;

//custom cursor adapter
public class BookCursorAdapter extends CursorAdapter {

    //listener for sale button
    private OnQuantityChangeListener listener;
    private Toast toast;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        //initialize the listener
        listener = (OnQuantityChangeListener) context;
    }

    /**
     * inflates the list item layout
     *
     * @param context reference to activity context that provides access to application resources
     * @param cursor  reference to the cursor holding the data for the individual list item
     * @param parent  viewgroup to which the individual list item view has to be attached
     * @return newly inflated list view item
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
    }

    /**
     * binds the individual views of the list item
     *
     * @param view    reference to individual list item view
     * @param context reference to activity context that provides access to application resources
     * @param cursor  reference to the cursor holding the data for the individual list item
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        //obtain references to the views
        TextView nameTextView = view.findViewById(R.id.name);
        TextView authorTextView = view.findViewById(R.id.author);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        ImageView imageView = view.findViewById(R.id.image);
        final Button saleButton = view.findViewById(R.id.saleButton);

        //set the title TextView to not extend to more than 1 line
        nameTextView.setSingleLine();

        //get the column indices for the required fields
        int rowIndex = cursor.getColumnIndex(_ID);
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int authorColumnIndex = cursor.getColumnIndex(COLUMN_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);

        //fetch the values for each field
        final long rowId = cursor.getLong(rowIndex);
        final String name = cursor.getString(nameColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int[] quantity = {cursor.getInt(quantityColumnIndex)};
        String imageUri = cursor.getString(imageColumnIndex);

        //display the book details and image
        nameTextView.setText(name);
        authorTextView.setText(author);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity[0]));
        imageView.setImageURI(Uri.parse(imageUri));

        //enable/disable sale button depending on stock availability
        if (quantity[0] == 0) {
            Utils.disableButton(context, saleButton);
        } else {
            Utils.enableButton(context, saleButton);
        }

        //set on click listener for the sale button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    //decrease the product quantity, ensuring it doesn't go below 0
                    if (quantity[0] > 0) {
                        quantity[0]--;
                        //call update quantity to update the value in the database
                        listener.updateQuantity(rowId, quantity[0]);
                    }
                    if (quantity[0] == 0) {
                        if (toast != null) {
                            //cancel any outstanding toasts
                            toast.cancel();
                        }
                        //
                        //display out of stock error when the quantity reduces to 0
                        toast = Toast.makeText(context, R.string.product_out_of_stock, Toast.LENGTH_SHORT);
                        toast.show();
                        //disable sale button
                        Utils.disableButton(context, saleButton);
                    } else {
                        //if product is available in stock, enable sale button
                        Utils.enableButton(context, saleButton);
                    }
                }
            }
        });
    }

}
