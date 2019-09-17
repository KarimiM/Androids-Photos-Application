package com.example.justinmay.photos;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class DataStorage {

    /**
     * The FILE_NAME
     */
    private static final String FILE_NAME = "userdata";

    /**
     * Saves the albums.
     * @param albums The albums to save
     * @param app The context of the app.
     */
    public static void saveAlbums(ArrayList<Album> albums, Context app) {
        try {
            FileOutputStream file = app.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream stream = new ObjectOutputStream(file);
            stream.writeInt(albums.size());
            for (Album a : albums) {
                stream.writeObject(a);
            }
            stream.close();
            file.close();
        } catch(IOException fe) {
            fe.printStackTrace();
        }
    }

    /**
     * Parses the albums.
     * @param albums The albums.
     * @param app The app.
     */
    public static void parseAlbums(ArrayList<Album> albums, Context app) {
        try {
            FileInputStream file = app.openFileInput(FILE_NAME);
            ObjectInputStream stream = new ObjectInputStream(file);
            int length = stream.readInt();
            for (int i = 0; i < length; i++) {
                Album add = (Album) stream.readObject();
                albums.add(add);
            }
            stream.close();
            file.close();
        } catch(Exception io) {
        }
    }
}
