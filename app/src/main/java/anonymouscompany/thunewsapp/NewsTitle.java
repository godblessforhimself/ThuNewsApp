package anonymouscompany.thunewsapp;

/**
 * Created by LTL on 2017/9/8.
 */
import java.util.LinkedList;
import java.util.List;

public class NewsTitle //新闻标题
{
        List<MyList> list;
        int pageNo,pageSize,totalPages,totalRecords;
        class MyList
        {
                String newsClassTag,news_ID,news_Source,news_Title,news_Time,news_URL,news_Author,lang_Type,news_Pictures,news_Video,news_Intro;
                double score;
        }
        NewsTitle()
        {
                list=new LinkedList<MyList>();
                pageNo=0;pageSize=0;totalPages=0;totalRecords=0;
        }
        NewsTitle(NewsText text)
        {
                list=new LinkedList<MyList>();
                MyList own=new MyList();
                own.news_ID=text.news_ID;
                own.newsClassTag=text.newsClassTag;
                own.news_Source=text.news_Source;
                own.news_Title=text.news_Title;
                own.news_Time=text.news_Time;
                own.news_URL=text.news_URL;
                own.news_Author=text.news_Author;
                own.lang_Type=text.lang_Type;
                own.news_Pictures=text.news_Pictures;
                own.news_Video=text.news_Video;
                own.news_Intro="";
                list.add(own);
                pageNo=0;pageSize=0;totalPages=0;totalRecords=0;

        }
}

