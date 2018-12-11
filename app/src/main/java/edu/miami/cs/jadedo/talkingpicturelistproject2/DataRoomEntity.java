package edu.miami.cs.jadedo.talkingpicturelistproject2;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import android.net.Uri;

@Entity (tableName = "ImagesWithDescription")

public class DataRoomEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "image_id")
    private int imageId;
    @ColumnInfo(name = "image_uri")
    private String imageUri;
    @ColumnInfo(name = "image_description")
    private String imageDescription;

    public DataRoomEntity(){
    }

    public int getId(){
        return (id);
    }

    public int getImageId(){
        return (imageId);
    }

    public String getImageUri(){
        return (imageUri);
    }

    public String getImageDescription(){
        return (imageDescription);
    }

    public void setId(int newId) {
        id = newId;
    }

    public void setImageId(int newImageId){
        imageId = newImageId;
    }

    public void setImageUri(String newImageUri){
        imageUri = newImageUri;
    }

    public void setImageDescription(String newImageDescription){
        imageDescription = newImageDescription;
    }
}
