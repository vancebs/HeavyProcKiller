package com.hf.heavyprockiller;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2016/1/24.
 *
 * Process List Adapter
 */
public class ProcListAdapter extends BaseAdapter {
    private List<Item> mItemList = new ArrayList<>();
    private String[] mBlackList = null;

    private static class Item {
        public Proc mProc;
        public boolean mIsChecked;

        public Item(Proc proc, boolean checked) {
            mProc = proc;
            mIsChecked = checked;
        }

        public void initCheckBox(CheckBox checkBox) {
            if (mProc == null) {
                checkBox.setText("<Invalid Process>");
                checkBox.setChecked(false);
            } else {
                checkBox.setText(String.format("% 3d%% %s", mProc.getPercent(), mProc.getName()));
                checkBox.setChecked(mIsChecked);
            }
        }
    }

    public ProcListAdapter(String[] blackList) {
        mBlackList = blackList;
    }

    @Override
    public int getCount() {
        return (mItemList == null) ? 0 : mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return ((mItemList == null) || (position < 0) || (position >= mItemList.size())) ? null : mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CheckBox checkBox;
        if (convertView == null) {
            checkBox = new CheckBox(parent.getContext());
        } else {
            checkBox = (CheckBox) convertView;
        }

        mItemList.get(position).initCheckBox(checkBox);

        return checkBox;
    }

    public void setList(List<Proc> list) {
        synchronized (mItemList) {
            mItemList.clear();

            for (Proc proc : list) {
                if (proc.getPercent() > 0) {
                    mItemList.add(new Item(proc,isInBlackList(proc)));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setBlackList(String[] list) {
        synchronized (mItemList) {
            mBlackList = list;

            if (mBlackList != null) {
                for (Item item : mItemList) {
                    item.mIsChecked = isInBlackList(item.mProc);
                }
            }
        }
        notifyDataSetInvalidated();
    }

    private boolean isInBlackList(Proc proc) {
        if (proc == null || mBlackList == null) {
            return false;
        }

        for (String name : mBlackList) {
            if (name.equals(proc.getName())) {
                return true;
            }
        }

        return false;
    }

    public void setItemChecked(int position, boolean checked) {
        if (position < 0 || position >= mItemList.size()) {
            return;
        }

        synchronized (mItemList) {
            mItemList.get(position).mIsChecked = checked;
        }
        notifyDataSetInvalidated();
    }

    public List<Proc> getCheckedList() {
        List<Proc> list = new ArrayList<>();

        synchronized (mItemList) {
            for (Item item : mItemList) {
                if (item.mIsChecked) {
                    list.add(item.mProc);
                }
            }
        }

        return list;
    }

    public boolean isItemChecked(int position) {
        if (position < 0 || position >= mItemList.size()) {
            return false;
        }

        return mItemList.get(position).mIsChecked;
    }
}
