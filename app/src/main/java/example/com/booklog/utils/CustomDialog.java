package example.com.booklog.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.booklog.R;

import static android.view.Window.FEATURE_NO_TITLE;

//implementing a custom dialog to display contact information for a supplier
public class CustomDialog extends Dialog implements View.OnClickListener {

    static final String EMAIL = "email";
    static final String PHONE = "phone";

    @BindView(R.id.supplierName)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.phone)
    TextView phone;

    private Context context;
    private String bookName;
    private String supplierEmail;
    private String supplierPhone;

    public CustomDialog(@NonNull Context context, String bookName, String supplierName, String supplierEmail, String supplierPhone) {
        super(context);
        this.requestWindowFeature(FEATURE_NO_TITLE);
        //set custom dialog layout
        this.setContentView(R.layout.layout_custom_dialog);

        this.context = context;
        this.bookName = bookName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;

        ButterKnife.bind(this);

        //display supplier name
        this.name.append(supplierName);

        //verify if supplier email is available and display/hide corresponding TextView accordingly
        if (supplierEmail.length() > 0) {
            email.setVisibility(View.VISIBLE);
            email.setText(String.format(context.getString(R.string.email_at), "\n" + supplierEmail));
        } else {
            email.setVisibility(View.GONE);
        }

        //verify if supplier phone is available and display/hide corresponding TextView accordingly
        if (supplierPhone.length() > 0) {
            phone.setVisibility(View.VISIBLE);
            phone.setText(String.format(context.getString(R.string.call_at), supplierPhone));
        } else {
            phone.setVisibility(View.GONE);
        }

        //set click listeners to start intents for email/phone
        email.setOnClickListener(this);
        phone.setOnClickListener(this);
    }

    /**
     * invoked on selecting phone/email from supplier contact dialog on the main screen
     *
     * @param view reference to view that registered the click event
     */
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.email:
                //set email intent
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + supplierEmail));
                intent.putExtra(Intent.EXTRA_SUBJECT, String.format(context.getString(R.string.email_subject), bookName));
                intent.putExtra(Intent.EXTRA_TEXT, String.format(context.getString(R.string.email_body), new Random().nextInt(100), bookName));
                break;
            case R.id.phone:
                //set phone dial intent
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhone));
                break;
        }
        //start intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            //dismiss once the intent was successfully handled
            dismiss();
        } else {
            Toast.makeText(context, "No suitable app to handle the current action", Toast.LENGTH_SHORT).show();
        }
    }
}
