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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.sspku.hj.app.MyApplication;
import cn.edu.sspku.hj.bean.City;
import cn.edu.sspku.hj.util.PinYin;


public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mbackBtn;
    private TextView mcitySelect;
    private ListView mlistView;
    private SearchView searchView;

    private MyApplication myApplication;

    private ArrayList<String> mSearchResult = new ArrayList<>(); //搜索结果，只放城市名
    private Map<String,String> nameToCode = new HashMap<>();  //城市名到编码
    private Map<String,String> nameToPinyin = new HashMap<>(); //城市名到拼音

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
                String returnCityName = mSearchResult.get(position);
                Toast.makeText(SelectCity.this, "你选择" + returnCityName,
                        Toast.LENGTH_SHORT).show();
                returnCode = nameToCode.get(returnCityName); //通过城市名Key，获得城市编码Value
                Log.d("Msea", returnCode);
                mcitySelect.setText("当前城市：" +  //更新TextView
                        returnCityName);
            }
        });



        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconified(true); //需要点击搜索图标，才展开搜索框
        searchView.setQueryHint("请输入城市名称或拼音");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) { //搜索栏不空时，执行搜索
                    if (mSearchResult != null) //清空上次搜索结果
                        mSearchResult.clear();
                    //遍历nameToPinyin的键值（它包含所有城市名），如果HashMap中的当前城市包含当前搜索框文字，
                    //或拼音包含当前搜索框拼音，则认为该城市符合要求，放入mSearchResult
                    for (String str : nameToPinyin.keySet()) {
                        if (str.contains(newText)||nameToPinyin.get(str).contains(newText)) {
                            mSearchResult.add(str);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                //实际不执行，文本框一变化就自动执行搜索
                Toast.makeText(SelectCity.this, "检索中", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        initView();

    }

    protected void initView() {
        myApplication = MyApplication.getInstance();
        ArrayList<City> mCityList = (ArrayList<City>) myApplication.getCityList();
        String strName;
        String strNamePinyin;
        String strCode;
        for (City city : mCityList) {
            strCode = city.getNumber();
            strName = city.getCity();
            strNamePinyin = PinYin.converterToSpell(strName); //城市名解析成拼音
            nameToCode.put(strName,strCode); //城市名到城市编码
            nameToPinyin.put(strName,strNamePinyin); //城市名到拼音
            mSearchResult.add(strName); //初始状态包含全部城市

        }
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

