package anonymouscompany.thunewsapp;

import android.content.Context;

import java.util.List;

/**
 * Created by LTL on 2017/9/8.
 */

public interface BackendInterface
{
    NewsTitle getNewsTitle(int page, int pagesize, Context context)  throws Exception;//返回新闻标题
    NewsTitle getNewsTitle(String news_ID,Context context) throws Exception;
    NewsText getNewsText(String news_ID, Context context)  throws Exception;//返回新闻详情
    NewsText getNewsText(NewsTitle title, Context context)  throws Exception;
    List<NewsTitle> getCollectionNews(Context context);//返回收藏的所有新闻标题
    void addCollectionNews(NewsText text, Context context);//收藏新闻
    void delCollectionNews(NewsText text, Context context);//删除收藏新闻
    void addShiledWord(String keyword, Context context);//添加屏蔽词
    void setPicturesDisplay(int type, Context context);//保持图片配置信息，type=0显示图片，type=1隐藏图片
    int getPicturesDisplay(Context context);//获得图片配置信息
    void viewed(NewsText text, Context context);//已看过新闻
}

