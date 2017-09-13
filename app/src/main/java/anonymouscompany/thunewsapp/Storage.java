package anonymouscompany.thunewsapp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by LTL on 2017/9/8.
 */

public class Storage
{
    private static String newsTextfile="newsTextfile.txt";
    private static String collectionfile="collectionfile.txt";
    private static String shieldwordsfile="shieldwords.txt";
    public static  synchronized void clearAllInfo(Context context)
    {
        try
        {
            File file = new File(context.getFilesDir(), newsTextfile);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            out.close();
            file = new File(context.getFilesDir(), collectionfile);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            out.close();
            file = new File(context.getFilesDir(), shieldwordsfile);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            out.close();
            ConfigI.clear(context);
        } catch (Exception e)
        {
        }
    }
    public static  synchronized void addTextFile(NewsText text,Context context)
    {
        try
        {
            File file = new File(context.getFilesDir(), newsTextfile);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(JasonClass.JsontoString(text)+"\n");
            out.close();
        } catch (Exception e)
        {
        }
    }
    public static  synchronized void addCollectionFile(NewsTitle title,Context context)
    {
        File file = new File(context.getFilesDir(), collectionfile);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(JasonClass.JsontoString(title)+"\n");
            out.close();
        }catch (Exception e)
        {

        }
    }
    public static  synchronized void delCollectionFile(String news_ID, Context context)
    {
        NewsTitle title=findCollectionNews(context);
        File file = new File(context.getFilesDir(), collectionfile);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (int i=0;i<title.list.size();i++)
                if (title.list.get(i).news_ID.equals(news_ID))
                {
                    title.list.remove(i);
                    break;
                }
            out.write(JasonClass.JsontoString(title)+"\n");
            out.close();
        }catch (Exception e)
        {

        }

    }
    public static  synchronized void addshieldwordsFile(String shieldword,Context context)
    {
        try
        {
            File file = new File(context.getFilesDir(), shieldwordsfile);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(shieldword + "\n");
            out.close();
        }catch (Exception e)
        {

        }
    }
    public static  synchronized void delshieldFile(String shieldword, Context context)
    {
        List<String> list=getShieldWords(context);
        File file = new File(context.getFilesDir(), shieldwordsfile);
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for (int i=0;i<list.size();i++)
                if (!list.get(i).equals(shieldword))
                {
                    out.write(list.get(i) + "\n");
                }
            out.close();
        }catch (Exception e)
        {
        }

    }
    public static List<String> getShieldWords(Context context)
    {
        List<String> list=new ArrayList<>();
        File file = new File(context.getFilesDir(), shieldwordsfile);
        try {
            Scanner scanf = new Scanner(new FileInputStream(file));
            while (scanf.hasNext()) list.add(scanf.nextLine());
            scanf.close();
        }catch (Exception e)
        {

        }
        return list;
    }
    public static boolean isShield(String news,Context context) throws Exception
    {
        List<String> shieldwords=getShieldWords(context);
        news=news.toLowerCase();
        int m=shieldwords.size();
        for (int i=0;i<m;i++)
                if (news.indexOf(shieldwords.get(i).toLowerCase())!=-1)
                    return true;
        return false;
    }
    public static  NewsText findText(String newsid,Context context) throws Exception
    {
        File file = new File(context.getFilesDir(), newsTextfile);
        Scanner scanf=new Scanner(new FileInputStream(file));
        while (scanf.hasNext())
        {
            String str=scanf.nextLine();
            NewsText text=JasonClass.StringtoJson(str,NewsText.class);
            if (text.news_ID.equals(newsid))
            {
                scanf.close();
                return text;
            }
        }
        scanf.close();
        return null;
    }
    public static  NewsTitle findTitle(Context context)
    {
        try
        {
            File file = new File(context.getFilesDir(), newsTextfile);
            Scanner scanf=new Scanner(new FileInputStream(file));
            NewsTitle title=new NewsTitle();
            while (scanf.hasNext())
            {
                String str=scanf.nextLine();
                NewsText text=JasonClass.StringtoJson(str,NewsText.class);
                title.list.addAll(new NewsTitle(text).list);
            }
            scanf.close();
            return title;
        }catch (Exception e)
        {

            Log.d("ltl","rrr" + e.toString());
        }
        return new NewsTitle();
    }
    public static NewsTitle findCollectionNews(Context context)
    {
        NewsTitle title=new NewsTitle();
        File file = new File(context.getFilesDir(), collectionfile);
        Scanner scanf=null;
        try
        {
            scanf=new Scanner(new FileInputStream(file));
            while (scanf.hasNext()) title.list.addAll(JasonClass.StringtoJson(scanf.nextLine(),NewsTitle.class).list);
            scanf.close();
        }catch(Exception e)
        {
            System.out.println("!");
        }
        return title;
    }

}
