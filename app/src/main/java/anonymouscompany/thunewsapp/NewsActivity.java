package anonymouscompany.thunewsapp;


import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    BackendInter bi;
    mWbshare wbshareInstance;
    mTts ttsInstance;
    NewsText news;
    LinearLayout start,pause,share,collect;
    ExpandableListView keyword;
    boolean collected = false;
    boolean recommend = true;
    TextView title,tag,author,time,text;
    ImageView img;
    String sharemsg = "";
    Handler handler,shareHandler;
    private static final int loadFailed = 0, loadSuccess = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //先做一个加载界面
        setContentView(R.layout.loading);
        showTip("正在加载");
        handler = new Handler()  {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what == loadFailed)
                {
                    showTip((String)msg.obj);
                }
                else
                {
                    setContentView(R.layout.activity_news);
                    showTip("加载成功");
                    init();
                    if (bi.isCollectionNews(news.news_ID,NewsActivity.this))
                    {
                        collected = true;
                        ((ImageView)findViewById(R.id.s31)).setImageResource(R.drawable.collected);
                    }
                    setListeners();

                    final mAdapter adapter = new mAdapter();
                    adapter.init(news);
                    keyword.setAdapter(adapter);
                    keyword.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                            String search = (String)adapter.getChild(i,i1);
                            Intent intent = new Intent(NewsActivity.this, BaikeWebActivity.class);
                            intent.putExtra("keyword", search);
                            showTip("跳转到百度百科...");
                            startActivity(intent);
                            return true;
                        }
                    });
                    text.setText(news.news_Content);
                    tag.setText(news.newsClassTag);
                    time.setText(news.news_Time);
                    author.setText(news.news_Author);
                    title.setText(news.news_Title);
                    if (!news.news_Pictures.equals(""))
                    {
                        String[] pictures = news.news_Pictures.split(";");
                        if (!pictures[0].equals(""))
                        {
                            Glide.with(NewsActivity.this)
                                    .load(pictures[0])
                                    .fitCenter()
                                    .dontAnimate()
                                    .placeholder(R.drawable.code)
                                    .into(img);
                            showTip(pictures[0]);
                            Log.d("Picture",pictures[0] + "newsid:" +news.news_ID);
                            recommend = false;
                        }

                        //新闻列表图片加载
                    }
                    if (recommend)
                    {/*
                        NewsText.Keyword word = news.Keywords.get(0);
                        Glide.with(NewsActivity.this)
                                .load(bi.getRandPictures(word.word))
                                .fitCenter()
                                .dontAnimate()
                                .placeholder(R.drawable.code)
                                .into(img);
                        showTip("图片推荐算法，根据keyword="+word.word);*/
                    }
                }
            }
        };
        shareHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                showTip(sharemsg);
                Bitmap map = msg.getData().getParcelable("bitmap");
                wbshareInstance.setThumbImg(map);
                wbshareInstance.shareMessage();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bi = new BackendInter();
                    news = bi.getNewsText(getIntent().getStringExtra("NewsText"), NewsActivity.this);
                    Message msg = new Message();
                    msg.what = loadSuccess;
                    handler.sendMessage(msg);
                } catch (Exception ex) {
                    Message msg = new Message();
                    msg.obj = ex.toString();
                    msg.what = loadFailed;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }

    void init()
    {
        wbshareInstance = new mWbshare();
        ttsInstance = new mTts();
        keyword = (ExpandableListView) findViewById(R.id.keyword);
        title = (TextView) findViewById(R.id.newsTitle2);
        tag = (TextView) findViewById(R.id.newsTag2);
        author = (TextView) findViewById(R.id.newsAuthor2);
        time = (TextView) findViewById(R.id.newsTime2);
        text = (TextView) findViewById(R.id.newsText);
        img = (ImageView) findViewById(R.id.imgres2);
        start = (LinearLayout) findViewById(R.id.s1);
        pause = (LinearLayout) findViewById(R.id.s2);
        collect = (LinearLayout) findViewById(R.id.s3);
        share = (LinearLayout) findViewById(R.id.s4);

    }
    private static final int NOTINIT = 0,SPEAKING = 1,PAUSING = 2,STOP = 3;
    private int ttsState = NOTINIT;
    void setListeners()
    {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ttsState == NOTINIT || ttsState == STOP)
                {
                    ttsInstance.initEngine(NewsActivity.this, news.news_Content);
                    ttsInstance.startSpeaking();
                    //动画效果
                    ((ImageView)findViewById(R.id.s11)).setImageResource(R.drawable.pause);
                    ((TextView)findViewById(R.id.s12)).setText(R.string.暂停);
                    showTip("播放");
                    ttsState = SPEAKING;
                }
                else if (ttsState == SPEAKING)
                {
                    ttsInstance.pauseSpeaking();
                    ((ImageView)findViewById(R.id.s11)).setImageResource(R.drawable.play);
                    ((TextView)findViewById(R.id.s12)).setText(R.string.resume);
                    showTip("暂停");
                    ttsState = PAUSING;
                }
                else if (ttsState == PAUSING)
                {
                    ttsInstance.resumeSpeaking();
                    ((ImageView)findViewById(R.id.s11)).setImageResource(R.drawable.pause);
                    ((TextView)findViewById(R.id.s12)).setText(R.string.暂停);
                    showTip("继续");
                    ttsState = SPEAKING;
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ttsInstance.isSpeaking())
                {
                    ttsInstance.stopSpeaking();
                }
                ttsInstance.destroy();
                ttsState = STOP;
                showTip("结束TTS语音");
                ((ImageView)findViewById(R.id.s11)).setImageResource(R.drawable.play);
                ((TextView)findViewById(R.id.s12)).setText(R.string.播放);
            }
        });
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (collected)
                {
                    bi.delCollectionNews(news,NewsActivity.this);
                    collected = false;
                    ((ImageView)findViewById(R.id.s31)).setImageResource(R.drawable.favorites);
                }
                else if (!collected)
                {
                    bi.addCollectionNews(news, NewsActivity.this);
                    collected = true;
                    ((ImageView)findViewById(R.id.s31)).setImageResource(R.drawable.collected);
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wbshareInstance.init(NewsActivity.this);
                wbshareInstance.setShareInfo(news);
                showTip("正在生成分享，请等待...");
                new Thread(shareDownload).start();
            }
        });
    }

    Runnable shareDownload = new Runnable() {
        @Override
        public void run() {
            if (news.news_Pictures.equals(""))
            {
                sharemsg = "fail";
                return;
            }
            String[] pictures = news.news_Pictures.split(";");
            for (String picture:pictures)
            {
                if (!picture.equals(""))
                {
                    try{
                        Bitmap bitmap = BitmapFactory.decodeStream(getImageStream(picture));
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("bitmap",bitmap);
                        msg.setData(bundle);
                        sharemsg = "success";
                        shareHandler.sendMessage(msg);
                    }catch (Exception e)
                    {
                        sharemsg = "fail";
                    }
                    return;
                }
            }


        }
    };

    public InputStream getImageStream(String path) throws Exception
    {
        showTip("getImageStream +" + path);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }
    private void showTip(String s)
    {
        Toast.makeText(NewsActivity.this,s, Toast.LENGTH_LONG).show();
    }
    private class mAdapter extends BaseExpandableListAdapter {
        List<NewsText.Keyword> keywordList;
        List<NewsText.Person> persons,locations;
        private static final int KEYWORD = 0, PERSON = 1, LOCATION = 2;
        public void init(NewsText news)
        {
            keywordList = news.Keywords;
            persons = news.persons;
            locations = news.locations;
        }
        String getParentText(int i)
        {
            switch (i)
            {
                case KEYWORD:
                    return "Keywords";
                case PERSON:
                    return "Persons";
                case LOCATION:
                    return  "Locations";
                default:
                    return "default";
            }
        }
        @Override
        public int getGroupCount() {
            return 3;
        }

        @Override
        public int getChildrenCount(int i) {
            switch (i)
            {
                case KEYWORD:
                    return keywordList.size();
                case PERSON:
                    return persons.size();
                case LOCATION:
                    return locations.size();
                default:
                    return 0;
            }
        }

        @Override
        public Object getGroup(int i) {
            switch (i)
            {
                case KEYWORD:
                    return keywordList;
                case PERSON:
                    return persons;
                case LOCATION:
                    return locations;
                default:
                    return null;
            }
        }

        @Override
        public Object getChild(int i, int i1) {
            switch (i)
            {
                case KEYWORD:
                    return keywordList.get(i1).word;
                case PERSON:
                    return persons.get(i1).word;
                case LOCATION:
                    return locations.get(i1).word;
                default:
                    return null;
            }
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i * i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            View res = LayoutInflater.from(NewsActivity.this).inflate(R.layout.expand_item, viewGroup,false);
            TextView text = res.findViewById(R.id.keys);
            if (text!=null)
            text.setText(getParentText(i));
            else
                showTip("cant find textview");
            return res;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            View res = LayoutInflater.from(NewsActivity.this).inflate(R.layout.expand_item, viewGroup,false);
            TextView text = res.findViewById(R.id.keys);
            if (text!=null)
            {
                if (i == KEYWORD)
                {
                    text.setText(keywordList.get(i1).word);
                }
                else if (i == PERSON)
                {
                    text.setText(persons.get(i1).word);
                }
                else
                    text.setText(locations.get(i1).word);
            }
            else
                showTip("cant find textview");
            return res;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}
