package bjtu.cit.yue.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import bjtu.cit.yue.Controller.Utils.UserListAdapter;
import bjtu.cit.yue.Entities.User;
import bjtu.cit.yue.R;

public class UserListActivity extends AppCompatActivity {

    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("已参与的用户列表");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        ArrayList<User> userList = intent.getParcelableArrayListExtra("list");

        adapter = new UserListAdapter(userList, UserListActivity.this);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }

}
