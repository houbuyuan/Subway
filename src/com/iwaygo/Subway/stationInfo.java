
package com.iwaygo.Subway;

import com.iwaygo.data.AlgoWay;
import com.iwaygo.util.DynamicArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class stationInfo extends Activity {
	private static final String TAG = "iWayGo";
	
	// ��������ؼ�
	private TextView textView_test, textView_test2, textView_test3, title1, title2;
	
	private Button backButton;
	
	// ���ݿ�
	AlgoWay algoWay = new AlgoWay(this);
	
	// ��ȡ��Ļ��Ϣ
	Display display;
	
	// ��Ϣ�б�
	private String startStationString, endStationString;
	
	public List<String> group;
	
	public List<List<String>> child;
	
	// private String[] stationsStrings,linesStrings;
	private static final int DIALOG1_KEY = 0;
	
	private static final int DIALOG2_KEY = 1;
	
	DynamicArray bundleArray = new DynamicArray();
	
	// �״μ���ʱ����
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ���봰��XML
		setContentView(R.layout.iwaygo_info);
		
		// ��ȡ��Ļ��Ϣ
		display = getWindowManager().getDefaultDisplay();
		Log.d(TAG,
				"onCreate getDefaultDisplay:X=" + display.getWidth() + "Y=" + display.getHeight());
		
		// ��ʼ�����������������Դ
		findViews();
		setListener();
		setDatabase();
		setAdapter();
		updateViews();
		
		if (getStations()) {
			algoWay.open();
			Bundle getStations = algoWay.getStationInfoByName(endStationString);
			if (!(getStations() == null)) {
				StringBuffer titleBuffer = new StringBuffer();
				titleBuffer.append(endStationString.toString() + "");
				for (int j = 0; j < getStations.getIntArray("_id_line").length; j++) {
					int resIDs = getResources().getIdentifier(
							"xs" + getStations.getIntArray("_id_line")[j], "drawable",
							"com.iwaygo.Subway");
					titleBuffer.append("  <img src='" + resIDs + "'>");
				}
				textView_test.setText(Html.fromHtml(titleBuffer.toString(), imageGetter, null));
				
				title1.setText("��ĩ�೵ʱ��");
				StringBuffer sf = new StringBuffer();
				for (int i = 0; i < getStations.getIntArray("_id_line").length; i++) {
					int resIDs = getResources().getIdentifier(
							"x" + getStations.getIntArray("_id_line")[i], "drawable",
							"com.iwaygo.Subway");
					sf.append("  <img src='" + resIDs + "'> �����г�<br />");
					sf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�׳� ");
					if (getStations.getStringArray("up_first_time")[i] == null) {
						sf.append("����");
					} else {
						sf.append(getStations.getStringArray("up_first_time")[i]);
					}
					sf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ĩ�� ");
					if (getStations.getStringArray("up_last_time")[i] == null) {
						sf.append("����");
					} else {
						sf.append(getStations.getStringArray("up_last_time")[i]);
					}
					sf.append("<br />");
					
					resIDs = getResources().getIdentifier(
							"x" + getStations.getIntArray("_id_line")[i], "drawable",
							"com.iwaygo.Subway");
					sf.append("  <img src='" + resIDs + "'> �����г�<br />");
					sf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�׳� ");
					if (getStations.getStringArray("down_first_time")[i] == null) {
						sf.append("����");
					} else {
						sf.append(getStations.getStringArray("down_first_time")[i]);
					}
					sf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ĩ�� ");
					if (getStations.getStringArray("down_last_time")[i] == null) {
						sf.append("����");
					} else {
						sf.append(getStations.getStringArray("down_last_time")[i]);
					}
					sf.append("<br />");
				}
				textView_test2.setText(Html.fromHtml(sf.substring(0, sf.length() - 6).toString(),
						imageGetter, null));
				sf = new StringBuffer();
				
				StringBuffer outs = new StringBuffer();
				outs.append(getStations.getStringArray("outs_info")[0]);
				int location = -1;
				int location_next = 0;
				while (true) {
					location_next = outs.indexOf("]", location + 1);
					if (location_next < 0) {
						break;
					}
					int resIDs = getResources().getIdentifier("exit", "drawable",
							"com.iwaygo.Subway");
					sf.append("  <img src='" + resIDs + "'>&nbsp;&nbsp;");
					sf.append(outs.substring(location + 2, location_next) + "<br />");
					location = location_next;
					location_next = outs.indexOf(" ", location + 1);
					sf.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					sf.append(outs.substring(location + 1, location_next) + "<br />");
					location = location_next;
				}
				
				title2.setText("������Ϣ");
				if (sf.length()>6) {
					textView_test3.setText(Html.fromHtml(sf.substring(0, sf.length() - 6).toString(),
							imageGetter, null));
				}else {
					textView_test3.setText("����");
				}
				
			}
			algoWay.close();
		} else {
			textView_test.setText("������������\n��������ѡ�����վ����2�λ��ˡ�������ѡ��лл��");
		}
	}
	
	// ��Ļ��תʱ����
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		display = getWindowManager().getDefaultDisplay();
		Log.d(TAG, "getDefaultDisplay:X=" + display.getWidth() + "Y=" + display.getHeight());
		updateViews();
	}
	
	// ���¿�����ʺ���Ļ
	private void updateViews() {
	}
	
	private void findViews() {
		title1 = (TextView) findViewById(R.id.title1);
		title2 = (TextView) findViewById(R.id.title2);
		textView_test = (TextView) findViewById(R.id.test_textView);
		textView_test2 = (TextView) findViewById(R.id.test_textView2);
		textView_test3 = (TextView) findViewById(R.id.test_textView3);
		backButton = (Button) findViewById(R.id.backbutton);
	}
	
	private void setListener() {
		backButton.setOnTouchListener(backButton_OnTouchListener);
	}
	
	private void setDatabase() {
		algoWay.init();
	}
	
	private void setAdapter() {
	}
	
	OnTouchListener backButton_OnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					backButton.setBackgroundResource(R.drawable.go_g);
					break;
				case MotionEvent.ACTION_UP:
					// loadingLayout.setVisibility(1);
					Intent intent = new Intent();
					// ��һ������Ϊ AndroidMinifest.xml�ļ������õ�package���ԣ��ڶ�������Ϊpackage +
					// class
					intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.showWay");
					intent.putExtra("startstation", startStationString);
					intent.putExtra("endstation", endStationString);
					startActivity(intent);
					showDialog(DIALOG2_KEY);
					System.gc();
					finish();
					// overridePendingTransition(R.anim.slide_top_to_bottom,
					// R.anim.my_alpha_action);
					
				default:
					backButton.setBackgroundResource(R.drawable.go);
					break;
			}
			
			return true;
		}
	};
	
	ImageGetter imageGetter = new ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			
			// ����id����Դ�ļ��л�ȡͼƬ����
			Drawable d = getResources().getDrawable(id);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}
	};
	
	private Boolean getStations() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			startStationString = extras.getString("startstation");
			endStationString = extras.getString("endstation");
		}
		return ((startStationString.length() != 0) && (endStationString.length() != 0));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Log.d("iWayGo", "onClick by search");
			Intent intent = new Intent();
			// ��һ������Ϊ AndroidMinifest.xml�ļ������õ�package���ԣ��ڶ�������Ϊpackage + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.showWay");
			intent.putExtra("startstation", startStationString);
			intent.putExtra("endstation", endStationString);
			startActivity(intent);
			showDialog(DIALOG2_KEY);
			System.gc();
			finish();
			// loadingLayout.setVisibility(4);
		}
		return false;
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
			case 3: {
				AlertDialog.Builder timeDialog = new AlertDialog.Builder(this);
				timeDialog.setTitle("The Current Time is...");
				timeDialog.setMessage("Now");
				return timeDialog.create();
			}
		}
		return null;
	}
}
