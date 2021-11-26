package com.kjh.snsmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kjh.snsmanager.timeline.JSONObjectViewModel;
import com.kjh.snsmanager.timeline.listitem.Post;
import com.kjh.snsmanager.timeline.listitem.Timeline;
import com.kjh.snsmanager.timeline.listview.TimelineView;
import com.kjh.snsmanager.vo.SimpleDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_PARENT = "TAG_PARENT";

    private static final String
            configFileName = "config.properties",
            configInformationHide = "hide-information",
            configInformationHideComment = "Hide information",
            informationShow = "설명 표시",
            informationYear = "연도를 클릭하여 해당 연도의 게시물을 보거나 숨길 수 있습니다.",
            informationMonth = "월을 클릭하여 해당 월의 게시물을 보거나 숨길 수 있습니다.",
            informationWeek = "주간 게시물을 보거나 숨길 수 있습니다.",
            informationDay = "특정 날짜의 게시물을 보거나 숨길 수 있습니다.",
            informationPost = "특정 게시물을 보거나 숨길 수 있습니다.";
    

    private static final SimpleDateFormat timeFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    // dateToTimeline에서 지속적으로 활용함
    private Timeline timelineData, lastYearData, lastMonthData, lastWeekData, lastDayData;
    private SimpleDate lastDate = new SimpleDate();


    // 이것만 있으면 타임라인 전체에 변화 감지 가능
    private TimelineView timelineView;

    // 타임라인 다시그리기용 레퍼런스
    private ScrollView timelineScrollView;


    private CheckBox chkInformation;


    // 평상 시 작성한 소스코드와 스타일이 많이 다른데 이는 논리적 흐름 때문에 코드 순서를 바꿔놓았기 때문
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TimelineView timelineView = new TimelineView(getApplicationContext(), timelineData);
        // 뷰 내부에서 메인 엑티비티의 메서드를 사용해야 하므로 메인 액티비티를 컨텍스트로 전달
        timelineData = new Timeline(informationYear);
        timelineView = new TimelineView(this, timelineData);
        timelineScrollView = findViewById(R.id.timelineScrollView);
        //mainListView = findViewById(R.id.mainListView);
        timelineScrollView.addView(timelineView);

        chkInformation = findViewById(R.id.chkInformation);
        chkInformation.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View checkbox) {
                if (((CheckBox)checkbox).isChecked()) {
                    // TODO : CheckBox is checked.
                    timelineView.setInformationVisible(true);

                } else {
                    // TODO : CheckBox is unchecked.
                    timelineView.setInformationVisible(false);
                }
            }
        });
        
        // 아래는 테스트 데이터
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

//        // 0번째가 맨 위
        Post lastPost = lastDayData.getLastPost();
        if (lastPost != null)
            lastPost.setWriteMode(true);

        timelineScrollView.invalidate();
    }

    // 어떤 포스트가 삭제되었는지 전수조사를 요청함
    public void notifyDataSetChanged() {
        timelineView.notifyDataSetChanged();
    }


    // 새 슬롯 만들기
    public void createNewPost(JSONObject writtenPostObject) {
        try {
            JSONArray data = new JSONArray();
            JSONObject newPostObject = makePostObject(writtenPostObject.getString(JSONTag.MESSAGE));
            JSONObject newPostSlotObject = makePostObject();

            data.put(newPostObject);
            data.put(newPostSlotObject);

            dataToTimeline(data);

            Post lastPost = lastDayData.getLastPost();
            lastPost.setWriteMode(true);
            timelineScrollView.invalidate();

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

        //for (Object obj : data) {
        //for (int i = data.length()-1; i >= 0 ; i--) {
        for (int i=0; i<data.length(); i++) {
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

            if ( !date.dayEquals(lastDate) ) {

                if ( !date.weekEquals(lastDate) ) {

                    if ( !date.monthEquals(lastDate) ) {

                        if ( !date.yearEquals(lastDate) ) {

                            timelineData.addTimeline(informationMonth, date.year+"년");
                            lastYearData = timelineData.getLastTimeline();
                            lastDate.year = date.year;
                        }

                        lastYearData.addTimeline(informationWeek, date.month+"월");
                        lastMonthData = lastYearData.getLastTimeline();
                        lastDate.month = date.month;
                    }

                    lastMonthData.addTimeline(informationDay, date.week+"주");
                    lastWeekData = lastMonthData.getLastTimeline();
                    lastDate.week = date.week;
                }

                String dayTitle = date.toDayTitle();

                lastWeekData.addTimeline(informationPost, dayTitle);
                lastDayData = lastWeekData.getLastTimeline();
                lastDate.day = date.day;
            }

            String timeFormat = "%s %d시 %d분 게시물";
            String timeTitle = String.format(timeFormat, am_pm_texts[am_pm], hour, minute);

            // 모든 게시물은 메인 클래스를 가지도록 함
            lastDayData.addPost(timeTitle, jsonObject);
            //lastDayData.add(0, new Post(timeTitle, jsonObject));
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