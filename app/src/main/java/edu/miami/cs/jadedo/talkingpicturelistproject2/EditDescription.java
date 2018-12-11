package edu.miami.cs.jadedo.talkingpicturelistproject2;

import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.arch.persistence.room.Room;
import android.widget.Toast;
import android.util.Log;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.widget.EditText;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditDescription extends AppCompatActivity {

    private static final String DATABASE_NAME = "ImagesWithDescription.db";
    private DataRoomDB imagesWithDescriptionDB;
    private String imageUriStringHere;
    private EditText enterDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_description);

        // Creating the persistent DB
        imagesWithDescriptionDB = Room.databaseBuilder(getApplicationContext(), DataRoomDB.class, DATABASE_NAME).allowMainThreadQueries().build();

        imageUriStringHere = this.getIntent().getStringExtra("edu.miami.cs.jadedo.talkingpicturelistproject2.image_uri_string");
        setImage();

        enterDescription = findViewById(R.id.enter_description);

        setDescription();
    }

    public void myClickHandler(View view){
        Intent returnIntent;
        DataRoomEntity imageData;

        switch (view.getId()){
            case R.id.btn_record:
                Toast.makeText(this, "This version of the app does not support Record",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_stop:
                finish();
                break;
            case R.id.btn_clear:
                enterDescription.setText("");
                break;
            case R.id.btn_save:
                imageData = imagesWithDescriptionDB.daoAccess().getImageByImageUri(imageUriStringHere);
                imageData.setImageDescription(enterDescription.getText().toString());
                imagesWithDescriptionDB.daoAccess().updateImage(imageData);
                returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
                break;
            default:
                break;
        }
    }

    public void setImage(){
        ImageView fullImage;

        fullImage = findViewById(R.id.full_image);
        fullImage.setImageURI(Uri.parse(imageUriStringHere));

    }

    public void setDescription(){
        String descriptionText;

        descriptionText = imagesWithDescriptionDB.daoAccess().getImageByImageUri(imageUriStringHere).getImageDescription();
        enterDescription.setText(descriptionText);
    }
}
