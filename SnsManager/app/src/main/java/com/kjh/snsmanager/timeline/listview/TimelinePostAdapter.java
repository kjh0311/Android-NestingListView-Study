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


public class TimelinePostAdapter extends BaseAdapter {

    private Context context;
    private TimelineView parentView;
    private int parentLevel;

    //private Vector<TimelineItem> items;
    private Vector<TimelineItem> items;
    // views 벡터는 인덱스로만 접근해야함
    // 그리고 마지막 레벨 타임라인에서만 접근함
    private Vector<PostView> postViews;
    // 포스트 뷰의 높이를 기록하는 벡터
    private int totalHeight = 0; // height의 누적값을 넣음
    private int heightGotCount = 0; // 구한 height의 수를 기록함


    public TimelinePostAdapter(MainActivity mainActivity, TimelineView parentView, int parentLevel) {
        this.context = mainActivity;
        this.parentView = parentView;
        this.parentLevel = parentLevel;
    }

    public void setItems(Vector<TimelineItem> items) {
        this.items = items;
        this.postViews = new Vector<PostView>();
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
        context = parent.getContext();
        TimelineItem item = items.get(position);
        if (convertView == null) {
            if (item instanceof Timeline) {
                convertView = new TimelineView(context, (Timeline) item, parentLevel+1, parentView);
            } else {
                //convertView = new PostView(context, (Post) item);

                PostView postView = new PostView(context, (Post) item, parentView);
                convertView = postView;

                postViews.add(postView);
//            Log.d("items", items.size()+"");
//            Log.d("views", views.size()+"");

                // 삭제나 게시할 때는 height 값이 안 바뀜
                Thread thread;
                new Thread(){
                    @Override
                    public void run() {
                        int height = -1;
                        //Log.d("do", "진입하기 전");
                        do {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //Log.d("do", "내부");
                            height = postView.getCheckedHeight();
                            //height = timelinePostView.getMeasuredHeight();
                        } while (height == -1);
                        totalHeight += height;
                        heightGotCount++;

//                    Log.d("height", height+"");
//                    Log.d("totalHeight", totalHeight+"");

                        if (heightGotCount == postViews.size()) {
                            // UI 다루는 작업은 메인 쓰레드에서 해야함
                            //parentView.setListHeight(totalHeight);
                            // 참고 사이트: https://hancho1111.tistory.com/183

//                            Log.d("제출 level", parentLevel+"");
//                            Log.d("원소 수", heightGotCount+"");
//                            Log.d("totalHeight", totalHeight+"");

                            ((MainActivity)context).runOnUiThread(new Runnable(){
                                @Override public void run() {
                                    parentView.setListHeight(totalHeight);
                                    //parentView.setListViewHeightBasedOnChildren();
                                }
                            });
                        }
                    }
                }.start();
            }
            //TimelinePostView timelinePostView = (TimelinePostView)convertView;
            //int height = ((TimelinePostView)convertView).getCheckedHeight();
        }
        else {
            // 여기에 걸리는 경우는 거의 없음
            if (item instanceof Timeline) {
                ((TimelineView)convertView).setData((Timeline) item);
            } else {
                ((PostView)convertView).setData((Post) item);
            }
        }

        if (item instanceof Post) {
            if (((Post) item).isWriteMode()) {
                ((PostView)convertView).setToWriteMode();
            }
        }

//        if (item instanceof Timeline) {
//            TimelineView timelineView;
//
////            convertView = new TimelineView(context, (Timeline) item);
//            if (convertView == null) {
//                convertView = new TimelineView(context, (Timeline) item);
//            } else {
//                ((TimelineView)convertView).setData((Timeline) item);
//            }
//            timelineView = (TimelineView) convertView;
//        } else {
////            convertView = new PostView(context, (Post) item);
//            if (convertView == null) {
//                convertView = new PostView(context, (Post) item);
//            } else {
//                ((PostView)convertView).setData((Post) item);
//            }
//
//            if (((Post) item).isWriteMode()) {
//                ((PostView)convertView).setToWriteMode();
//            }
//        }

        return convertView;
    }
}