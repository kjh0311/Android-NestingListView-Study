package com.kjh.snsmanager;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.kjh.snsmanager.timeline.JSONObjectViewModel;
import com.kjh.snsmanager.timeline.fragment.PostFragment;
import com.kjh.snsmanager.timeline.fragment.TimelineFragment;
import com.kjh.snsmanager.vo.SimpleDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UsingFragmentMainActivity extends AppCompatActivity {
    private static final String TAG_PARENT = "TAG_PARENT";

    private static final String
            configFileName = "config.properties",
            configInformationHide = "hide-information",
            configInformationHideComment = "Hide information",
            informationHide = "설명 숨기기",
            informationYear = "연도를 클릭하여 해당 연도의 게시물을 보거나 숨길 수 있습니다.",
            informationMonth = "월을 클릭하여 해당 월의 게시물을 보거나 숨길 수 있습니다.",
            informationWeek = "주간 게시물을 보거나 숨길 수 있습니다.",
            informationDay = "특정 날짜의 게시물을 보거나 숨길 수 있습니다.",
            informationPost = "특정 게시물을 보거나 숨길 수 있습니다.";

    private static final SimpleDateFormat timeFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    // dateToTimeline에서 지속적으로 활용함
    private TimelineFragment timelineFragment, lastYearFragment, lastMonthFragment, lastWeekFragment, lastDayFragment;
    private SimpleDate prevDate = new SimpleDate();


    private JSONObjectViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //timelineFragment = new TimelineFragment();
        timelineFragment = TimelineFragment.newInstance(informationYear);

//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.timeline_container, timelineFragment)
//                .commit();

        // 참고 사이트: https://developer.android.com/guide/fragments/communicate?hl=ko
        viewModel = new ViewModelProvider(this).get(JSONObjectViewModel.class);
        viewModel.getObject().observe(this, object -> {
            // Perform an action with the latest item data
            // PostFragment에서 viewModel에 select를 호출하면 실행되는 부분
            createNewPost(object);
        });
    }
    
    // 프래그먼트가 전개된 후에 프래그먼트에 내용을 추가해야함
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        PostFragment lastPostFragment = null;

        JSONArray data = new JSONArray();
        JSONObject newPost;

        newPost = makePostObject("게시물1");
        data.put(newPost);

        newPost = makePostObject("게시물2");
        data.put(newPost);

        newPost = makePostObject("게시물3");
        data.put(newPost);

        newPost = makePostObject();
        data.put(newPost);

        try {
            dataToTimeline(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lastPostFragment = (PostFragment) lastDayFragment.getLastFragment();
        lastPostFragment.setToWriteMode();
    }

    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment parentFragment = fragmentManager.findFragmentByTag(TAG_PARENT);
        if (parentFragment != null && parentFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
            parentFragment.getChildFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    // 새 슬롯 만들기
    private void createNewPost(JSONObject writtenPostObject) {
        try {
            PostFragment lastPostFragment = null;

            JSONArray data = new JSONArray();
            JSONObject newPostObject = makePostObject(writtenPostObject.getString(JSONTag.MESSAGE));
            JSONObject newPostSlotObject = makePostObject();

            data.put(newPostSlotObject);
            data.put(newPostObject);

            dataToTimeline(data);
            lastPostFragment = (PostFragment) lastDayFragment.getLastFragment();
            Log.d("index", lastPostFragment.getIndex()+"");
            lastDayFragment.checkAllPosts();
            lastPostFragment.setToWriteMode();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private JSONObject makePostObject() {
        return makePostObject("");
    }


    private JSONObject makePostObject(String message) {
        JSONObject newPost = new JSONObject();

        Date now = new Date();
        TimeZone.setDefault( TimeZone.getTimeZone("UTC"));
//        System.out.println(now);
        timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = timeFormatter.format(now) + "+0000";
//    	System.out.println("UTC time: " + utcTime);

        timeFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        TimeZone.setDefault( TimeZone.getTimeZone("Asia/Seoul"));

        try {
            newPost.put(JSONTag.TIME, utcTime);
            newPost.put(JSONTag.MESSAGE, message);
            newPost.put(JSONTag.ID, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newPost;
    }


    private void dataToTimeline(JSONArray data) throws JSONException {

        String am_pm_texts[] = { "오전", "오후" };

        // 다음은 게시물 당 날짜를 기록하는 변수
        SimpleDate date = new SimpleDate();
        // 현재 날짜를 기록
//        DateVO nowDate = new DateVO();
        int am_pm, hour, minute;
//        setToNowDate(nowDate);

//        for (Object obj : data) {
        for (int i = data.length()-1; i >= 0 ; i--) {
            JSONObject jsonObject = (JSONObject) data.get(i);
//        	System.out.println(jsonObject);

            String created_time = (String) jsonObject.get("created_time");
            System.out.println(created_time);
//        	Calendar calendar = setDate(date, created_time);

//        	TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
            // 타임존 로케일 설정
//        	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.KOREA);
            Calendar calendar = Calendar.getInstance(Locale.KOREA);
            if (created_time != null) {
                try {
//        			timeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    created_time = convertUtcToLocal(created_time);
                    calendar.setTime(timeFormatter.parse(created_time));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }

            // 한국 시간으로 변환
//        	calendar.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//        	calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            // month 값은 + 1을 붙혀야함
            date.year = calendar.get(Calendar.YEAR);
            date.month = calendar.get(Calendar.MONTH) + 1;
            date.week = calendar.get(Calendar.WEEK_OF_MONTH);
            date.day = calendar.get(Calendar.DATE);

            am_pm = calendar.get(Calendar.AM_PM);
            hour = calendar.get(Calendar.HOUR);
            minute = calendar.get(Calendar.MINUTE);
//        	System.out.println("변환이후 - am_pm: " + am_pm + ", hour: " + hour + ", minute: " + minute);
//        	System.out.println(date);

            if ( !date.dayEquals(prevDate) ) {

                if ( !date.weekEquals(prevDate) ) {

                    if ( !date.monthEquals(prevDate) ) {

                        if ( !date.yearEquals(prevDate) ) {

                            //timelinePanel.addTimeline(informationMonth, date.year+"년");
                            timelineFragment.addTimeline(informationMonth, date.year+"년");
                            lastYearFragment = (TimelineFragment) timelineFragment.getLastFragment();
                            prevDate.year = date.year;
                        }

                        // yearPanel 생성이 확실히 보장된 후 넣음
                        lastYearFragment.addTimeline(informationWeek, date.month+"월");
                        lastMonthFragment = (TimelineFragment) lastYearFragment.getLastFragment();
                        prevDate.month = date.month;
                    }

                    lastMonthFragment.addTimeline(informationDay, date.week+"주");
                    lastWeekFragment = (TimelineFragment) lastMonthFragment.getLastFragment();
                    prevDate.week = date.week;
                }

                String dayTitle = date.toDayTitle();
                lastWeekFragment.addTimeline(informationPost, dayTitle);
                lastDayFragment = (TimelineFragment) lastWeekFragment.getLastFragment();
                prevDate.day = date.day;
            }

            String timeFormat = "%s %d시 %d분 게시물";
            String timeTitle = String.format(timeFormat, am_pm_texts[am_pm], hour, minute);

            // 모든 게시물은 메인 클래스를 가지도록 함
            lastDayFragment.addPost(timeTitle, jsonObject);
        }
    }


    private String convertUtcToLocal(String utcTime){
        String localTime = "";

        // 표준시를 Date 포맷으로 변경
        Date dateUtcTime;
        try {
            dateUtcTime = timeFormatter.parse(utcTime);
            // 표준시 Date 포맷을 long 타입의 시간으로 변경
            long longUtcTime = dateUtcTime.getTime();

            // TimeZone을 통해 시간차이 계산 (썸머타임 고려 getRawOffset 대신 getOffset 함수 활용)
            TimeZone zone = TimeZone.getDefault();
            int offset = zone.getOffset(longUtcTime);
            long longLocalTime = longUtcTime + offset;

            // long 타입의 로컬 시간을 Date 포맷으로 변경
            Date dateLocalTime = new Date();
            dateLocalTime.setTime(longLocalTime);

            // 로컬 시간을 문자열로 변경하여 리턴
            localTime = timeFormatter.format(dateLocalTime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localTime;
    }

}