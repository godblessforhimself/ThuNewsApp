package anonymouscompany.thunewsapp;

import android.content.Context;

import java.util.List;

/**
 * Created by LTL on 2017/9/8.
 */

public interface BackendInterface
{
    NewsTitle getNewsTitle(int page, int pagesize, int category,Context context)  throws Exception;//返回新闻标题.category=0为不分类
    NewsTitle getNewsTitle(String news_ID,Context context) throws Exception;
    NewsText getNewsText(String news_ID, Context context)  throws Exception;//返回新闻详情
    NewsTitle getCollectionNews(Context context);//返回收藏的所有新闻标题
    void addCollectionNews(NewsText text, Context context);//收藏新闻
    void delCollectionNews(NewsText text, Context context);//删除收藏新闻
    boolean isCollectionNews(String news_ID,Context context);//是否被收藏过
    void addShiledWord(String shieldword, Context context);//添加屏蔽词
    void delShiledWord(String shieldword,Context context);//删除屏蔽词
    void setPicturesDisplay(int type, Context context);//保持图片配置信息，type=0显示图片，type=1隐藏图片
    int getPicturesDisplay(Context context);//获得图片配置信息
    void viewed(NewsText text, Context context);//已看过新闻
    boolean isviewed(String news_ID,Context context);//是否看过
    NewsTitle searchNewsTitel(String keyword,int page, int pagesize, int category,Context context) throws Exception;//搜索新闻
    NewsTitle likeNewsTitel(Context context) throws Exception;//推荐新闻
    void clearAllInfo(Context context);//清空缓存
    void setNight(int type, Context context);//保持夜间模式配置信息
    int getNight(Context context);//获得是否为夜间模式
    String getRandPictures(String keyword);//获得推荐图片
}

