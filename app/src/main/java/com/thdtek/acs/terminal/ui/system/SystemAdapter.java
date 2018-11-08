package com.thdtek.acs.terminal.ui.system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdtek.acs.terminal.R;

import java.util.List;

/**
 * Time:2018/6/22
 * User:lizhen
 * Description:
 */

public class SystemAdapter extends BaseAdapter {
    private Context mContext;
    private List<SystemBean> mList;

    public SystemAdapter(Context context, List<SystemBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private ViewHolderItem mViewHolderItem;
    private ViewHolderTitle mViewHolderTitle;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == 0) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_system_title, parent, false);
                mViewHolderTitle = new ViewHolderTitle(convertView);
                convertView.setTag(mViewHolderTitle);
            } else {
                mViewHolderTitle = (ViewHolderTitle) convertView.getTag();
            }
            mViewHolderTitle.mTvTitle.setText(mList.get(position).getTitle());
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_system, parent, false);
                mViewHolderItem = new ViewHolderItem(convertView);
                convertView.setTag(mViewHolderItem);
            } else {
                mViewHolderItem = (ViewHolderItem) convertView.getTag();
            }
            SystemBean systemBean = mList.get(position);
            mViewHolderItem.mTvLeft.setText(systemBean.getLeftMsg());
            mViewHolderItem.mTvRight.setText(systemBean.getRightMsg());

        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    public static class ViewHolderItem {
        public TextView mTvLeft;
        public TextView mTvRight;

        public ViewHolderItem(View view) {
            mTvLeft = view.findViewById(R.id.tv_left);
            mTvRight = view.findViewById(R.id.tv_right);
        }
    }

    public static class ViewHolderTitle {
        public TextView mTvTitle;

        public ViewHolderTitle(View view) {
            mTvTitle = view.findViewById(R.id.tv_title);
        }
    }
}
