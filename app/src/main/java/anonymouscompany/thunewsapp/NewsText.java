package anonymouscompany.thunewsapp;

import java.util.List;

public class NewsText //新闻正文
{
   class Person
    {
        String word;
        int count;
    }
    class Keyword
    {
        String word;
        double score;
    }
    String seggedTitle;
    List<String> seggedPListOfContent;
    List<Person> persons;
    List<Person> locations;
    List<Keyword> Keywords;
    List<Keyword> bagOfWords;
    int wordCountOfTitle,wordCountOfContent;
    String newsClassTag,news_ID,news_Category,news_Source,news_Title,inborn_KeyWords,news_Time;
    String news_URL,news_Author,news_Content,lang_Type,crawl_Source,news_Journal,crawl_Time,news_Pictures,news_Video,repeat_ID;

   List<Person> organizations;
}
