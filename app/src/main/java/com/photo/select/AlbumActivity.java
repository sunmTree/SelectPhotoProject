package com.photo.select;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.photo.AppConfig;
import com.photo.bean.AlbumBean;
import com.photo.model.IUpdateView;
import com.photo.model.OnItemClickListener;
import com.photo.model.PickPhotoPresenter;

import java.util.Arrays;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements IUpdateView<AlbumBean>,OnItemClickListener {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = "AlbumActivity";

    private static final int REQUEST_PERMISSION = 0x1;
    private static final String EXTRA_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

    private RecyclerView mRecyclerView;
    private PhotoAdapter mAdapter;
    private PickPhotoPresenter mPresenter;
    private ProgressBar mProgressBar;

    private long firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        mProgressBar = (ProgressBar) findViewById(R.id.main_progress);
        initRecycler();
        mPresenter = new PickPhotoPresenter(this, this);
        firstTime = System.currentTimeMillis();
        int permission = ActivityCompat.checkSelfPermission(this, EXTRA_PERMISSION);
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{EXTRA_PERMISSION},
                    REQUEST_PERMISSION);
        } else {
            mPresenter.getAllAlbum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            List<String> permissionList = Arrays.asList(permissions);
            int indexOf = permissionList.indexOf(EXTRA_PERMISSION);
            if (indexOf != -1 && grantResults[indexOf] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.getAllAlbum();
            }
        }
    }

    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PhotoAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void updateView(List<AlbumBean> photoList) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        mAdapter.addAll(photoList);
        long secondTime = System.currentTimeMillis();
        if (DEBUG) {
            Log.d(TAG, "cast time [ " + (secondTime - firstTime) + " ] ms");
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        AlbumBean albumBean = mAdapter.getData().get(position);
        Intent intent = new Intent(AlbumActivity.this, AlbumDetailActivity.class);
        intent.putExtra("FilePath", albumBean.getAlbumPath());
        startActivity(intent);
    }

}
