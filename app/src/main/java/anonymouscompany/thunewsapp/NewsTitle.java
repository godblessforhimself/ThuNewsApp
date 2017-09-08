package anonymouscompany.thunewsapp;

/**
 * Created by LTL on 2017/9/8.
 */
import java.util.List;

public class NewsTitle //新闻标题
{
        List<MyList> list;
        int pageNo,pageSize,totalPages,totalRecords;
        class MyList
        {
                String newsClassTag,news_ID,news_Source,news_Title,news_Time,news_URL,news_Author,lang_Type,news_Pictures,news_Video,news_Intro;
        }
}

