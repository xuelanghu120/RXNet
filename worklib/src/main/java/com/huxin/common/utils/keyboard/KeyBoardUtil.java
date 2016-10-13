package com.huxin.common.utils.keyboard;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huxin.common.application.Global;
import com.huxin.common.utils.ServiceManager;

/**
 * Created by 56417 on 2016/10/12.
 */

public class KeyBoardUtil {

    public static boolean isShow(Context context) {
        InputMethodManager im = ServiceManager.getInputMethodManager(context);
        boolean active = im.isActive();
        return active;
    }

    public static void showSoftInputDelay(final EditText editText) {
        Global.postDelay2UI(new Runnable() {
            @Override
            public void run() {
                showSoftInput(editText);
            }
        }, 500);
    }

    /**
     * 弹出键盘
     */
    public static void showSoftInput(final EditText editText) {
        if (null != editText) {
            editText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }

    /**
     * 显示软键盘
     *
     * @param context
     */
    public static void showInputMehtod(Context context) {
        InputMethodManager inputMethodManager = ServiceManager.getInputMethodManager(context);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭软键盘
     *
     * @param context
     */
    public static void closeInputMethod(Context context) {
        if (context != null) {
            if (isShow(context)) {
                InputMethodManager inputMethodManager = ServiceManager
                        .getInputMethodManager(context);
                View cf = ((Activity) context).getCurrentFocus();
                if (inputMethodManager != null && cf != null && cf.getWindowToken() != null) {
                    inputMethodManager.hideSoftInputFromWindow(((Activity) context)
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    /**
     * 强制关闭软键盘？
     *
     * @param context
     */
    public static void forceCloseSoftInputKeyboard(Activity context) {
        if (context != null) {
            InputMethodManager inputMethodManager = ServiceManager.getInputMethodManager(context);
            if (context.getCurrentFocus() != null
                    && context.getCurrentFocus().getWindowToken() != null) {
                inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }
}
