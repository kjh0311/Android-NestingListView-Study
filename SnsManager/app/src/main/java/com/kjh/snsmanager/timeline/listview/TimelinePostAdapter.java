package com.kjh.snsmanager.timeline.listview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kjh.snsmanager.MainActivity;
import com.kjh.snsmanager.timeline.listitem.TimelineItem;
import com.kjh.snsmanager.timeline.listitem.Post;
import com.kjh.snsmanager.timeline.listitem.Timeline;

import java.util.Vector;


//public class TimelinePostAdapter extends BaseAdapter {
public class TimelinePostAdapter extends BaseAdapter {

    private Context context;
    private TimelineView parentView;
    private int parentLevel;

    //private Vector<TimelineItem> items;
    private Vector<TimelineItem> items;
    //private Vector<TimelinePostView> items;
    // views 벡터는 인덱스로만 접근해야함
    private Vector<TimelinePostView> views;
    // 포스트 뷰의 높이를 기록하는 벡터
    private int totalHeight = 0; // height의 누적값을 넣음
    private int heightGotCount = 0; // 구한 height의 수를 기록함


    public TimelinePostAdapter(MainActivity mainActivity, TimelineView parentView, int parentLevel) {
        this.context = mainActivity;
        this.parentView = parentView;
        this.parentLevel = parentLevel;

        this.views = new Vector<TimelinePostView>();
    }

    public void setItems(Vector<TimelineItem> items) {
    //public void setItems(Vector<TimelinePostView> items) {
        this.items = items;
        //this.views = new Vector<TimelinePostView>(items.size());
//        for (int i=0; i<items.size(); i++) {
//            views.add(null);
//        }
    }

    // 아이템에 상응하는 뷰를 미리 지정함
    public void setViews(Vector<TimelinePostView> views) {
        this.views = views;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 이 메서드는 처음에 매 요소마다 불리고, notifyDataSetChanged 호출 이후 또 불린다.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean viewGotten = false;

        context = parent.getContext();

        Log.d("어댑터", "getView 호출");

//        TimelinePostView item = items.get(position);

        Log.d("level", parentLevel+"");
        Log.d("items.size()", items.size()+"");
        Log.d("views.size()", views.size()+"");
        Log.d("position", position+"");

//        if (items.size() != views.size()) {
//            //Log.d("어댑터", "게시물 삭제 혹은 생성 발생");
//            // 처음 열 때, 따로 생성 모두 포함됨
//            if (items.size() > views.size()) {
//                Log.d("어댑터", "게시물 추가");
//            } else {
//                Log.d("어댑터", "게시물 삭제");
//            }
//        }
        
        // 게시물 삭제 시 삭제된 게시물 탐지해서 저장된 뷰 지우기
        if (items.size() < views.size()) {
            for (TimelinePostView view : views) {
                boolean deleted = true;
                for (TimelineItem item : items) {
                    if (view.getData() == item) {
                        deleted = false;
                        break;
                    }
                }
                if (deleted) {
                    views.remove(view);
                }
            }
        }

        TimelineItem item = items.get(position);

        if (position < views.size()) {
            convertView = views.get(position);
            viewGotten = true;
        }

        if (convertView == null) {
            Log.d("어댑터", "게시물 생성");

            if (item instanceof Timeline) {
                convertView = new TimelineView(context, (Timeline) item, parentLevel+1, parentView);
            } else {
                convertView = new PostView(context, (Post) item, parentView);
            }

            //views.set(position, (TimelinePostView) convertView);
            views.add((TimelinePostView) convertView);

            // views는 리스트 뷰 크기만 변경해도 어댑터의 재생성으로 초기화 되므로 반드시 다른 곳에 저장해두어야 함
            if (items.size() == views.size()) {
                parentView.setChildViews(views);
                Log.d("뷰 전달", views.size()+"");
            } // views가 초기화된 경우 다시 불러오기
            //TimelinePostView timelinePostView = (TimelinePostView)convertView;
            //int height = ((TimelinePostView)convertView).getCheckedHeight();
        }
        else {
            // 게시물 처음 숨길 때 이곳으로 감
            // 게시물 생성, 삭제 시 실행
            if (viewGotten) {

            }

            Log.d("어댑터", "게시물 생성 안 함");
            if (item instanceof Timeline) {
                ((TimelineView)convertView).setData((Timeline) item);
            } else {
                ((PostView)convertView).setData((Post) item);
            }
        }

        if (item instanceof Post) {
//        if (item instanceof PostView) {
            PostView postView = (PostView) convertView;
//            PostView postView = (PostView) item;

//            Log.d("items", items.size()+"");
//            Log.d("views", views.size()+"");

            Thread thread;
            new Thread(){
                @Override
                public void run() {
                    int height = -1;
//                    Log.d("do", "진입하기 전");
                    do {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //Log.d("do", "내부");
//                        height = postView.getCheckedHeight();
                        height = postView.getCheckedHeight();
                        //height = timelinePostView.getMeasuredHeight();
                    } while (height == -1);
                    totalHeight += height;
                    heightGotCount++;

//                    Log.d("height", height+"");
//                    Log.d("totalHeight", totalHeight+"");

//                    if (heightGotCount == views.size()) {
                    if (heightGotCount == items.size()) {
                        // UI 다루는 작업은 메인 쓰레드에서 해야함
                        //parentView.setListHeight(totalHeight);
                        // 참고 사이트: https://hancho1111.tistory.com/183

//                            Log.d("제출 level", parentLevel+"");
//                            Log.d("원소 수", heightGotCount+"");
//                            Log.d("totalHeight", totalHeight+"");

                        ((MainActivity)context).runOnUiThread(new Runnable(){
                            @Override public void run() {
                                parentView.setListHeight(totalHeight);
                                totalHeight = 0;
                                heightGotCount = 0;
                                //parentView.setListViewHeightBasedOnChildren();
                            }
                        });

                    }
                }
            }.start();
        }

        if (item instanceof Post) {
            if (((Post) item).isWriteMode()) {
                ((PostView)convertView).setToWriteMode();
            }
        }

        return convertView;
    }


}