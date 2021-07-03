package com.udangtangtang.emotion_mapfile.view;

import android.app.Dialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.udangtangtang.emotion_mapfile.R;
import com.udangtangtang.emotion_mapfile.presenter.PlusEmotionPresenter;

public class PlusEmotion {
    private PlusEmotionPresenter plusEmotionPresenter;
    private Context context;
    private RadioGroup rdg;
    private RadioButton rd_btn1, rd_btn2;
    private EditText edt_ment;
    private Button btn_ok;
    private Button btn_cancel;


    public PlusEmotion(Context context){
        this.context = context;
        plusEmotionPresenter = new PlusEmotionPresenter(context);
    }

    public void callFunciton(){
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_plus);
        dlg.show();

        rdg = dlg.findViewById(R.id.rdg);
        rd_btn1 = dlg.findViewById(R.id.rg_btn1);
        rd_btn2 = dlg.findViewById(R.id.rg_btn2);
        edt_ment = dlg.findViewById(R.id.edt_ment);
        btn_ok = dlg.findViewById(R.id.btn_ok);
        btn_cancel = dlg.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }
}
