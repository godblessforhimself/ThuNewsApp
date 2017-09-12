package anonymouscompany.thunewsapp;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import java.util.ArrayList;

/**
 * Created by Tony on 2017/9/8.
 */
interface mWbshareInterface
{
    //微博分享的回调函数由mWbshare类实现，不开放接口，要改在mWbshare类中修改。
    void init(Activity context);//初始化
    void setShareInfo(NewsText news);
    void setText(String text);//设置分享的文字
    void setUrl(String url);//设置分享的网址url
    void setThumbImg(Bitmap img);
    void setImgs(ArrayList<Uri> pictures);//设置分享的图片，必须是本地图片，uri是资源标识符，通过图片地址转换
    void shareMessage();//生成微博分享
}

public class mWbshare implements mWbshareInterface,WbShareCallback
{
    private String mText,mUrl;
    private Context mContext;
    private ArrayList<Uri> mImgs;
    //缩略图
    private Bitmap thumbImg;
    private WbShareHandler shareHandler;

    @Override
    public void init(Activity context) {
        mContext = context;
        WbSdk.install(context, new AuthInfo(context,"3261203981","http://www.baidu.com",""));
        shareHandler = new WbShareHandler(context);
        boolean reg = shareHandler.registerApp();
        showTip("register = " + (reg ? "true" : "false"));
    }

    @Override
    public void setShareInfo(NewsText news)
    {
        String text = "标题:" + news.news_Title + " 简介:" + news.news_Content;
        setText(text);
        String url = news.news_URL;
        setUrl(url);
    }

    @Override
    public void setText(String text) {
        mText = text;
    }

    @Override
    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public void setImgs(ArrayList<Uri> pictures) {
        mImgs = pictures;
    }
    public void setThumbImg(Bitmap im)
    {
        thumbImg = im;
    }
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title ="mediaObject title";
        mediaObject.description = "描述";
       // Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bird);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        //mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = mUrl;
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }
    @Override
    public void shareMessage() {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (mText != null)
        {
            TextObject textObject = new TextObject();
            textObject.text = mText.substring(0,100)+"...";
            textObject.title = "title";
            weiboMessage.textObject = textObject;
        }
        if (mUrl != null)
        {
            weiboMessage.mediaObject = getWebpageObj();
        }
        if (mImgs != null)
        {
            MultiImageObject imgObj = new MultiImageObject();
            imgObj.setImageList(mImgs);
            weiboMessage.multiImageObject = imgObj;
        }
        if (thumbImg != null)
        {
            MultiImageObject imgObj = new MultiImageObject();
            imgObj.setThumbImage(thumbImg);
            weiboMessage.multiImageObject = imgObj;
        }
        shareHandler.shareMessage(weiboMessage, false);
    }

    @Override
    public void onWbShareSuccess() {
        showTip("分享成功，返回");
    }

    @Override
    public void onWbShareCancel() {
        showTip("分享取消，返回");
    }

    @Override
    public void onWbShareFail() {
        showTip("分享失败，返回");
    }
    private void showTip(String tips)
    {
        Toast.makeText(mContext,tips,Toast.LENGTH_SHORT).show();
    }
}
