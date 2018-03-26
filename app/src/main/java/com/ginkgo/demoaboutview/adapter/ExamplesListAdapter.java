package com.ginkgo.demoaboutview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ginkgo.demoaboutview.R;

import java.util.List;

/**
 * Created by Administrator on 2018/3/26 0026.
 */

public class ExamplesListAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mExamleList;

    public ExamplesListAdapter(Context context, List<String> exampleList){
        this.mContext = context;
        this.mExamleList = exampleList;
    }

    @Override
    public int getCount() {
        return mExamleList.size();
    }

    @Override
    public Object getItem(int i) {
        return mExamleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.list_item_examples, null);
            viewHolder = new ViewHolder();
            viewHolder.exampleTextView = (TextView)view.findViewById(R.id.tv_example_name);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.exampleTextView.setText(mExamleList.get(i));
        return view;
    }

    private static class ViewHolder{
        TextView exampleTextView;
    }
}
