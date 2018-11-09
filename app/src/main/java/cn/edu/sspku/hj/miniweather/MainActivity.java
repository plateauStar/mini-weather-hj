package cn.edu.sspku.hj.miniweather;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.IOException;
import java.lang.Exception;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


import cn.edu.sspku.hj.app.MyApplication;
import cn.edu.sspku.hj.bean.City;
import cn.edu.sspku.hj.util.NetUtil;
import cn.edu.sspku.hj.bean.TodayWeather;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;
    public LocationClient mLocationClient = null;

    private ImageView mUpdateBtn;
    private ProgressBar mProgressBar;
    private ImageView mCitySelect, mLocationImg;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv,
            pmQualityTv, temperatureTv, temperatureNowTv, climateTv, windTv, city_name_Tv;
    private ImageView weatherImg, pmImg;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) { //lambda表达式
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    weatherAdapter = new WeatherAdapter(todayWeatherArrayList);
                    mRecyclerView.setAdapter(weatherAdapter);
                    break;
                default:
                    break;
            }
        }
    };

    private ArrayList<TodayWeather> todayWeatherArrayList;
    private TodayWeather item;
    private RecyclerView mRecyclerView;
    private WeatherAdapter weatherAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setAddrType("all");
        option.setIgnoreKillProcess(false);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setScanSpan(5000);              // 设置定时定位的时间间隔。单位毫秒
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明




        mRecyclerView = (RecyclerView) findViewById(R.id.days_after_info);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.updating_bar);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！ ", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！ ", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();
//        queryWeatherCode("101020100");
    }

    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_image);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        temperatureNowTv = (TextView) findViewById(R.id.temperature_now);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
