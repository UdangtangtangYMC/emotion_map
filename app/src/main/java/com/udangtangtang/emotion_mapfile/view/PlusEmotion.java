package com.udangtangtang.emotion_mapfile.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.AddEmotionCallback;
import com.udangtangtang.emotion_mapfile.presenter.PlusEmotionPresenter;

public class PlusEmotion {
    private PlusEmotionPresenter plusEmotionPresenter;
    private Context context;
    private EditText edt_ment;

    private String comment;


    public PlusEmotion(Context context, PlusEmotionPresenter plusEmotionPresenter) {
        this.context = context;
        this.plusEmotionPresenter = plusEmotionPresenter;

    }

    public void callFunction(AddEmotionCallback addEmotionCallback) {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlg.setContentView(R.layout.dialog_plus);

        dlg.getWindow().getAttributes().windowAnimations = R.style.AnimationPopup;

        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dlg.getWindow().setAttributes(params);
        dlg.show();

        /*init(dlg);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int emotion_id = rdg.getCheckedRadioButtonId();
                //int happy_id = R.id.rg_btn1;
                //selected_emotion = plusEmotionPresenter.get_emotion(emotion_id, happy_id);
                comment = edt_ment.getText().toString();
                if (!comment.equals("")) {
                    plusEmotionPresenter.insert_emotion(selected_emotion, comment);
                    addEmotionCallback.onSuccess();
                    dlg.dismiss();
                } else {
                    Toast.makeText(context, "를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "취소되었습니다", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });*/
    }

    public void init(Dialog dlg) {
        /*rdg = dlg.findViewById(R.id.rdg);
        rd_btn1 = dlg.findViewById(R.id.rg_btn1);
        rd_btn2 = dlg.findViewById(R.id.rg_btn2);*/
        edt_ment = dlg.findViewById(R.id.edt_ment);
        /*btn_ok = dlg.findViewById(R.id.btn_ok);
        btn_cancel = dlg.findViewById(R.id.btn_cancel);*/
    }
}
