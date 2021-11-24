package com.kjh.snsmanager.timeline.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kjh.snsmanager.MainActivity;
import com.kjh.snsmanager.R;
import com.kjh.snsmanager.timeline.listitem.Post;
import com.kjh.snsmanager.timeline.listitem.Timeline;


public class TimelineView extends TimelinePostView {
    private MainActivity mainActivity;
    private TimelinePostAdapter adapter;
    private TimelineView parentView;

    private LinearLayout layout;

    private TextView timeTextView;
    private TextView informationTextView;

    private LinearLayout listViewContainer;
    private TextView testTextView;
    private ListView listView;

    private int level, height;
    private boolean decresing; // 감소모드

    // MainActivity에서만 호출
    public TimelineView(Context context, Timeline data) {
        this(context, data, 0, null);
    }

    // TimelinePostAdapter 에서만 호출
    TimelineView(Context context, Timeline data, int level, TimelineView parentView) {
        super(context);
        this.mainActivity = (MainActivity) context;
        this.adapter = new TimelinePostAdapter(this.mainActivity, this, level);
        this.level = level;
        this.parentView = parentView;

        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timeline,this, true);
        //inflater.inflate(R.layout.timeline,this);
        //View v = inflater.inflate(R.layout.timeline,this, false);
        //addView(v);

        layout = findViewById(R.id.layout);

        timeTextView = findViewById(R.id.timeTextView);
        informationTextView = findViewById(R.id.informationTextView);

        //listViewContainer = findViewById(R.id.listViewContainer);
        //testTextView = findViewById(R.id.testTextView);
        listView = findViewById(R.id.listView);

        //Toast.makeText(super.getContext(), data.getInformation(), Toast.LENGTH_SHORT).show();

        if (data != null) {
            dataToView(data);
        }
    }

    public void setData(Timeline data) {
        dataToView(data);
    }

    private void dataToView(Timeline data) {
        informationTextView.setText(data.getInformation());
        timeTextView.setText(data.getTime());
        adapter.setItems(data.getItems());
        listView.setAdapter(this.adapter);

        //setListViewHeightBasedOnChildren(listView);
    }

    void setListViewHeightBasedOnChildren() {
        setListViewHeightBasedOnChildren(listView);
    }

    // 출처: https://newbedev.com/how-to-change-listview-height-dynamically-in-android
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            // 다음 줄만 수정
            totalHeight += listItem.getMeasuredHeight()/1.5; // 1.5배로 나눠야 정확한 듯함
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


    // 어댑터에서 크기 구해온 뒤 호출하는 메서드
    // 마지막 레벨이면 어댑터에서 호출되고, 그렇지 않다면 차일드에 의해 호출됨
    void setListHeight(int newHeight) {
//        Log.d("decresing", decresing+"");
//        Log.d("level", level+"");
//        Log.d("newHeight", newHeight+"");

        // 감소모드에서는 증가 못 시킴
        if (decresing && newHeight > height) {
            return;
        } // 감소모드가 아니면 감소시키면 안 됨
        else if (!decresing && newHeight < height) {
            return;
        }
        height = newHeight;
        ViewGroup.LayoutParams params;

        params = listView.getLayoutParams();
        params.height = newHeight + (listView.getDividerHeight()*adapter.getCount()-1);
        listView.setLayoutParams(params);
        listView.requestLayout();

        if (parentView != null) {
            parentView.setListHeight(newHeight+200);
        }

    }

    public void requestDecrease() {
        decresing = true;
        if (parentView != null) {
            parentView.requestDecrease();
        }
    }

    public void requestIncrease() {
        decresing = false;
        if (parentView != null) {
            parentView.requestIncrease();
        }
    }
}
