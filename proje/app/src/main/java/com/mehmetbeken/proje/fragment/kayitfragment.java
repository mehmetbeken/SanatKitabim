package com.mehmetbeken.proje.fragment;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mehmetbeken.proje.R;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Permission;

import static android.app.Activity.RESULT_OK;


public class kayitfragment extends Fragment {

    ImageView imageView;
    Button button;
    EditText yearText, artnameText, painternameText;
    Bitmap selectedimage;
    String info="";
    SQLiteDatabase database;

    public kayitfragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kayitfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        yearText = view.findViewById(R.id.yearText);
        painternameText = view.findViewById(R.id.painternameText);
        artnameText = view.findViewById(R.id.artnameText);
        imageView = view.findViewById(R.id.imageView);
        button = view.findViewById(R.id.button);

        database = getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);


        if (getArguments() != null) {
            info = kayitfragmentArgs.fromBundle(getArguments()).getInfo();
        } else {
            info = "new";
        }
        System.out.println(info);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save(view);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectimage(view);
            }
        });

        if (info.matches("new")){

            artnameText.setText("");
            painternameText.setText("");
            yearText.setText("");
            button.setVisibility(View.VISIBLE);


            Bitmap selectedImage=BitmapFactory.decodeResource(getContext().getResources(),R.drawable.selectimage);
            imageView.setImageBitmap(selectedImage);
        }else {
            int artId=kayitfragmentArgs.fromBundle(getArguments()).getArtId();
            button.setVisibility(View.INVISIBLE);

            try {

              Cursor cursor=database.rawQuery("SELECT*FROM arts WHERE id=?",new String[]{String.valueOf(artId)});

              int artNameIx=cursor.getColumnIndex("artname");
              int painterNameIx=cursor.getColumnIndex("paintername");
              int yearIx=cursor.getColumnIndex("year");
              int imageIx=cursor.getColumnIndex("image");


              while (cursor.moveToNext()){

                  artnameText.setText(cursor.getString(artNameIx));
                  painternameText.setText(cursor.getString(painterNameIx));
                  yearText.setText(cursor.getString(yearIx));

                  byte[] bytes=cursor.getBlob(imageIx);
                  Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                  imageView.setImageBitmap(bitmap);



              }
cursor.close();





            }catch (Exception e){

            }







        }



























    }


    //Resim Seçme ve İzin

    public void selectimage(View view) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            Uri imageData = data.getData();


            try {

                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), imageData);
                    selectedimage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedimage);


                } else {

                    selectedimage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedimage);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        super.onActivityResult(requestCode, resultCode, data);
    }
    //Kaydetme

    public void Save(View view) {

        NavDirections action = kayitfragmentDirections.actionKayitfragmentToListefragment();
        Navigation.findNavController(view).navigate(action);



        String artName=artnameText.getText().toString();
        String painterName=painternameText.getText().toString();
        String year=yearText.getText().toString();

        Bitmap smallimage=makeSmallerImage(selectedimage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallimage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();


        try {
            SQLiteDatabase database=getActivity().openOrCreateDatabase("Arts",Context.MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB)");

            String sqlString="INSERT INTO arts(artname,paintername,year,image) VALUES (?,?,?,?)";

            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,artName);
            sqLiteStatement.bindString(2,painterName);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();


        }catch (Exception e){


        }









    }
    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }

}