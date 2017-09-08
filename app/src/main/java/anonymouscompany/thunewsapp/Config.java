package anonymouscompany.thunewsapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LTL on 2017/9/8.
 */

public class Config //用户设置保持
{
    static public void Save(String key,String value,Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("tony.xml",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    static public String load(String key,Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences("tony.xml",Context.MODE_PRIVATE);
        return sharedPref.getString(key,"0");
    }
}
