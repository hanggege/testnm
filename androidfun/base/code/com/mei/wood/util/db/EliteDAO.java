package com.mei.wood.util.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mei.orc.john.model.JohnUser;
import com.mei.orc.util.string.StringUtilKt;


public class EliteDAO {
    private static DBOpenHelper helper;// 创建DBOpenHelper对象
    private static SQLiteDatabase db;// 创建SQLiteDatabase对象
    private static EliteDAO dao;


    public static EliteDAO getInstance(Context context) {
        if (dao == null || helper == null) {
            dao = new EliteDAO(context.getApplicationContext());
        }
        return dao;
    }

    private EliteDAO(Context context) {
        helper = new DBOpenHelper(context);
    }


    public void addEliteidDB(final long id) {
        Elite_save_data data = new Elite_save_data(id, System.currentTimeMillis());
        addOrupdata(data);
    }

    /**
     * 添加数据
     */
    private void add(Elite_save_data data) {
        try {
            db = helper.getWritableDatabase();
            db.execSQL("insert into elite_db (_id,time) values (?,?)", new Object[]{
                    data.id, data.time});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 寻找15天内的已读
     */
    public boolean findby15days(long id) {
        Elite_save_data saveData = find(StringUtilKt.stringConcate(id, JohnUser.getSharedUser().getUserID()));
        if (saveData != null) {
            long time = (System.currentTimeMillis() - saveData.time) / (24 * 60 * 60 * 1000);
            if (time >= 0 && time < 15) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加数据 如果存在则更新
     */
    private void addOrupdata(Elite_save_data data) {
        Elite_save_data save = find(data.id);
        if (save == null) {
            add(data);
        } else {
            updata(data);
        }
    }

    /**
     * 更新信息
     */
    private void updata(Elite_save_data data) {
        try {
            db = helper.getWritableDatabase();
            db.execSQL("update elite_db set time=? where _id=?", new Object[]{
                    data.time, data.id});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查找信息
     */
    private Elite_save_data find(String id) {
        Cursor cursor = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select _id,time from elite_db where _id = ?",
                    new String[]{id});
            if (cursor != null && cursor.moveToNext()) {
                return new Elite_save_data(cursor.getLong(
                        cursor.getColumnIndex("_id")),
                        cursor.getLong(cursor.getColumnIndex("time")));
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 获取总记录数
     */
    public long getCount() {
        Cursor cursor = null;
        try {
            db = helper.getWritableDatabase();
            cursor = db.rawQuery("select count(_id) from elite_db", null);
            if (cursor.moveToNext()) {
                return cursor.getLong(0);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void destroyDB() {
        if (helper != null) {
            helper.close();
        }
        helper = null;
        db = null;
        dao = null;
    }

}
