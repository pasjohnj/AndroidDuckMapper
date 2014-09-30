package com.example.cps251_jh7_duckmapper;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DuckHelper extends SQLiteOpenHelper {
	
	private static final String Database_filename="ducks.db";
    private static final int Database_version=1;
    Context context;

    public DuckHelper(Context context) {
        super(context, Database_filename, null, Database_version);
        this.context = context; //so I can call a getResources below!!
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
    	Log.d("MINE: onCreate Update", "in DuckHelper.onCreate");
		
    	InputStream inputStream = context.getResources().openRawResource(R.raw.db);
		Scanner scanner = new Scanner(inputStream);
		
		while(scanner.hasNextLine()){ //I'm creating the database, which is a large text file, from an external text file in res/raw
			String sql = scanner.nextLine();
			db.execSQL(sql);
			//Log.d("SQL Update: " , "updated successfully");
		}
    	
        Log.d("MINE: DATABASE UPDATE", "DATABASE CREATION SUCCESSFUL.");
        
             
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE ducks");
        onCreate(db);        
    }

}
