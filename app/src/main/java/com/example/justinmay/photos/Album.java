package com.example.justinmay.photos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Album implements Serializable {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = 2250112614096569637L;

    /**
     * The name of the album.
     */
    private String albumName;

    /**
     * The photos in this album
     */
    private ArrayList<Photo> photos;

    /**
     * The start date.
     */
    private Date start;

    /**
     * The end date.
     */
    private Date end;

    /**
     * Constructs a new {@code Album} object.
     * @param albumName The album name.
     */
    public Album(String albumName) {
        this.setAlbumName(albumName);
        this.photos = new ArrayList<>();
    }

    /**
     * Gets the album's name.
     * @return the albumName
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * Sets the album's name.
     * @param albumName the albumName to set
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     * Gets the pictures from this album
     * @return ArrayList of photos for this album, null if none
     */
    public ArrayList<Photo> getPhotos(){
        return photos;
    }

    /**
     * Adds a photo to this album
     * @param a is a photo to add to this album
     */
    public void addPhoto(Photo a) {
        this.photos.add(a);
    }

    /**
     * Removes the photo.
     * @param remove The photo to remove.
     */
    public void remove(Photo remove) {
        this.photos.remove(remove);
    }


    /**
     * Gets the start date.
     * @return The start date.
     */
    public String getStartDate() {
        if (start == null) {
            return "N/A";
        }
        String[] data = start.toString().split(" ");
        return data[1] + " " + data[2] + ", " + data[5];
    }

    /**
     * Gets the end date.
     * @return The end date.
     */
    public String getEndDate() {
        if (end == null) {
            return "N/A";
        }
        String[] data = end.toString().split(" ");
        return data[1] + " " + data[2] + ", " + data[5];
    }

    /**
     * Removes a photo from this album
     * @param a is a photo to remove from this album
     */
    public void removePhoto(String a) {

    }
}