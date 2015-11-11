package bjtu.cit.yue.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bjtu.cit.yue.R;
import bjtu.cit.yue.Entities.User;
import bjtu.cit.yue.Utils.PreferenceUtils;

public class DetailActivity extends AppCompatActivity {

    private ArrayList<User> userList = new ArrayList<User>();

    private Button btn_enroll;
    private TextView tv_num;

    private int num = 0;
    private HashMap<String, String> event;

    private boolean isHoster = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("活动详情");
        setSupportActionBar(toolbar);

/*
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/


        event = (HashMap<String, String>) getIntent().getSerializableExtra("event");
        if(event == null) {
            Toast.makeText(DetailActivity.this, "当前活动不存在！", Toast.LENGTH_LONG).show();
        }
        else {
            //显示活动详情
            TextView tv_title = (TextView)findViewById(R.id.title);
            TextView tv_content = (TextView)findViewById(R.id.content);
            tv_num = (TextView)findViewById(R.id.num);
            TextView tv_username = (TextView)findViewById(R.id.username);
            TextView tv_deadline = (TextView)findViewById(R.id.deadline);

            updateUserList();


            //打开参与用户列表
            tv_num.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailActivity.this, UserListActivity.class);
                    intent.putExtra("event", event);
                    intent.putParcelableArrayListExtra("list", userList);
                    startActivity(intent);
                }
            });

            btn_enroll = (Button) findViewById(R.id.enroll);
            if (event.get("userId").equals(PreferenceUtils.getString(DetailActivity.this, "id", ""))) {
                isHoster = true;
                btn_enroll.setText("解散该活动");
            }
            btn_enroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (btn_enroll.getText().toString()) {
                        case "加入":
                            enroll(PreferenceUtils.getString(DetailActivity.this, "id", ""), event.get("id"));
                            break;
                        case "解散该活动":
                            dismiss(event.get("id"));
                            break;
                        case "退出该活动":
                            unenroll(PreferenceUtils.getString(DetailActivity.this, "id", ""), event.get("id"));
                            break;
                        default:
                            return;
                    }
                }
            });

            tv_title.setText(event.get("title"));
            tv_content.setText(event.get("content"));
            tv_username.setText("发起人:  " + event.get("username"));
            tv_num.setText("已参与人数:  " + userList.size() + "人");
            tv_deadline.setText("报名截止日期: " + event.get("deadline"));

        }

    }

    private void updateUserList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("id", event.get("id"));
        client.post(DetailActivity.this, "http://yueapi.sinaapp.com/api/getEventUser.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.e("test", new String(bytes));
                userList.clear();
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    JSONArray array = obj.getJSONArray("users");
                    int size = array.length();
                    for (int j = 0; j < size; j++) {
                        JSONObject user = array.getJSONObject(j);
                        User u = new User();
                        u.setId(Integer.parseInt(user.getString("id")));
                        u.setUsername(user.getString("username"));
                        u.setGender(user.getString("gender"));
                        u.setPhone(user.getString("phone"));
                        u.setPic(user.getString("pic"));
                        if (!isHoster && user.getString("id").equals(PreferenceUtils.getString(DetailActivity.this, "id", ""))) {
                            btn_enroll.setText("退出该活动");
                        }
                        userList.add(u);
                    }
                    num = userList.size();
                    tv_num.setText("已参与人数:  " + num + "人");
                } catch (JSONException e) {
                    e.printStackTrace();
                    num = userList.size();
                    tv_num.setText("已参与人数:  " + num + "人");
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(DetailActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void enroll(String userId, String eventId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("userId", userId + "");
        params.add("eventId", eventId + "");
        client.post(DetailActivity.this, "http://yueapi.sinaapp.com/api/enroll.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                Log.e("test", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        btn_enroll.setText("退出该活动");
                        Toast.makeText(DetailActivity.this, "加入成功!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(DetailActivity.this, "加入失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateUserList();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(DetailActivity.this, "网络连接失败,请稍后再试!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dismiss(String eventId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id", eventId);
        client.post(DetailActivity.this, "http://yueapi.sinaapp.com/api/dismiss.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                Log.e("test", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        Toast.makeText(DetailActivity.this, "解散成功!", Toast.LENGTH_SHORT).show();
                        DetailActivity.this.finish();
                    }
                    else {
                        Toast.makeText(DetailActivity.this, "解散失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(DetailActivity.this, "网络连接失败,请稍后再试!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unenroll(String userId, String eventId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("userId", userId + "");
        params.add("eventId", eventId + "");
        client.post(DetailActivity.this, "http://yueapi.sinaapp.com/api/unenroll.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                Log.e("test", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        btn_enroll.setText("加入");
                        Toast.makeText(DetailActivity.this, "退出成功!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(DetailActivity.this, "退出失败,请稍后再试!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateUserList();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(DetailActivity.this, "网络连接失败,请稍后再试!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
