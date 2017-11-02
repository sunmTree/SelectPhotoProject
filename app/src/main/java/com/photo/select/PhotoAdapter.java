package com.photo.select;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.photo.AppConfig;
import com.photo.bean.AlbumBean;
import com.photo.model.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2017/11/2.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private static final String TAG = "PhotoAdapter";
    private static final boolean DEBUG = AppConfig.DEBUG;

    private List<AlbumBean> photoList;
    private Context mContext;
    private OnItemClickListener mListener;

    public PhotoAdapter(Context context) {
        mContext = context;
        photoList = new ArrayList<>();
    }

    public List<AlbumBean> getData() {
        return photoList;
    }

    public void addAll(List<AlbumBean> list) {
        if (!photoList.isEmpty()) {
            photoList.clear();
        }
        photoList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.album_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AlbumBean albumBean = photoList.get(position);
        if (DEBUG) {
            Log.d(TAG, "position " + position);
        }
        holder.mContent.setText(albumBean.getAlbumPath());
        holder.mTitle.setText(albumBean.getAlbumName());
        Glide.with(mContext)
                .load(albumBean.getFirstImgPath())
                .into(holder.mImage);

        if (mListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return photoList == null ? 0 : photoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mTitle;
        private TextView mContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.album_item_img);
            mTitle = (TextView) itemView.findViewById(R.id.album_item_title);
            mContent = (TextView) itemView.findViewById(R.id.album_item_content);
        }
    }

}
