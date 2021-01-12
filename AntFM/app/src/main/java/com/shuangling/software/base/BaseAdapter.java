package com.shuangling.software.base;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;
/**
 * 简单RecycleView.Adapter封装(只适合唯一一个item样式)
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseHolder> {
    private static final String TAG = "BaseAdapter";
protected List<T> data;
    protected int layoutRes;
public BaseAdapter(int layoutRes, List<T> data) {
        this.layoutRes = layoutRes;
        this.data = data;
    }
public BaseAdapter(List<T> data) {
        this.data = data;
    }
public void setData(List<T> data) {
        this.data = data;
    }
@Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG,"onCreateViewHolder：" + viewType);
        BaseHolder baseHolder = new BaseHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(viewType),parent,false));
        createBaseHolder(baseHolder, parent, viewType);
        return baseHolder;
    }
/**
     * 默认资源只有一个
     * @param viewType
     * @return
     */
    public int getLayoutRes(int viewType) {
        return layoutRes;
    }
/**
     * 默认类型只有一个
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return 0;
    }
@Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        convert(holder,data.get(position));
    }
protected abstract void convert(BaseHolder helper, T item);
/**
     * 创建holder回调
     * @param helper
     * @param parent
     * @param viewType
     */
    protected void createBaseHolder(BaseHolder helper, ViewGroup parent, int viewType) {
}
@Override
    public int getItemCount() {
        return data.size();
    }
}
