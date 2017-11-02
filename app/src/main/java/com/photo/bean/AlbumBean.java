package com.photo.bean;

/**
 * Created by Admin on 2017/11/2.
 */

public class AlbumBean {

    private String albumName;
    private String albumPath;
    private String firstImgPath;
    private int albumPhotoCount;

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public int getAlbumPhotoCount() {
        return albumPhotoCount;
    }

    public void setAlbumPhotoCount(int albumPhotoCount) {
        this.albumPhotoCount = albumPhotoCount;
    }

    @Override
    public String toString() {
        return "AlbumBean{" +
                "albumName='" + albumName + '\'' +
                ", albumPath='" + albumPath + '\'' +
                ", firstImgPath='" + firstImgPath + '\'' +
                ", albumPhotoCount=" + albumPhotoCount +
                '}';
    }
}
