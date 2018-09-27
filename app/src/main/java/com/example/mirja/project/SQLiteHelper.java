package com.example.mirja.project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory,
                        int version) {




        super(context, name, factory, version);
    }
    public void queryData(String sql){
        SQLiteDatabase database= getWritableDatabase();
        database.execSQL(sql);
    }
    //insert data
    public void insertData(String name,String age,String phone,byte[] image){
        SQLiteDatabase database= getWritableDatabase();
        //query to insert data into database table
        String sql="INSERT INTO RECORD VALUES(NULL,?,?,?,?)";
        SQLiteStatement statement= database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1,name);
        statement.bindString(2,age);
        statement.bindString(1,phone);
        statement.bindBlob(4,image);
        statement.executeInsert();

    }
    //update Data
    public void updateData(String name,String age,String phone,byte[] image,int id){
        SQLiteDatabase database=getWritableDatabase();
        String sql="UPDATE RECORD SET name=?, age=?, phone=?, image=? WHERE id=?";
        SQLiteStatement statement=database.compileStatement(sql);
        statement.bindString(1,name);
        statement.bindString(2,age);
        statement.bindString(1,phone);
        statement.bindBlob(4,image);
        statement.bindDouble(5,(double)id);
        statement.execute();
        database.close();
    }
    //delete Data
    public void deleteData(int id){
        SQLiteDatabase database = getWritableDatabase();
        String sql= "DELETE FROM RECORD WHERE id=?";
        SQLiteStatement statement= database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1,(double)id);
        statement.execute();
        database.close();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database =getReadableDatabase();
        return database.rawQuery(sql,null);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
