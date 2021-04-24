package com.example.welcome;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class SettingsActivity extends AppCompatActivity {

    private ImageView imageView2;
    private ImageView imageView;

    private DBHelper dbHelper;

    private static final int REQUEST_GALLERY = 1;
    private static final int RESULT_LOAD_IMAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.icon);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        getImageFromDB();


    }

    public void onClickChangeAvatarButton(View view) {
        Button button = view.findViewById(R.id.change_avatar_button);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        getImage();

    }

    private void getImageFromDB(){
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_IMAGE, null,null,null, null, null, null);
        if (cursor.moveToFirst()){
            byte[] image = cursor.getBlob(1);
            Bitmap newImage = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView2 = findViewById(R.id.imageView2);
            Bitmap bMapScaled = Bitmap.createScaledBitmap(newImage, 500, 500, false);
            imageView2.setImageBitmap(bMapScaled);
        }
    }


    private void getImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgSaveInDB(selectedImage);
                Bitmap bMapScaled = Bitmap.createScaledBitmap(selectedImage, 500, 500, false);
                imageView2.setImageBitmap(bMapScaled);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void imgSaveInDB(Bitmap selectedImage){
        dbHelper = new DBHelper(this);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] img = bos.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IMAGE, img);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_IMAGE, null, null, null, null, null, null);

        if (cursor.getCount() > 0){
            System.out.println("TRUEEE");
            database.execSQL("drop table if exists " + DBHelper.TABLE_IMAGE);
            database.execSQL("create table " + DBHelper.TABLE_IMAGE + "(" + DBHelper.KEY_ID + " integer primary key," + DBHelper.KEY_IMAGE + " blob" + ")");
            database.insert(DBHelper.TABLE_IMAGE, null, contentValues);
        } else{
            System.out.println("TRU2");
            database.insert(DBHelper.TABLE_IMAGE, null, contentValues);
        }

    }
}

