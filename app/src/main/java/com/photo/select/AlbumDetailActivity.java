package com.photo.select;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.photo.AppConfig;
import com.photo.bean.PhotoBean;
import com.photo.model.DetailPhotoPresenter;
import com.photo.model.IUpdateView;

import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity implements IUpdateView<PhotoBean> {
    private static final String TAG = "AlbumDetailActivity";
    private static final boolean DEBUG = AppConfig.DEBUG;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private AlbumDetailAdapter mAdapter;
    private DetailPhotoPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        mProgressBar = (ProgressBar) findViewById(R.id.main_progress);
        initRecycler();
        mPresenter = new DetailPhotoPresenter(this, this);

        String filePath = getIntent().getStringExtra("FilePath");
        if (!TextUtils.isEmpty(filePath)) {
            mPresenter.getPhotoDetail(filePath);
        }
    }

    private void initRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AlbumDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateView(List<PhotoBean> photoList) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        mAdapter.setPhotoList(photoList);
    }
}
