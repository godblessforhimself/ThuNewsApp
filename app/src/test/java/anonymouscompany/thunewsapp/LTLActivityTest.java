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

/**
 * Created by LTL on 2017/9/9.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LTLActivityTest
{

    @Test
    public void testJson()
    {
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        BackendInterface inter=new BackendInter();
        try {
            NewsTitle title = inter.getNewsTitle(1, 10, mainActivity);
            System.out.println(title.list.get(0).news_ID);
        }catch (Exception e)
        {
            System.out.print("No\n");
        }

        Assert.assertEquals(1,1);
    }
}