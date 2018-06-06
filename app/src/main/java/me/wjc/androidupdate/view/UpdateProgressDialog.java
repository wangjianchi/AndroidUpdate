package me.wjc.androidupdate.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.wjc.androidupdate.R;

/**
 * @author:: wangjianchi
 * @time: 2018/6/6  15:33.
 * @description:
 */
public class UpdateProgressDialog extends Dialog {
    private TextView mTextTitle;
    private ProgressBar mProgressBar;
    public UpdateProgressDialog(@NonNull Context context) {
        super(context,R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_progress);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mTextTitle = findViewById(R.id.tv_title);
        mProgressBar = findViewById(R.id.pb_loading);
    }

    public void setProgress(int progress){
        mTextTitle.setText("正在下载中... "+progress+"%");
        mProgressBar.setProgress(progress);
    }
}
