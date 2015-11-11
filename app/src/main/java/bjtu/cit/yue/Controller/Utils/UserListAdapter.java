package bjtu.cit.yue.Controller.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

import bjtu.cit.yue.Entities.User;
import bjtu.cit.yue.R;

/**
 * Created by zachary on 15/11/11.
 */
public class UserListAdapter extends BaseAdapter {

    private List<User> list = null;
    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public UserListAdapter(List<User> list, Context context) {
        this.list = list;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.list_item_user_list, null);
        ImageView pic = (ImageView) convertView.findViewById(R.id.pic);
        TextView tv_username = (TextView) convertView.findViewById(R.id.username);

        String picUrl = list.get(position).getPic();
        if ("".equals(picUrl)) {
            picUrl = "drawable://" + R.drawable.user;
        }
        imageLoader.displayImage(picUrl, pic);
        String username = list.get(position).getUsername();
        if (position == 0) {
            username = username + "  (活动发起者)";
        }
        tv_username.setText(username);
        return convertView;
    }
}
