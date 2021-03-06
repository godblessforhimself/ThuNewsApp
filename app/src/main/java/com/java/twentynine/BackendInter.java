package com.java.twentynine;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LTL on 2017/9/8.
 */

public class BackendInter implements  BackendInterface
{

    public  NewsTitle getNewsTitle(int page, int pagesize,int catagory, Context context) throws Exception
    {
        NewsTitle title=null;
        try {
            if (ReversedNews.isConnection(context))
            {
                String str = ReversedNews.getReversedNews(page, pagesize, catagory);
                title = JasonClass.StringtoJson(str, NewsTitle.class);
             }else{
                title=Storage.findTitle(context);
            }
            for (int i=0;i<title.list.size();i++)
            {
                if (Storage.isShield(title.list.get(i).news_Title, context))
                {
                    title.list.remove(i);
                    i--;
                }
            }
        }catch (IOException e)
        {

        }
        if (title==null) Log.d("ltl","gettitel");
        return title;
    }
    public NewsTitle getNewsTitle(String news_ID,Context context) throws Exception
    {
        NewsText text=getNewsText(news_ID,context);
        if(Storage.isShield(text.news_Title,context))
        {
            Exception e=new Exception();
            throw e;
        }
        NewsTitle title=new NewsTitle(text);
        return title;
    }
    public String cleanContent(String content)
    {
        char ch=12288;
        String newString="";
        if (content.charAt(0)!=ch) newString+=""+ch+ch;
        int i=0;
        while (i<content.length())
        {
            int j=i;
            while (j<content.length()&&content.charAt(j)==ch) j++;
            if (j-i>=2&&i!=0)
            {
                newString+="\n"+ch+ch;
                i=j;
                continue;
            }
            newString+=content.charAt(i++);
        }
        return newString;
    }
    public NewsText getNewsText(String news_ID,Context context) throws Exception
    {
        String oncesee=ConfigI.load(news_ID,context);
        NewsText text;
        if (!oncesee.equals("0")) text=Storage.findText(news_ID,context);
        else
        {
            if (!ReversedNews.isConnection(context))
            {
                throw  new Exception();
            }
            String str=ReversedNews.getReversedNewsText(news_ID);
            text=JasonClass.StringtoJson(str,NewsText.class);
            text.news_Content=cleanContent(text.news_Content);
        }
        return text;
    }
    public NewsTitle getCollectionNews(Context context)
    {
        return Storage.findCollectionNews(context);
    }
    public void addCollectionNews(NewsText text,Context context)
    {
       if (ConfigI.load(text.news_ID,context).equals("2")) return;
        viewed(text,context);
        NewsTitle title=new NewsTitle(text);
        ConfigI.Save(text.news_ID,"2",context);
        Storage.addCollectionFile(title,context);
    }
    public void delCollectionNews(NewsText text ,Context context)
    {
        NewsTitle title=new NewsTitle(text);
        ConfigI.Save(text.news_ID,"1",context);
        Storage.delCollectionFile(text.news_ID,context);
    }
    public boolean isCollectionNews(String news_ID,Context context)
    {
        if (ConfigI.load(news_ID,context).equals("2")) return true;
        return false;
    }
    public  void addShiledWord(String shieldword,Context context)
    {
        Storage.addshieldwordsFile(shieldword,context);
    }
    public  void delShiledWord(String shieldword,Context context)
    {
        Storage.delshieldFile(shieldword,context);
    }
    public List<String> getShiledWord(Context context)
    {
        return Storage.getShieldWords(context);
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
        if (!ConfigI.load(text.news_ID,context).equals("0")) return;
        ConfigI.Save(text.news_ID,"1",context);
        for (int i=0;i<text.Keywords.size();i++)
        {
            double sorce=text.Keywords.get(i).score+Double.parseDouble(ConfigI.load(text.Keywords.get(i).word,context));
            ConfigI.Save(text.Keywords.get(i).word,Double.toString(sorce),context);
            double max=Double.parseDouble(ConfigI.load("maxkeyword",context));
            if (sorce>max)
            {
                ConfigI.Save("maxkeyword",Double.toString(sorce),context);
                ConfigI.Save("LTL",text.Keywords.get(i).word,context);
            }
        }
        Storage.addTextFile(text,context);
     }
    public boolean isviewed(String news_ID,Context context)
    {
        if (!ConfigI.load(news_ID,context).equals("0")) return true;
        return false;
    }
    public NewsTitle searchNewsTitel(String keyword,int page, int pagesize, int category,Context context) throws Exception
    {
        if (!ReversedNews.isConnection(context))
        {
            throw new Exception();
        }
        String str=ReversedNews.getReversedSearchNews(keyword,page,pagesize,category);
        NewsTitle title=JasonClass.StringtoJson(str,NewsTitle.class);
        for (int i=0;i<title.list.size();i++)
        {
                if (Storage.isShield(title.list.get(i).news_Title,context))
                {
                    title.list.remove(i);
                    i--;
                }
        }
        return title;
    }
    public NewsTitle likeNewsTitel(Context context) throws Exception
    {
       /* NewsTitle title=getNewsTitle(5,50,0,context);
        for (int i=0;i<title.list.size();i++)
        {
            NewsText text=getNewsText(title.list.get(i).news_ID,context);
            double sorce=0;
            int len=20;
            if (text.Keywords.size()<len) len=text.Keywords.size();
            for (int j=0;j<len;j++)
            {
                sorce+=Double.parseDouble(ConfigI.load(text.Keywords.get(j).word,context));
            }
            title.list.get(i).score=sorce;
        }
        Collections.sort(title.list,new Comparator<NewsTitle.MyList>()
        {
            public int compare(NewsTitle.MyList a, NewsTitle.MyList b)
            {
                return (int) (a.score - b.score);
            }
        });

        if (title==null) Log.d("ltl","gettitel2222");*/
        String maxword=ConfigI.load("LTL",context);
        Log.d("ltl",maxword);
        int random = new Random().nextInt(10)+1;
        Log.d("ltl",Integer.toString(random));
        return searchNewsTitel(maxword,random, 100, 0,context);
    }
    public void clearAllInfo(Context context)
    {
        Storage.clearAllInfo(context);
    }
    public void setNight(int type, Context context)
    {
        ConfigI.Save("NightInfo",Integer.toString(type),context);
    }
    public int getNight(Context context)
    {
        return Integer.parseInt(ConfigI.load("NightInfo",context));
    }
    public String getRandPictures(List<NewsText.Keyword> keywords)
    {
        try
        {
            for (int i=0;i<keywords.size();i++)
            {
                String str = ReversedNews.getRandPicturs(keywords.get(i).word);
                Pattern pattern = Pattern.compile("http://img.ivsky.com/img/tupian/t/.*?\\.(jpg|png)");
                Matcher matcher = pattern.matcher(str);
                while (matcher.find())return matcher.group();
            }
        }catch (Exception e)
        {

        }
        return "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1193931039,3903211748&fm=27&gp=0.jpg";
    }
}
