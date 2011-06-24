
package com.iwaygo.Subway;

import com.iwaygo.data.AlgoWay;
import com.iwaygo.util.DynamicArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class setting extends Activity {
	private static final String TAG = "iWayGo";
	
	// ��������ؼ�
	private Button search_button, input_button, line_button, setting_button;
	
	private ListView listlineListView;
	
	LayoutInflater inflater;// = LayoutInflater.from(this);
	
	// ���ݿ�
	AlgoWay algoWay = new AlgoWay(this);
	
	// ��ȡ��Ļ��Ϣ
	Display display;
	
	// ��Ϣ�б�
	//private String startStationString, endStationString;
	
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
		setContentView(R.layout.iwaygo_lineandstation);
		
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
		search_button.setWidth(display.getWidth() / 4);
		input_button.setWidth(display.getWidth() / 4);
		line_button.setWidth(display.getWidth() / 4);
		setting_button.setWidth(display.getWidth() / 4);
	}
	
	private void findViews() {
		search_button = (Button) findViewById(R.id.search_button);
		input_button = (Button) findViewById(R.id.input_button);
		line_button = (Button) findViewById(R.id.line_button);
		setting_button = (Button) findViewById(R.id.setting_button);
		//setting_button.setPressed(true);
		//setting_button.setTextColor(Color.WHITE);

		listlineListView = (ListView) findViewById(R.id.listlineView);
		listlineListView.setBackgroundColor(Color.WHITE);
		listlineListView.setCacheColorHint(Color.WHITE);
		inflater = LayoutInflater.from(this);
	}
	
	private void setListener() {
		search_button.setOnClickListener(button_search_ClickListener);
		input_button.setOnClickListener(button_input_ClickListener);
		line_button.setOnClickListener(button_line_ClickListener);
		listlineListView.setOnItemClickListener(listlinesetOnItemClickListener);
	}
	
	private void setDatabase() {
		
	}
	
	private void setAdapter() {
		listlineListView.setAdapter(getAdapter());
	}
	
	public ListAdapter getAdapter() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("title_setting", "����Ĭ�Ͻ���");
		map.put("main_setting", "���ó�������ʱ��Ĭ�Ͻ���");
		listItem.add(map);
		map = new HashMap<String, Object>();
		map.put("title_setting", "���ڳ���ͨ");
		map.put("main_setting", "�����Ϣ");
		listItem.add(map);
		
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.list_items_setting, new String[] {
						"title_setting", "main_setting"
				}, new int[] {
						R.id.title_setting, R.id.main_setting
				});
		return listItemAdapter;
	}
	
	OnItemClickListener listlinesetOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			View wind;
			switch (position) {
				case 0:
					new AlertDialog.Builder(setting.this).setTitle("����Ĭ�Ͻ���")
							.setSingleChoiceItems(R.array.item_dialog, 0, null)
							.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss(); // �ر�alertDialog
								}
							}).show();
					break;
				case 1:
					wind = inflater.inflate(R.layout.about, null);
					new AlertDialog.Builder(setting.this).setTitle("���ڳ���ͨ").setView(wind)
							.setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss(); // �ر�alertDialog
								}
							}).show();
					break;
				
				default:
					break;
			}
		}
	};
	
	View.OnClickListener button_search_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by search");
			Intent intent = new Intent();
			// ��һ������Ϊ AndroidMinifest.xml�ļ������õ�package���ԣ��ڶ�������Ϊpackage + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.iWayGoSubway");
			startActivity(intent);
			System.gc();
			setting.this.finish();
		}
	};
	
	View.OnClickListener button_input_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by search");
			Intent intent = new Intent();
			// ��һ������Ϊ AndroidMinifest.xml�ļ������õ�package���ԣ��ڶ�������Ϊpackage + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.viewMap");
			startActivity(intent);
			System.gc();
			setting.this.finish();
		}
	};
	
	View.OnClickListener button_line_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by line");
			Intent intent = new Intent();
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.lineAndStation");
			System.gc();
			startActivity(intent);
			setting.this.finish();
			
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
