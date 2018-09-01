package example.com.booklog.activity;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.booklog.R;
import example.com.booklog.listener.OnDialogButtonClick;

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

    //listener reference to handle intents for supplier email and/or phone
    private OnDialogButtonClick listener;
    private String supplierEmail;
    private String supplierPhone;

    public CustomDialog(@NonNull Context context, String supplierName, String supplierEmail, String supplierPhone) {
        super(context);
        this.requestWindowFeature(FEATURE_NO_TITLE);
        //set custom dialog layout
        this.setContentView(R.layout.layout_custom_dialog);

        //initialize listener and supplier contact details
        this.listener = (OnDialogButtonClick) context;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;

        ButterKnife.bind(this);

        //display supplier name
        name.append(supplierName);

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
        switch (view.getId()) {
            case R.id.email:
                listener.onChooseContactType(supplierEmail, EMAIL);
                break;
            case R.id.phone:
                listener.onChooseContactType(supplierPhone, PHONE);
                break;
        }
    }
}
