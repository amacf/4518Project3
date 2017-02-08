package org.amcafee.project3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.amcafee.project3.ActivityDatabaseSchema.ActivityTable;

public class ActivityBaseHelper extends SQLiteOpenHelper{
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "activityBase.db";

    public ActivityBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + DATABASE_NAME + "(" +
                " _id integer primary key autoincrement, " +
                ActivityTable.Cols.UUID + ", " +
                ActivityTable.Cols.TYPE + ", " +
                ActivityTable.Cols.TIME +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
