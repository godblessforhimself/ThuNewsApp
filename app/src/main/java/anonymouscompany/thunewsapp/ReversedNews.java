package anonymouscompany.thunewsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by LTL on 2017/9/8.
 */

public class ReversedNews //连接服务器
{
    static  public String getReversedNews(int page, int pagesize,int catagory) throws IOException
    {
        String str="http://166.111.68.66:2042/news/action/query/latest?pageNo="+page+"&pageSize="+pagesize;
        if (catagory!=0) str+="&catagory="+catagory;
        URL website = new URL(str);
        HttpURLConnection con = (HttpURLConnection)website.openConnection();
        InputStreamReader in = new InputStreamReader(con.getInputStream(),"utf-8");
        BufferedReader buffer = new BufferedReader(in);
        String response,result = "";
        while ((response = buffer.readLine())!=null)
        {
            result += response;
        }
        return result;
    }
    static public String getReversedNewsText(String newid) throws IOException
    {
        URL website = new URL("http://166.111.68.66:2042/news/action/query/detail?newsId="+newid);
        HttpURLConnection con = (HttpURLConnection)website.openConnection();
        InputStreamReader in = new InputStreamReader(con.getInputStream(),"utf-8");
        BufferedReader buffer = new BufferedReader(in);
        String response,result = "";
        while ((response = buffer.readLine())!=null)
        {
            result += response;
        }
        return result;
    }
    static public String getReversedSearchNews(String keyword,int page, int pagesize,int catagory) throws IOException
    {
        String str="http://166.111.68.66:2042/news/action/query/search?keyword="+keyword;
        if (page!=0&&pagesize!=0) str+="&pageNo="+page+"&pageSize="+pagesize;
        if (catagory!=0) str+="&catagory="+catagory;

        URL website = new URL(str);
        HttpURLConnection con = (HttpURLConnection)website.openConnection();
        InputStreamReader in = new InputStreamReader(con.getInputStream(),"utf-8");
        BufferedReader buffer = new BufferedReader(in);
        String response,result = "";
        while ((response = buffer.readLine())!=null)
        {
            result += response;
        }
        return result;
    }
    static public String getRandPicturs(String keyword) throws  IOException
    {

        URL website = new URL("http://www.ivsky.com/search.php?q="+keyword);
        HttpURLConnection con = (HttpURLConnection)website.openConnection();
        InputStreamReader in = new InputStreamReader(con.getInputStream(),"utf-8");
        BufferedReader buffer = new BufferedReader(in);
        String response,result = "";
        while ((response = buffer.readLine())!=null)
        {
            result += response;
        }
        return result;
    }
}

