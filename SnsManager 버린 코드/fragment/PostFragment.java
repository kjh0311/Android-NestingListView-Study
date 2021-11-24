package com.kjh.snsmanager.timeline.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kjh.snsmanager.JSONTag;
import com.kjh.snsmanager.R;
import com.kjh.snsmanager.timeline.JSONObjectViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends TimelinePostFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mStrTime;
    private String mStrJson;
    private String mStrMessage;

    private JSONObjectViewModel viewModel;

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


    public PostFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramStrTime Parameter 1.
     * @param paramStrJsonData Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String paramStrTime, String paramStrJsonData) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, paramStrTime);
        args.putString(ARG_PARAM2, paramStrJsonData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStrTime = getArguments().getString(ARG_PARAM1);
            mStrJson = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // private LinearLayout writeLayout, readLayout, editLayout;
        readLayout = view.findViewById(R.id.readLayout);
        editLayout = view.findViewById(R.id.editLayout);
        writeLayout = view.findViewById(R.id.writeLayout);
        
        timeTextView = view.findViewById(R.id.timeTextView);

//        // 읽기모드
//        private TextView messageTextView;
//        private Button btnModify, btnDelete;
//
//        // 편집모드
//        private EditText modifyEditText;
//        private Button btnSave, btnCancel;
//
//        // 쓰기모드
//        private EditText newEditText;
//        private Button btnWrite;
        
        // 읽기모드
        messageTextView = view.findViewById(R.id.messageTextView);
        btnModify = view.findViewById(R.id.btnModify);
        btnDelete = view.findViewById(R.id.btnDelete);
        
        // 편집모드
        modifyEditText = view.findViewById(R.id.modifyEditText);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);
        
        // 쓰기모드
        newEditText = view.findViewById(R.id.newEditText);
        btnPost = view.findViewById(R.id.btnPost);


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

        
        // 게시 시간 표시
        timeTextView.setText(mStrTime);

        // 기본값은 읽기모드
        try {
            JSONObject jsonObject = new JSONObject(mStrJson);
            mStrMessage = jsonObject.getString(JSONTag.MESSAGE);
            //newEditText.setText(mStrMessage);
            messageTextView.setText(mStrMessage);
            //modifyEditText.setText(mStrMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return view;
    }


    // reference: https://developer.android.com/guide/fragments/communicate?hl=ko
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(JSONObjectViewModel.class);
    }

    //    // 읽기모드
//    messageTextView = view.findViewById(R.id.messageTextView);
//    btnModify = view.findViewById(R.id.btnModify);
//    btnDelete = view.findViewById(R.id.btnDelete);
//
//    // 편집모드
//    modifyEditText = view.findViewById(R.id.modifyEditText);
//    btnSave = view.findViewById(R.id.btnSave);
//    btnCancel = view.findViewById(R.id.btnCancel);
//
//    // 쓰기모드
//    newEditText = view.findViewById(R.id.newEditText);
//    btnEdit = view.findViewById(R.id.btnWrite);
    
    
    // 읽기 모드 버튼 동작
    // 게시글을 편집모드용 EditText로 옮기고 편집모드로 전환
    private void modify() {
        modifyEditText.setText(messageTextView.getText());
        setToEditMode();
    }
    // 사용자에게 확인 대화상자 열어야함
    private void delete() {
        // 이 프래그먼트를 제거하고, 상위 프래그먼트가 빈 경우 상위 프래그먼트도 같이 제거함
        super.deleteRequestToParent();
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
        // MainActivity에 게시물 생성 및 삭제를 요청

        //messageTextView.setText(newEditText.getText());
        //setToReadMode();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSONTag.MESSAGE, newEditText.getText());
            viewModel.setJsonObject(jsonObject);
            delete();
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

    public String getMessage() {
        return mStrMessage;
    }
}