package com.nishthapandya.imageeditor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class startUpActivity extends AppCompatActivity {

    private static final String TAG = "StartUpActivity";
    private static final int MY_REQUEST_WRITE_CAMERA = 10;
    private static final int CAPTURE_CAMERA = 11;

    private static final int MY_REQUEST_READ_WRITE_GALLERY = 12;
    private static final int MY_REQUEST_GALLERY = 13;

    private Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);


        Button mCameraBtn = findViewById(R.id.camera_btn);
        Button mGalleryBtn = findViewById(R.id.gallery_btn);

        //when camera button clicked
        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking runtime permission for camera
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        //permission denied, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                        //showing popup for runtime permission
                        requestPermissions(permission,MY_REQUEST_WRITE_CAMERA);
                    }
                    else{
                        openCamera();
                    }
                }
                else{
                    openCamera();
                }
            }
        });

        //when Gallery button is clicked
        mGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                        //permission is not granted, request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, MY_REQUEST_READ_WRITE_GALLERY);

                    }
                    else{
                        openGallery();
                    }
                }
                else{
                    openGallery();
                }
            }
        });

    }

    private void openGallery() {

        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MY_REQUEST_GALLERY);

        Log.v(TAG, "gallery intent");
        Log.v(TAG, "editActivity intent");

        /*Intent newIntent = new Intent(this, EditImage.class);
        newIntent.putExtra("IMAGE_URI", image_uri);*/
    }


    private void openCamera() {

        Log.v(TAG, "image uri not set");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Log.v(TAG, "image uri set");
        Log.v(TAG, "editActivity intent");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, CAPTURE_CAMERA);

        Log.v(TAG, "camera intent");

        /*Intent newIntent = new Intent(this, EditImage.class);
        newIntent.putExtra("IMAGE_URI", image_uri);*/

    }

    /*
    *    handling permission result
    *    this method is called when the user presses Allow or Deny from the Permission Request Popup
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case MY_REQUEST_WRITE_CAMERA:{
                if(     grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(this, "Granted Permission", Toast.LENGTH_SHORT).show();
                    openCamera();
                    break;
                }
                else{
                    Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show();
                }
            }

            case MY_REQUEST_READ_WRITE_GALLERY:{
                if(     grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(this, "Granted Permission", Toast.LENGTH_SHORT).show();
                    openGallery();
                    break;
                }
                else{
                    Toast.makeText(this, "Permission Denied !", Toast.LENGTH_SHORT).show();
                }
            }
                /*
                {

                if(     grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Granted Permission ! ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Permission Denied ! ", Toast.LENGTH_SHORT).show();
                }

            }*/
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.v(TAG, "Inside the onActivityResult(), imageView not set");

        if(resultCode != RESULT_OK){
            Log.v(TAG, "photo not got");
            return;
        }

        switch(requestCode){
            case CAPTURE_CAMERA:
                break;

            case MY_REQUEST_GALLERY: {
                image_uri = data.getData();
                break;
            }
        }
        Log.v(TAG, "image Uri displayed");
        Intent newIntent = new Intent(this, EditImage.class);
        newIntent.putExtra("KEY", image_uri.toString());
        startActivity(newIntent);

        super.onActivityResult(requestCode, resultCode, data);
    }
}