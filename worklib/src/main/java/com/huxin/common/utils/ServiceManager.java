package com.huxin.common.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Vibrator;
import android.text.ClipboardManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class ServiceManager {

	public static InputMethodManager getInputMethodManager(Context context) {
		return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	public static WindowManager getWindowsManager(Context context) {
		WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return window;
	}

	public static NotificationManager getNotificationManager(Context context) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		return nm;
	}

	@SuppressWarnings("deprecation")
	public static ClipboardManager getClipboardManager(Context context) {
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb;
	}

	public static Vibrator getVibrator(Context context) {
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		return vibrator;
	}

}
