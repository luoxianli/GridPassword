# GridPassword
格子密码输入控件，带自定义数字键盘

调用方法：
1.在布局文件中引入
  <com.kayak.gridpasswordview.GridPasswordView
        android:id="@+id/gridPasswordView"
        android:layout_width="360dp"
        android:layout_height="60dp"
        android:layout_marginTop="8dp" />

2.在代码中设置数字键盘是否随机:   
  GridPasswordView gridPasswordView = (GridPasswordView) findViewById(R.id.gridPasswordView);
	gridPasswordView.setRandomNum(true);
  
3.获取密码  
  String password = gridPasswordView.getPassword();
