package com.shuangling.software.adapter;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shuangling.software.R;
import com.shuangling.software.entity.Column;
import com.shuangling.software.utils.CommonUtils;
import com.shuangling.software.utils.ImageLoader;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
public class ColumnGridAdapter extends BaseAdapter {
private List<Column> columns;
    private LayoutInflater inflater;
    private Context context;
public ColumnGridAdapter(Context context, List<Column> columns) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.columns = columns;
    }
public int getCount() {
return columns.size();
    }
@Override
    public Column getItem(int position) {
        return columns.get(position);
    }
@Override
    public long getItemId(int position) {
        return position;
    }
public View getView(final int position, View convertView, ViewGroup parent) {
convertView = inflater.inflate(R.layout.second_column_large_newline_item_layout, parent, false);
        int width = (CommonUtils.getScreenWidth() - CommonUtils.dip2px(30)) / 2;
        int height = (int) (width / 1.67);
//        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(width, height);
//        convertView.setLayoutParams(param);
        Column column=getItem(position);
        ViewHolder holder = new ViewHolder(convertView);
holder.text.setText(column.getName());
        if (!TextUtils.isEmpty(column.getIcon())) {
            Uri uri = Uri.parse(column.getIcon());
            ImageLoader.showThumb(uri, holder.logo, width, height);
        }
        return convertView;
    }
class ViewHolder {
        @BindView(R.id.logo)
        SimpleDraweeView logo;
        @BindView(R.id.text)
        TextView text;
ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
