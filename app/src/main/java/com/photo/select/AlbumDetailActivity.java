package com.photo.select;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.photo.AppConfig;
import com.photo.bean.PhotoBean;
import com.photo.encode.MediaUtils;
import com.photo.model.DetailPhotoPresenter;
import com.photo.model.IUpdateView;
import com.photo.model.OnItemClickListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDetailActivity extends AppCompatActivity implements IUpdateView<PhotoBean>,OnItemClickListener {
    private static final String TAG = "AlbumDetailActivity";
    private static final boolean DEBUG = AppConfig.DEBUG;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private AlbumDetailAdapter mAdapter;
    private DetailPhotoPresenter mPresenter;

    private String saveImagePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "mv";
    private String saveVideoPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "mv";
    //视频名称
    private String videoName = System.currentTimeMillis() + "image2mv.mp4";
    //生成的无音轨视频路径
    String videoPath = saveVideoPath + File.separator + videoName;

    private List<PhotoBean> mSelectedList = new ArrayList<>();

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

        File file = new File(saveImagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save_create) {
            new Thread(){
                @Override
                public void run() {
                    createVideo();
                }
            }.start();
        }
        return true;
    }

    private void createVideo() {
        //读取本地处理后的图片
        ArrayList<File> frames = new ArrayList<>();
        File file;
        for (int i = 0; i < mSelectedList.size(); i++) {
             file = new File(mSelectedList.get(i).getFilePath());
            frames.add(file);
        }
        File fileVideo = new File(saveVideoPath, videoName);
        if (!fileVideo.exists()) {
            try {
                fileVideo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            MediaUtils encodeDecoder = new MediaUtils(fileVideo, frames, this);
            encodeDecoder.startEncodeAndDecoder(720, 720, 720 * 720, saveImagePath);
//            combineVideo();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private void initRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AlbumDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
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

    @Override
    public void onItemClick(View view, int position) {
        PhotoBean photoBean = mAdapter.getDatas().get(position);
        int index = mSelectedList.indexOf(photoBean);
        if (index != -1) {
            mSelectedList.remove(index);
        } else {
            mSelectedList.add(photoBean);
        }
    }
}
