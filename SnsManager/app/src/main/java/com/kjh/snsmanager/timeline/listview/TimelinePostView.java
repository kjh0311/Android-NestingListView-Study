package com.kjh.snsmanager.timeline.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.kjh.snsmanager.timeline.listitem.TimelineItem;

//abstract public class TimelinePostView extends LinearLayout {
abstract public class TimelinePostView extends ScrollView {
    private TimelineItem data;
    protected int height = -1; // 높이가 입력 안 된 경우 -1

    public TimelinePostView(Context context, TimelineItem data) {
        super(context);
        this.data = data;
    }

    public void setData(TimelineItem data) {
        this.data = data;
    }

    public TimelineItem getData() {
        return data;
    }

    public abstract boolean clearEditTextFocus();

    public abstract boolean restoreEditTextFocus();
}
