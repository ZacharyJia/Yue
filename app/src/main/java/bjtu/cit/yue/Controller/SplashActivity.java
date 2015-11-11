package bjtu.cit.yue.Controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.parsers.SAXParser;

import bjtu.cit.yue.R;
import bjtu.cit.yue.Utils.PreferenceUtils;


public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String username = PreferenceUtils.getString(SplashActivity.this, "username", "");
        String password = PreferenceUtils.getString(SplashActivity.this, "password", "");
        PreferenceUtils.putBoolean(SplashActivity.this, "isLogin", false);


        if(username.equals("") || password.equals(""))
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, 2000);
        }
        else
        {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("username", username);
            params.add("password", password);
            client.post("http://yueapi.sinaapp.com/api/login.php", params,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.e("hah", new String(bytes));
                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));
                        int status = jsonObject.getInt("code");
                        if (status == 0)
                        {
                            PreferenceUtils.putString(SplashActivity.this, "id", jsonObject.getString("id"));
                            PreferenceUtils.putString(SplashActivity.this ,"phone", jsonObject.getString("phone"));
                            PreferenceUtils.putString(SplashActivity.this ,"gender", jsonObject.getString("gender"));
                            PreferenceUtils.putString(SplashActivity.this, "pic", jsonObject.getString("pic"));
                            PreferenceUtils.putString(SplashActivity.this, "token", jsonObject.getString("token"));
                            PreferenceUtils.putBoolean(SplashActivity.this, "isLogin", true);
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                        else
                        {
                            Toast.makeText(SplashActivity.this, "用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    SplashActivity.this.finish();
                                }
                            }, 2000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(SplashActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    }, 2000);
                }
            });
        }


    }
}
