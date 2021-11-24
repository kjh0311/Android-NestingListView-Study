package com.kjh.snsmanager.timeline.listitem;

import com.kjh.snsmanager.MainActivity;

import org.json.JSONObject;

public class Post extends TimelineItem {
    private String timeTitle;
    private JSONObject jsonObject;
    private boolean writeMode;

    // 타임라인에서 addPost로만 생성
    Post(Timeline parentTimeline, String timeTitle, JSONObject jsonObject) {
        super(parentTimeline);
        this.timeTitle = timeTitle;
        this.jsonObject = jsonObject;
        this.writeMode = false;
    }

    public String getTimeTitle() {
        return timeTitle;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public boolean isWriteMode() {
        return writeMode;
    }

    public void setWriteMode(boolean writeMode) {
        this.writeMode = writeMode;
        // 시간표시 제목을 없앰
        // 이미 추가된 노드에 설정하는 것이므로 노드 순서에는 영향이 없을 것임
        this.timeTitle = "";
    }
}
