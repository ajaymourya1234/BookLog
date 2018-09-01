package example.com.booklog.listener;

public interface OnQuantityChangeListener {

    void updateQuantity(long rowId, int newQuantity);
}
