package bjtu.cit.yue.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjtu.cit.yue.R;
import bjtu.cit.yue.Utils.PreferenceUtils;


public class LoginActivity extends AppCompatActivity {

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_login = (Button)findViewById(R.id.btn_login);
        Button btn_reg = (Button)findViewById(R.id.btn_reg);

        final EditText et_username = (EditText)findViewById(R.id.username);
        final EditText et_password = (EditText)findViewById(R.id.password);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                if (usernameValidate(username)) {
                    if (password.equals("")) {
                        Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.add("username", username);
                        params.add("password", password);
                        client.post("http://yueapi.sinaapp.com/api/login.php", params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(bytes));
                                    int status = jsonObject.getInt("code");
                                    if (status == 0) {
                                        PreferenceUtils.putString(LoginActivity.this, "username", username);
                                        PreferenceUtils.putString(LoginActivity.this, "password", password);
                                        PreferenceUtils.putString(LoginActivity.this, "id", jsonObject.getString("id"));
                                        PreferenceUtils.putString(LoginActivity.this, "phone", jsonObject.getString("phone"));
                                        PreferenceUtils.putString(LoginActivity.this, "gender", jsonObject.getString("gender"));
                                        PreferenceUtils.putString(LoginActivity.this, "pic", jsonObject.getString("pic"));
                                        PreferenceUtils.putString(LoginActivity.this, "token", jsonObject.getString("token"));
                                        PreferenceUtils.putBoolean(LoginActivity.this, "isLogin", true);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新登录", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                Toast.makeText(LoginActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "用户名不合法，请重新输入！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    boolean usernameValidate(String username)
    {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
