package com.kayak.gridpasswordview.imebugfixer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.kayak.gridpasswordview.GridPasswordView;
import com.kayak.gridpasswordview.R;
import com.kayak.gridpasswordview.imebugfixer.ImeDelBugFixedEditText.OnDelKeyEventListener;

/**
 * 
 * @author Luo Xianli
 * 
 */
public class CustomBoardEditText extends EditText {
	private Context context;
	private KeyboardView keyboardView;
	private Keyboard numKeyboard;// 数字键盘
	private boolean isRandomNum = false;// 是否随机数字键盘
	private boolean isNumKeyBoard = true;// 是否数字键盘

	private EditText ed;

	private PopupWindow mKeyboardWindow;
	private Window mWindow;
	private View mDecorView;
	private View mContentView;

	private int scrolldis = 0; // 输入框在键盘被弹出时，要被推上去的距离

	public static int screenw = -1;// 未知宽高
	public static int screenh = -1;
	public static int screenh_nonavbar = -1; // 不包含导航栏的高度
	public static int real_scontenth = -1; // 实际内容高度， 计算公式:屏幕高度-导航栏高度-电量栏高度
	public static float density = 1.0f;
	public static int densityDpi = 160;

	private OnDelKeyEventListener onDelKeyEventListener;

	public CustomBoardEditText(Context context) {
		super(context);
		this.context = context;
		this.ed = this;
		init();
	}

