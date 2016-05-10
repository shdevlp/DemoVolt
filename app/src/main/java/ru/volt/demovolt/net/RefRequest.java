package ru.volt.demovolt.net;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import ru.volt.demovolt.R;
import ru.volt.demovolt.RecordType;
import ru.volt.demovolt.interfaces.ServerCallback;

/**
 * Created by dave on 06.05.16.
 */
public class RefRequest {
    private static volatile RefRequest instance = null;

    private final String url = "http://jsonplaceholder.typicode.com/posts";
    private final String P_USER_ID = "userId";
    private final String P_RECORD_ID = "id";
    private final String P_TITLE = "title";
    private final String P_BODY = "body";

    private Activity activity;

    private RefRequest(Activity activity) {
        this.activity = activity;
    }

    /**
     * Получить единственный экземпляр класса
     * @return
     */
    public static RefRequest getInstance(Activity activity) {
        if (instance == null) {
            synchronized (RefRequest.class) {
                if (instance == null) {
                    instance = new RefRequest(activity);
                }
            }
        }
        return instance;
    }

    /**
     * Запрос на получение данных
     * @param callback
     */
    public void posts(final ServerCallback callback) {
        HTTPRequest jsObjRequest = new HTTPRequest(Request.Method.GET,
                url, new HashMap<String, String>(), new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (response != null) {
                        final int len = response.length();

                        RecordType[] records = new RecordType[len];
                        for (int i = 0; i < len; i++) {
                            JSONObject json = response.getJSONObject(i);

                            int recordId = -1;
                            if (json.has(P_RECORD_ID)) {
                                recordId = json.getInt(P_RECORD_ID);
                            }

                            int userId = -1;
                            if (json.has(P_USER_ID)) {
                                userId = json.getInt(P_USER_ID);
                            }

                            String title = null;
                            if (json.has(P_TITLE)) {
                                title = json.get(P_TITLE).toString();
                            }

                            String body = null;
                            if (json.has(P_BODY)) {
                                body = json.get(P_BODY).toString();
                            }

                            records[i] = new RecordType(userId, recordId, title, body, false);
                        }

                        callback.onSuccess(records);
                    } else {
                        callback.onError(activity.getString(R.string.error_response));
                    }
                } catch(JSONException ex){
                    ex.printStackTrace();
                    callback.onError(ex.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                callback.onError(getErrorResponseMessage(error));
            }
        });

        RequestQueue mRequestQueue = Volley.newRequestQueue(activity);
        mRequestQueue.add(jsObjRequest);
    }

    /**
     * Расшифровка ошибки
     * @param error
     * @return
     */
    public String getErrorResponseMessage(VolleyError error) {
        return error.getCause().getLocalizedMessage();
    }
}
