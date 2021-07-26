package com.udangtangtang.emotion_mapfile.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.PlusEmotionPresenter;
import com.udangtangtang.emotion_mapfile.presenter.Refreshable;

public class PlusEmotion extends DialogFragment {
    private final String TAG = "PlusEmotion";
    private PlusEmotionPresenter plusEmotionPresenter;
    private Context context;
    private EditText edt_comment;
    private LabeledSwitch emotionSwitch;
    private Button submit;
    private TextView ems;
    private Refreshable refreshable;


    public PlusEmotion(Context context, PlusEmotionPresenter plusEmotionPresenter, Refreshable refreshable) {
        this.context = context;
        this.plusEmotionPresenter = plusEmotionPresenter;
        this.refreshable = refreshable;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 다이얼로그의 배경을 투명하게 변경
        Dialog dlg = getDialog();
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.getWindow().getAttributes().windowAnimations = R.style.AnimationPopup;

        //배경 터치로 인해 닫히는 현상 방지
        getDialog().setCanceledOnTouchOutside(false);

        // 다이얼로그에 존재하는 view 초기화
        View view = inflater.inflate(R.layout.dialog_plus,null);
        edt_comment = view.findViewById(R.id.edt_comment);
        Log.d(TAG, "onCreateDialog:"+edt_comment);
        emotionSwitch = view.findViewById(R.id.emotion_switch);
        Log.d(TAG, "onCreateDialog: "+emotionSwitch);
        submit = view.findViewById(R.id.submit);
        Log.d(TAG, "onCreateDialog: "+submit);
        ems = view.findViewById(R.id.ems);
        Log.d(TAG, "onCreateDialog: "+ems);

        // switch의 on, off 문구 설정
        emotionSwitch.setLabelOn("빡침");
        emotionSwitch.setLabelOff("기쁨");
        emotionSwitch.setColorBorder(getResources().getColor(R.color.transparent,null));
        emotionSwitch.setColorOn(getResources().getColor(R.color.angry_switch,null));

        addEventListener();

        return view;
    }

    /**
     * 이벤트 리스너가 필요한 View 객체에 리스너를 추가하는 메소드
     */
    private void addEventListener(){
        // EditText의 내용 변경에 대한 이벤트 리스너 추가
        edt_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged: ");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ems.setText(getString(R.string.ems, String.valueOf(s.length())));
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: ");
            }
        });

        // 등록버튼에 이벤트 리스너 추가
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = edt_comment.getText().toString();
                if (comment.equals("")) {
                    Toast.makeText(context,"내용을 입력해주세요.",Toast.LENGTH_LONG).show();
                } else{
                    if (emotionSwitch.isOn()) {
                        plusEmotionPresenter.insert_emotion("빡침",comment, refreshable);
                    } else{
                        plusEmotionPresenter.insert_emotion("기쁨",comment, refreshable);
                    }
                    getDialog().dismiss();
                }
            }
        });

        // 이후에 창이 닫힐 경우 다시 버튼을 활성화 해주기 위함.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDialog().getOwnerActivity().findViewById(R.id.btn_plus).setEnabled(true);
            }
        }, 400);
    }
}
