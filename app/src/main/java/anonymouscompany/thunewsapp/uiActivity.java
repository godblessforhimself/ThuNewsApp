package anonymouscompany.thunewsapp;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.io.*;

import com.yalantis.phoenix.PullToRefreshView;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import java.util.ArrayList;
import java.util.List;

public class uiActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static BackendInter news = new BackendInter();
    private RecyclerView recyclerView;
    private List<NewsTitle.MyList> mNews;
    private int newsNum, currentPage,pageSize;
    private HomeAdapter mAdapter;
    private PullToRefreshView mPullToRefreshView;
    private View mheader,mfooter;
    private int refreshoradd = 0;
    private int isonsearchorfavourite = 0;
    private SearchView mSearchView = null;
    private void showTip(String s)
    {
        Toast.makeText(uiActivity.this,s, Toast.LENGTH_SHORT).show();
    }
    private void refresh()
    {
        refreshoradd = 1;
        new Thread(netWorkTask).start();
    }
    public void addNews()
    {
        refreshoradd = 0;
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

    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (refreshoradd == 0) {
                mNews.addAll((List) msg.getData().getParcelableArrayList("news"));
                newsNum = mNews.size();
                currentPage ++;
                mAdapter.init(mNews);
                showTip("News fetch" + newsNum);
                mAdapter.notifyDataSetChanged();
            } else {
                mNews.addAll(0, (List) msg.getData().getParcelableArrayList("news"));
                newsNum = mNews.size();
                currentPage ++;
                mAdapter.init(mNews);
                showTip("News fetch" + newsNum);
                mAdapter.notifyDataSetChanged();
            }
        }
    };
    Runnable netWorkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            List<NewsTitle.MyList> e;
            try{
                e = news.getNewsTitle(currentPage,pageSize,0,uiActivity.this).list;
                data.putParcelableArrayList("news",(ArrayList)e);
                msg.setData(data);
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
        recyclerView = (RecyclerView)findViewById(R.id.recycle_0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !recyclerView.canScrollVertically(1))
                {
                    addNews();
                    showTip("loading more news");
                }
            }
        });
        mAdapter = new HomeAdapter();
        recyclerView.setAdapter(mAdapter);
        addNews();

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        news.setPicturesDisplay(0, uiActivity.this);
        try {
            mSearchView = (SearchView) findViewById(R.id.searchview);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setSubmitButtonEnabled(true);
            mSearchView.setQueryHint("Search");
        } catch (Exception ex) {
            showTip(ex.toString());
        }

    }

    //用户输入字符时激发该方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return true;
    }
    //单击搜索按钮时激发该方法
    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        showTip(query);
        if (mSearchView != null) {
            // 得到输入管理对象
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
            }
            mSearchView.clearFocus(); // 不获取焦点
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.D_N) {

        } else if (id == R.id.Favourites) {

        } else if (id == R.id.Marks) {
            showTip("Marks");
            if (findViewById(R.id.MarksBoxs).getVisibility() == View.VISIBLE) {
                findViewById(R.id.MarksBoxs).setVisibility(View.GONE);
            } else {
                findViewById(R.id.MarksBoxs).setVisibility(View.VISIBLE);
            }
        }

        return true;
    }



    class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title,tag,author,time,intro,index;
        ImageView img;
        String id = "";
        public mViewHolder(View itemView) {
            super(itemView);
            if (itemView == mheader || itemView == mfooter)
                return;
            title = itemView.findViewById(R.id.newsTitle);
            tag = itemView.findViewById(R.id.newsTag);
            author = itemView.findViewById(R.id.newsAuthor);
            time = itemView.findViewById(R.id.newsTime);
            intro = itemView.findViewById(R.id.newsInfo);
            index = itemView.findViewById(R.id.newsIndex);
            img = itemView.findViewById(R.id.imgres);
            itemView.setOnClickListener(this);
        }

        public void set(NewsTitle.MyList it)
        {
            title.setText(it.news_Title);
            intro.setText("  "+it.news_Intro);
            author.setText(it.news_Author);
            tag.setText(it.newsClassTag);
            time.setText(it.news_Time);
            img.setImageURI(Uri.fromFile(new File(it.news_Pictures)));
            id = it.news_ID;

            if (itemView == mheader || itemView == mfooter)
                return;
            if(news.isviewed(id, uiActivity.this)) {
                title.setTextColor(Color.GRAY);
                intro.setTextColor(Color.GRAY);
            } else {
                title.setTextColor(Color.BLACK);
                intro.setTextColor(Color.BLACK);
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
                        title.setTextColor(Color.BLACK);
                        intro.setTextColor(Color.BLACK);
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
    }
    class HomeAdapter extends RecyclerView.Adapter<mViewHolder>
    {
        public static final int HEAD = 0;
        public static final int FOOT = 1;
        public static final int MIDDLE = 2;
        private int _count = 0;
        private List<NewsTitle.MyList> _list;
        public void init(List<NewsTitle.MyList> e)
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
            holder.index.setText("index:"+position);
        }



        @Override
        public int getItemCount() {
            return _count + 2;
        }

    }
}
