package cn.edu.sspku.hj.miniweather;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import cn.edu.sspku.hj.bean.TodayWeather;

public class WeatherAdapter extends RecyclerView.Adapter <WeatherAdapter.ViewHolder> {

    private List<TodayWeather> mWeatherList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView weatherIconV;
        TextView dayOfWeekV;
        TextView temperatureOfDayV;
        TextView typeOfDayV;
        TextView windOfDayV;
        public ViewHolder(View view)
        {
            super(view);
            weatherIconV = (ImageView) view.findViewById(R.id.weather_icon);
            dayOfWeekV = (TextView) view.findViewById(R.id.day_of_week);
            temperatureOfDayV = (TextView) view.findViewById(R.id.temperature_future);
            typeOfDayV = (TextView) view.findViewById(R.id.type_future);
            windOfDayV = (TextView) view.findViewById(R.id.fengli_future);
        }
    }

    public WeatherAdapter(List<TodayWeather> todayWeatherList){
        mWeatherList = todayWeatherList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item,
                viewGroup,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TodayWeather futureWeather = mWeatherList.get(position);
        holder.dayOfWeekV.setText(futureWeather.getDate());
        holder.temperatureOfDayV.setText(futureWeather.getHigh()+" ~ "+futureWeather.getLow());
        holder.typeOfDayV.setText(futureWeather.getType());
        holder.windOfDayV.setText(futureWeather.getFengli());
        setWeatherImage2(holder.weatherIconV,holder.typeOfDayV.getText().toString());

    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public void setWeatherImage2(ImageView weatherImg, String type) //调用其它类的方法，
                                                            // 考虑做成一个服务/广播调用
    {
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

}
