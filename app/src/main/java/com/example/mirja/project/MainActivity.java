package com.example.mirja.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    EditText mEdtName;
    EditText mEdtAge;
    EditText mEdtPhone;
    Button mBtnAdd;
    Button mBtnList;
    ImageView mImageView;
    final int REQUEST_CODE_GALLERY=999;

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("new Record");

        mEdtName=findViewById(R.id.edtName);
        mEdtAge=findViewById(R.id.edtAge);
        mEdtPhone=findViewById(R.id.edtPhone);
        mBtnAdd=findViewById(R.id.btnAdd);
        mBtnList=findViewById(R.id.btnList);
        mImageView=findViewById(R.id.imageView);
        //creating database
        mSQLiteHelper= new SQLiteHelper(this,"RECORDDB.sqlite",null,1);
        //creating table in RECORD
        mSQLiteHelper.queryData
                ("CREATE TABLE IF NOT EXISTS RECORD(id INTEGER PRIMARY KEY " +
                        "AUTOINCREMENT, name VARCHAR, age VARCHAR, phone VARCHAR, " +
                        "image BLOB)");



        //for taking a photo by clicking on image add sign
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //taking permission to excess image galery
                //run time permission
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY

                );


            }
        });

        //for adding new member
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSQLiteHelper.insertData(
                            mEdtName.getText().toString().trim(),
                            mEdtAge.getText().toString().trim(),
                            mEdtPhone.getText().toString().trim(),
                           imageViewToByte(mImageView)
                    );
                    Toast.makeText(MainActivity.this,
                            "add successfully",Toast.LENGTH_SHORT).show();
                    //reset view
                    mEdtName.setText("");
                    mEdtAge.setText("");
                    mEdtPhone.setText("");
                    mImageView.setImageResource(R.drawable.addphoto);

                }
                catch (Exception e){
                    e.printStackTrace();

                }


            }
        });


        // for seeing existing members
        mBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,
                        RecordListActivity.class));

            }
        });
    }

   public static byte[] imageViewToByte(ImageView image) {
       Bitmap bitmap=((BitmapDrawable)image.getDrawable()).getBitmap();
       ByteArrayOutputStream stream =new ByteArrayOutputStream();
       bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
       byte[] byteArray = stream.toByteArray();
       return byteArray;

   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_CODE_GALLERY){
            if(grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(this,"donot have permission to access",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==REQUEST_CODE_GALLERY && resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            CropImage.activity(imageUri).
                    setGuidelines(CropImageView.Guidelines.ON).
                    setAspectRatio(1,1)
            .start(this);
        }
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri resultUri =result.getUri();
                //set actual result image in image view
                mImageView.setImageURI(resultUri);

            }
            else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error=result.getError();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
