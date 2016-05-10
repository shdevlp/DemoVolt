package ru.volt.demovolt.net;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by dave on 06.05.16.
 */
public class HTTPRequest extends Request<JSONArray> {
    private Response.Listener<JSONArray> listener;
    private Map<String, String> params;

    public HTTPRequest(int method, String url, Map<String, String> params,
                       Response.Listener<JSONArray> reponseListener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    };

    /**
     * Парсинг полученных данных в json массив
     * @param response
     * @return
     */
    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        String jsonString = null;
        try {
            jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

       return Response.success(jsonArray,
               HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(JSONArray response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}
