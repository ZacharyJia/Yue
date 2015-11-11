package bjtu.cit.yue.Controller.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bjtu.cit.yue.R;

/**
 * Created by zachary on 15/11/2.
 */
public class MainAdapter extends BaseAdapter {

    public List<HashMap<String, String>> getList() {
        return list;
    }

    private List<HashMap<String, String>> list = new ArrayList<>();
    private LayoutInflater inflater = null;

    public MainAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<HashMap<String, String>> list) {
        this.list = list;
    }



    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_item_main, null);
        TextView tv_title = (TextView) convertView.findViewById(R.id.title);
        TextView tv_content = (TextView) convertView.findViewById(R.id.content);

        tv_title.setText(list.get(position).get("title"));
        tv_content.setText(list.get(position).get("content"));
        return convertView;
    }
}
