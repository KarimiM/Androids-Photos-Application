package com.example.justinmay.photos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * The current album.
     */
    public static Album currentAlbum = null;
    public static Photo currentPhoto = null;

   public static ArrayList<Album> albums = new ArrayList<Album>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set up screen
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        DataStorage.parseAlbums(albums, getApplicationContext());
        //example albums, add albums once internet works
        //albums.add(new Album("1234", "bob"));
        //albums.add(new Album("test12345","boo"));
        this.refreshAlbumList();


        //reference buttons
        ImageButton deleteAlbumButton = findViewById(R.id.addPhotoToAlbumButton);
        ImageButton addAlbumButton = findViewById(R.id.addAlbumButton);
        ImageButton renameAlbumButton = findViewById(R.id.renameAlbumButton);

        //add listeners
        addAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = com.example.justinmay.photos.HomeActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Album to Add");

                // Set up the input
                final EditText input = new EditText(context);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (addAlbum(m_Text)) {
                            refreshAlbumList();
                            DataStorage.saveAlbums(albums, getApplicationContext());
                        } else {
                            errorToast(context, "This album already exists!");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        deleteAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = com.example.justinmay.photos.HomeActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Album to Delete");

                // Set up the input
                final EditText input = new EditText(context);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (deleteAlbum(m_Text)) {
                            refreshAlbumList();
                            DataStorage.saveAlbums(albums, getApplicationContext());
                        } else {
                            errorToast(context, "This album doesn't exist!");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        renameAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Context context = com.example.justinmay.photos.HomeActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Rename Album");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText oldName = new EditText(context);
                oldName.setHint("Old Name");
                layout.addView(oldName);

                final EditText newName = new EditText(context);
                newName.setHint("New Name");
                layout.addView(newName);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String one = oldName.getText().toString().trim();
                        String two = newName.getText().toString().trim();
                        if (renameAlbum(one, two)) {
                            refreshAlbumList();
                            DataStorage.saveAlbums(albums, getApplicationContext());
                        } else {
                            errorToast(context, "Error renaming album.");
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });



    }

    public boolean addAlbum(String name) {
        name = name.trim();
        for (Album a :  albums) {
            if (a.getAlbumName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        Album add = new Album(name);
        albums.add(add);
        return true;
    }

    public boolean deleteAlbum(String name) {
        name = name.trim();
        Album remove = null;
        for (Album a : albums) {
            if (a.getAlbumName().equalsIgnoreCase(name)) {
                remove = a;
                break;
            }
        }
        if (remove != null) {
            albums.remove(remove);
            return true;
        }
        return false;
    }

    /**
     * Rename album
     * @param oldName
     * @param newName
     * @return
     */
    public boolean renameAlbum(String oldName, String newName) {
        for (Album a : albums) {
            if (a.getAlbumName().equalsIgnoreCase(newName)) {
                return false;
            }
        }
        for (Album a : albums) {
            if (a.getAlbumName().equalsIgnoreCase(oldName)) {
                a.setAlbumName(newName);
                return true;
            }
        }
        return false;
    }

    /**
     * Refresh album list
     */
    private void refreshAlbumList() {

        ScrollView scrollView = findViewById(R.id.buttonScrollView);
        scrollView.removeAllViews();
        //create linearlayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //loop through array list
        for(int i = 0; i < albums.size(); i++){
            //set up button
            Album a = albums.get(i);
            Button button = new Button(this);
            button.setText(a.getAlbumName());
            button.setId(i); //button ID is same as array index
            button.getBackground().setAlpha(128);
            button.setOnClickListener(this);
            linearLayout.addView(button);
        }

        //add buttons to scroll view
        scrollView.addView(linearLayout);
    }


    @Override
    public void onClick(View view) {
        currentAlbum = albums.get(view.getId());
        Intent intent1 = new Intent(this, PhotoActivity.class);
        startActivity(intent1);
    }

    public static void errorToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }
}
