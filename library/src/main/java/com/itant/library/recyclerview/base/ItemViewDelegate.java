package com.itant.library.recyclerview.base;


/**
 * Created by iTant on 2017/1/15.
 */
public interface ItemViewDelegate<T>
{

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
