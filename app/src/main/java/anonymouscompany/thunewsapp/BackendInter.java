package anonymouscompany.thunewsapp;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by LTL on 2017/9/8.
 */

public class BackendInter implements  BackendInterface
{
    public  NewsTitle getNewsTitle(int page, int pagesize, Context context) throws Exception
    {
        NewsTitle title;
        String str=ReversedNews.getReversedNews(page,pagesize);
        title=JasonClass.StringtoJson(str,NewsTitle.class);
        NewsText text=getNewsText(title,context);
        if(Storage.isShield(text,context))
        {
            Exception e=new Exception();
            throw e;
        }
        return title;
    }
    public NewsTitle getNewsTitle(String news_ID,Context context) throws Exception
    {
        NewsText text=getNewsText(news_ID,context);
        if(Storage.isShield(text,context))
        {
            Exception e=new Exception();
            throw e;
        }
        NewsTitle title=new NewsTitle(text);
        return title;
    }
    public NewsText getNewsText(String news_ID,Context context) throws Exception
    {
        String oncesee=ConfigI.load(news_ID,context);
        NewsText text;
        if (oncesee.equals("1")) text=Storage.findText(news_ID,context);
        else
        {
            String str=ReversedNews.getReversedNewsText(news_ID);
            text=JasonClass.StringtoJson(str,NewsText.class);
        }
        return text;
    }
    public NewsText getNewsText(NewsTitle title,Context context) throws Exception
    {
        return getNewsText(title.list.get(0).news_ID,context);
    }
    public List<NewsTitle> getCollectionNews(Context context)
    {
        return Storage.findCollectionNews(context);
    }
    public void addCollectionNews(NewsText text,Context context)
    {
        viewed(text,context);
        NewsTitle title=new NewsTitle(text);
        Storage.addCollectionFile(title,context);
    }
    public void delCollectionNews(NewsText text ,Context context)
    {
        NewsTitle title=new NewsTitle(text);
        Storage.delCollectionFile(title,context);
    }
    public  void addShiledWord(String shieldword,Context context)
    {
        Storage.addshieldwordsFile(shieldword,context);
    }
    public  void delShiledWord(String shieldword,Context context)
    {
        Storage.delshieldFile(shieldword,context);
    }
    public void setPicturesDisplay(int type,Context context)
    {
        ConfigI.Save("PicturesDisplay",Integer.toString(type),context);
    }
    public int getPicturesDisplay(Context context)
    {
        return Integer.parseInt(ConfigI.load("PicturesDisplay",context));
    }
    public void viewed(NewsText text,Context context)
    {
        if (ConfigI.load(text.news_ID,context).equals("1")) return;
        ConfigI.Save(text.news_ID,"1",context);
        Storage.addTextFile(text,context);
     }
    public boolean isviewed(String news_ID,Context context)
    {
        if (ConfigI.load(news_ID,context).equals("1")) return true;
        return false;
    }
    public List<NewsTitle> searchNewsTitel(String keyword,Context context) throws  Exception
    {
        String str=ReversedNews.getReversedSearchNews(keyword);
        List<NewsTitle> list=JasonClass.StringtoJson(str,List.class);
        List<NewsTitle> newlist=new LinkedList<NewsTitle>();
        for (int i=0;i<list.size();i++)
        {
                NewsText text=getNewsText(list.get(i),context);
                if (Storage.isShield(text,context)) continue;
                newlist.add(list.get(i));
        }
        return newlist;
    }
}
