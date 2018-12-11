package edu.miami.cs.jadedo.talkingpicturelistproject2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import android.net.Uri;

import java.util.List;

@Dao
public interface DataRoomAccess {

    @Query("SELECT * FROM ImagesWithDescription ORDER BY image_id ASC")
    List<DataRoomEntity> fetchAllImages();

    @Query("SELECT * FROM ImagesWithDescription where id LIKE :id")
    DataRoomEntity getImageByDBId(int id);

    @Query("SELECT * FROM ImagesWithDescription where image_id LIKE :id")
    DataRoomEntity getImageByImageId(int id);

    @Query("SELECT * FROM ImagesWithDescription where image_uri LIKE :uri")
    DataRoomEntity getImageByImageUri(String uri);

    @Insert
    void addImage(DataRoomEntity newImage);

    @Delete
    void deleteImage(DataRoomEntity oldImage);

    @Update
    void updateImage(DataRoomEntity oldImage);
}
