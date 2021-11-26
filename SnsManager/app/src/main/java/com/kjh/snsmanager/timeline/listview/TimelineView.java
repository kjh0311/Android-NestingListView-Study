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
import com.kjh.snsmanager.timeline.listitem.TimelineItem;

import java.util.concurrent.CopyOnWriteArrayList;


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

    private int level; //, height;
    private boolean decreasing; // 감소모드

    private boolean hidden;
    private int hiddenHeight;
    private CopyOnWriteArrayList<TimelinePostView> childViews;


    // MainActivity에서만 호출
    public TimelineView(Context context, Timeline data) {
        this(context, data, 0, null);
    }

    // TimelinePostAdapter 에서만 호출
    TimelineView(Context context, Timeline data, int level, TimelineView parentView) {
        super(context, data);
        this.mainActivity = (MainActivity) context;
        this.adapter = new TimelinePostAdapter(this.mainActivity, data.getItems(),this, level);
        this.level = level;
        this.parentView = parentView;

        // null 포인터 처리를 위한 더미 벡터
        this.childViews = new CopyOnWriteArrayList<TimelinePostView>();

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
//            Log.d("timeTextView", "click");
//            Log.d("height", height+"");
//            Log.d("hiddenHeight", hiddenHeight+"");
//            Log.d("hidden", hidden+"");

            // 세 번을 눌러야 hidden이 true가 됨
            // 어댑터 때문에 이렇게 되는 것으로 보임
            // 어댑터에서 게시물을 지우고 다시 생성하기 때문
            // 다시 지우기 방지해야 할 듯
            if (hidden) {
                setDecreasing(false);
                setListHeight(hiddenHeight);
                //restoreEditTextFocus();
                hidden = false;
            } else {
                // 에디트 텍스트에 포커스가 있으면 프로그램이 정지되므로 포커스를 막음
                clearEditTextFocus();
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


    @Override
    public void setData(TimelineItem data) {
        this.setData((Timeline)data);
    }

    @Override
    public boolean clearEditTextFocus() {
        boolean focusCleared = false;

        for (TimelinePostView view: childViews) {
            focusCleared = view.clearEditTextFocus();
            if (focusCleared) {
                //Log.d("focusCleared", focusCleared+"");
                break;
            }

        }
        return focusCleared;
    }

    @Override
    public boolean restoreEditTextFocus() {
        boolean focusRequested = false;
        for (TimelinePostView view: childViews) {
            //view.clearEditTextFocus();
            focusRequested = view.restoreEditTextFocus();
            if (focusRequested)
            {
                //Log.d("focusRequested", focusRequested+"");
                // 진입은 성공했지만 포커스 부여가 안 됨
                break;
            }
        }
        return focusRequested;
    }

    public void setData(Timeline data) {
        super.setData(data);
        dataToView(data);
    }

    private void dataToView(Timeline data) {
        informationTextView.setText(data.getInformation());
        timeTextView.setText(data.getTime());
        adapter.setItems(data.getItems());
        listView.setAdapter(this.adapter);
        //listView.requestFocus();


        //setListViewHeightBasedOnChildren(listView);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


    // 어댑터에서 크기 구해온 뒤 호출하는 메서드
    // 마지막 레벨이면 어댑터에서 호출되고, 그렇지 않다면 차일드에 의해 호출됨
    void setListHeight(int newHeight) {
//        Log.d("decreasing", decreasing+"");
//        Log.d("level", level+"");
//        Log.d("height", height+"");
//        Log.d("newHeight", newHeight+"");

        //newHeight = 2516;

        // 감소모드에서는 증가 못 시킴
        if (decreasing && newHeight > height) {
            return;
        } // 감소모드가 아니면 감소시키면 안 됨
        else if (!decreasing && newHeight < height) {
            return;
        } else if (newHeight == height){
            return;
        }
        height = newHeight;
        ViewGroup.LayoutParams params;

        params = listView.getLayoutParams();
        params.height = newHeight + (listView.getDividerHeight()*adapter.getCount()-1);
        listView.setLayoutParams(params);

        if (parentView != null) {
            //parentView.setListHeight(newHeight+200);
            parentView.setListViewHeightByChildren();
        }

    }

    void setListViewHeightByChildren() {
        //setListHeight(newHeight+200);
        int newHeight = 0;

        TimelinePostView view = null;
        for (int i=0; i < childViews.size(); i++) {
            view = childViews.get(i);
//            TimelineView timelineView = (TimelinePostView) view;
            newHeight += view.height;
        }
        if (view instanceof TimelineView) {
            newHeight += 200;
        }
        
        setListHeight(newHeight);
    }

    public void setDecreasing(boolean decreasing) {
        this.decreasing = decreasing;
        if (parentView != null) {
            parentView.setDecreasing(decreasing);
        }
    }

    public void setChildViews(CopyOnWriteArrayList<TimelinePostView> childViews) {
        this.childViews = childViews;
        
        // 여기서 높이 구하면 될 듯
        // 여기서 높이 구한 후 구한 높이가 기존과 다르면 변경
    }

    public CopyOnWriteArrayList<TimelinePostView> getChildViews() {
        return childViews;
    }

    public void setInformationVisible(boolean informationVisible) {
        if (informationVisible) {
            informationTextView.setVisibility(VISIBLE);
        } else {
            informationTextView.setVisibility(GONE);
        }

        for (TimelinePostView view: childViews) {
            if (view instanceof TimelineView) {
                ((TimelineView)view).setInformationVisible(informationVisible);
            }
        }
    }
}
