package cn.edu.sspku.hj.miniweather;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;

import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


import cn.edu.sspku.hj.app.MyApplication;
import cn.edu.sspku.hj.bean.City;


public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mbackBtn;
    private TextView mcitySelect;
    private ListView mlistView;
    private SearchView searchView;

    private LinearLayout listViewTopLayout;
    private TextView listViewFirstLine;

    private MyApplication myApplication;

    private ArrayList<City> mCityList = new ArrayList<>();
    private ArrayList<City> mSearchResult = new ArrayList<>(); //搜索结果，只放城市名

    private SearchAdapter searchAdapter = null;
    private CityAdapter adapter;

    private AlphabetIndexer indexer;
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //    private int lastFirstVisibleItem = -1;
    private boolean notSearch = true;

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
                City returnCity;
                if(!notSearch)
                {
                    returnCity = mSearchResult.get(position);
                }
                else returnCity = mCityList.get(position);
                Toast.makeText(SelectCity.this, "你选择" + returnCity.getCity(),
                        Toast.LENGTH_SHORT).show();
                returnCode = returnCity.getNumber();
                Log.d("Msea", returnCode);
                mcitySelect.setText("当前城市：" + returnCity.getCity());
            }
        });

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setQueryHint("请输入城市名称或拼音");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) { //搜索栏不空时，执行搜索
                    mSearchResult.clear();
                    notSearch = false;
                    //遍历城市列表，当前城市包含当前搜索框文字，或拼音包含当前搜索框拼音，则认为该城市符合要求，放入mSearchResult
                    for (City city : mCityList) {
                        if (city.getCity().contains(newText) ||
                                city.getAllPY().toLowerCase().contains(newText)
                                ||
                                city.getAllFirstPY().contains(newText.toUpperCase())) {
                            mSearchResult.add(city);
                        }
                    }

                        searchAdapter = new SearchAdapter(SelectCity.this, R.layout.city_item,
                                mSearchResult);

                    mlistView.setAdapter(searchAdapter);
                    listViewTopLayout.setVisibility(View.GONE);
                    searchAdapter.notifyDataSetChanged();
                } else {
                    notSearch = true;
                    mlistView.setAdapter(adapter);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //实际不执行，文本框一变化就自动执行搜索
                Toast.makeText(SelectCity.this, "检索完毕", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        initView();
    }

    protected void initView() {

        myApplication = MyApplication.getInstance();
        mCityList = (ArrayList<City>) myApplication.getCityList();
        for (City city : mCityList)
            mSearchResult.add(city);
        Cursor cursor = myApplication.getCursor();
        startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 6, alphabet);
        adapter = new CityAdapter //新建适配器
                (SelectCity.this, R.layout.city_item, mCityList);
        adapter.setmIndexer(indexer);
        mlistView.setAdapter(adapter); //接上适配器

        listViewTopLayout = findViewById(R.id.list_view_title_layout);
        listViewFirstLine = findViewById(R.id.list_view_first);

        mlistView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                int section = indexer.getSectionForPosition(firstVisibleItem);
//                if(firstVisibleItem == indexer.getPositionForSection(section))
//                    listViewTopLayout.setVisibility(View.INVISIBLE);
//                else listViewTopLayout.setVisibility(View.VISIBLE);
                listViewFirstLine.setText(String.valueOf(alphabet.charAt(section)));
            }
        });
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



