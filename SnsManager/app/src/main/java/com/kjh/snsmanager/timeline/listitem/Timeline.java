package com.kjh.snsmanager.timeline.listitem;

import org.json.JSONObject;

import java.util.Vector;

// 이 클래스가 게시물 생성/삭제, 순서 지정에 중요한 역할을 함
public class Timeline extends TimelineItem {
    private final String information, time;
    private final Vector<TimelineItem> items;


    public Timeline() {
        this("");
    }
    public Timeline(String information) {
        this(information, "");
    }
    public Timeline(String information, String time) {
        this(null, information, time);
    }
    // 이 클래스 내에서 생성할 때만 페어런트를 지정함
    private Timeline(Timeline parentTimeline, String information, String time) {
        super(parentTimeline);
        this.information = information;
        this.time = time;
        items = new Vector<TimelineItem>();
    }


    public String getInformation() {
        return information;
    }
    public String getTime() {
        return time;
    }
    public Vector<TimelineItem> getItems() {
        return items;
    }

    private TimelineItem getLastItem() {
        // 맨 위에 맨 마지막 것이 추가됨
        if (items.size()>0)
            return items.get(0);
        else
            return null;
    }
    public Timeline getLastTimeline() {
        return (Timeline) getLastItem();
    }
    public Post getLastPost() {
        return (Post) getLastItem();
    }


    public void removeChild(TimelineItem timelineItem) {
        items.remove(timelineItem);
        if (items.size() <= 0) {
            super.requestRemoveThis();
        }
    }

    public void addTimeline(String information, String time) {
        Timeline childTimeline = new Timeline(this, information, time);
        // 맨 위에 맨 마지막 것을 추가함
        items.add(0, childTimeline);
    }

    public void addPost(String timeTitle, JSONObject jsonObject) {
        Post childPost = new Post(this, timeTitle, jsonObject);
        // 맨 위에 맨 마지막 것을 추가함
        items.add(0, childPost);
        //items.add(childPost);
    }
}
