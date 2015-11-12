package bjtu.cit.yue.Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import bjtu.cit.yue.R;
import bjtu.cit.yue.Utils.PreferenceUtils;
import io.rong.imkit.RongIM;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_logout;
    private Button btn_username;
    private ImageView pic;
    private Button btn_msg;

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("个人中心");

        setSupportActionBar(toolbar);

        btn_username = (Button)findViewById(R.id.username);
        btn_logout = (Button)findViewById(R.id.logout);
        pic = (ImageView)findViewById(R.id.pic);
        btn_msg = (Button)findViewById(R.id.msg);

        btn_msg.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_username.setText(PreferenceUtils.getString(UserActivity.this, "username", ""));
        pic.setOnClickListener(this);

        ImageLoader imageLoader = ImageLoader.getInstance();
        String picUrl = PreferenceUtils.getString(UserActivity.this, "pic", "");
        if (picUrl.equals("")) {
            picUrl = "drawable://" + R.drawable.user;
        }
        imageLoader.displayImage(picUrl, pic);
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
        else if (v == btn_msg) {
            RongIM.getInstance().startConversationList(UserActivity.this);
        }
        else if (v == pic) {

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.e("debug", "" + which);
                    if (which == 0) {
                        //从相册选择
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, GALLERY_REQUEST_CODE);
                    }
                    else if (which == 1) {
                        //拍照
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    }
                }
            };
            AlertDialog dialog = new AlertDialog.Builder(UserActivity.this)
                    .setItems(new CharSequence[]{"从相册中选择", "拍照"}, listener)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bm = extras.getParcelable("data");
                Uri uri = Uri.fromFile(saveBitmap(bm));
                Log.e("debug", uri.toString());
                Log.e("debug", uri.normalizeScheme().toString());
                startImageZoom(uri);
            }
        }
        else if (requestCode == GALLERY_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            Uri fileUri = convertUri(uri);
            startImageZoom(fileUri);
        }
        else if (requestCode == CROP_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Bundle extras = data.getExtras();
            if (extras == null) {
                return;
            }
            Bitmap bm = extras.getParcelable("data");
            pic.setImageBitmap(bm);
            sendImage(saveBitmap(bm));
        }
    }

    private File saveBitmap(Bitmap bm)
    {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/bjtu.cit.yue");
        if(!tmpDir.exists())
        {
            tmpDir.mkdir();
        }
        File img = new File(tmpDir.getAbsolutePath() + "/pic.png");
        try {
            FileOutputStream fos = new FileOutputStream(img);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fos);
            fos.flush();
            fos.close();
            return img;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri convertUri(Uri uri)
    {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return Uri.fromFile(saveBitmap(bitmap));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startImageZoom(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 144);
        intent.putExtra("outputY", 144);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }


    private void sendImage(File file)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        final AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();

        try {
            params.put("file", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        client.post("http://yueapi.sinaapp.com/api/upload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                Log.e("debug", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getInt("code") == 0) {
                        String url = obj.getString("url");
                        RequestParams param = new RequestParams();
                        param.add("userId", PreferenceUtils.getString(UserActivity.this, "id", ""));
                        param.add("url", url);
                        AsyncHttpClient client2 = new AsyncHttpClient();
                        client2.post(UserActivity.this, "http://yueapi.sinaapp.com/api/changePic.php", param, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                            }
                        });
                        PreferenceUtils.putString(UserActivity.this, "pic", url);
                        Toast.makeText(UserActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UserActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UserActivity.this, "上传失败", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(UserActivity.this, "网络错误,请检查您的网络连接", Toast.LENGTH_LONG).show();
            }
        });

    }

}
