package cn.edu.sspku.hj.app;

import cn.edu.sspku.hj.bean.City;
import cn.edu.sspku.hj.db.CityDB;

import android.app.Application;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class MyApplication extends Application {
    private static final String TAG = "MyAPP";

    private static MyApplication mApplication;//静态

    private CityDB mCityDB;

    private List<City> mCityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyApplication->Oncreate");
        mApplication = this;
        mCityDB = openCityDB(); //返回database对象
        initCityList();
    }

    private void initCityList() {
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {     //启动子线程
// TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
//        int i = 0;
//        for (City city : mCityList) {
//            i++;
//            String cityName = city.getCity();
//            String cityCode = city.getNumber();
//            Log.d(TAG, cityCode + ":" + cityName);
//        }
//        Log.d(TAG, "i=" + i);
        return true;
    }

    public Cursor getCursor(){
        return mCityDB.getCursor();
    }

    public List<City> getCityList() {
        return mCityList;
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    private CityDB openCityDB() {
        String path = "/data" //获得绝对路径
                + Environment.getDataDirectory().getAbsolutePath
                ()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG, path);
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
                Log.i("MyApp", "mkdirs");
            }
            Log.i("MyApp", "db is not exists");
            try {
                InputStream is = getAssets().open("city.db");//读入原始数据库信息
                FileOutputStream fos = new FileOutputStream(db);//将读入的原始数据写入db
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) { //inputStream读入字节流
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

}
