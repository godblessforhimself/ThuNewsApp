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

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class uiTestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<NewsTitle.MyList> mNews;
    private int newsNum, currentPage,pageSize;
    private HomeAdapter mAdapter;
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
        init();
        setContentView(R.layout.ui_test);
        setTitle("uiTestActivity");
        recyclerView = (RecyclerView)findViewById(R.id.recycle_0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        mAdapter = new HomeAdapter();
        recyclerView.setAdapter(mAdapter);
        Button add = (Button)findViewById(R.id.add_button);

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
            title.setText(it.news_Title);
            intro.setText("  "+it.news_Intro);
            author.setText(it.news_Author);
            tag.setText(it.newsClassTag);
            time.setText(it.news_Time);
            //holder.img.setImageURI(Uri.fromFile(new File(it.news_Pictures)));
        }
    }
    class HomeAdapter extends RecyclerView.Adapter<mViewHolder>
    {
        private int _count = 0;
        private List<NewsTitle.MyList> _list;

        public void init(List<NewsTitle.MyList> e)
        {
            _count = e.size();
            _list = e;
        }
        @Override
        public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(uiTestActivity.this);
            View view = layoutInflater.inflate(R.layout.plain_recycler,parent,false);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(mViewHolder holder, int position) {
            NewsTitle.MyList it = _list.get(position);
            holder.set(it);
            holder.index.setText("index:"+position);
        }



        @Override
        public int getItemCount() {
            return _count;
        }
    }
}
