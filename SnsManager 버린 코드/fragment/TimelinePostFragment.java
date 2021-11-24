package com.kjh.snsmanager.timeline.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

// TimelineFragement 와 PostFragment의 공통된 기능을 모아두는 프래그먼트
public class TimelinePostFragment extends Fragment {
    private int index;

    // 상위 프래그먼트가 하위 프래그먼트에게 인덱스를 정해줌
    protected void setIndex(int index) {
        this.index = index;
    }
    // 자신의 인덱스를 얻어옮
    public int getIndex() {
        return index;
    }

    // 상위 프래그먼트에게 요청 전달 (https://developer.android.com/training/basics/fragments/pass-data-between?hl=ko)
    // 하위 프래그먼트 감시는 TimelineFragment의 onCreate 메서드에 구현됨
    protected void deleteRequestToParent() {
        Bundle result = new Bundle();
        result.putInt("index", index);
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("delete", result);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        //return super.onCreateView(inflater, container, savedInstanceState);
//    }
}
