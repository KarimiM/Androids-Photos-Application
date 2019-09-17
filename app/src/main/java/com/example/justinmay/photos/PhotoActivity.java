package com.example.justinmay.photos;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.IOException;

public class PhotoActivity extends AppCompatActivity{

    private static final int PICK_IMAGE_REQUEST = 1;

    int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set up activity
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo);

        //reference Buttons
        ImageButton addPhoto = findViewById(R.id.addPhotoToAlbumButton);
        ImageButton searchPhoto = findViewById(R.id.searchButton);

        refreshPhotoList();

        //ask for permission
        ActivityCompat.requestPermissions(PhotoActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        searchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = com.example.justinmay.photos.PhotoActivity.this;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Search by tag:");

                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText oldName = new EditText(context);
                oldName.setHint("Location");
                layout.addView(oldName);

                final EditText newName = new EditText(context);
                newName.setHint("Person");
                layout.addView(newName);

                builder.setView(layout);

                // Set up the buttons
                builder.setPositiveButton("Search for both", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String one = oldName.getText().toString().trim();
                        String two = newName.getText().toString().trim();
                        if (one.isEmpty() && two.isEmpty()) {
                            refreshPhotoList();
                        } else {
                            refreshPhotoList(true, one, two, true);
                        }
                    }
                });
                builder.setNegativeButton("Search for either", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String one = oldName.getText().toString().trim();
                        String two = newName.getText().toString().trim();
                        if (one.isEmpty() && two.isEmpty()) {
                            refreshPhotoList();
                        } else {
                            refreshPhotoList(true, one, two, false);
                        }
                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(PhotoActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPhotoList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            getContentResolver().takePersistableUriPermission (uri, Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            String uriString = uri.toString();
            Log.d("masood", "uri: " + uriString);
            Photo newPhoto =  new Photo(uriString);

            HomeActivity.currentAlbum.addPhoto(newPhoto);

            DataStorage.saveAlbums(HomeActivity.albums, getApplicationContext());


            refreshPhotoList();
        }
    }
    @Override
    public void onCreateContextMenu (ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo){
        //Context menu
        index = v.getId();
        menu.setHeaderTitle("Photo Options");
        menu.add(Menu.NONE, 1, Menu.NONE, "Delete");
        menu.add(Menu.NONE, 2, Menu.NONE, "Move");
    }

    @Override
    public boolean onContextItemSelected (MenuItem item) {
        final Photo p = HomeActivity.currentAlbum.getPhotos().get(index);
        if (item.getItemId() == 1) {
            HomeActivity.currentAlbum.getPhotos().remove(p);
            DataStorage.saveAlbums(HomeActivity.albums, getApplicationContext());
            refreshPhotoList();
        } else {
            final Context context = com.example.justinmay.photos.PhotoActivity.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Album to Move to");

            // Set up the input
            final EditText input = new EditText(context);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m_Text = input.getText().toString();
                    if (moveToAlbum(m_Text, p)) {
                        HomeActivity.currentAlbum.getPhotos().remove(p);
                        DataStorage.saveAlbums(HomeActivity.albums, getApplicationContext());
                    } else {
                        HomeActivity.errorToast(context, "This album doesn't exist!");
                    }
                    refreshPhotoList();
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
        return super.onContextItemSelected(item);
    }

    private void refreshPhotoList() {
        refreshPhotoList(false, "", "", false);
    }

    private void refreshPhotoList(boolean filter, String location, String person, boolean and) {
        ScrollView scrollView = findViewById(R.id.photoScrollView);
        scrollView.removeAllViews();

        //create linearlayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //loop through array list
        int i = 0;
        for (Photo p : HomeActivity.currentAlbum.getPhotos()) {
            if (filter) {
                boolean foundPerson = false;
                boolean foundLocation = false;
                if (!person.isEmpty()) {
                    for (String check : p.getPeople()) {
                        if (check.equalsIgnoreCase(person)) {
                            foundPerson  = true;
                            break;
                        }
                    }
                }
                if (!location.isEmpty()) {
                    if (p.getLocation().toLowerCase().contains(location.toLowerCase())) {
                        foundLocation = true;
                    }
                }
                if (and && (!foundLocation && !location.isEmpty() || !foundPerson && !person.isEmpty())) {
                    continue;
                }
                if (!and && !foundLocation && !foundPerson) {
                    continue;
                }
            }
            final Photo temp = p;
            final ImageButton photo = new ImageButton(this);
            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    registerForContextMenu(photo);
                    openContextMenu(photo);
                    return true;
                }
            });
            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity.currentPhoto = temp;
                    Intent intent1 = new Intent(PhotoActivity.this, photoViewActivity.class);
                    startActivity(intent1);
                }
            });
            photo.setBackgroundColor(Color.TRANSPARENT);
            photo.setId(i++);
            String path = p.getPath();
            Uri uri = Uri.parse(path);
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                photo.setImageBitmap(bitmap);
                linearLayout.addView(photo);
            } catch (Exception e)  {
                e.printStackTrace();
            }
        }

        //add buttons to scroll view
        scrollView.addView(linearLayout);
    }

    boolean moveToAlbum(String name, Photo p) {
        for (Album a : HomeActivity.albums) {
            if (a.getAlbumName().equalsIgnoreCase(name)) {
                a.getPhotos().add(p);
                return true;
            }
        }
        return false;
    }

}
