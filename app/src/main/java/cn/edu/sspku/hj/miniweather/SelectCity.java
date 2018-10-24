package cn.edu.sspku.hj.miniweather;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.sspku.hj.app.MyApplication;
import cn.edu.sspku.hj.bean.City;


public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mbackBtn;
    private TextView mcitySelect;
    private ListView mlistView;
    private String returnCode = "101010100"; //默认值为北京的代码
    private MyApplication myApplication;
    private ArrayList<String> mcityCodeList = new ArrayList<>();
    private ArrayList<String> mcityNameList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mbackBtn = (ImageView) findViewById(R.id.title_back);
        mbackBtn.setOnClickListener(this);
        mcitySelect = (TextView) findViewById(R.id.title_name);
        mlistView = (ListView) findViewById(R.id.city_list);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCity.this, "你选择" + mcityNameList.get(position)
                        + mcityCodeList.get(position), Toast.LENGTH_SHORT).show();
                returnCode = mcityCodeList.get(position);
                mcitySelect.setText("当前城市：" + mcityNameList.get(position));
            }
        });

        initView();

    }

    protected void initView() {
        myApplication = MyApplication.getInstance();
        ArrayList<City> mCityList = (ArrayList<City>) myApplication.getCityList();
        for (City city : mCityList) {
            mcityCodeList.add(city.getNumber());
            mcityNameList.add(city.getCity());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<> //新建适配器
                (SelectCity.this, android.R.layout.simple_list_item_1, mcityNameList);
        mlistView.setAdapter(adapter); //接上适配器


    }

    @Override
    public void onClick(View v) { //重写onClick事件
        switch (v.getId()) {
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", returnCode); //将ListView中获得的cityCode放入intent
                setResult(RESULT_OK, i); //回调onActivityResult()
                finish();
                break;
            default:
                break;
        }
    }
}

