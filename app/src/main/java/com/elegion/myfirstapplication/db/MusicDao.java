package com.elegion.myfirstapplication.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.elegion.myfirstapplication.model.Album;
import com.elegion.myfirstapplication.model.Song;

import java.util.List;

@Dao
public interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);

    //albums

    @Query("SELECT * FROM album")
    List<Album> getAlbums();

    //удалить альбом
    @Delete
    void deleteAlbum(Album albumdb);

    //удалить альбом по id
    @Query("DELETE FROM album where id = :albumId")
    void deleteAlbumById(int albumId);

    //songs

    @Query("select * from song")
    List<Song> getSongs();

    //удалить песню
    @Delete
    void deleteSong(Song song);

    //удалить песню по id
    @Query("DELETE FROM song where id = :songId")
    int deleteSongById(int songId);

}
