package com.example.myapp.URL;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonRequestWithAuth<T> extends Request<T>
{
    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Listener<T> listener;

    private static Map<String, String> mHeader = new HashMap<String, String>();
     /**
     * 设置访问自己服务器时必须传递的参数，密钥等
     */
/*    static
    {
        mHeader.put("APP-Key", "LBS-AAA");
        mHeader.put("APP-Secret", "ad12msa234das232in");
    }*/

    /**
     * @param url
     * @param clazz
     *            我们最终的转化类型
     *
     * @param listener
     * @param appendHeader
     *            附加头数据
     * @param errorListener
     */
    public JsonRequestWithAuth(String url, Class<T> clazz, Listener<T> listener, Map<String, String> appendHeader,
                               ErrorListener errorListener)
    {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        mHeader.putAll(appendHeader);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        // 默认返回 return Collections.emptyMap();
        return mHeader;
    }

    @Override
    protected void deliverResponse(T response)
    {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response)
    {
        try
        {
            /**
             * 得到返回的数据
             */
            String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            String str = new String(jsonStr.getBytes("ISO-8859-1"),"utf-8");
            /**
             * 转化成对象
             */
            return Response.success(gson.fromJson(str, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e)
        {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e)
        {
            return Response.error(new ParseError(e));
        }
    }
}