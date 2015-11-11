package bjtu.cit.yue;

import android.app.Application;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by zachary on 15/11/7.
 */
public class App extends Application {

    private static HashMap<String, String> userPic = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderConfiguration config  = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        imageLoader.setDefaultLoadingListener(new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.user));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // Empty implementation
            }
        });

        RongIM.init(this);
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                String pic = App.getUserPic(s);
                UserInfo userInfo = new UserInfo(s, s, Uri.parse(pic));
                return userInfo;
            }
        }, true);

    }

    public static void setUserPic(String username, String pic) {
        userPic.put(username, pic);
    }

    public static String getUserPic(String username) {
        return userPic.get(username);
    }
}
