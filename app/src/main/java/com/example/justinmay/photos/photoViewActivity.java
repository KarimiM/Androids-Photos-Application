package com.example.justinmay.photos;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class photoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_view);
        
        //refresh screen
        refreshScreen();

        //references to buttons
        ImageView mainphoto = findViewById(R.id.mainPhoto);
        mainphoto.setOnTouchListener(new OnSwipeTouchListener(this){

            @Override
            public void onSwipeLeft(){
                //increment
                int size = HomeActivity.currentAlbum.getPhotos().size();
                int index = -1;
                for (int i = 0; i < size; i++) {
                    Photo p = HomeActivity.currentAlbum.getPhotos().get(i);
                    if (p == HomeActivity.currentPhoto) {
                        if (i == size - 1) {
                            index = 0;
                        } else {
                            index = i + 1;
                        }
                        break;
                    }
                }
                HomeActivity.currentPhoto = HomeActivity.currentAlbum.getPhotos().get(index);
                refreshScreen();

            }

            @Override
            public void onSwipeRight(){
                //decrement
                int size = HomeActivity.currentAlbum.getPhotos().size();
                int index = -1;
                for (int i = 0; i < size; i++) {
                   Photo p = HomeActivity.currentAlbum.getPhotos().get(i);
                    if (p == HomeActivity.currentPhoto) {
                        if (i == 0) {
                            index = size - 1;
                        } else {
                            index = i - 1;
                        }
                        break;
                    }
                }
                HomeActivity.currentPhoto = HomeActivity.currentAlbum.getPhotos().get(index);
                refreshScreen();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScreen();
    }

    private void refreshScreen(){
        //referencing things
        ImageView mainPhoto = findViewById(R.id.mainPhoto);
        EditText location = findViewById(R.id.locationEditText);
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                HomeActivity.currentPhoto.setPhotoLocation(text.toString());
                DataStorage.saveAlbums(HomeActivity.albums, getApplicationContext());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ScrollView people = findViewById(R.id.peopleScrollView);
        people.removeAllViews();

        //create linearlayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //set up photo
        Photo p = HomeActivity.currentPhoto;
        String path = p.getPath();
        Uri uri = Uri.parse(path);
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            mainPhoto.setImageBitmap(bitmap);

        } catch (Exception e)  {
            e.printStackTrace();
        }

        //set up textEdit
        String locationText = p.getLocation();
        location.setText(locationText);

        //set up people
        ArrayList<String> thePeople = p.getPeople();
        for(String person: thePeople){
            Button temp = new Button(this);
            temp.setText(person);
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Context context = com.example.justinmay.photos.photoViewActivity.this;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete this tag?");
                    final Button button = (Button) view;
                    // Set up the buttons
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removePersonTag(button.getText().toString());
                            DataStorage.saveAlbums(HomeActivity.albums, getApplicationContext());
                            refreshScreen();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            });
            linearLayout.addView(temp);
        }

        //add person button
        Button addPerson = new Button(this);
        addPerson.setText("Tag Person");
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        final Context context = com.example.justinmay.photos.photoViewActivity.this;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Person Tag to Add:");

        // Set up the input
        final EditText input = new EditText(context);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                if (addPersonTag(m_Text)) {
                    refreshScreen();
                    DataStorage.saveAlbums(HomeActivity.albums, getApplicationContext());
                } else {
                    HomeActivity.errorToast(context, "This person tag already exists!");
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
        linearLayout.addView(addPerson);

        //set up scroll view
        people.addView(linearLayout);

    }

    boolean addPersonTag(String person) {
        for (String s : HomeActivity.currentPhoto.getPeople()) {
            if (s.equalsIgnoreCase(person)) {
                return false;
            }
        }
        HomeActivity.currentPhoto.getPeople().add(person);
        return true;
    }

    boolean removePersonTag(String person) {
        int index = 0;
        for (int i = 0; i < HomeActivity.currentPhoto.getPeople().size(); i++) {
            if (HomeActivity.currentPhoto.getPeople().get(i).equalsIgnoreCase(person)) {
                index = i;
                break;
            }
        }
        HomeActivity.currentPhoto.getPeople().remove(index);
        return true;
    }
}
