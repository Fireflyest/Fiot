package com.fireflyest.fiot.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.fireflyest.fiot.R;

public class IconEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Drawable imgInable;
    private Drawable accountIcon;
    private Drawable passwordIcon;
    private Drawable searchIcon;
    private Drawable eyeIcon;
    private Drawable emptyIcon;

    private String iconName;

    public IconEditText(Context context) {
        super(context);
    }

    public IconEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init(Context context, AttributeSet attrs){
//        imgInable = ContextCompat.getDrawable(context, R.drawable.ic_account);
//        accountIcon = ContextCompat.getDrawable(context, R.drawable.ic_account);
//        passwordIcon = ContextCompat.getDrawable(context, R.drawable.ic_password);
//        emptyIcon = ContextCompat.getDrawable(context, R.drawable.ic_empty);
//        eyeIcon = ContextCompat.getDrawable(context, R.drawable.ic_eye);
        searchIcon = ContextCompat.getDrawable(context, R.drawable.ic_search);

//        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Icon, 0, 0);
//        iconName = typedArray.getString(R.styleable.Icon_iconName);
//        typedArray.recycle();


        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setLeftIcon();
    }

    // 设置删除图片
    private void setDrawable() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, this.length() < 1 ? null:imgInable, null);
    }

    // 设置删除图片
    private void setLeftIcon() {
//        switch (iconName){
//            case "account":
//                setCompoundDrawablesWithIntrinsicBounds(accountIcon, null, emptyIcon, null);
//                break;
//            case "password":
//                setCompoundDrawablesWithIntrinsicBounds(passwordIcon, null, emptyIcon, null);
//                break;
//            case "search":
//                setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, emptyIcon, null);
//                break;
//            default:
//        }
        setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, emptyIcon, null);

    }

}
