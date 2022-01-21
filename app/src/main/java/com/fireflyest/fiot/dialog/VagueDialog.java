package com.fireflyest.fiot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.fireflyest.fiot.R;
import com.fireflyest.fiot.view.BlurringView;

public class VagueDialog extends Dialog {

    public interface OnItemClickListener{
        void onClick(int id);
    }

    private OnItemClickListener itemClickListener;

    public VagueDialog setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        return this;
    }

    public VagueDialog setOnDismiss(OnDismissListener dismissListener) {
        this.setOnDismissListener(dismissListener);
        return this;
    }

    /**
     * 全屏的模糊弹窗
     *
     */
    public VagueDialog(@NonNull Activity context, @LayoutRes int res) {
        this(context, context.getWindow().getDecorView(), -1, res);
    }

    /**
     * @param blurredView 模糊之前的View
     * @param vagueHeight 距离底部的高度 -1时全屏模糊
     * @param res         弹出的布局  需包含id blurring_view rl_button rl_button ll_release iv_close
     */
    public VagueDialog(@NonNull Context context, View blurredView, int vagueHeight, @LayoutRes int res) {
        super(context, R.style.vague_dialog_style);
        //获取当前Activity所在的窗体
        try {

            View view = View.inflate(context, res, null);
            //获取View 的id
            BlurringView mBlurringView = view.findViewById(R.id.blurring_background);

            //给出了模糊视图并刷新模糊视图。
            if (blurredView != null) {
                mBlurringView.setBlurredView(blurredView, vagueHeight);
                mBlurringView.invalidate();
            }
//            view.measure(0, 0);
//            if (vagueHeight != -1) {
//                lp.height = view.getMeasuredHeight();
//            }

            // 按钮点击事件
            LinearLayout deviceAdd = view.findViewById(R.id.device_add);
            LinearLayout commandAdd = view.findViewById(R.id.command_add);
            deviceAdd.setOnClickListener(v -> {
                itemClickListener.onClick(R.id.device_add);
                dismiss();
            });
            commandAdd.setOnClickListener(v ->{
                itemClickListener.onClick(R.id.command_add);
                dismiss();
            });

            //获取View 的id
            ImageView btClose = view.findViewById(R.id.dialog_close);
            btClose.setOnClickListener(v -> dismiss());

            setContentView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        super.show();

        Window dialogWindow = getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
        this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }
}
