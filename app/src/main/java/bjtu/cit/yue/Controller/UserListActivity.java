package bjtu.cit.yue.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import javax.security.auth.login.LoginException;

import bjtu.cit.yue.App;
import bjtu.cit.yue.Controller.Utils.UserListAdapter;
import bjtu.cit.yue.Entities.User;
import bjtu.cit.yue.R;
import bjtu.cit.yue.Utils.PreferenceUtils;

public class UserListActivity extends AppCompatActivity {

    private UserListAdapter adapter;
    private boolean isHoster = false;
    private ListView listView;
    private User hoster = null;
    private HashMap<String, String> event;
    private int selectedId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("已参与的用户列表");
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();
        event = (HashMap<String, String>) intent.getSerializableExtra("event");
        ArrayList<User> userList = intent.getParcelableArrayListExtra("list");

        adapter = new UserListAdapter(userList, UserListActivity.this);

        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        hoster = userList.get(0);
        if (hoster.getUsername().equals(PreferenceUtils.getString(UserListActivity.this, "username", ""))) {
            isHoster = true;
        }

        if (isHoster) {
            Toast.makeText(UserListActivity.this, "活动发起者可以长按进行管理", Toast.LENGTH_LONG).show();
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedId = position;
                return false;
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "剔除");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = adapter.getList().get(position);
                if (user.getUsername().equals(PreferenceUtils.getString(UserListActivity.this, "username", ""))) {
                    Toast.makeText(UserListActivity.this, "你自己^_^", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(UserListActivity.this, UserInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            //从该活动中删除用户
            final User user = adapter.getList().get(selectedId);
            Log.e("debug", String.valueOf(user));
            if (user.getUsername().equals(hoster.getUsername())) {
                Toast.makeText(UserListActivity.this, "不能删除活动发起人!", Toast.LENGTH_SHORT).show();
            }
            else {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.add("eventId", event.get("id"));
                params.add("userId", String.valueOf(user.getId()));
                client.post("http://yueapi.sinaapp.com/api/deleteUserFromEvent.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        String result = new String(bytes);
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (obj.getInt("code") == 0) {
                                Toast.makeText(UserListActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
                                adapter.getList().remove(user);
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                Toast.makeText(UserListActivity.this, "删除错误,稍后再试!", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(UserListActivity.this, "网络连接失败,请稍后再试!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }

        return super.onContextItemSelected(item);
    }
}
