package anonymouscompany.thunewsapp;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Exchanger;

public class NewsActivity extends AppCompatActivity {
    BackendInter bi = new BackendInter();
    NewsText news = null;
    Toolbar toolbar = null;
    ImageButton share = null;
    Switch switchbutton = null;

    TextView title,tag,author,time,text;
    ImageView img;
    private void showTip(String s)
    {
        Toast.makeText(NewsActivity.this,s, Toast.LENGTH_LONG).show();
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

                        text.setText(news.news_Content);
                        tag.setText(news.newsClassTag);
                        time.setText(news.news_Time);
                        author.setText(news.news_Author);
                        title.setText(news.news_Title);
                        if (!news.news_Pictures.equals("")) {

                        }
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

            }
        });

        switchbutton = (Switch) findViewById(R.id.switch1);
        //switchbutton.setChecked();
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


    }
}
