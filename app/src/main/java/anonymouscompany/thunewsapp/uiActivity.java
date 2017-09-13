package anonymouscompany.thunewsapp;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.io.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yalantis.phoenix.PullToRefreshView;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO;
import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES;

public class uiActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static BackendInter news = new BackendInter();
    private RecyclerView recyclerView;
    private List<NewsTitle.MyList> mNews, mNewsBackup;
    private int newsNum, currentPage,pageSize;

    private HomeAdapter mAdapter;
    private PullToRefreshView mPullToRefreshView;
    private TabLayout tabLayout;
    private View mheader,mfooter;
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
        Toast.makeText(uiActivity.this,s, Toast.LENGTH_SHORT).show();
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
        new Thread(netWorkTask).start();
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
        refreshoradd = ADD;
        showTip("addNews, you should set some view changed...");
        new Thread(netWorkTask).start();
    }
    public void init()
    {
        mNews = new ArrayList<NewsTitle.MyList>();
        newsNum = 0;
        currentPage = 1;
        pageSize = 10;
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
        public void handleMessage(Message msg) {
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
            showTip("News fetch finish, total num:" + newsNum);
        }
    };
    //所有runnable，非主线程都不能使用showtip等改变ui的函数，替代办法写到handler handleMessage中。
    //runnable结束调用handler更新ui
    Runnable netWorkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            List<NewsTitle.MyList> e;
            try{
                if (issearching == 0) {
                    e = news.getNewsTitle(currentPage, pageSize, cate, uiActivity.this).list;
                } else {
                    e = news.searchNewsTitel(keyword, issearching, pageSize, cate, uiActivity.this).list;
                }
                data.putParcelableArrayList("news",(ArrayList)e);
                msg.setData(data);
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (Exception ex)
            {
                Log.d("exception",ex.toString());
            }

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
                if (dy * dy > 100)
                {
                    Glide.with(uiActivity.this).pauseRequests();
                }
                else
                {
                    Glide.with(uiActivity.this).resumeRequests();
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
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    //刷新结束调用run
                    public void run() {
                        showTip("Refresh finish...");
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        LinearLayout blay1 = (LinearLayout) findViewById(R.id.blay1);
        blay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView iv = (ImageView) findViewById(R.id.bmenu_img2);
                if (isfavourites == 1) {
                    currentPage--;
                    iv.setImageResource(R.drawable.favorite);
                    isfavourites = 0;
                }
                mPullToRefreshView.setRefreshing(true);
                refresh();
            }
        });

        LinearLayout blay2 = (LinearLayout) findViewById(R.id.blay2);
        blay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        LinearLayout blay3 = (LinearLayout) findViewById(R.id.blay3);
        blay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            public void onClick(View view) {
                ImageView iv = (ImageView) findViewById(R.id.bmenu_img2);
                if (isfavourites == 1) {
                    mNews.clear();
                    iv.setImageResource(R.drawable.favorite);
                    isfavourites = 0;
                }

                mPullToRefreshView.setRefreshing(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        refreshoradd = REFRESH;
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        List<NewsTitle.MyList> e = new ArrayList<NewsTitle.MyList>();
                        try{
                            suggestlock.lock();
                            e.addAll(suggest.subList(0, pageSize));
                            for (int i = 0; i < pageSize; i++) {
                                suggest.remove(0);
                            }
                            suggestlock.unlock();
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
                    }
                }).start();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (suggestlock.tryLock()) {
                        suggest.clear();
                        suggest.addAll(news.likeNewsTitel(uiActivity.this).list);
                        suggestlock.unlock();
                    }
                } catch (Exception ex)
                {
                }
            }
        }).start();


        LinearLayout blay5 = (LinearLayout) findViewById(R.id.blay5);
        blay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(uiActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        TabLayout tl = (TabLayout) findViewById(R.id.headTab);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
    public boolean onQueryTextChange(String query) {
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
    public boolean onQueryTextSubmit(String query) {
        showTip(query);
        keyword = query;
        if (query.equals("")) {
            issearching = 0;
            currentPage--;
        } else {
            issearching = 1;
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
            id = it.news_ID;
            title.setText(it.news_Title);
            intro.setText(it.news_Intro);
            tag.setText("# "+it.newsClassTag);
            people.setText(it.news_Author);
            img.setImageURI(Uri.fromFile(new File(it.news_Pictures)));

            if (itemView == mheader || itemView == mfooter)
                return;
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
                needRecommend = false;
            if (!it.news_Pictures.equals("") && needRecommend)
            {
                String[] pictures = it.news_Pictures.split(";");
                //显示第一张
                if (!pictures[0].equals(""))
                {
                    Glide.with(uiActivity.this)
                            .load(pictures[0])
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
                    public void handleMessage(Message msg)
                    {
                        String url = msg.getData().getString("url");
                        Glide.with(uiActivity.this)
                                .load(url)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .dontAnimate()
                                .placeholder(R.drawable.elephant)
                                .into(img);
                    }
                };
                new Thread(getPicture).start();
            }
        }

        public void onClick(View v) {
            //showTip(id);
            new Thread(opennews).start();
        }

        Handler hd = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if(news.isviewed(id, uiActivity.this)) {
                        title.setTextColor(Color.GRAY);
                        intro.setTextColor(Color.GRAY);
                    } else {
                        title.setTextColor(index.getTextColors());
                        intro.setTextColor(index.getTextColors());
                    }
                } else {
                    showTip((String)msg.obj);
                }
            }
        };

        Runnable opennews = new Runnable() {
            @Override
            public void run() {
                try {
                    news.viewed(news.getNewsText(id, uiActivity.this), uiActivity.this);
                    Intent intent = new Intent(uiActivity.this, NewsActivity.class);
                    intent.putExtra("NewsText", id);
                    Message msg = new Message();
                    msg.what = 0;
                    hd.sendMessage(msg);
                    startActivity(intent);
                } catch (Exception ex) {
                    Log.d("exception",ex.toString());
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = ex.toString();
                    hd.sendMessage(msg);
                }
            }
        };
        Runnable getPicture = new Runnable()
        {
            //获取网址url
            @Override
            public void run() {
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
