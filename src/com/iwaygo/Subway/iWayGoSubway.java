
package com.iwaygo.Subway;

import com.iwaygo.data.AlgoWay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class iWayGoSubway extends Activity {
	private static final String TAG = "iWayGo";
	
	// 声明窗体控件
	private EditText startEditText, endEditText;
	
	private LinearLayout loadingLayout, noneFocusLayout;
	
	private Spinner moreSpinner;
	
	private ScrollView searchScrollView;
	
	private ImageButton selectStartByLineButton, selectStartBylocationButton,
			selectEndByLineButton, selectEndBylocationButton;
	
	private Button search_button, input_button, line_button, setting_button, goButton;
	
	// 数据库
	AlgoWay algoWay = new AlgoWay(this);
	
	// 获取屏幕信息
	Display display;
	
	// 信息列表
	private String[] stationsStrings, linesStrings;
	
	private static final int DIALOG1_KEY = 0;
	
	private static final int DIALOG2_KEY = 1;
	
	// 临时变量
	int st = 1;
	
	// 首次加载时载入
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		// 标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 载入窗体XML
		setContentView(R.layout.iwaygo_main);
		// 获取屏幕信息
		display = getWindowManager().getDefaultDisplay();
		Log.d(TAG,
				"onCreate getDefaultDisplay:X=" + display.getWidth() + "Y=" + display.getHeight());
		
		// 初始化整个程序所需的资源
		findViews();
		setListener();
		setDatabase();
		setAdapter();
		updateViews();
		/*
		 * Timer timer = new Timer(); timer.schedule(new TimerTask() {
		 * @Override public void run() {
		 * ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE
		 * )).toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN); } }, 3000);
		 */
	}
	
	// 屏幕旋转时调用
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		display = getWindowManager().getDefaultDisplay();
		Log.d(TAG, "getDefaultDisplay:X=" + display.getWidth() + "Y=" + display.getHeight());
		updateViews();
	}
	
	// 更新宽度以适合屏幕
	private void updateViews() {
		search_button.setWidth(display.getWidth() / 4);
		input_button.setWidth(display.getWidth() / 4);
		line_button.setWidth(display.getWidth() / 4);
		setting_button.setWidth(display.getWidth() / 4);
		startEditText.setWidth(display.getWidth() - getPx(130));
		endEditText.setWidth(display.getWidth() - getPx(130));
	}
	
	private int getPx(float dip) {
		return (int) (dip * getResources().getDisplayMetrics().density + 0.5f);
	}
	
	private void findViews() {
		search_button = (Button) findViewById(R.id.search_button);
		//search_button.setPressed(true);
		//search_button.setTextColor(Color.WHITE);
		input_button = (Button) findViewById(R.id.input_button);
		line_button = (Button) findViewById(R.id.line_button);
		setting_button = (Button) findViewById(R.id.setting_button);
		goButton = (Button) findViewById(R.id.gobutton);
		
		selectStartByLineButton = (ImageButton) findViewById(R.id.startbyline);
		selectStartBylocationButton = (ImageButton) findViewById(R.id.startbylocation);
		selectEndByLineButton = (ImageButton) findViewById(R.id.endbyline);
		selectEndBylocationButton = (ImageButton) findViewById(R.id.endbylocation);
		
		startEditText = (EditText) findViewById(R.id.startEditText);
		endEditText = (EditText) findViewById(R.id.endEditText);
		
		startEditText.setText("小红门");
		endEditText.setText("沙河");
		
		searchScrollView = (ScrollView) findViewById(R.id.searchScrollView);
		searchScrollView.setVisibility(1);
		loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
		noneFocusLayout = (LinearLayout) findViewById(R.id.nonefocus);
		moreSpinner = (Spinner) findViewById(R.id.spinner1);
	}
	
	private void setListener() {
		input_button.setOnClickListener(button_map_ClickListener);
		line_button.setOnClickListener(button_line_ClickListener);
		setting_button.setOnClickListener(button_setting_ClickListener);
		
		selectStartByLineButton.setOnClickListener(selectStartByLineButton_ClickListener);
		selectStartBylocationButton.setOnClickListener(selectStartByLocationButton_ClickListener);
		selectEndByLineButton.setOnClickListener(selectEndByLineButton_ClickListener);
		selectEndBylocationButton.setOnClickListener(selectEndByLocationButton_ClickListener);
		
		moreSpinner.setOnItemSelectedListener(moreSpinner_OnItemSelectedListener);
		
		goButton.setOnTouchListener(goButton_OnTouchListener);
	}
	
	private void setDatabase() {
		algoWay.init();
		algoWay.open();
		linesStrings = algoWay.getAllLines();
		// StringBuffer sf = new StringBuffer();
		// Bundle getLinkBundle = algoWay.findLineLinksByStation("和平西桥");
		// for (int i = 0; i < getLinkBundle.getIntArray("LineGoto").length;
		// i++) {
		// sf.append("\tLineGoto::"+getLinkBundle.getIntArray("LineGoto")[i]);
		// sf.append("\tTransfer::"+getLinkBundle.getIntArray("Transfer")[i]);
		// sf.append("\tThroughLineGoto::"+getLinkBundle.getIntArray("ThroughLineGoto")[i]);
		// sf.append("\n");
		// }
		// Log.d(TAG, "getLinkBundle------------>\n"+sf.toString());
		algoWay.close();
	}
	
	private void setAdapter() {
	}
	
	void showToast(CharSequence msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public ListAdapter getAdapter() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		for (int i = 0; i < linesStrings.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			int resID = getResources().getIdentifier("s"+(i+1), "drawable", "com.iwaygo.Subway");
			map.put("ImageManager", resID);
			map.put("ItemManager", linesStrings[i]);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.list_items,
				new String[] {
						"ImageManager", "ItemManager"
				}, new int[] {
						R.id.ImageManager, R.id.ItemManager
				});
		return listItemAdapter;
	}
	
	OnTouchListener goButton_OnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					goButton.setBackgroundResource(R.drawable.go_g);
					break;
				case MotionEvent.ACTION_UP:
					// loadingLayout.setVisibility(1);
					Intent intent = new Intent();
					// 第一个参数为 AndroidMinifest.xml文件中配置的package属性，第二个参数为package +
					// class
					intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.showWay");
					intent.putExtra("startstation", startEditText.getText().toString());
					intent.putExtra("endstation", endEditText.getText().toString());
					startActivity(intent);
					finish();
					//showDialog(DIALOG2_KEY);
					System.gc();
					// overridePendingTransition(R.anim.slide_top_to_bottom,
					// R.anim.my_alpha_action);
					
				default:
					goButton.setBackgroundResource(R.drawable.go);
					break;
			}
			
			return true;
		}
	};
	
	OnClickListener selectStartByLineButton_ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(iWayGoSubway.this).setTitle("选择线路名")
			// .setItems(linesStrings, new DialogInterface.OnClickListener() {
					.setAdapter(getAdapter(), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							algoWay.open();
							String[] tempOut = new String[] {
								linesStrings[which]
							};
							stationsStrings = algoWay.getAllStationsByLine(tempOut);
							algoWay.close();
							new AlertDialog.Builder(iWayGoSubway.this)
									.setTitle("选择站名")
									.setItems(stationsStrings,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog,
														int which) {
													startEditText.setText(stationsStrings[which]
															.toString());
												}
											})
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss(); // 关闭alertDialog
										}
									}).show();
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss(); // 关闭alertDialog
						}
					}).show();
		}
	};
	
	OnClickListener selectStartByLocationButton_ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnClickListener selectEndByLineButton_ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(iWayGoSubway.this).setTitle("选择线路名")
			// .setItems(linesStrings, new DialogInterface.OnClickListener() {
					.setAdapter(getAdapter(), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							algoWay.open();
							String[] tempOut = new String[] {
								linesStrings[which]
							};
							stationsStrings = algoWay.getAllStationsByLine(tempOut);
							algoWay.close();
							new AlertDialog.Builder(iWayGoSubway.this)
									.setTitle("选择站名")
									.setItems(stationsStrings,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog,
														int which) {
													endEditText.setText(stationsStrings[which]
															.toString());
												}
											})
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss(); // 关闭alertDialog
										}
									}).show();
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss(); // 关闭alertDialog
						}
					}).show();
		}
	};
	
	OnClickListener selectEndByLocationButton_ClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	OnItemSelectedListener moreSpinner_OnItemSelectedListener = new OnItemSelectedListener() {
		
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			// showToast("Spinner1: 线路=" + stationsStrings[arg2].toString() +
			// " arg2=" + arg2 + " arg3=" + arg3);
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			// showToast("Spinner1: unselected");
		}
	};
	
	View.OnClickListener button_map_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by map");
			loadingLayout.setVisibility(4);
			Intent intent = new Intent();
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.viewMap");
			System.gc();
			showDialog(DIALOG2_KEY);
			noneFocusLayout.requestFocus();
			startActivity(intent);
			iWayGoSubway.this.finish();
		}
	};
	
	View.OnClickListener button_line_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by line");
			loadingLayout.setVisibility(4);
			Intent intent = new Intent();
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.lineAndStation");
			System.gc();
			showDialog(DIALOG2_KEY);
			noneFocusLayout.requestFocus();
			startActivity(intent);
			iWayGoSubway.this.finish();

		}
	};
	
	View.OnClickListener button_setting_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by setting");
			loadingLayout.setVisibility(4);
			Intent intent = new Intent();
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.setting");
			System.gc();
			showDialog(DIALOG2_KEY);
			noneFocusLayout.requestFocus();
			startActivity(intent);
			iWayGoSubway.this.finish();

		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Log.d("iWayGo", "onClick by search");
			System.gc();

			finish();
			// loadingLayout.setVisibility(4);
		}
		return false;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		noneFocusLayout.requestFocus();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG1_KEY: {
				ProgressDialog dialog = new ProgressDialog(this);
				dialog.setTitle("Indeterminate");
				dialog.setMessage("Please wait while loading...");
				dialog.setIndeterminate(true);
				dialog.setCancelable(true);
				return dialog;
			}
			case DIALOG2_KEY: {
				ProgressDialog dialog = new ProgressDialog(this);
				dialog.setMessage("Please wait while loading...");
				dialog.setIndeterminate(true);
				dialog.setCancelable(true);
				return dialog;
			}
		}
		return null;
	}
	
}
