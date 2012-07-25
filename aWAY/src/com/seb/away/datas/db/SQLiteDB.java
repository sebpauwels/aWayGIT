/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seb.away.datas.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class SQLiteDB extends SQLiteOpenHelper
{

    private static final String TABLE = "USER";
    private static final String COL_ID = "USER_ID";
    private static final String COL_NAME = "USER_NAME";
    private static final String COL_PASS = "USER_PASS";
    private static final String COL_REMEMBER = "USER_REMEMBER";
    private static String CREATE_BDD = "";

    public SQLiteDB(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //on créé la table à partir de la requête écrite dans la variable CREATE_BDD
        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        //On peut fait ce qu'on veut ici moi j'ai décidé de supprimer la table et de la recréer
        //comme ça lorsque je change la version les id repartent de 0
        db.execSQL("DROP TABLE " + TABLE + ";");
        onCreate(db);
    }

    public void dbSQLReqMaker(String datas[][])
    {
        CREATE_BDD = "CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME + " TEXT NOT NULL, "
                + COL_PASS + " TEXT NOT NULL";

        for (int i = 0; i < datas.length/2; i++)
        {
                CREATE_BDD += ", " + datas[i][0].toUpperCase() + " TEXT NOT NULL";

        }
        CREATE_BDD += ", " + COL_REMEMBER + " INTEGER NOT NULL DEFAULT 0);";

        Log.i("aWAY_DEBUG", " _database : SQL generated : /n"+CREATE_BDD);
    }
}
