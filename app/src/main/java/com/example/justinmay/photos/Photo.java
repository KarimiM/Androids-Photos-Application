package com.example.justinmay.photos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Photo implements Serializable {

    /**
     *The serial Version UID
     */
    private static final long serialVersionUID = 2250112614096569637L;

    /**
     * The path to this photo.
     */
    private String path;

    /**
     *  The photo location.
     */
    private String location = "";

    /**
     * The people in this photo.
     */
    private ArrayList<String> people;

    /**
     * Constructs a new {@code Album} object.
     * @param path the Path.
     */
    public Photo(String path) {
        this.path = path;
        this.people = new ArrayList<>();
    }

    /**
     * Gets the file path to this photo.
     * @return the path of this photo
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the photo's location.
     * @param location the location for this photo.
     */
    public void setPhotoLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the location of the photo.
     * @return the location of this photo
     */
    public String getLocation() {
        return location;
    }

    /**
     * Adds a person.
     * @param person The person
     * @return True if added succesfully.
     */
    public boolean addPerson(String person) {
        for (String s  : people) {
            if (s.equalsIgnoreCase(person)) {
                return false;
            }
        }
        people.add(person);
        return true;
    }

    /**
     * Removes a person from the arraylist.
     * @param person
     * @return
     */
    public boolean removePerson(String person) {
        String remove = null;
        for (String s : people) {
            if (s.equalsIgnoreCase(person)) {
                remove = s;
                break;
            }
        }
        if (remove == null) {
            return false;
        }
        people.remove(remove);
        return true;
    }

    /**
     *
     * @return tHe people.
     */
    public ArrayList<String> getPeople() { return people; };
}
