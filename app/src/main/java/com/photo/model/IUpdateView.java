package com.photo.model;

import java.util.List;

/**
 * Created by Admin on 2017/11/2.
 */

public interface IUpdateView<T> {
    void showDialog();
    void updateView(List<T> photoList);
}
