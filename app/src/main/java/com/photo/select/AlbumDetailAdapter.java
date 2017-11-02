package com.photo.select;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photo.AppConfig;
import com.photo.bean.PhotoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/11/2.
 */

public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.ViewHolder> {
    private static final String TAG = "AlbumDetailAdapter";
    private static final boolean DEBUG = AppConfig.DEBUG;

    private Context mContext;
    private List<PhotoBean> mPhotoList;

    public AlbumDetailAdapter(Context context) {
        this.mContext = context;
        mPhotoList = new ArrayList<>();
    }

    public void setPhotoList(List<PhotoBean> list) {
        if (!mPhotoList.isEmpty()) {
            mPhotoList.clear();
        }
        mPhotoList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoBean photoBean = mPhotoList.get(position);
        Glide.with(mContext)
                .load(photoBean.getFilePath())
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.isEmpty() ? 0 : mPhotoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.photo_img);
        }
    }
}
