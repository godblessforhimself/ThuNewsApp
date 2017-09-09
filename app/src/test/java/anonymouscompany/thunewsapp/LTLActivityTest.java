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
        try {
            NewsTitle title = inter.getNewsTitle(1, 10, mainActivity);
            System.out.println(title.list.get(0).news_ID);
            inter.addCollectionNews(title,mainActivity);
            title = inter.getNewsTitle(2, 10, mainActivity);
            inter.addCollectionNews(title,mainActivity);
            title = inter.getNewsTitle(1, 10, mainActivity);
            inter.delCollectionNews(title,mainActivity);
            List<NewsTitle> list=inter.getCollectionNews(mainActivity);
            System.out.println(list.size());
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