package bjtu.cit.yue;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import bjtu.cit.yue.Utils.PreferenceUtils;

public class AddEventActivity extends AppCompatActivity {

    private int year = 0;
    private int month = 0;
    private int day = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("组织新活动");
        setSupportActionBar(toolbar);


        final EditText et_title = (EditText)findViewById(R.id.et_title);
        final EditText et_content = (EditText)findViewById(R.id.et_content);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();

                if ("".equals(title) || "".equals(content)) {
                    Toast.makeText(AddEventActivity.this, "标题或介绍不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.add("title", title);
                params.add("content", content);
                params.add("deadline", year + "-" + month + "-" + day);
                params.add("userId", PreferenceUtils.getString(AddEventActivity.this, "id", ""));
                client.post(AddEventActivity.this, "http://yueapi.sinaapp.com/api/newEvent.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        Log.e("hah", new String(bytes));

                        try {
                            JSONObject jsonObject = new JSONObject(new String(bytes));
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                Toast.makeText(AddEventActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                AddEventActivity.this.finish();
                            }
                            else {
                                Toast.makeText(AddEventActivity.this, "服务器出现错误,请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(AddEventActivity.this, "网络错误,请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        final TextView deadline = (TextView)findViewById(R.id.deadline);
        deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date(System.currentTimeMillis());
                if (year == 0) {
                    year = date.getYear() + 1900;
                    month = date.getMonth();
                    day = date.getDay();
                }
                DatePickerDialog dialog = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        AddEventActivity.this.year = year;
                        AddEventActivity.this.month = monthOfYear + 1;
                        AddEventActivity.this.day = dayOfMonth;
                        deadline.setText(year + "年" + (monthOfYear + 1) +"月" + dayOfMonth +  "日");
                    }
                }, year, month, day);
                dialog.show();
            }
        });
    }

}
