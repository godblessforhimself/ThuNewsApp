package anonymouscompany.thunewsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BaikeWebActivity extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent =this.getIntent();
        String keyword=intent.getStringExtra("keyword");
        setContentView(R.layout.activity_baike_web);
        getSupportActionBar().hide();
        webView=(WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);//
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });//当需要从一个网页跳转到另一个网页时，目标网页仍在当前WebView中显示而不是打开浏览器
        webView.loadUrl("https://baike.baidu.com/item/"+keyword);
    }
}
