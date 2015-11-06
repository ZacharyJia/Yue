package bjtu.cit.yue;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class DetailActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<User>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("活动详情");
        setSupportActionBar(toolbar);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        HashMap<String, String> event = (HashMap<String, String>) getIntent().getSerializableExtra("event");
        if(event == null) {
            Toast.makeText(DetailActivity.this, "当前活动不存在！", Toast.LENGTH_LONG).show();
        }
        else {
            //显示活动详情
            TextView tv_title = (TextView)findViewById(R.id.title);
            TextView tv_content = (TextView)findViewById(R.id.content);
            final TextView tv_num = (TextView)findViewById(R.id.num);
            TextView tv_username = (TextView)findViewById(R.id.username);
            TextView tv_deadline = (TextView)findViewById(R.id.deadline);

            tv_title.setText(event.get("title"));
            tv_content.setText(event.get("content"));
            tv_username.setText("发起人:  " + event.get("username"));
            tv_num.setText("已参与人数:  " + userList.size() + "人");
            tv_deadline.setText("报名截止日期: " + event.get("deadline"));

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("id", event.get("id"));
            client.post(DetailActivity.this, "http://yueapi.sinaapp.com/api/getEventUser.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.e("test", new String(bytes));
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
                            userList.add(u);
                        }
                        tv_num.setText("已参与人数:  " + userList.size() + "人");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(DetailActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                }
            });


        }

    }

}
