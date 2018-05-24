package www.seotoolzz.com.Ask.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static android.content.Context.MODE_PRIVATE;

public class AsksUtil {
    private static Context mCtx;
    private static AsksUtil mInstance;
    private RequestQueue mRequestQueue;

    private AsksUtil(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized AsksUtil getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AsksUtil(context);
        }
        return mInstance;
    }

    public static boolean isLogin(AppCompatActivity activity) {
        SharedPreferences sharePrefs = activity.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
        String token = sharePrefs.getString("token", null);
        if (token == null) {
            return false;
        } else {
            return true;
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

