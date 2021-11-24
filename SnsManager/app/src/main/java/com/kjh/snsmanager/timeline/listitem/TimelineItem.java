package com.kjh.snsmanager.timeline.listitem;

abstract public class TimelineItem {
    private Timeline parentTimeline;

    protected TimelineItem() {
        this(null);
    }

    protected TimelineItem(Timeline parentTimeline) {
        this.parentTimeline = parentTimeline;
    }

    public void requestRemoveThis() {
        if (parentTimeline != null) {
            parentTimeline.removeChild(this);
        }
    }
}
