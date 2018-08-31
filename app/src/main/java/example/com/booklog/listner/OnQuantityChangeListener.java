package example.com.booklog.listner;

public interface OnQuantityChangeListener {

    void updateQuantity(long rowId, int newQuantity);
}
