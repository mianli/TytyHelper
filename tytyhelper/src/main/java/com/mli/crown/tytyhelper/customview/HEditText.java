package com.mli.crown.tytyhelper.customview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mli.crown.tytyhelper.R;
/**
 * Created by mli on 2017/4/22.
 */

public class HEditText extends android.support.v7.widget.AppCompatEditText{


    private int mPaddingRight;
    private boolean mClickRegion;

    public HEditText(Context context) {
        this(context, null);
    }

    public HEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new InputConnectionListener(super.onCreateInputConnection(outAttrs), false);
    }

    private void initView() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && s.length() > 0) {
                    setClearable(true);
                }else {
                    setClearable(false);
                }
        }
        });
        setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
//                    clearFocus();
                    hideInputKeyboard();
                }
                return false;
            }
        });
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused) {
            if(TextUtils.isEmpty(getText())) {
                setClearable(false);
            }else {
                setClearable(true);
            }
        }else {
            setClearable(false);
        }
    }

    private void setClearable(boolean clearable) {
        if(clearable) {
            if(getCompoundDrawables()[2] == null) {
                Drawable rightDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.delete, null);
                rightDrawable.setBounds(new Rect(0, 0, getMeasuredHeight() / 3, getMeasuredHeight() / 3));
                setCompoundDrawables(null, null, rightDrawable, null);
                if(mPaddingRight == 0) {
                    mPaddingRight = getPaddingRight();
                }
                setPadding(getPaddingLeft(), getPaddingTop(), mPaddingRight + getMeasuredHeight() / 3, getPaddingBottom());
            }
        }else {
            setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP && mClickRegion) {
            int eventX = (int) event.getRawX();

            int eventY = (int) event.getRawY();

            Rect rect = new Rect();

            getGlobalVisibleRect(rect);
            rect.top -= getMeasuredHeight() / 3;
            rect.bottom -= getMeasuredHeight() / 3;
            rect.right -= getMeasuredHeight() / 3;
            rect.left = rect.right - getMeasuredHeight() / 3;
            if(rect.contains(eventX, eventY)){
                setText("");
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE
                || event.getAction() == MotionEvent.ACTION_DOWN){
            //这里是右边删除图标的点击位置
            int eventX = (int) event.getX() - getMeasuredWidth() + getMeasuredHeight() / 3 * 2;
            int eventY = (int) event.getY() - getMeasuredHeight() / 3;

            if(eventX < 0 || eventX > getMeasuredHeight() / 3 || eventY < 0 || eventY > getMeasuredHeight() / 3) {
                mClickRegion = false;
            }else {
                mClickRegion = true;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setInputFocus(boolean inputFocus) {
        if(inputFocus) {
            setFocusable(true);
            setFocusableInTouchMode(true);
            requestFocus();

            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(this, 0);
        }else {
            hideInputKeyboard();
        }
    }

    private void hideInputKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private class InputConnectionListener extends InputConnectionWrapper {

        public InputConnectionListener(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean finishComposingText() {
            //键盘消失时失去焦点
            clearFocus();
            return super.finishComposingText();
        }
    }

}
