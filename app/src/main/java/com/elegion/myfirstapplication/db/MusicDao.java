package com.elegion.myfirstapplication.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.elegion.myfirstapplication.model.Album;
import com.elegion.myfirstapplication.model.AlbumSong;
import com.elegion.myfirstapplication.model.Comment;
import com.elegion.myfirstapplication.model.Song;

import java.util.List;

@Dao
public interface MusicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbum(Album albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long setLinkAlbumSongs(AlbumSong linkAlbumSongs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComments(List<Comment> comments);

    //albums

    @Query("SELECT * FROM album")
    List<Album> getAlbums();

    @Query("select * from album where id = :albumId")
    Album getAlbumWithId(int albumId);

    //удалить альбом
    @Delete
    void deleteAlbum(Album album);

    //удалить альбом по id
    @Query("DELETE FROM album where id = :albumId")
    void deleteAlbumById(int albumId);

    //songs

    @Query("SELECT * FROM song")
    List<Song> getSongs();

    //удалить песню
    @Delete
    void deleteSong(Song song);

    //удалить песню по id
    @Query("DELETE FROM song where id = :songId")
    int deleteSongById(int songId);

    //получить список песен переданного id альбома
    @Query("SELECT * FROM song sn, albumsong asg WHERE sn.id = asg.song_id and asg.album_id = :albumId")
    List<Song> getSongsFromAlbum(int albumId);

    //comments

    @Query("SELECT * FROM comment")
    List<Comment> getComments();

    //удалить песню
    @Delete
    void deleteComment(Comment comment);

    //удалить песню по id
    @Query("DELETE FROM comment where id = :commentId")
    int deleteCommentById(int commentId);

    //получить список комментариев переданного id альбома
    @Query("SELECT * FROM comment where album_id = :albumId")
    List<Comment> getAlbumComments(int albumId);

}
