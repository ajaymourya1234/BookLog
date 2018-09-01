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

public class CustomDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.supplierName)
    TextView name;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.phone)
    TextView phone;

    OnDialogButtonClick listener;
    String supplierName;
    String supplierEmail;
    String supplierPhone;

    public CustomDialog(@NonNull Context context, String supplierName, String supplierEmail, String supplierPhone) {
        super(context);
        this.requestWindowFeature(FEATURE_NO_TITLE);
        this.setContentView(R.layout.layout_custom_dialog);

        this.listener = (OnDialogButtonClick) context;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;

        ButterKnife.bind(this);

        name.setText(supplierName);
        if (supplierEmail.length() > 0) {
            email.setVisibility(View.VISIBLE);
            email.setText(String.format(context.getString(R.string.email_at), supplierEmail));
        } else {
            email.setVisibility(View.GONE);
        }
        email.setOnClickListener(this);

        if (supplierPhone.length() > 0) {
            phone.setVisibility(View.VISIBLE);
            phone.setText(String.format(context.getString(R.string.call_at), supplierPhone));
        } else {
            phone.setVisibility(View.GONE);
        }
        phone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email:
                listener.onChooseEmail(supplierEmail);
                break;
            case R.id.phone:
                listener.onChoosePhone(supplierPhone);
                break;
        }
    }
}
