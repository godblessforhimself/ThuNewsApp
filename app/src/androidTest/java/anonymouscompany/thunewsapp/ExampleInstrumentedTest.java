package anonymouscompany.thunewsapp;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception
    {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(appContext,BaikeWebActivity.class);
        intent.putExtra("keyword","蒋介石");
        appContext.startActivity(intent);
        assertEquals("anonymouscompany.thunewsapp", appContext.getPackageName());
    }
}
