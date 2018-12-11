package edu.miami.cs.jadedo.talkingpicturelistproject2;

import android.arch.persistence.room.PrimaryKey;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.View;
import android.arch.persistence.room.Room;
import android.widget.Toast;
import android.util.Log;
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Activity;
import android.speech.tts.TextToSpeech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyTalkingPictureList extends AppCompatActivity implements AdapterView.OnItemClickListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, AdapterView.OnItemLongClickListener, TextToSpeech.OnInitListener, FullImageDialogFragment.DialogOver {

    private static final String DATABASE_NAME = "ImagesWithDescription.db";
    private DataRoomDB imagesWithDescriptionDB;
    private Cursor imagesCursor;
    private Cursor audioCursor;
    private List<DataRoomEntity> dbEntities;
    private ListView theList;
    public MediaPlayer myPlayer = null;
    private final int ACTIVITY_EDIT_DESCRIPTION = 1;
    private TextToSpeech mySpeaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String[] queryFields = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_talking_picture_list);

        // Creating the persistent DB
        imagesWithDescriptionDB = Room.databaseBuilder(getApplicationContext(), DataRoomDB.class, DATABASE_NAME).allowMainThreadQueries().build();

        // Querying the images
        imagesCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, queryFields, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);

        if (imagesCursor != null && imagesCursor.getCount() > 0) {
            updateImageDBFromContent();
            fillList();
            startPlayingMusic();

        } else {
            Toast.makeText(this,"Cannot query MediaStore for audio",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        mySpeaker = new TextToSpeech(this,this);
    }


    // Creating the image database
    private void updateImageDBFromContent() {

        DataRoomEntity imageData;
        int imageId;
        int imageDBId;
        boolean imageFound = false;

        imagesCursor.moveToFirst();
        do {
            imageId = imagesCursor.getInt(imagesCursor.getColumnIndex(MediaStore.Images.Media._ID));
            if (imagesWithDescriptionDB.daoAccess().getImageByImageId(imageId) == null) {
                imageData = new DataRoomEntity();
                imageData.setImageId(imageId);
                imageData.setImageUri(imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                imageData.setImageDescription("No description yet");

                imagesWithDescriptionDB.daoAccess().addImage(imageData);
            }
        } while (imagesCursor.moveToNext());

        dbEntities = imagesWithDescriptionDB.daoAccess().fetchAllImages();

        for (DataRoomEntity oneRow: dbEntities){

            imagesCursor.moveToFirst();
            while (imagesCursor.moveToNext() && !imageFound) {
                imageFound = (oneRow.getImageId() == imagesCursor.getInt(imagesCursor.getColumnIndex(MediaStore.Images.Media._ID)));
            }
            if (!imageFound) {
                imagesWithDescriptionDB.daoAccess().deleteImage(oneRow);
            } else {
                imageFound = false;
            }
        }
    }

    // Mapping the content of the Database to the ListView
    private void fillList() {

        String[] displayFields = {
                "image_uri",
                "image_description"
        };

        int[] displayViews = {
                R.id.thumbnail,
                R.id.description
        };

        ListView theList;
        SimpleAdapter listAdapter;

        theList = findViewById(R.id.the_list);
        listAdapter = new SimpleAdapter(this, fetchAllImages(), R.layout.list_item, displayFields, displayViews);
        theList.setAdapter(listAdapter);
        theList.setOnItemClickListener(this);
        theList.setOnItemLongClickListener(this);
    }

    // Retrieve all images from the database
    private ArrayList<HashMap<String,Object>> fetchAllImages() {
        HashMap<String, Object> oneItem;
        ArrayList<HashMap<String, Object>> listItems;

        dbEntities = imagesWithDescriptionDB.daoAccess().fetchAllImages();

        listItems = new ArrayList<>();
        for (DataRoomEntity oneRow: dbEntities) {
            oneItem = new HashMap<>();
            oneItem.put("image_uri", Uri.parse(oneRow.getImageUri()));
            oneItem.put("image_description", oneRow.getImageDescription());
            listItems.add(oneItem);
        }
        return (listItems);
    }

    public void startPlayingMusic(){

        int totalSongNum;
        int randomSong;
        String audioFilename;

        String[] queryFields = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, queryFields, null, null, MediaStore.Audio.Media.TITLE + " ASC" );
        totalSongNum = audioCursor.getCount();
        Log.i("SONG NUM", "" + totalSongNum);
        if ( audioCursor.moveToFirst() && totalSongNum > 0) {
            randomSong = (int) (Math.random() * totalSongNum);
            audioCursor.moveToPosition(randomSong);
            audioFilename = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            try {
                myPlayer = new MediaPlayer();
                myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                myPlayer.setDataSource(audioFilename);
                myPlayer.setLooping(true);
                myPlayer.setOnCompletionListener(this);
                myPlayer.setOnErrorListener(this);
                myPlayer.prepare();
                myPlayer.start();
                Toast.makeText(this, "Playing " + audioFilename,Toast.LENGTH_SHORT).show();
            } catch (IOException e){
                Toast.makeText(this, "Cannot play random song",Toast.LENGTH_LONG).show();
            }

        } else {
            myPlayer = MediaPlayer.create(this, R.raw.chin_chin_choo);
            myPlayer.setLooping(true);
            myPlayer.setOnCompletionListener(this);
            myPlayer.setOnErrorListener(this);
            myPlayer.start();
        }
    }

    // Dialog shows up and Description is spoken when a row item is clicked
    public void onItemClick(AdapterView<?> parent,View view,int position,
                            long rowId) {
        String imageUriString;
        String whatToSay;

        imageUriString = dbEntities.get(position).getImageUri();

        FullImageDialogFragment fullImageDialogFragment = new FullImageDialogFragment();
        fullImageDialogFragment.show(getFragmentManager(), imageUriString);
        myPlayer.pause();

        whatToSay = dbEntities.get(position).getImageDescription();
        if (whatToSay != null && whatToSay.length() > 0) {
            mySpeaker.speak(whatToSay, TextToSpeech.QUEUE_ADD, null, "WHAT_I_SAID");
        } else {
            Toast.makeText(this,"Nothing to say",Toast.LENGTH_SHORT).show();
        }
    }

    // Opens a second activity to edit the description upon a long click on one of the views
    public boolean onItemLongClick (AdapterView<?> parent,View view,int position,
                                    long rowId){
        String imageUriString;

        Intent editDescription = new Intent();

        myPlayer.pause();

        imageUriString = dbEntities.get(position).getImageUri();

        editDescription.setClassName("edu.miami.cs.jadedo.talkingpicturelistproject2", "edu.miami.cs.jadedo.talkingpicturelistproject2.EditDescription");
        editDescription.putExtra("edu.miami.cs.jadedo.talkingpicturelistproject2.image_uri_string", imageUriString);
        startActivityForResult(editDescription, ACTIVITY_EDIT_DESCRIPTION);

        return (true);
    }

    public void onPrepared(MediaPlayer mediaPlayer) {

        mediaPlayer.start();
    }
    //-----------------------------------------------------------------------------
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
    //-----------------------------------------------------------------------------
    public boolean onError(MediaPlayer mediaPlayer,int whatHappened,int extra) {

        mediaPlayer.stop();
        mediaPlayer.release();
        myPlayer = null;
        return(true);
    }

    // Activity Result for EditDescription Activity
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_EDIT_DESCRIPTION:
                if (resultCode == Activity.RESULT_OK) {
                    fillList();
                } else {
                    Toast.makeText(this, "Oops someone is backing out",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    // Checking the initialization status of mySpeaker (TextToSpeech)
    public void onInit(int status){
        if (status == TextToSpeech.SUCCESS) {
           // Toast.makeText(this, "Speaking the description",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"You need to install TextToSpeech",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        myPlayer.release();
        imagesCursor.close();
    }

    @Override
    public void onResume(){
        super.onResume();
        myPlayer.start();
    }

    // After Dialog is dismissed, stop TTS and resume music
    public void stopTalkingAndPlayMusic(){
        mySpeaker.stop();
        myPlayer.start();
    }
}
