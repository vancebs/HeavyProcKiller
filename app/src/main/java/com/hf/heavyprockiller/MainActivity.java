package com.hf.heavyprockiller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MSG_REFRESH_LIST_BEGIN = 0;
    private static final int MSG_REFRESH_LIST_END = 1;

    private ProcListAdapter mAdapter = null;

    private Button mRefreshButton;
    private Button mKillButton;
    private ListView mListView;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST_BEGIN:
                    refreshListBegin();
                    break;

                case MSG_REFRESH_LIST_END:
                    refreshListEnd((List<Proc>)msg.obj);
                    break;
            }

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKillButton = (Button) findViewById(R.id.btn_kill);
        mRefreshButton = (Button) findViewById(R.id.btn_refresh);
        mListView = (ListView) findViewById(R.id.list);

        mKillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKillButtonClicked();
            }
        });

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefreshButtonClicked();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });
        mListView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    onItemClicked((int)mListView.getSelectedItemId());
                    return true;
                }
                return false;
            }
        });

        // init adapter
        String[] blackList = getResources().getStringArray(R.array.black_list);
        mAdapter = new ProcListAdapter(blackList);

        // init list
        mListView.setAdapter(mAdapter);
        refreshList();
    }

    private void onKillButtonClicked() {
        List<Proc> list = mAdapter.getCheckedList();

        int killedProc = 0;
        for (Proc proc : list) {
            if (proc.kill()) {
                killedProc++;
            }
        }

        Toast.makeText(this, "" + killedProc + " processes killed. " + (list.size() - killedProc) + " processes failed to kill.", Toast.LENGTH_SHORT).show();

        // update list
        refreshList();
    }

    private void onRefreshButtonClicked() {
        refreshList();
    }

    private void onItemClicked(int position) {
        mAdapter.setItemChecked(position, !mAdapter.isItemChecked(position));
    }

    private void onListItemSelected(int position) {
        mAdapter.setItemChecked(position, !mAdapter.isItemChecked(position));
    }

    private void refreshList() {
        mHandler.sendEmptyMessage(MSG_REFRESH_LIST_BEGIN);
    }

    private void refreshListBegin() {
        // disable buttons
        mRefreshButton.setText(R.string.button_refreshing);
        mRefreshButton.setEnabled(false);
        mKillButton.setEnabled(false);
        mListView.setEnabled(false);

        // get top list and update list
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(MSG_REFRESH_LIST_END, Proc.top()).sendToTarget();
            }
        });
        thread.start();
    }

    private void refreshListEnd(List<Proc> list) {
        // update list
        mAdapter.setList(list);

        // enable buttons
        mRefreshButton.setText(R.string.button_refresh);
        mRefreshButton.setEnabled(true);
        mKillButton.setEnabled(true);
        mListView.setEnabled(true);
    }

    // not used at present
    @SuppressWarnings("unused")
    private void startKillerService() {
        Intent serviceIntent = new Intent(this, KillerService.class);
        serviceIntent.setAction(KillerService.ACTION_KILL);
        startService(serviceIntent);
    }
}
