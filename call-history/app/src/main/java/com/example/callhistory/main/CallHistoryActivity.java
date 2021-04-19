package com.example.callhistory.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.callhistory.BaseApplication;
import com.example.callhistory.R;
import com.example.callhistory.utils.BroadcastUtils;
import com.example.callhistory.utils.ToastUtils;
import com.sendbird.calls.DirectCallLog;
import com.sendbird.calls.DirectCallLogListQuery;
import com.sendbird.calls.SendBirdCall;

public class CallHistoryActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayoutEmpty;
    private RecyclerView mRecyclerViewHistory;
    private HistoryRecyclerViewAdapter mRecyclerViewHistoryAdapter;
    private LinearLayoutManager mRecyclerViewLinearLayoutManager;
    private DirectCallLogListQuery mDirectCallLogListQuery;
    private Context mContext;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_call_history);
        initView();
        setVisibility();
        registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    public void initView(){

        mProgressBar = findViewById(R.id.progress_bar);
        mLinearLayoutEmpty = findViewById(R.id.linear_layout_empty);
        mRecyclerViewHistory = findViewById(R.id.recycler_view_history);
        mRecyclerViewHistoryAdapter = new HistoryRecyclerViewAdapter(mContext);
        mRecyclerViewHistory.setAdapter(mRecyclerViewHistoryAdapter);
        mRecyclerViewLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerViewHistory.setLayoutManager(mRecyclerViewLinearLayoutManager);

        mRecyclerViewHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int lastItemPosition = mRecyclerViewLinearLayoutManager.findLastVisibleItemPosition();
                if (lastItemPosition >= 0 && lastItemPosition == mRecyclerViewHistoryAdapter.getItemCount() - 1) {
                    if (mDirectCallLogListQuery != null && mDirectCallLogListQuery.hasNext() && !mDirectCallLogListQuery.isLoading()) {
                        mProgressBar.setVisibility(View.VISIBLE);

                        mDirectCallLogListQuery.next((list, e) -> {
                            mProgressBar.setVisibility(View.GONE);

                            if (e != null) {
                                ToastUtils.showToast(mContext, e.getMessage());
                                return;
                            }

                            if (list.size() > 0) {
                                int positionStart = mRecyclerViewHistoryAdapter.getItemCount();
                                mRecyclerViewHistoryAdapter.addCallLogs(list);
                                mRecyclerViewHistoryAdapter.notifyItemRangeChanged(positionStart, list.size());
                            }
                        });
                    }
                }
            }
        });
    }

    public void setVisibility(){
        mRecyclerViewHistory.setVisibility(View.GONE);
        mLinearLayoutEmpty.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mDirectCallLogListQuery = SendBirdCall.createDirectCallLogListQuery(new DirectCallLogListQuery.Params().setLimit(20));
        mDirectCallLogListQuery.next((list, e) -> {
            mProgressBar.setVisibility(View.GONE);

            if (e != null) {
                ToastUtils.showToast(mContext, e.getMessage());
                return;
            }

            if (list.size() > 0) {
                mRecyclerViewHistory.setVisibility(View.VISIBLE);
                mLinearLayoutEmpty.setVisibility(View.GONE);

                mRecyclerViewHistoryAdapter.setCallLogs(list);
                mRecyclerViewHistoryAdapter.notifyDataSetChanged();
            } else {
                mRecyclerViewHistory.setVisibility(View.GONE);
                mLinearLayoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void registerReceiver() {
        Log.i(BaseApplication.TAG, "[MainActivity] registerReceiver()");

        if (mReceiver != null) {
            return;
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(BaseApplication.TAG, "[MainActivity] onReceive()");

                DirectCallLog callLog = (DirectCallLog)intent.getSerializableExtra(BroadcastUtils.INTENT_EXTRA_CALL_LOG);
                if (callLog != null) {
                    addLatestCallLog(callLog);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.INTENT_ACTION_ADD_CALL_LOG);
        registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        Log.i(BaseApplication.TAG, "[MainActivity] unregisterReceiver()");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    void addLatestCallLog(DirectCallLog callLog) {
        if (mRecyclerViewHistoryAdapter != null) {
            mRecyclerViewHistoryAdapter.addLatestCallLog(callLog);
            mRecyclerViewHistoryAdapter.notifyDataSetChanged();
        }
    }

}
