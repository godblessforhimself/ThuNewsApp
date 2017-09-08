package anonymouscompany.thunewsapp;

/**
 * Created by LTL on 2017/9/8.
 */

import com.google.gson.Gson;

public class JasonClass //Jason解析类
{
    static <T> T StringtoJson(String str,Class<T> type)
    {
            Gson gson=new Gson();
            return gson.fromJson(str,type);
    }
    static <T> String  JsontoString(T Json)
    {
        Gson gson=new Gson();
        return gson.toJson(Json);
    }
}
