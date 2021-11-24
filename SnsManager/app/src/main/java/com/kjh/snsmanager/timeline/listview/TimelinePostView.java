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

    public TimelinePostView(Context context) {
        super(context);
    }
}
