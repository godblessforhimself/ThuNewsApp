package com.java.twentynine;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yalantis.phoenix.PullToRefreshView;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class uiActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final int OPENNEWS = 9;
    private static BackendInter news = new BackendInter();
    private RecyclerView recyclerView;
    private List<NewsTitle.MyList> mNews, mNewsBackup;
    private int newsNum, currentPage,pageSize;

    private HomeAdapter mAdapter;
    private PullToRefreshView mPullToRefreshView;
    private TabLayout tabLayout;
    private View mheader,mfooter;
    private mViewHolder currentNews;
    private int issearching = 0;
    private int isfavourites = 0;
    private int refreshoradd = REFRESH;
    private static final int REFRESH = 1, ADD = 0;
    private String keyword = "";
    private int cate = 0;
    private Lock suggestlock = new ReentrantLock();
    //是否正在网络通信
    private boolean isLoading = false;
    private SearchView mSearchView = null;
    private List<NewsTitle.MyList> suggest = new ArrayList<>();
    private void showTip(String s)
    {
        Log.d("uiActivity.class","Tip = " + s);
    }
    private void refresh()
    {
        if (isfavourites == 1) {
            mNews.clear();
            mNews.addAll(news.getCollectionNews(uiActivity.this).list);
            newsNum = mNews.size();
            mAdapter.init(mNews);
            mAdapter.notifyDataSetChanged();
            return;
        } else {

        }
        refreshoradd = REFRESH;
        showTip("Refresh, you should set some view changed...");
        if (!isLoading)
        {
            new Thread(netWorkTask).start();
            isLoading = true;
        }
        else
        {
            showTip("Previous thread is loading,wait");
        }
    }
    public void addNews()
    {
        Log.d("UITEST","testttt");
        if (isfavourites == 1) {
            mNews.clear();
            mNews.addAll(news.getCollectionNews(uiActivity.this).list);
            newsNum = mNews.size();
            mAdapter.init(mNews);
            mAdapter.notifyDataSetChanged();
            return;
        } else {

        }
        if (!isLoading)
        {
            new Thread(netWorkTask).start();
            isLoading = true;
        }
        else
        {
            showTip("Previous thread is loading,wait");
        }
    }
    public void init()
    {
        mNews = new ArrayList<NewsTitle.MyList>();
        newsNum = 0;
        currentPage = 1;
        pageSize = 50;
        mheader = LayoutInflater.from(this).inflate(R.layout.recyclerview_header,null,false);
        mfooter = LayoutInflater.from(this).inflate(R.layout.recyclerview_footer,null,false);
        recyclerView = (RecyclerView)findViewById(R.id.recycle_0);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        tabLayout = (TabLayout) findViewById(R.id.headTab);
        mAdapter = new HomeAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        addNews();
    }
    Handler handler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                showTip((String) msg.obj);
                return;
            }
            if (refreshoradd == ADD) {
                mNews.addAll((ArrayList) msg.getData().getParcelableArrayList("news"));
            }
            else if (refreshoradd == REFRESH){
                mNews.addAll(0, (ArrayList) msg.getData().getParcelableArrayList("news"));
                mPullToRefreshView.setRefreshing(false);
            }
            newsNum = mNews.size();
            if (issearching == 0) {
                currentPage++;
            } else {
                issearching++;
            }
            mAdapter.init(mNews);
            mAdapter.notifyDataSetChanged();
            isLoading = false;
            showTip("News fetch finish, total num:" + newsNum);
        }
    };
    //所有runnable，非主线程都不能使用showtip等改变ui的函数，替代办法写到handler handleMessage中。
    //runnable结束调用handler更新ui
    Runnable netWorkTask = new Runnable() {
        @Override
        public synchronized void run() {
            if (!suggestlock.tryLock()) {
                return;
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            List<NewsTitle.MyList> e = new ArrayList<>();
            try{
                if (issearching == 0) {
                    e.addAll(news.getNewsTitle(currentPage, pageSize, cate, uiActivity.this).list);
                } else {
                    e.addAll(news.searchNewsTitel(keyword, issearching, pageSize, cate, uiActivity.this).list);
                }
                data.putParcelableArrayList("news",(ArrayList)e);
                msg.setData(data);
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception ex)
            {
                Log.d("exception",ex.toString());
                msg.what = 0;
                msg.obj = ex.toString();
                handler.sendMessage(msg);
            }
            suggestlock.unlock();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);
        init();
        getSupportActionBar().hide();
        //上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !recyclerView.canScrollVertically(1))
                {
                    addNews();
                }
            }
        });

        try {
            mSearchView = (SearchView) findViewById(R.id.searchview);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setSubmitButtonEnabled(true);
            mSearchView.setQueryHint("Search");
        } catch (Exception ex) {
            showTip(ex.toString());
        }

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //调用刷新
                refresh();
            }
        });
        LinearLayout blay1 = (LinearLayout) findViewById(R.id.blay1);
        blay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                ImageView iv = (ImageView) findViewById(R.id.bmenu_img2);
                if (isfavourites == 1) {
                    currentPage--;
                    iv.setImageResource(R.drawable.favorite);
                    isfavourites = 0;
                }
                mPullToRefreshView.setRefreshing(true);
                recyclerView.scrollToPosition(0);
                refresh();
            }
        });

        LinearLayout blay2 = (LinearLayout) findViewById(R.id.blay2);
        blay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                ImageView iv = (ImageView) findViewById(R.id.bmenu_img2);
                if (isfavourites == 1) {
                    currentPage--;
                    iv.setImageResource(R.drawable.favorite);
                } else {
                    iv.setImageResource(R.drawable.favorite_y);
                }
                isfavourites = isfavourites ^1;
                refresh();
            }
        });

        int d_n = news.getNight(uiActivity.this);
        if ((d_n != MODE_NIGHT_YES)&&(d_n != MODE_NIGHT_NO)) {
            d_n = MODE_NIGHT_NO;
        }
        news.setNight(d_n, uiActivity.this);

        LinearLayout blay3 = (LinearLayout) findViewById(R.id.blay3);
        blay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                int d_n = news.getNight(uiActivity.this);
                if (d_n == MODE_NIGHT_YES) {
                    d_n = MODE_NIGHT_NO;
                } else {
                    d_n = MODE_NIGHT_YES;
                }
                news.setNight(d_n, uiActivity.this);
                AppCompatDelegate.setDefaultNightMode(news.getNight(uiActivity.this));
                recreate();
            }
        });

        LinearLayout blay4 = (LinearLayout) findViewById(R.id.blay4);
        blay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                ImageView iv = (ImageView) findViewById(R.id.bmenu_img2);
                if (isfavourites == 1) {
                    mNews.clear();
                    iv.setImageResource(R.drawable.favorite);
                    isfavourites = 0;
                }
                mPullToRefreshView.setRefreshing(true);

                new Thread(new Runnable() {
                    @Override
                    public synchronized void run() {
                        if (!suggestlock.tryLock()) {
                            return;
                        }
                        refreshoradd = REFRESH;
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        List<NewsTitle.MyList> e = new ArrayList<NewsTitle.MyList>();
                        try {
                            suggest.clear();
                            suggest.addAll(news.likeNewsTitel(uiActivity.this).list);
                            e.addAll(suggest.subList(0, pageSize));
                            data.putParcelableArrayList("news", (ArrayList) e);
                            msg.setData(data);
                            msg.what = 1;
                            handler.sendMessage(msg);
                        } catch (Exception ex) {
                            Log.d("exception", ex.toString());
                            msg.what = 0;
                            msg.obj = ex.toString();
                            handler.sendMessage(msg);
                        }
                        suggestlock.unlock();
                    }
                }).start();
            }
        });

        LinearLayout blay5 = (LinearLayout) findViewById(R.id.blay5);
        blay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                Intent intent = new Intent(uiActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        TabLayout tl = (TabLayout) findViewById(R.id.headTab);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public synchronized void onTabSelected(TabLayout.Tab tab) {
                    cate = tab.getPosition();
                    //showTip(new Integer(cate).toString());
                    mNews.clear();
                    currentPage = 1;
                    refresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AppCompatDelegate.setDefaultNightMode(news.getNight(uiActivity.this));
    }

    @Override
    public synchronized boolean onQueryTextChange(String query) {
        // TODO Auto-generated method stub
        keyword = query;
        if (query.equals("")) {
            issearching = 0;
            currentPage--;
            if (currentPage ==0) {
                currentPage = 1;
            }
            if (mSearchView != null) {
                // 得到输入管理对象
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                    imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                }
                mSearchView.clearFocus(); // 不获取焦点
            }
            refresh();
        } else {
            issearching = 1;
        }
        return true;
    }
    //单击搜索按钮时激发该方法
    @Override
    public synchronized boolean onQueryTextSubmit(String query) {
        showTip(query);
        keyword = query;
        if (query.equals("")) {
            issearching = 0;
            currentPage--;
        } else {
            issearching = 1;
            mNews.clear();
        }
        if (currentPage ==0) {
            currentPage = 1;
        }
        if (mSearchView != null) {
            // 得到输入管理对象
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
            }
            mSearchView.clearFocus(); // 不获取焦点
        }
        refresh();
        return true;
    }
    private static final int OPENSUCCESS = 1,OPENFAILED = 0;
    @Override
    protected void onActivityResult(int request, int result, Intent intent)
    {
        if (request == OPENNEWS)
        {
            //result = 1 success  , 0 = failed
            showTip("result = " + result);
            if (currentNews != null)
            {
                if(news.isviewed(currentNews.id, uiActivity.this)) {
                    currentNews.title.setTextColor(Color.GRAY);
                    currentNews.intro.setTextColor(Color.GRAY);
                } else {
                    currentNews.title.setTextColor(currentNews.index.getTextColors());
                    currentNews.intro.setTextColor(currentNews.index.getTextColors());
                }
            }
        }
    }
    class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title,tag, people,intro,index;
        ImageView img;
        String id = "";
        Handler loadPic;
        public mViewHolder(View itemView) {
            super(itemView);
            if (itemView == mheader || itemView == mfooter)
                return;
            title = itemView.findViewById(R.id.newsTitle);
            tag = itemView.findViewById(R.id.newsTag);
            people = itemView.findViewById(R.id.newsPeople);
            intro = itemView.findViewById(R.id.newsInfo);
            index = itemView.findViewById(R.id.newsIndex);
            img = itemView.findViewById(R.id.imgres);
            itemView.setOnClickListener(this);

        }

        public void set(NewsTitle.MyList it)
        {
            if (itemView == mheader || itemView == mfooter)
                return;
            id = it.news_ID;
            title.setText(it.news_Title);
            intro.setText(it.news_Intro);
            tag.setText("# "+it.newsClassTag);
            people.setText(it.news_Author);


            if(news.isviewed(id, uiActivity.this)) {
                title.setTextColor(Color.GRAY);
                intro.setTextColor(Color.GRAY);
            } else {
                title.setTextColor(index.getTextColors());
                intro.setTextColor(index.getTextColors());
            }
            //新闻列表图片加载
            boolean needRecommend = true;
            if (news.getPicturesDisplay(uiActivity.this) == 1)
            {
                needRecommend = false;
                img.setVisibility(View.GONE);
            }
            else
            {
                img.setVisibility(View.VISIBLE);
            }

            String pic = it.news_Pictures;
            if ((pic.contains(".jpg") || pic.contains(".png") || pic.contains(".bmp") || pic.contains(".jpeg"))&& needRecommend)
            {
                String[] pictures = it.news_Pictures.split(";");
                String[] picture = pictures[0].split(" ");
                //显示第一张
                if (picture[0].contains(".jpg") || picture[0].contains(".png") || picture[0].contains(".bmp")
                        || picture[0].contains(".jpeg"))
                {
                    Glide.with(getApplicationContext())
                            .load(picture[0])
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .override(500,500)
                            .fitCenter()
                            .skipMemoryCache(true)
                            .dontAnimate()
                            .placeholder(R.drawable.elephant)
                            .into(img);
                    needRecommend = false;
                }

            }
            if (needRecommend)
            {
                loadPic = new Handler()
                {
                    @Override
                    public synchronized void handleMessage(Message msg)
                    {
                        String url = msg.getData().getString("url");
                        Glide.with(getApplicationContext())
                                .load(url)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .skipMemoryCache(true)
                                .override(500,500)
                                .fitCenter()
                                .dontAnimate()
                                .placeholder(R.drawable.elephant)
                                .into(img);
                    }
                };
                new Thread(getPicture).start();
            }
        }

        public synchronized void onClick(View v) {
            //showTip(id);
            //new Thread(opennews).start();
            Intent intent = new Intent(uiActivity.this, NewsActivity.class);
            intent.putExtra("NewsText", id);
            currentNews = this;
            showTip("current NEWs != null" + (currentNews != null));
            startActivityForResult(intent, OPENNEWS);
            //灰色在返回时修改
        }
        Runnable getPicture = new Runnable()
        {
            //获取网址url
            @Override
            public synchronized void run() {
                try {
                    NewsText itemText = news.getNewsText(id, uiActivity.this);
                    String recommend =  news.getRandPictures(itemText.Keywords);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("url", recommend);
                    msg.setData(bundle);
                    loadPic.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

     private class HomeAdapter extends RecyclerView.Adapter<mViewHolder>
    {
        static final int HEAD = 0;
        static final int FOOT = 1;
        static final int MIDDLE = 2;
        private int _count = 0;
        private List<NewsTitle.MyList> _list;
        void init(List<NewsTitle.MyList> e)
        {
            _count = e.size();
            _list = e;
        }
        @Override
        public int getItemViewType(int position)
        {

           if (position == 0)
               return HEAD;
            if (position == _count + 1)
                return FOOT;
            return MIDDLE;
        }
        @Override
        public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case HEAD:
                    return new mViewHolder(mheader);
                case FOOT:
                    return new mViewHolder(mfooter);
                case MIDDLE:
                    break;
            }
            LayoutInflater layoutInflater = LayoutInflater.from(uiActivity.this);
            View view = layoutInflater.inflate(R.layout.plain_recycler,parent,false);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(mViewHolder holder, int position) {
            if (position == 0 || position == _count + 1 || _count == 0)
                return;
            NewsTitle.MyList it = _list.get(position - 1);
            holder.set(it);
            //holder.index.setText("index:"+position);
        }



        @Override
        public int getItemCount() {
            return _count + 2;
        }

    }
}
