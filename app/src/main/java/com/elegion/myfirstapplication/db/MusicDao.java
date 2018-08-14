package com.elegion.myfirstapplication.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.elegion.myfirstapplication.model.Album;

import java.util.List;

@Dao
public interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    @Query("SELECT * FROM album")
    List<Album> getAlbums();

    //удалить альбом
    @Delete
    void deleteAlbum(Album albumdb);

    //удалить альбом по id
    @Query("DELETE FROM album where id = :albumId")
    void deleteAlbumById(int albumId);
}
