package me.wjc.androidupdate.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.wjc.androidupdate.R;
import me.wjc.androidupdate.update.UpdateBean;

/**
 * @author:: wangjianchi
 * @time: 2018/6/6  15:20.
 * @description:
 */
public class UpdateDialog extends Dialog {
    public UpdateDialog(@NonNull Context context) {
        super(context,R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }
    public void initView(UpdateBean updateBean, final OnUpdateClickListener listener){
        TextView tv_version = findViewById(R.id.tv_version);
        TextView tv_size = findViewById(R.id.tv_size);
        TextView tv_description = findViewById(R.id.tv_description);
        tv_version.setText("版本："+updateBean.getVersion());
        tv_size.setText("包大小："+updateBean.getSize());
        tv_description.setText("版本说明：  "+updateBean.getDescription());
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.update();
            }
        });
    }
    public interface OnUpdateClickListener{
        void update();
    }
}
