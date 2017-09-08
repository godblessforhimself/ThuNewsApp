package anonymouscompany.thunewsapp;

import android.content.Context;

import java.util.List;

/**
 * Created by LTL on 2017/9/8.
 */

public interface BackendInterface
{
    NewsTitle getNewsTitle(int page, int pagesize, Context context)  throws Exception;//返回新闻标题，
    NewsText getNewsText(NewsTitle title, Context context)  throws Exception;//返回新闻详情
    List<NewsTitle> getCollectionNews(Context context);//返回收藏的所有新闻标题
    void addCollectionNews(NewsTitle title, Context context);//收藏新闻
    void delCollectionNews(NewsTitle title, Context context);//删除收藏新闻
    void addShiledWord(String keyword, Context context);//添加屏蔽词
    void setPicturesDisplay(int type, Context context);//保持图片配置信息，type=0显示图片，type=1隐藏图片
    int getPicturesDisplay(Context context);//获得图片配置信息
    void viewed(NewsTitle title, NewsText text, Context context);//已看过新闻
}

