package com.example.mirja.project;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;
    ImageView imageViewIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Record list");

        mListView = findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        mListView.setAdapter(mAdapter);
        //get all data from database
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String age = cursor.getString(2);
            String phone = cursor.getString(3);
            byte[] image = cursor.getBlob(4);
            //add to list

            mList.add(new Model(id, name, age, phone, image));

        }
        mAdapter.notifyDataSetChanged();
        if (mList.size() == 0) {
            // if there is no records
            Toast.makeText(this, "no record ...", Toast.LENGTH_SHORT).show();
        }
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alart dialog display update or delete
                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);
                dialog.setTitle("Choose an action ");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            //update
                            Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            //show update dialog
                            showDialogUpdate(RecordListActivity.this,arrID.get(position));

                        }
                        if (i == 1) {
                            //delete
                            Cursor c= MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
                            ArrayList<Integer> arrID=new ArrayList<Integer>();
                            while(c.moveToNext()) {
                                arrID.add(c.getInt(0));

                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();

                return true;
            }
        });


    }

    private void showDialogDelete(final int idRecord) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("are u sour to delete?");
        dialogDelete.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this,"delete successfully",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("error",e.getMessage());
                }
                updateRecordList();

            }
        });
        dialogDelete.setNegativeButton("canecl", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(Activity activity, final int position){
         final Dialog dialog= new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");
        imageViewIcon=dialog.findViewById(R.id.imageViewRecord);
        final EditText edtName= dialog.findViewById(R.id.edtName);
        final EditText edtAge=dialog.findViewById(R.id.edtAge);
        final EditText edtPhone=dialog.findViewById(R.id.edtPhone);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        //set width of dialog
        int width= (int)(activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height of dialog
        int height= (int)(activity.getResources().getDisplayMetrics().heightPixels*0.7);
        dialog.getWindow().setLayout(width,height);
        dialog.show();
        // in update dialog click on image icon to update image
        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888

                );


            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    MainActivity.mSQLiteHelper.updateData(
                            edtName.getText().toString().trim(),
                            edtAge.getText().toString().trim(),
                            edtPhone.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewIcon),
                            position
                    );
                   dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Update Successfull",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error){
                    Log.e("Update error",error.getMessage());

                }
                updateRecordList();
            }
        });


    }

    private void updateRecordList() {
        //get data from database
        Cursor cursor=MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while(cursor.moveToNext()){
            int id=cursor.getInt(0);
            String name=cursor.getString(1);
            String age=cursor.getString(2);
            String phone=cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            mList.add(new Model(id,name,age,phone,image));
        }
        mAdapter.notifyDataSetChanged();


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

        if(requestCode==888){
            if(grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent galleryIntent= new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,888);
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

        if(requestCode==888 && resultCode==RESULT_OK){
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
                imageViewIcon.setImageURI(resultUri);

            }
            else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error=result.getError();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
