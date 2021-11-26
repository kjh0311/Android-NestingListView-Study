package com.kjh.snsmanager.timeline.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kjh.snsmanager.JSONTag;
import com.kjh.snsmanager.MainActivity;
import com.kjh.snsmanager.R;
import com.kjh.snsmanager.timeline.listitem.Post;
import com.kjh.snsmanager.timeline.listitem.TimelineItem;

import org.json.JSONException;
import org.json.JSONObject;

public class PostView extends TimelinePostView {
    private MainActivity mainActivity;
    private Post data;
    private TimelineView parentView;

    private LinearLayout readLayout, editLayout, writeLayout;
    private TextView timeTextView;
    // 읽기모드
    private TextView messageTextView;
    private Button btnModify, btnDelete;
    // 편집모드
    private EditText modifyEditText;
    private Button btnSave, btnCancel;
    // 쓰기모드
    private EditText newEditText;
    private Button btnPost;

    private int height = -1; // 높이가 입력 안 된 경우 -1


    PostView(Context context, Post data, TimelineView parentView) {
        super(context, data);
        this.mainActivity = (MainActivity) context;
        this.data = data;
        this.parentView = parentView;

        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.post,this, true);

        readLayout = findViewById(R.id.readLayout);
        editLayout = findViewById(R.id.editLayout);
        writeLayout = findViewById(R.id.writeLayout);

        timeTextView = findViewById(R.id.timeTextView);

        // 읽기모드
        messageTextView = findViewById(R.id.messageTextView);
        btnModify = findViewById(R.id.btnModify);
        btnDelete = findViewById(R.id.btnDelete);

        // 편집모드
        modifyEditText = findViewById(R.id.modifyEditText);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // 쓰기모드
        newEditText = findViewById(R.id.newEditText);
        btnPost = findViewById(R.id.btnPost);

        // 게시 시간 표시
        timeTextView.setText(data.getTimeTitle());

        // 기본값은 읽기모드
        try {
            JSONObject jsonObject = data.getJsonObject();
            String message = jsonObject.getString(JSONTag.MESSAGE);
            messageTextView.setText(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnModify.setOnClickListener((button)->{
            modify();
        });
        btnDelete.setOnClickListener((button)->{
            delete();
        });

        btnSave.setOnClickListener((button)->{
            save();
        });
        btnCancel.setOnClickListener((button)->{
            cancel();
        });

        btnPost.setOnClickListener((button)->{
            post();
        });
    }

    // 읽기 모드 버튼 동작
    // 게시글을 편집모드용 EditText로 옮기고 편집모드로 전환
    private void modify() {
        modifyEditText.setText(messageTextView.getText());
        setToEditMode();
    }
    // 사용자에게 확인 대화상자 열어야함
    private void delete() {
        Log.d("PostView", "삭제");
        parentView.setDecreasing(true);
        data.requestRemoveThis();
        // mainActivity에 어떤 요소가 삭제되었는지 전수조사를 요청함
        mainActivity.notifyDataSetChanged();
    }

    // 편집 모드 버튼 동작
    private void save() {
        messageTextView.setText(modifyEditText.getText());
        setToReadMode();
    }
    private void cancel() {
        setToReadMode();
    }

    // 쓰기 모드 버튼 동작
    private void post() {
        Log.d("PostView", "게시");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONTag.MESSAGE, newEditText.getText());
            mainActivity.createNewPost(jsonObject);
            data.requestRemoveThis();
            // mainActivity에 어떤 요소가 삭제되었는지 전수조사를 요청함
            mainActivity.notifyDataSetChanged();
            parentView.setDecreasing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setToReadMode() {
        readLayout.setVisibility(LinearLayout.VISIBLE);
        editLayout.setVisibility(LinearLayout.INVISIBLE);
        writeLayout.setVisibility(LinearLayout.INVISIBLE);
    }

    private void setToEditMode() {
        readLayout.setVisibility(LinearLayout.INVISIBLE);
        editLayout.setVisibility(LinearLayout.VISIBLE);
        writeLayout.setVisibility(LinearLayout.INVISIBLE);
    }

    // 밖에서만 접근함
    public void setToWriteMode() {
        readLayout.setVisibility(LinearLayout.INVISIBLE);
        editLayout.setVisibility(LinearLayout.INVISIBLE);
        writeLayout.setVisibility(LinearLayout.VISIBLE);
    }

    @Override
    public void setData(TimelineItem data) {
        this.setData((Post)data);
    }

    @Override
    public void clearFocus() {
        modifyEditText.clearFocus();
        newEditText.clearFocus();
    }

    public void setData(Post data) {
        this.data = data;
    }


    // 다음 두 메서드는 크기 구하기 위해서 사용
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        height = getHeight();
        //Log.d("height", getHeight()+"");
    }

    public int getCheckedHeight() {
        int checkedHeight;
        // 이거 강제로 호출하니까 됨
        onWindowFocusChanged(true);
        checkedHeight = height;
        //height = -1; // 체크 안 한 상태로 돌려놓음
        return checkedHeight;
    }
}
