package anonymouscompany.thunewsapp;

import android.content.res.AssetManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by MacheNike on 2017/9/9.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LTLActivityTest
{

    @Test
    public void testJson(){
        String str = null;
        str = RuntimeEnvironment.application.getResources().getString(R.string.app_name);

        AssetManager am = null;
        am = RuntimeEnvironment.application.getAssets();
        String strData = null;
        try {
            InputStream inputStream = am.open("json01.txt");
            byte buf[] = new byte[1024];
            inputStream.read(buf);
            strData = new String(buf);
            strData =strData.trim();
            strData.trim();
        } catch (IOException e) {

        }
       }
}