package anonymouscompany.thunewsapp;

import android.content.res.AssetManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by LTL on 2017/9/9.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LTLActivityTest
{

    @Test
    public void testBack()
    {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        BackendInterface inter=new BackendInter();
        try
        {
            NewsTitle title=inter.getNewsTitle(1,3,0,mainActivity);
            for (int i=0;i<title.list.size();i++)
                System.out.println(title.list.get(i).news_ID);
            System.out.println();
            inter.addCollectionNews(inter.getNewsText(title.list.get(0).news_ID,mainActivity),mainActivity);
            inter.addCollectionNews(inter.getNewsText(title.list.get(2).news_ID,mainActivity),mainActivity);
            inter.addCollectionNews(inter.getNewsText(title.list.get(0).news_ID,mainActivity),mainActivity);
            title=inter.getCollectionNews(mainActivity);
            for (int i=0;i<title.list.size();i++)
                System.out.println(title.list.get(i).news_ID);

        }catch (Exception e)
        {
            System.out.print("No\n");
        }

        Assert.assertEquals(1,1);
    }
    @Test
    public void testJason()
    {
        try {
            String str = ReversedNews.getReversedNewsText("20160913041301d5fc6a41214a149cd8a0581d3a014f");
            NewsText text = JasonClass.StringtoJson(str, NewsText.class);
        }catch(Exception e)
        {

        }
    }
}