package example.com.booklog.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import example.com.booklog.R;
import example.com.booklog.activity.CustomDialog;
import example.com.booklog.listener.OnQuantityChangeListener;

import static example.com.booklog.data.BookContract.BookEntry.COLUMN_AUTHOR;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_IMAGE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_PRICE;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_QUANTITY;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_NAME;
import static example.com.booklog.data.BookContract.BookEntry.COLUMN_SUPPLIER_PHONE;
import static example.com.booklog.data.BookContract.BookEntry._ID;

public class BookCursorAdapter extends CursorAdapter {

    private OnQuantityChangeListener listener;
    private Toast toast;

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
        Button saleButton = view.findViewById(R.id.saleButton);
        Button contactButton = view.findViewById(R.id.contact);

        int rowIndex = cursor.getColumnIndex(_ID);
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int authorColumnIndex = cursor.getColumnIndex(COLUMN_AUTHOR);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(COLUMN_IMAGE);
        int supplierNameIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_NAME);
        int supplierPhoneIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_PHONE);
        int supplierEmailIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_EMAIL);

        final long rowId = cursor.getLong(rowIndex);
        String name = cursor.getString(nameColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        final int[] quantity = {cursor.getInt(quantityColumnIndex)};
        String imageUri = cursor.getString(imageColumnIndex);
        final String supplierName = cursor.getString(supplierNameIndex);
        final String supplierPhone = cursor.getString(supplierPhoneIndex);
        final String supplierEmail = cursor.getString(supplierEmailIndex);

        nameTextView.setText(name);
        authorTextView.setText(author);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity[0]));
        imageView.setImageURI(Uri.parse(imageUri));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if (quantity[0] > 0) {
                        quantity[0]--;
                        listener.updateQuantity(rowId, quantity[0]);
                    } else {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(context, "No more books to sell", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog dialog = new CustomDialog(context, supplierName, supplierEmail, supplierPhone);
                dialog.show();
            }
        });

        /*contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View customView = LayoutInflater.from(context).inflate(R.layout.layout_custom_dialog, null, false);
                TextView name = customView.findViewById(R.id.supplierName);
                TextView email = customView.findViewById(R.id.email);
                TextView phone = customView.findViewById(R.id.phone);

                name.setText(supplierName);
                if (supplierEmail.length() > 0 ) {
                    email.setVisibility(View.VISIBLE);
                    email.setText(supplierEmail);
                    email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:" + supplierEmail));
                        }
                    });
                } else {
                    email.setVisibility(View.GONE);
                }

                if (supplierPhone.length() > 0 ) {
                    phone.setVisibility(View.VISIBLE);
                    phone.setText(supplierPhone);
                    phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + supplierPhone));
                        }
                    });
                } else {
                    phone.setVisibility(View.GONE);
                }
                builder.setView(view).show();
            }
        });*/

    }

}
