package com.hf.heavyprockiller;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fan on 2016/1/24.
 *
 * Process List Adapter
 */
public class ProcListAdapter extends BaseAdapter {
    private final List<Item> mItemList = new ArrayList<>();
    private String[] mBlackList = null;

    private static class Item {
        public Proc mProc;
        public boolean mIsChecked;

        public Item(Proc proc, boolean checked) {
            mProc = proc;
            mIsChecked = checked;
        }

        public void initCheckBox(final CheckBox checkBox) {
            if (mProc == null) {
                checkBox.setText(R.string.invalid_process);
                checkBox.setChecked(false);
            } else {
                checkBox.setText(String.format("% 3d%% %s", mProc.getPercent(), mProc.getName()));
                checkBox.setChecked(mIsChecked);
            }

            // Monitor DPAD_CENTER key
            checkBox.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_UP) {
                        checkBox.setChecked(!mIsChecked);
                    }
                    return false;
                }
            });

            // Monitor check state changed
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mIsChecked = isChecked;
                }
            });
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

    @SuppressWarnings("unused")
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
        return position >= 0 && position < mItemList.size() && mItemList.get(position).mIsChecked;

    }
}