//        cityInfoTv = findViewById(R.id.cityInfo);   //  显示获取城市信息按钮
        mLocationImg = findViewById(R.id.title_location); // 定位按钮
        mLocationImg.setOnClickListener(MainActivity.this);





        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        temperatureNowTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }

    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i, 1);
        }
        if (view.getId() == R.id.title_update_btn) {
            setUpdateVisible(0);
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE); //获得SharedPreference对象，Context的方法
            String cityCode = sharedPreferences.getString("main_city_code", "101020100");//默认值
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                Toast.makeText(MainActivity.this, "网络OK！ ", Toast.LENGTH_LONG).show();
                timeTv.setText("同步中……");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！ ", Toast.LENGTH_LONG).show();
            }
        }
        if (view.getId() == R.id.title_location) {
            timeTv.setText("定位中……");
            mLocationClient.start();
            setUpdateVisible(0);
//            mLocationImg.setVisibility(View.GONE);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            setPreference(newCityCode);
            Log.d("myWeather", "选择的城市代码为：" + newCityCode);
            //cityCode不一样，API提供的内容可能不一样，解析可能有错误
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                Toast.makeText(MainActivity.this, "网络OK！ ", Toast.LENGTH_LONG).show();
                timeTv.setText("同步中……");
                queryWeatherCode(newCityCode);
                Toast.makeText(MainActivity.this, "选择城市代码" + newCityCode, Toast.LENGTH_LONG).show();
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！ ", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void setPreference(String newCityCode) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE); //获得SharedPreference对象，Context的方法
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("main_city_code", newCityCode);
        editor.apply();
    }

    /**
     * *
     *
     * @param cityCode
     */
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {  //子线程启动，传递message给主线程
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    Log.d("myWeather", "InputOUtBegin");
                    URL url = new URL(address); //url 无效连接会怎样，无法更新
                    con = (HttpURLConnection) url.openConnection(); //打开链接
                    con.setRequestMethod("GET"); //使用GET方法, 提交表单POST方法
                    con.setConnectTimeout(5000); //5s，连接超时
                    con.setReadTimeout(5000); //读超时

                    Log.d("myWeather", "connection established");
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    Log.d("myWeather", "str transfer");
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message(); //新建Message
                        msg.what = UPDATE_TODAY_WEATHER; //便于主线程识别的id
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg); //mHandler
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect(); //关闭连接
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0; //有多个日期的风向，
        // 昨天、今天、后几天，API一般显示第一天在前，防止重复更新
        // 如果返回的城市没有天气信息，程序会崩溃……
        // 尝试设today各项值为N/A
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;


        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    // 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    // 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                            todayWeatherArrayList = new ArrayList<>();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                xmlPullParser.next();
                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                xmlPullParser.next();
                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                xmlPullParser.next();
                                Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                xmlPullParser.next();
                                Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                xmlPullParser.next();

                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                xmlPullParser.next();
                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang")) {
                                xmlPullParser.next();
                                if (item != null)
                                    item.setFengxiang(xmlPullParser.getText());
                                if (fengxiangCount == 0)
                                    todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli")) {
                                xmlPullParser.next();
                                if (item != null && fengliCount > 0 && fengliCount % 2 == 0)
                                    item.setFengli(xmlPullParser.getText());
                                if (fengliCount == 0)
                                    todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date")) {
                                xmlPullParser.next();
                                if (item != null)
                                    item.setDate(xmlPullParser.getText().split("日")[1]);
                                if (dateCount == 0) {
                                    todayWeather.setDate(xmlPullParser.getText());
                                    dateCount++;
                                }
                            } else if (xmlPullParser.getName().equals("high")) {
                                xmlPullParser.next();
                                if (item != null)
                                    item.setHigh(xmlPullParser.getText().substring(2).trim());
                                if (highCount == 0)
                                    todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low")) {
                                xmlPullParser.next();
                                if (item != null)
                                    item.setLow(xmlPullParser.getText().substring(2).trim());
                                if (lowCount == 0)
                                    todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type")) {
                                xmlPullParser.next();
                                if (item != null)
                                    item.setType(xmlPullParser.getText());
                                if (typeCount == 0)
                                    todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals(("weather"))) {
                                item = new TodayWeather();
                            }
                            break;
                        }
                        // 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG: {
                        if (xmlPullParser.getName().equals("weather")) {
                            todayWeatherArrayList.add(item);
                        }
                        break;
                    }
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }

        } catch (
                XmlPullParserException e)

        {
            e.printStackTrace();
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }

        todayWeatherArrayList.remove(0);

        return todayWeather;

    }

    void updateTodayWeather(TodayWeather todayWeather) {

        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度： " + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        temperatureNowTv.setText("温度：" + todayWeather.getWendu() + "℃");
        // set pmImg
        if (!todayWeather.getPm25().equals("N/A")) {
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if (pm25 >= 0 && pm25 <= 50) {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            } else if (pm25 >= 51 && pm25 <= 100)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            else if (pm25 >= 101 && pm25 <= 150)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            else if (pm25 >= 151 && pm25 <= 200)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            else if (pm25 >= 201 && pm25 <= 300)
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            else pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
        }
        //set weatherImg

        setWeatherImage(weatherImg, todayWeather.getType());

        setUpdateVisible(1);
        Toast.makeText(MainActivity.this, "更新成功！ ", Toast.LENGTH_SHORT).show();
    }

    public void setWeatherImage(ImageView weatherImg, String type) {
        switch (type) {

            case "多云":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                break;
            case "暴雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                break;
            default:
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        }
    }

    void setUpdateVisible(int flag) {
        if (flag == 1) {
            mProgressBar.setVisibility(View.GONE);
            mUpdateBtn.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.GONE);
        }
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String city = null;
            city = location.getCity();    //获取城市
           // Toast.makeText(MainActivity.this,"city+"+city,Toast.LENGTH_LONG).show();
//            cityInfoTv.setText(city);
            setUpdateVisible(1);

            if(city.trim().length() != 0)
            {
                MyApplication myApplicationForMain = MyApplication.getInstance();
                List<City> cityList = myApplicationForMain.getCityList();
                for(City tempCity:cityList)
                {
                    if(city.contains(tempCity.getCity()))
                    {
                        queryWeatherCode(tempCity.getNumber());
                        setPreference(tempCity.getNumber());
//                        mLocationImg.setVisibility(View.VISIBLE);
                        mLocationClient.stop();
                        break;
                    }
                }
            }
            else {
//                mLocationImg.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"定位失败，请打开gps",
                    Toast.LENGTH_LONG).show();
                timeTv.setText("定位失败，请使用搜索功能");
            }
        }


    }
}


