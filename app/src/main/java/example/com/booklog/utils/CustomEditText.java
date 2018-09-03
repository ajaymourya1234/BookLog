package example.com.booklog.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

public class CustomEditText extends TextInputEditText {


    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null);
    }
}
