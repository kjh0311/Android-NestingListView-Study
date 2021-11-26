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
import java.util.concurrent.CopyOnWriteArrayList;


//public class TimelinePostAdapter extends BaseAdapter {
public class TimelinePostAdapter extends BaseAdapter {

    private Context context;
    private TimelineView parentView;
    private int parentLevel;

    //private Vector<TimelineItem> items;
    private Vector<TimelineItem> items;
    //private Vector<TimelinePostView> items;
    // 뷰 삭제 시 오류나므로 Vector 대신 CopyOnWriteArrayList 사용
    private CopyOnWriteArrayList<TimelinePostView> views;
    // 포스트 뷰의 높이를 기록하는 벡터
    //private CopyOnWriteArrayList<Integer> viewHeights;

//    private boolean heightChecked = false;
    private int totalHeight = 0; // height의 누적값을 넣음
    private int heightGotCount = 0; // 구한 height의 수를 기록함


    public TimelinePostAdapter(MainActivity mainActivity, Vector<TimelineItem> items, TimelineView parentView, int parentLevel) {
        this.context = mainActivity;
        this.items = items;
        this.parentView = parentView;
        this.parentLevel = parentLevel;
        this.views = new CopyOnWriteArrayList<TimelinePostView>();
        //this.views = new CopyOnWriteArrayList<Integer>();
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
    public void setViews(CopyOnWriteArrayList<TimelinePostView> views) {
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

    // 삭제, 생성 시 높이 구하기 위해서 오버라이드 함
//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//        heightChecked = false;
//    }

    // 이 메서드는 처음에 매 요소마다 불리고, notifyDataSetChanged 호출 이후 또 불린다.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean dataChanged = false;

        context = parent.getContext();

//        Log.d("어댑터", "getView 호출");

//        TimelinePostView item = items.get(position);

//        Log.d("level", parentLevel+"");
//        Log.d("items.size()", items.size()+"");
//        Log.d("views.size()", views.size()+"");
//        Log.d("position", position+"");


        if (items.size() != views.size()) {
            dataChanged = true;
        }
        
        // 게시물 삭제 시 삭제된 게시물 탐지해서 저장된 뷰 지우기
        // 뷰가 더 많음
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
        } // 게시물 추가 시 추가된 아이템 탐지해서 뷰 만들기
        // 아이템이 더 많음
        // 그리고 게시물 추가 중이면 views 의 크기는 0이 될 수 없음
        else if (items.size() > views.size()) {
            for (int i=0; i<items.size(); i++) {
                TimelineItem item = items.get(i);

                // if 문에 한 번도 안 걸리면 true
                boolean isNewItem = true;
                for (TimelinePostView view : views) {
                    if (view.getData() == item) {
                        isNewItem = false;
                        break; // 뷰가 이미 있음
                    }
                }
                if (isNewItem) {
                    // 처음 실행 시에는 여기에 안 걸리도록 제한함
                    // 제한한 이유는 높이 계산이 오작동하기 때문
//                    Log.d("i < views.size()", "조건 검사");
                    if (i < views.size()) {
//                        Log.d("i < views.size()", "조건 통과");
                        View view = addViewFromItem(item);
                        views.add(i, (TimelinePostView) view);
//                        Log.d("어댑터", "getView 재귀 호출");
                        getView(i, view, parent);
//                        Log.d("어댑터", "getView 재귀 호출 복귀");
                    }
                }
            }
        }

        TimelineItem item = items.get(position);

        // 처음 실행 시에는 views.size() 값이 
//        Log.d("views.size()", views.size()+"");
//        Log.d("position", position+"");

        // 처음 실행 시에만 전체 뷰를 새로 만들도록 필터링해서 속도 최적화함
        if (position < views.size()) {
            convertView = views.get(position);
            //viewGotten = true;
        }

//        if (dataChanged) return convertView;
//        Log.d("convertView == null", "조건 검사");
        if (convertView == null) {
//            Log.d("convertView == null", "조건 통과");
            convertView = addViewFromItem(item);
            views.add((TimelinePostView) convertView);
            //TimelinePostView timelinePostView = (TimelinePostView)convertView;
            //int height = ((TimelinePostView)convertView).getCheckedHeight();
        }
        else {
            // 게시물 생성, 삭제, 숨기기 후에는 여기에 진입해야 제대로 반영됨
            // 게시물 숨기기와 관련된 부분
            // 그리고 게시물 생성, 삭제와 관련된 부분
//            Log.d("게시물 생성 안 함", "setData 호출");
            
            // 이렇게하면 무한 재귀호출에 의한 스택 오버플로우 문제가 발생함
            ((TimelinePostView)convertView).setData(item);
//            if (item instanceof Timeline) {
//                ((TimelineView)convertView).setData((Timeline) item);
//            } else {
//                ((PostView)convertView).setData((Post) item);
//            }
        }

        if (items.size() > 0 && items.size() == views.size()) {
            parentView.setChildViews(views);

            new Thread() {
                @Override
                public void run() {
                    boolean heightChecked = false;

                    for (int i=0; i<views.size(); i++) {
                    //for (TimelinePostView view : views) {
                        if (i>position) {
                            break;
                        }
                        else if (items.get(0) instanceof Post) {
                            //PostView postView = (PostView) view;
                            PostView postView = (PostView) views.get(i);
                            int height;
                            do {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
//                                    Log.d("do", "내부");
//                                    height = postView.getCheckedHeight();
                                height = postView.getCheckedHeight();
                                //height = timelinePostView.getMeasuredHeight();
                            } while (height == -1);

                            heightChecked = true;
                        }
                    }

                    if (heightChecked) {
                        ((MainActivity)context).runOnUiThread(() -> {
                            parentView.setListViewHeightByChildren();
                        });
                    }
                }
            }.start();
        }

        // 높이조절 부분 (게시물 생성 여부와 무관하게 있어야함)

        // 아이템 수 만큼 실행 되어야함
//        if (item instanceof Post) {
//            PostView postView = (PostView)convertView;
//
//            new Thread(){
//                @Override
//                public void run() {
//                    int height = -1;
////                    Log.d("do", "진입하기 전");
//                    do {
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
////                        Log.d("do", "내부");
////                        height = postView.getCheckedHeight();
//                        height = postView.getCheckedHeight();
//                        //height = timelinePostView.getMeasuredHeight();
//                    } while (height == -1);
//                    totalHeight += height;
//                    heightGotCount++;
//
////                    Log.d("height", height+"");
////                    Log.d("totalHeight", totalHeight+"");
//
//                    // 새로 생긴 뷰는 확인 안 함을 확인
//                    //Log.d("postView", postView+"");
//                    Log.d("items.size()", items.size()+"");
//                    Log.d("views.size()", views.size()+"");
//                    Log.d("heightGotCount", heightGotCount+"");
//
////                    if (heightGotCount == views.size()) {
//                    if (heightGotCount == items.size()) {
//                        // UI 다루는 작업은 메인 쓰레드에서 해야함
//                        //parentView.setListHeight(totalHeight);
//                        // 참고 사이트: https://hancho1111.tistory.com/183
//
//                        Log.d("제출 level", parentLevel+"");
//                        Log.d("원소 수", heightGotCount+"");
//                        Log.d("totalHeight", totalHeight+"");
//
//                        ((MainActivity)context).runOnUiThread(() -> {
//                            //parentView.setDecreasing(false);
//                            parentView.setListHeight(totalHeight);
//                            totalHeight = 0;
//                            heightGotCount = 0;
//                            //parentView.setListViewHeightBasedOnChildren();
//                        });
//
//                    }
//                }
//            }.start();
//        }

        return convertView;
    }
    
    
    private View addViewFromItem(TimelineItem item) {
//        Log.d("어댑터", "게시물 생성");
        View view;

        if (item instanceof Timeline) {
            view = new TimelineView(context, (Timeline) item, parentLevel+1, parentView);
        } else {
            PostView postView = new PostView(context, (Post) item, parentView);
            view = postView;

            if (((Post) item).isWriteMode()) {
                postView.setToWriteMode();
            }
        }

        //views.set(position, (TimelinePostView) convertView);


        // views는 리스트 뷰 크기만 변경해도 어댑터의 재생성으로 초기화 되므로 반드시 다른 곳에 저장해두어야 함
//        if (items.size() == views.size()) {
//            parentView.setChildViews(views);
//            Log.d("뷰 전달", views.size()+"");
//        } // views가 초기화된 경우 다시 불러오기

        return view;
    }
    
}