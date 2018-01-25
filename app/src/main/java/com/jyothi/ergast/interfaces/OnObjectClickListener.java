package com.jyothi.ergast.interfaces;

/**
 * Created by jasapu on 20-12-2017.
 */

public interface OnObjectClickListener<T> extends BaseListener {

    /**
     * Item has been clicked.
     *
     * @param item object associated with the clicked item.
     */
    void onItemClicked(T item);

    /**
     * Item has been long pressed.
     *
     * @param item object associated with the clicked item.
     */
    void onItemLongPress(T item);

}
