package com.kjh.snsmanager.timeline.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kjh.snsmanager.MainActivity;
import com.kjh.snsmanager.R;
import com.kjh.snsmanager.timeline.listitem.Timeline;

import java.util.Vector;


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
    private boolean decreasing; // 감소모드

    private boolean hidden;
    private int hiddenHeight;
    private Vector<TimelinePostView> childViews;


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

        // null 포인터 처리를 위한 더미 벡터
        this.childViews = new Vector<TimelinePostView>();

        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.timeline,this, true);
        //inflater.inflate(R.layout.timeline,this);
        //View v = inflater.inflate(R.layout.timeline,this, false);
        //addView(v);

        layout = findViewById(R.id.layout);

        timeTextView = findViewById(R.id.timeTextView);
        informationTextView = findViewById(R.id.informationTextView);

        timeTextView.setOnClickListener((view)->{
            Log.d("timeTextView", "click");
            Log.d("height", height+"");
            Log.d("hiddenHeight", hiddenHeight+"");
            Log.d("hidden", hidden+"");

            // 세 번을 눌러야 hidden이 true가 됨
            // 어댑터 때문에 이렇게 되는 것으로 보임
            // 어댑터에서 게시물을 지우고 다시 생성하기 때문
            // 다시 지우기 방지해야 할 듯
            if (hidden) {
                setDecreasing(false);
                setListHeight(hiddenHeight);
                hidden = false;
            } else {
                hiddenHeight = height;
                setDecreasing(true);
                setListHeight(0);
                hidden = true;
            }
        });

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
        if (decreasing && newHeight > height) {
            return;
        } // 감소모드가 아니면 감소시키면 안 됨
        else if (!decreasing && newHeight < height) {
            return;
        }
        height = newHeight;
        ViewGroup.LayoutParams params;

        params = listView.getLayoutParams();
        params.height = newHeight + (listView.getDividerHeight()*adapter.getCount()-1);
        listView.setLayoutParams(params);

        if (parentView != null) {
            parentView.setListHeight(newHeight+200);
        }

    }

    public void setDecreasing(boolean decreasing) {
        this.decreasing = decreasing;
        if (parentView != null) {
            parentView.setDecreasing(decreasing);
        }
    }

    public void setChildViews(Vector<TimelinePostView> childViews) {
        this.childViews = childViews;
    }

    public Vector<TimelinePostView> getChildViews() {
        return childViews;
    }
}
