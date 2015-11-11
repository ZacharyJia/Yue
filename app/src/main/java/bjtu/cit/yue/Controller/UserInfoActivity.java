package bjtu.cit.yue.Controller;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import bjtu.cit.yue.App;
import bjtu.cit.yue.Entities.User;
import bjtu.cit.yue.R;
import io.rong.imkit.RongIM;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("用户信息");
        setSupportActionBar(toolbar);


        final User user = getIntent().getParcelableExtra("user");
        ImageView pic = (ImageView)findViewById(R.id.pic);
        TextView tv_username = (TextView)findViewById(R.id.username);
        TextView tv_gender = (TextView)findViewById(R.id.gender);
        TextView tv_phone = (TextView)findViewById(R.id.phone);

        tv_gender.setText("性别:" + user.getGender());
        tv_username.setText("用户名:" + user.getUsername());
        tv_phone.setText("手机:" + user.getPhone());

        ImageLoader loader = ImageLoader.getInstance();
        String picUrl = user.getPic();
        if ("".equals(picUrl)) {
            picUrl = "drawable://" + R.drawable.user;
        }
        loader.displayImage(picUrl, pic);

        Button btn_msg = (Button)findViewById(R.id.msg);
        btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.setUserPic(user.getUsername(), user.getPic());
                RongIM.getInstance().startPrivateChat(UserInfoActivity.this, user.getUsername(), null);

            }
        });

    }

}
