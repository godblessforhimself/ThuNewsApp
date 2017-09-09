package anonymouscompany.thunewsapp;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import java.util.ArrayList;

/**
 * Created by Tony on 2017/9/8.
 */
interface mWbshareInterface
{
    //微博分享的回调函数由mWbshare类实现，不开放接口，要改在mWbshare类中修改。
    void init(Context context);//初始化
    void setText(String text);//设置分享的文字
    void setUrl(String url);//设置分享的网址url
    void setImgs(ArrayList<Uri> pictures);//设置分享的图片，必须是本地图片，uri是资源标识符，通过图片地址转换
    void shareMessage();//生成微博分享
}

public class mWbshare implements mWbshareInterface,WbShareCallback
{
    private String mText,mUrl;
    private Context mContext;
    private ArrayList<Uri> mImgs;
    private WbShareHandler shareHandler;

    @Override
    public void init(Context context) {
        mContext = context;
        WbSdk.install(context, new AuthInfo(context,"3261203981","http://www.baidu.com",""));
        shareHandler = new WbShareHandler((Activity)context);
        shareHandler.registerApp();
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

    @Override
    public void shareMessage() {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (mText != null)
        {
            TextObject textObject = new TextObject();
            textObject.text = mText;
            textObject.title = "title";
            weiboMessage.textObject = textObject;
        }
        if(mUrl != null)
        {
            BaseMediaObject mediaObject = new BaseMediaObject() {
                @Override
                public int getObjType() {
                    return 0;
                }

                @Override
                protected BaseMediaObject toExtraMediaObject(String s) {
                    return null;
                }

                @Override
                protected String toExtraMediaString() {
                    return null;
                }
            };
            mediaObject.actionUrl = mUrl;
            weiboMessage.mediaObject = mediaObject;
        }
        if (mImgs != null)
        {
            MultiImageObject multiImageObject = new MultiImageObject();
            multiImageObject.setImageList(mImgs);
            weiboMessage.multiImageObject = multiImageObject;
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
