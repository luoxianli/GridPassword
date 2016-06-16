package com.kayak.gridpasswordview;

import android.content.Context;
import android.util.Log;

/**
 * 调试器
 * 
 * @author Luo Xianli
 * 
 */
public class Logger {

	private static Context context;

	private static boolean enabled = true;

	public static final String TAG_DEFAULT = "GridPasswordView";

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		Logger.context = context;
	}

	public static void d(String msg) {
		d(TAG_DEFAULT, msg);
	}

	public static void d(String tag, String msg) {
		if (enabled) {
			Log.d(tag, msg);
		}
	}

	public static void e(String msg) {
		e(TAG_DEFAULT, msg);
	}

	public static void e(String tag, String msg) {
		if (enabled) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (enabled) {
			Log.e(tag, msg, tr);
		}
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		Logger.enabled = enabled;
	}

}
