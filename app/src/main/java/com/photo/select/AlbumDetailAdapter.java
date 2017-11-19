package com.photo.select;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.photo.AppConfig;
import com.photo.bean.PhotoBean;
import com.photo.model.OnItemClickListener;

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
    private OnItemClickListener mItemClickListener;

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

    public List<PhotoBean> getDatas() {
        return mPhotoList;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PhotoBean photoBean = mPhotoList.get(position);
        Glide.with(mContext)
                .load(photoBean.getFilePath())
                .into(holder.mImage);
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v, position);
                    boolean checked = holder.mRadioButton.isChecked();
                    holder.mRadioButton.setChecked(!checked);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.isEmpty() ? 0 : mPhotoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private RadioButton mRadioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.photo_img);
            mRadioButton = (RadioButton) itemView.findViewById(R.id.photo_selected);
        }
    }
}
