package cn.edu.sspku.hj.miniweather;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.sspku.hj.app.MyApplication;
import cn.edu.sspku.hj.bean.City;


public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mbackBtn;
    private TextView mcitySelect;
    private ListView mlistView;
    private SearchView searchView;

    private MyApplication myApplication;
    private ArrayList<String> mcityCodeAndNameList = new ArrayList<>();
    private ArrayList<String> mSearchResult;

    private ArrayAdapter<String> adapter;

    private String returnCode = "101010100"; //默认值为北京的代码


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
                Toast.makeText(SelectCity.this, "你选择" + mSearchResult.get(position), Toast.LENGTH_SHORT).show();
                returnCode = mSearchResult.get(position).substring(0, 9);
                Log.d("Msea", returnCode);
                mcitySelect.setText("当前城市：" +
                        mSearchResult.get(position).substring(9));
            }
        });

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconified(true);
        searchView.setQueryHint("请输入城市名称");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //实际不搜索，文本框一变化就自动执行搜索
                Toast.makeText(SelectCity.this, "检索中", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //newText先搜汉字的，后面升级拼音模糊搜索
                if (!TextUtils.isEmpty(newText)) {
                    Toast.makeText(SelectCity.this, "开始执行搜索", Toast.LENGTH_SHORT).show();
                    if (mSearchResult != null)
                        mSearchResult.clear();
                    for (String str : mcityCodeAndNameList) {
                        if (str.contains(newText)) {
                            mSearchResult.add(str);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                //后期改filter实现？实现filterable接口
                return true; //return true 和 false 有什么区别？
            }
        });

        initView();

    }

    protected void initView() {
        myApplication = MyApplication.getInstance();
        ArrayList<City> mCityList = (ArrayList<City>) myApplication.getCityList();
        for (City city : mCityList) {
            mcityCodeAndNameList.add(city.getNumber() + city.getCity());
        }
        mSearchResult = new ArrayList<>(mcityCodeAndNameList); //浅拷贝
        adapter = new ArrayAdapter<> //新建适配器
                (SelectCity.this, android.R.layout.simple_list_item_1, mSearchResult);
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

