package cn.edu.sspku.hj.miniweather;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;

import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

    private RelativeLayout sectionToastLayout;

    /**
     * 右侧可滑动字母表
     */
    private Button alphabetButton;

    /**
     * 弹出式分组上的文字
     */
    private TextView sectionToastText;


    private AlphabetIndexer indexer;
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //    private int lastFirstVisibleItem = -1;
    private boolean notSearch = true;

    private String returnCode = "101010100"; //默认值为北京的代码

    private Handler mHander;
    private final int ADAPTER_PLUG_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mbackBtn = (ImageView) findViewById(R.id.title_back);
        mbackBtn.setOnClickListener(this);
        mcitySelect = (TextView) findViewById(R.id.title_name);

        sectionToastText = (TextView) findViewById(R.id.section_toast_text);
        alphabetButton = (Button) findViewById(R.id.alphabetButton);
        sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);

        mlistView = (ListView) findViewById(R.id.city_list);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City returnCity;
                searchView.clearFocus();
                if (!notSearch) {
                    returnCity = mSearchResult.get(position);
                } else returnCity = mCityList.get(position);
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
            @SuppressLint("WrongConstant")
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
                    alphabetButton.setVisibility(View.INVISIBLE);
                    searchAdapter.notifyDataSetChanged();

                } else {
                    notSearch = true;
                    mlistView.setAdapter(adapter);
                    alphabetButton.setVisibility(View.VISIBLE);
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
        mHander = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADAPTER_PLUG_IN:
                        updateListView((CityAdapter) msg.obj);
                        break;
                    default:
                        break;
                }
            }
        };


    }

    protected void initView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
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
                if(adapter != null) {
                    Message msg = new Message();
                    msg.what = ADAPTER_PLUG_IN;
                    msg.obj = adapter;
                    mHander.sendMessage(msg);
                }

            }
        }).start();
    }




    protected void updateListView (CityAdapter adapterIn) {
        mlistView.setAdapter(adapterIn); //接上适配器
        setAlphabetListener();

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

    private void setAlphabetListener() {
        alphabetButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float alphabetHeight = alphabetButton.getHeight();
                float y = event.getY();
                int sectionPosition = (int) ((y / alphabetHeight) / (1f / 26f));
                if (sectionPosition < 0) {
                    sectionPosition = 0;
                } else if (sectionPosition > 25) {
                    sectionPosition = 25;
                }
                String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
                int position = indexer.getPositionForSection(sectionPosition);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alphabetButton.setBackgroundResource(R.drawable.a_z);
                        sectionToastLayout.setVisibility(View.VISIBLE);
                        sectionToastText.setText(sectionLetter);
                        mlistView.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sectionToastText.setText(sectionLetter);
                        mlistView.setSelection(position);
                        break;
                    default:
                        alphabetButton.setBackgroundResource(R.drawable.a_z);
                        sectionToastLayout.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

}



