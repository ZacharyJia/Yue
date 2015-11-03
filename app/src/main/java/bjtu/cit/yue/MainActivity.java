package bjtu.cit.yue;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PullToRefreshBase.OnRefreshListener2{

    int offset = 0;
    private MainAdapter adapter = null;

    private boolean isRefreshing = false;
    private PullToRefreshListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("活动列表");
        setSupportActionBar(toolbar);

        listView = (PullToRefreshListView)findViewById(R.id.listView);
        listView.setOnRefreshListener(this);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        adapter = new MainAdapter(MainActivity.this);
        listView.setAdapter(adapter);
        refresh();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> event = adapter.getList().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

        //添加按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_user) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        if (!isRefreshing) {
            isRefreshing = true;
            refresh();
        }
        else {
            listView.onRefreshComplete();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (!isRefreshing) {
            isRefreshing = true;
            loadMore();
        }
        else {
            listView.onRefreshComplete();
        }

    }

    private void refresh() {
        int num = 10;
        int offset = 0;
        final List<HashMap<String, String>> list = new ArrayList<>();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("num", num + "");
        params.add("offset", offset + "");
        client.post(MainActivity.this, "http://yueapi.sinaapp.com/api/getEventList.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        JSONArray array = obj.getJSONArray("events");
                        for(int index = 0; index < array.length(); index++) {
                            JSONObject event = array.getJSONObject(index);
                            HashMap<String, String>  map = new HashMap<String, String>();
                            map.put("id", event.getString("id"));
                            map.put("title", event.getString("title"));
                            map.put("content", event.getString("content"));
                            map.put("deadline", event.getString("deadline"));
                            map.put("userId", event.getString("userId"));
                            list.add(map);
                        }
                        MainActivity.this.offset = array.length();
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "服务器错误,请稍后再试！", Toast.LENGTH_LONG).show();
                    }
                    isRefreshing = false;
                    listView.onRefreshComplete();

                } catch (JSONException e) {
                    e.printStackTrace();
                    isRefreshing = false;
                    listView.onRefreshComplete();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MainActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                isRefreshing = false;
                listView.onRefreshComplete();
            }
        });
    }

    private void loadMore() {
        int num = 10;
        final List<HashMap<String, String>> list = adapter.getList();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("num", num + "");
        params.add("offset", offset + "");
        client.post(MainActivity.this, "http://yueapi.sinaapp.com/api/getEventList.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        JSONArray array = obj.getJSONArray("events");
                        for(int index = 0; index < array.length(); index++) {
                            JSONObject event = array.getJSONObject(i);
                            HashMap<String, String>  map = new HashMap<String, String>();
                            map.put("id", event.getString("id"));
                            map.put("title", event.getString("title"));
                            map.put("content", event.getString("content"));
                            map.put("deadline", event.getString("deadline"));
                            map.put("userId", event.getString("userId"));
                            list.add(map);
                        }
                        MainActivity.this.offset += array.length();
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "没有更多活动了,自己组织一个吧!", Toast.LENGTH_LONG).show();
                    }
                    isRefreshing = false;
                    listView.onRefreshComplete();

                } catch (JSONException e) {
                    e.printStackTrace();
                    isRefreshing = false;
                    listView.onRefreshComplete();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MainActivity.this, "网络连接失败，请检查网络后重试！", Toast.LENGTH_LONG).show();
                isRefreshing = false;
                listView.onRefreshComplete();
            }
        });

    }
}
