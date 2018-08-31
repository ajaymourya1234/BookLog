package example.com.booklog;

public interface OnQuantityChangeListener {

    void updateQuantity(long rowId, int newQuantity);
}
