package cn.edu.sspku.hj.db;

import android.content.Context;
import cn.edu.sspku.hj.bean.City;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;
import java.util.ArrayList;

public class CityDB {
    public static final String CITY_DB_NAME = "city.db";
    private static final String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path) {
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }

    public Cursor getCursor(){
        return db.rawQuery("SELECT * from " + CITY_TABLE_NAME +" ORDER BY "+"firstpy", null);
    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        Cursor c = this.getCursor(); //rawQuerry，获取整个表
        while (c.moveToNext()) {
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province, city, number, firstPY, allPY, allFirstPY);
            list.add(item);
        }
        return list;
    }
}

