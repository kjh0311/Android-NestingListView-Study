package com.kjh.snsmanager.timeline.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kjh.snsmanager.R;

import org.json.JSONObject;

import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends TimelinePostFragment {

    public static final String TAG_CHILD = "TAG_CHILD";
    public static final String KEY_NUMBER = "KEY_NUMBER";
    private int mNumber = 0;

    private FragmentManager.OnBackStackChangedListener mListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            FragmentManager fragmentManager = getChildFragmentManager();
            int count = 0;
            for (Fragment f : fragmentManager.getFragments()) {
                if (f != null) {
                    count++;
                }
            }
            mNumber = count;
        }
    };


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String
            ARG_PARAM_INFORMATION = "ARG_PARAM_INFORMATION",
            ARG_PARAM_TIME = "ARG_PARAM_TIME";

    // TODO: Rename and change types of parameters
    private String strInformation, strTime;
    //private String mParam2;
    //private boolean bInnerFragment;
    private TextView informationTextView, timeTextView;
    private Vector<TimelinePostFragment> childFragments;

    public TimelineFragment() {
        // Required empty public constructor
        childFragments = new Vector<TimelinePostFragment>();
    }


    // 최상위 프래그먼트 생성할 때 사용할 메서드
    public static TimelineFragment newInstance(String paramInformationText) {
        return newInstance(paramInformationText, "");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramInformationText Parameter 1.
     * @param paramTimeText Parameter 2.
     * @return A new instance of fragment TimelineFragment.
     */
    
    // 년월주일에 해당하는 프래그먼트를 생성할 때 사용하는 메서드
    // TODO: Rename and change types and number of parameters
    //public static TimelineFragment newInstance(String param1, String param2) {
    public static TimelineFragment newInstance(String paramInformationText, String paramTimeText) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_INFORMATION, paramInformationText);
        args.putString(ARG_PARAM_TIME, paramTimeText);
        fragment.setArguments(args);
        return fragment;
    }

    
    // 번들 사용 이유: 프래그먼트 재생성 시 복구 하기 위함
    // 출처: https://m.blog.naver.com/tpgns8488/220989078813
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            strInformation = getArguments().getString(ARG_PARAM_INFORMATION);
            strTime = getArguments().getString(ARG_PARAM_TIME);
        }

        // 하위 프래그먼트를 감시함 (https://developer.android.com/training/basics/fragments/pass-data-between?hl=ko)
        // 상위 프래그먼트에게 요청 전달은 슈퍼 클래스인 TimelinePostFragment에 정의함
        // We set the listener on the child fragmentManager
        getChildFragmentManager().setFragmentResultListener("delete", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                int index = bundle.getInt("index");
                // Do something with the result..
                Log.d("index", index+"");
                
                // 뒷 번호들을 모두 setIndex 메서드를 이용해서 앞으로 당겨야함
                FragmentManager childFragmentManager = getChildFragmentManager();
                childFragmentManager.beginTransaction()
                        .remove(childFragments.get(index))
                        .commit();
                childFragments.remove(index);

                if (childFragments.size() > 0) {
                    // 뒷 번호들 앞으로 당기기
                    for (int i = index; i< childFragments.size(); i++) {
                        childFragments.get(i).setIndex(i);
                    }
                } else {
                    deleteRequestToParent();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        informationTextView = view.findViewById(R.id.informationTextView);
        timeTextView = view.findViewById(R.id.timeTextView);

        informationTextView.setText(strInformation);
        timeTextView.setText(strTime);

        return view;
    }

    public Fragment getLastFragment() {
//        FragmentManager childFragmentManager = getChildFragmentManager();
//        return childFragmentManager.getFragments().
//                get(childFragmentManager.getBackStackEntryCount());
        //return childFragments.firstElement(); // 맨 마지막 것을 맨 처음에 추가하므로 first가 마지막임
        return childFragments.lastElement(); // 맨 마지막 것을 맨 처음에 추가하므로 first가 마지막임
    }

    public void addTimeline(String strInformation, String strTime) {
        TimelineFragment fragment = TimelineFragment.newInstance(strInformation, strTime);
        //getChildFragmentManager().beginTransaction().replace(R.id.inner_fragment, innerFragment).commit();
        addChildFragment(fragment);
    }

    public void addPost(String timeTitle, JSONObject jsonObject) {

        PostFragment fragment = PostFragment.newInstance(timeTitle, jsonObject.toString());
        //getChildFragmentManager().beginTransaction().replace(R.id.inner_fragment, innerFragment).commit();
        addChildFragment(fragment);
    }


    private void addChildFragment(TimelinePostFragment fragment) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        childFragmentManager.beginTransaction()
                .add(R.id.inner_fragment, fragment)
                //.addToBackStack(null)
                .commitNow(); // Now 붙히면 커밋이 완료될 때까지 대기 -> 성능에 안 좋을 수 있으므로 시간나면 다른 방법도 알아보기
        fragment.setIndex(childFragments.size());
        childFragments.add(fragment);
    }


    public void checkAllPosts() {
        for (TimelinePostFragment object: childFragments) {
            PostFragment postFragment = (PostFragment) object;
            Log.d("POST", postFragment.getIndex() + ":" + postFragment.getMessage());
        }
    }
}