	public CustomBoardEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.ed = this;
		init();
	}

	public CustomBoardEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		this.ed = this;
		init();
	}

	/**
	 * 设置键盘
	 * 
	 * @param isRandomNum
	 *            数字键盘是否随机排列 默认不随机
	 * @param isRandomQwer
	 *            英文键盘是否随机排列 默认不随机
	 * @param isNumKeyboard
	 *            默认键盘显示（默认英文）：true 数字键盘 false 英文键盘
	 */
	public void setKeyboard(boolean isRandomNum) {
		this.isRandomNum = isRandomNum;
	}

	public boolean isRandomNum() {
		return isRandomNum;
	}

	public void setRandomNum(boolean isRandomNum) {
		this.isRandomNum = isRandomNum;
	}

	// 初始化
	private void init() {

		// 隐藏系统键盘
		hideSysInput();

		initAttributes(context);

		numKeyboard = new Keyboard(context, R.layout.keyboard_symbols_md);

		keyboardView = (KeyboardView) LayoutInflater.from(context).inflate(
				R.layout.keyboard_view, null);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setOnKeyboardActionListener(listener);

		mKeyboardWindow = new PopupWindow(keyboardView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mKeyboardWindow.setAnimationStyle(R.style.AnimationFade);
		mKeyboardWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				if (scrolldis > 0) {
					int temp = scrolldis;
					scrolldis = 0;
					if (null != mContentView) {
						mContentView.scrollBy(0, -temp);
					}
				}
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mKeyboardWindow) {
				if (mKeyboardWindow.isShowing()) {
					mKeyboardWindow.dismiss();
					return true;
				}
			}
		}

		return super.onKeyDown(keyCode, event);

	}

	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		this.mWindow = ((Activity) getContext()).getWindow();
		this.mDecorView = this.mWindow.getDecorView();
		this.mContentView = this.mWindow
				.findViewById(Window.ID_ANDROID_CONTENT);

		hideSysInput();
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		hideKeyboard();

		mKeyboardWindow = null;
		keyboardView = null;
		numKeyboard = null;

		mDecorView = null;
		mContentView = null;
		mWindow = null;
	}

	// 设置是数字键盘
	public void setNum(final GridPasswordView gridPasswordView,
			final CustomBoardEditText tradersPasswordBoard) {
		setKeyboard(false);
		// 隐藏系统键盘
		hideSysInput();
		showKeyboard();
		keyboardView.setKeyboard(numKeyboard);
		// 获取到
	}

	public void setkeyboardView() {
		keyboardView.setKeyboard(numKeyboard);
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Log.d("keydemo", "--------onkey-->" + primaryCode);
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				hideKeyboard();
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (editable != null && editable.length() > 0) {
					if (onDelKeyEventListener != null) {
						onDelKeyEventListener.onDeleteClick();
					}
				}
			} else if (primaryCode == 57419) { // go left
				if (start > 0) {
					ed.setSelection(start - 1);
				}
			} else if (primaryCode == 57421) { // go right
				if (start < ed.length()) {
					ed.setSelection(start + 1);
				}
			} else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};

	public OnDelKeyEventListener getOnDelKeyEventListener() {
		return onDelKeyEventListener;
	}

	public void setOnDelKeyEventListener(
			OnDelKeyEventListener onDelKeyEventListener) {
		this.onDelKeyEventListener = onDelKeyEventListener;
	}

	/**
	 * 是否显示数字键盘
	 * 
	 * @param isNum
	 */
	public void showKeyboard() {
		ed.requestFocus();
		if (null != mKeyboardWindow) {
			if (!mKeyboardWindow.isShowing()) {
				if (isRandomNum) {
					randomNumKey();
				}

				if (isNumKeyBoard) {
					keyboardView.setKeyboard(numKeyboard);
				}

				mKeyboardWindow.showAtLocation(this.mDecorView, Gravity.BOTTOM,
						0, 0);
				mKeyboardWindow.update();

				if (null != mDecorView && null != mContentView) {
					int[] pos = new int[2];
					ed.getLocationOnScreen(pos);
					// 获取popupwindow高度
					// float height = dpToPx(ctx, 200);
					mKeyboardWindow.getContentView().measure(0, 0);
					float height = mKeyboardWindow.getContentView()
							.getMeasuredHeight();

					Rect outRect = new Rect();
					mDecorView.getWindowVisibleDisplayFrame(outRect);

					int screen = real_scontenth;
					scrolldis = (int) ((pos[1] + ed.getMeasuredHeight() - outRect.top) - (screen - height));

					if (scrolldis > 0) {
						mContentView.scrollBy(0, scrolldis);
					}
				}

			}
		}
	}

	/**
	 * 隐藏键盘
	 */
	public void hideKeyboard() {
		if (null != mKeyboardWindow) {
			if (mKeyboardWindow.isShowing()) {
				mKeyboardWindow.dismiss();
			}
		}
	}

	/**
	 * 密度转换为像素值
	 * 
	 * @param dp
	 * @return
	 */
	public static int dpToPx(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/**
	 * 不弹出系统软键盘
	 */
	public void hideSysInput() {
		((Activity) context).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		int currentVersion = android.os.Build.VERSION.SDK_INT;
		String methodName = null;
		if (currentVersion >= 16) {
			// 4.2
			methodName = "setShowSoftInputOnFocus";
		} else if (currentVersion >= 14) {
			// 4.0
			methodName = "setSoftInputShownOnFocus";
		}

		if (methodName == null) {
			ed.setInputType(InputType.TYPE_NULL);
		} else {
			Class<EditText> cls = EditText.class;
			Method setShowSoftInputOnFocus;
			try {
				setShowSoftInputOnFocus = cls.getMethod(methodName,
						boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(ed, false);
			} catch (NoSuchMethodException e) {
				ed.setInputType(InputType.TYPE_NULL);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 数字随机
	 */
	private void randomNumKey() {
		// 随机数
		int[] num = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Random random = new Random();
		for (int i = 0; i < num.length; i++) {
			int m = random.nextInt(10);
			int n = random.nextInt(10);
			int z = num[m];
			num[m] = num[n];
			num[n] = z;
		}
		List<Key> list = numKeyboard.getKeys();
		int data = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).codes[0] >= 48 && list.get(i).codes[0] <= 57) {
				list.get(i).label = String.valueOf(num[data]);
				list.get(i).codes[0] = num[data] + 48;
				data++;
			}
		}

	}

	private void initAttributes(Context context) {
		initScreenParams(context);
		// ed.setLongClickable(false);
		ed.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		removeCopyAbility();

		if (ed.getText() != null) {
			ed.setSelection(ed.getText().length());
		}

		ed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.d("keydemo", "showKeyboard");
				ed.requestFocus();
				showKeyboard();
			}
		});
		ed.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					Log.d("keydemo", "hideKeyboard");
					hideKeyboard();
				} else {
					showKeyboard();
				}
			}
		});

	}

	@TargetApi(11)
	private void removeCopyAbility() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ed.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					return false;
				}
			});
		}
	}

	private void initScreenParams(Context context) {
		DisplayMetrics dMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		display.getMetrics(dMetrics);

		screenw = dMetrics.widthPixels;
		screenh = dMetrics.heightPixels;
		density = dMetrics.density;
		densityDpi = dMetrics.densityDpi;

		screenh_nonavbar = screenh;

		int ver = Build.VERSION.SDK_INT;

		// 新版本的android 系统有导航栏，造成无法正确获取高度
		if (ver == 13) {
			try {
				Method mt = display.getClass().getMethod("getRealHeight");
				screenh_nonavbar = (Integer) mt.invoke(display);
			} catch (Exception e) {
			}
		} else if (ver > 13) {
			try {
				Method mt = display.getClass().getMethod("getRawHeight");
				screenh_nonavbar = (Integer) mt.invoke(display);
			} catch (Exception e) {
			}
		}

		real_scontenth = screenh_nonavbar - getStatusBarHeight(context);

	}

	/**
	 * 电量栏高度
	 * 
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sbar;
	}
}
