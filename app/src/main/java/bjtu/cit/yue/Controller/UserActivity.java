package bjtu.cit.yue.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import bjtu.cit.yue.R;
import bjtu.cit.yue.Utils.PreferenceUtils;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_logout;
    private Button btn_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("个人中心");
        setSupportActionBar(toolbar);

        btn_username = (Button)findViewById(R.id.username);
        btn_logout = (Button)findViewById(R.id.logout);


        btn_logout.setOnClickListener(this);
        btn_username.setText(PreferenceUtils.getString(UserActivity.this, "username", ""));

    }

    @Override
    public void onClick(View v) {
        if (v == btn_logout) {
            PreferenceUtils.putString(UserActivity.this, "username", null);
            PreferenceUtils.putString(UserActivity.this, "password", null);
            PreferenceUtils.putString(UserActivity.this, "id", null);
            PreferenceUtils.putString(UserActivity.this, "phone", null);
            PreferenceUtils.putString(UserActivity.this, "gender", null);
            PreferenceUtils.putString(UserActivity.this, "pic", null);
            PreferenceUtils.putBoolean(UserActivity.this, "isLogin", false);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}