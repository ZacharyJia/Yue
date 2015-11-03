package bjtu.cit.yue;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bjtu.cit.yue.Utils.PreferenceUtils;


public class RegActivity extends AppCompatActivity {

    private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);


        Button btn_reg = (Button)findViewById(R.id.btn_reg);
        final EditText et_username = (EditText)findViewById(R.id.username);
        final EditText et_password = (EditText)findViewById(R.id.password);
        final EditText et_phone = (EditText)findViewById(R.id.phonenumber);
        final RadioButton rb_male = (RadioButton)findViewById(R.id.gender_male);
        final RadioButton rb_female = (RadioButton)findViewById(R.id.gender_female);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                final String phone = et_phone.getText().toString();
                final String gender = rb_male.isChecked() ? "男" : "女";

                if(!usernameValidate(username))
                {
                    Toast.makeText(RegActivity.this, "用户名不合法，请重新输入！", Toast.LENGTH_SHORT).show();
                }
                else if(password.equals(""))
                {
                    Toast.makeText(RegActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                }
                else if (phone.equals(""))
                {
                    Toast.makeText(RegActivity.this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    params.add("password", password);
                    params.add("phone", phone);
                    params.add("gender", gender);
                    client.post("http://yueapi.sinaapp.com/api/reg.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            try {
                                JSONObject jsonObject = new JSONObject(new String(bytes));
                                int status = jsonObject.getInt("code");
                                if (status == 0)
                                {
                                    Toast.makeText(RegActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    PreferenceUtils.putString(RegActivity.this, "username", username);
                                    PreferenceUtils.putString(RegActivity.this, "password", password);
                                    PreferenceUtils.putString(RegActivity.this, "phone", phone);
                                    PreferenceUtils.putString(RegActivity.this, "gender", gender);
                                    PreferenceUtils.putString(RegActivity.this, "id", jsonObject.getString("id"));
                                    Intent intent = new Intent(RegActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    RegActivity.this.finish();
                                }
                                else
                                {
                                    Toast.makeText(RegActivity.this, "该用户名已经存在！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(RegActivity.this, "网络错误，请稍后重试！", Toast.LENGTH_SHORT).show();
                        }
                    });
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
