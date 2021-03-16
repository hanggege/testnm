package com.mei.wood.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joker on 15/12/31.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;// 定义数据库版本号
    private static final String DBNAME = "elite_record.db";// 定义数据库名

    public DBOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table elite_db (_id char primary key,time Long)");// 创建支出信息表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
