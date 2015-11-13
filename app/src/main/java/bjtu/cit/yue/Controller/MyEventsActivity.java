package bjtu.cit.yue.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
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
import java.util.List;

import bjtu.cit.yue.Controller.Utils.MainAdapter;
import bjtu.cit.yue.R;
import bjtu.cit.yue.Utils.PreferenceUtils;

public class MyEventsActivity extends AppCompatActivity {

    public static int HOST_EVENT = 0;
    public static int JOIN_EVENT = 1;

    private ListView listView = null;
    private MainAdapter adapter = null;

    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        type = getIntent().getIntExtra("type", 0);
        if (type == HOST_EVENT) {
            toolbar.setTitle("我发起的活动");
        }
        else {
            toolbar.setTitle("我参加的活动");
        }
        setSupportActionBar(toolbar);

        listView = (ListView)findViewById(R.id.listView);
        adapter = new MainAdapter(MyEventsActivity.this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("test", "" + position);
                HashMap<String, String> event = adapter.getList().get(position);
                Intent intent = new Intent(MyEventsActivity.this, DetailActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });


        getData();

    }

    private List<HashMap<String, String>> getData() {
        final List<HashMap<String, String>> list = new ArrayList<>();

        String url = null;
        if (type == HOST_EVENT) {
            url = "http://yueapi.sinaapp.com/api/getUserHostEvents.php";
        }
        else {
            url = "http://yueapi.sinaapp.com/api/getUserJoinEvents.php";
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("userId", PreferenceUtils.getString(MyEventsActivity.this, "id", ""));

        client.post(MyEventsActivity.this, url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                Log.e("debug", result);

                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        JSONArray array = obj.getJSONArray("events");
                        for (int index = 0; index < array.length(); index++) {
                            JSONObject event = array.getJSONObject(index);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("id", event.getString("id"));
                            map.put("title", event.getString("title"));
                            map.put("content", event.getString("content"));
                            map.put("deadline", event.getString("deadline"));
                            map.put("userId", event.getString("userId"));
                            map.put("username", event.getString("username"));
                            list.add(map);
                        }
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        String tip = null;
                        if (type == HOST_EVENT) {
                            tip = "您还没组织过活动,快去组织一个吧!";
                        }
                        else {
                            tip = "您还没有参加过活动,快去参加一个吧!";
                        }
                        Toast.makeText(MyEventsActivity.this, tip, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MyEventsActivity.this, "网络错误,请检查网络后再试!", Toast.LENGTH_SHORT).show();
            }
        });

        return list;
    }

}
