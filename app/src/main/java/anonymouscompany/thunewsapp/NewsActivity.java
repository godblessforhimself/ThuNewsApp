package anonymouscompany.thunewsapp;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Exchanger;

public class NewsActivity extends AppCompatActivity {
    BackendInter bi = new BackendInter();
    NewsText news = null;
    Toolbar toolbar = null;
    ImageButton share = null;
    Switch switchbutton = null;
    mWbshare wbshareInstance = new mWbshare();
    ImageView tts;
    TextView title,tag,author,time,text;
    ImageView img;
    String sharemsg = "";
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
    Handler shareHandler = new Handler()
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
    public InputStream getImageStream(String path) throws Exception{
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tts = (ImageView)findViewById(R.id.tts_button);
        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mTts mtts = new mTts();
                mtts.initEngine(NewsActivity.this,news.news_Content);
                mtts.startSpeaking();

            }
        });

        ImageButton imagebutton = (ImageButton)findViewById(R.id.imageButton2);
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        share = (ImageButton)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wbshareInstance = new mWbshare();
                wbshareInstance.init(NewsActivity.this);
                wbshareInstance.setShareInfo(news);
                showTip("正在生成分享，请等待...");
                new Thread(shareDownload).start();
            }
        });

        switchbutton = (Switch) findViewById(R.id.switch1);
        switchbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    bi.addCollectionNews(news, NewsActivity.this);
                } else {
                    bi.delCollectionNews(news, NewsActivity.this);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    news = (new BackendInter()).getNewsText(getIntent().getStringExtra("NewsText"), NewsActivity.this);

                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception ex) {
                    Message msg = new Message();
                    msg.obj = ex.toString();
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }
    Handler handler = new Handler()  {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                showTip((String)msg.obj);
            } else {
                toolbar.setTitle(news.news_Title);
                setSupportActionBar(toolbar);
                title = (TextView) findViewById(R.id.newsTitle2);
                tag = (TextView) findViewById(R.id.newsTag2);
                author = (TextView) findViewById(R.id.newsAuthor2);
                time = (TextView) findViewById(R.id.newsTime2);
                text = (EditText) findViewById(R.id.newsText);
                img = (ImageView) findViewById(R.id.imgres2);
                switchbutton.setChecked(bi.isCollectionNews(news.news_ID, NewsActivity.this));
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
                                .override(300, 200)
                                .fitCenter()
                                .dontAnimate()
                                .placeholder(R.drawable.code)
                                .into(img);
                        showTip(pictures[0]);
                        Log.d("Picture",pictures[0] + "newsid:" +news.news_ID);
                    }

                    //新闻列表图片加载
                }
            }
        }
    };
}
