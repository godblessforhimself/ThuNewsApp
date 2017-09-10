package anonymouscompany.thunewsapp;


import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import java.util.ArrayList;
import java.util.List;

public class uiTestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<NewsTitle.MyList> mNews;
    private int newsNum, currentPage,pageSize;
    private HomeAdapter mAdapter;
    private PullToRefreshView mPullToRefreshView;
    private View mheader,mfooter;
    private void showTip(String s)
    {
        Toast.makeText(uiTestActivity.this,s, Toast.LENGTH_SHORT).show();
    }
    public void addNews()
    {
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
        Log.d("EMPTY","mheader" + ((mheader == null) ? "is empty" : " not empty"));
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mNews.addAll((List) msg.getData().getParcelableArrayList("news"));
            newsNum = mNews.size();
            currentPage ++;
            mAdapter.init(mNews);
            showTip("News fetch" + newsNum);
            mAdapter.notifyDataSetChanged();
        }
    };
    Runnable netWorkTask = new Runnable() {
        @Override
        public void run() {
            BackendInter news = new BackendInter();
            Message msg = new Message();
            Bundle data = new Bundle();
            List<NewsTitle.MyList> e;
            try{
                e = news.getNewsTitle(currentPage,pageSize,uiTestActivity.this).list;
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

        setContentView(R.layout.ui_test);
        init();
        setTitle("uiTestActivity");
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
        Button add = (Button)findViewById(R.id.add_button);

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNews();
            }
        });
        Button delete = (Button)findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.notifyDataSetChanged();

            }
        });
    }
    class mViewHolder extends RecyclerView.ViewHolder
    {
        TextView title,tag,author,time,intro,index;
        ImageView img;
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
        }

        public void set(NewsTitle.MyList it)
        {
            if (itemView == mheader || itemView == mfooter)
                return;
            title.setText(it.news_Title);
            intro.setText("  "+it.news_Intro);
            author.setText(it.news_Author);
            tag.setText(it.newsClassTag);
            time.setText(it.news_Time);
            //img.setImageURI(Uri.fromFile(new File(it.news_Pictures)));
        }
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
            LayoutInflater layoutInflater = LayoutInflater.from(uiTestActivity.this);
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
