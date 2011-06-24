
package com.iwaygo.Subway;

import com.iwaygo.data.AlgoWay;
import com.iwaygo.util.DynamicArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class lineAndStation extends Activity {
	private static final String TAG = "iWayGo";
	
	// 声明窗体控件
	private Button search_button, input_button, line_button, setting_button;
	
	private ListView listlineListView;
	
	// 数据库
	AlgoWay algoWay = new AlgoWay(this);
	
	// 获取屏幕信息
	Display display;
	
	// 信息列表
	//private String startStationString, endStationString;
	
	private String[] stationsStrings, linesStrings;
	
	public List<String> group;
	
	public List<List<String>> child;
	
	private int line = 0;
	
	// private String[] stationsStrings,linesStrings;
	private static final int DIALOG1_KEY = 0;
	
	private static final int DIALOG2_KEY = 1;
	
	DynamicArray bundleArray = new DynamicArray();
	
	// 首次加载时载入
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 载入窗体XML
		setContentView(R.layout.iwaygo_lineandstation);
		
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
	}
	
	private void findViews() {
		search_button = (Button) findViewById(R.id.search_button);
		input_button = (Button) findViewById(R.id.input_button);
		line_button = (Button) findViewById(R.id.line_button);
		//line_button.setPressed(true);
		//line_button.setTextColor(Color.WHITE);

		setting_button = (Button) findViewById(R.id.setting_button);
		
		listlineListView = (ListView) findViewById(R.id.listlineView);
		listlineListView.setBackgroundColor(Color.WHITE);
		listlineListView.setCacheColorHint(Color.WHITE);
	}
	
	private void setListener() {
		search_button.setOnClickListener(button_search_ClickListener);
		input_button.setOnClickListener(button_input_ClickListener);
		setting_button.setOnClickListener(button_setting_ClickListener);

		listlineListView.setOnItemClickListener(listlinesetOnItemClickListener);
	}
	
	private void setDatabase() {
		algoWay.init();
		algoWay.open();
		linesStrings = algoWay.getAllLines();
		algoWay.close();
	}
	
	private void setAdapter() {
		listlineListView.setAdapter(getAdapter());
	}
	
	public ListAdapter getAdapter() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		
		for (int i = 0; i < linesStrings.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			int resID = getResources()
					.getIdentifier("s" + (i + 1), "drawable", "com.iwaygo.Subway");
			map.put("ImageManager", resID);
			map.put("ItemManager", linesStrings[i]);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem, R.layout.list_items_line,
				new String[] {
						"ImageManager", "ItemManager"
				}, new int[] {
						R.id.ImageManager, R.id.ItemManager
				});
		return listItemAdapter;
	}
	
	OnItemClickListener listlinesetOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			line = position;
			algoWay.open();
			String[] tempOut = new String[] {
				linesStrings[position]
			};
			stationsStrings = algoWay.getAllStationsByLine(tempOut);
			algoWay.close();
			MyAdapter adapter = new MyAdapter(lineAndStation.this);
			listlineListView.setAdapter(adapter);
			listlineListView.setOnItemClickListener(listlinesetStationOnItemClickListener);
		}
	};
	
	OnItemClickListener listlinesetStationOnItemClickListener = new OnItemClickListener() {
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			 Intent intent = new Intent();
			
			 intent.setClassName("com.iwaygo.Subway",
			 "com.iwaygo.Subway.stationInfoline");
			 intent.putExtra("startstation",
			 stationsStrings[position-1].toString());
			 intent.putExtra("endstation", stationsStrings[position-1].toString());
			 startActivity(intent);
			 showDialog(DIALOG2_KEY);
			 System.gc();
		}
	};
	
	View.OnClickListener button_search_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by search");
			Intent intent = new Intent();
			// 第一个参数为 AndroidMinifest.xml文件中配置的package属性，第二个参数为package + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.iWayGoSubway");
			startActivity(intent);
			System.gc();
			lineAndStation.this.finish();
		}
	};
	
	View.OnClickListener button_input_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by search");
			Intent intent = new Intent();
			// 第一个参数为 AndroidMinifest.xml文件中配置的package属性，第二个参数为package + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.viewMap");
			startActivity(intent);
			System.gc();
			lineAndStation.this.finish();
		}
	};
	View.OnClickListener button_setting_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by setting");
			Intent intent = new Intent();
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.setting");
			System.gc();
			startActivity(intent);
			lineAndStation.this.finish();

		}
	};
	ImageGetter imageGetter = new ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			
			// 根据id从资源文件中获取图片对象
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
	
	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return stationsStrings.length+1;
		}
		
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				
				holder = new ViewHolder();
				
				convertView = mInflater.inflate(R.layout.list_items_station, null);
				holder.img = (ImageView) convertView.findViewById(R.id.ImageManager);
				holder.title = (TextView) convertView.findViewById(R.id.ItemManager);
				holder.info = (TextView) convertView.findViewById(R.id.ImageTEXT);
				holder.time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
				
			} else {
				
				holder = (ViewHolder) convertView.getTag();
			}
			if (position>0) {
				algoWay.open();
				int[] tmp = algoWay.getLineByStation(stationsStrings[position-1]);
				StringBuffer sfBuffer = new StringBuffer();
				if (tmp.length > 0) {
					for (int j = 0; j < tmp.length; j++) {
						if (tmp[j] > 1000) {
							tmp[j] -= 1000;
						}
						if ((tmp[j] != line + 1) && (tmp[j] != 4) && (tmp[j] != 11) && (tmp[j] != 13)
								&& (tmp[j] != 17)) {
							int resID = getResources().getIdentifier("x" + (tmp[j]), "drawable",
									"com.iwaygo.Subway");
							sfBuffer.append(" <img src='" + resID + "'>");
						}
						
					}
					Log.d(TAG, "TTTTT:::" + tmp + "::::" + line);
				}
				algoWay.close();
				
				holder.title.setText(stationsStrings[position-1]);
				holder.info.setText(Html.fromHtml(sfBuffer.toString(), imageGetter, null));
				holder.time.setText("12:00 13:00");
				holder.title.setTextSize(18);
				holder.info.setTextSize(18);
				holder.time.setTextSize(18);
				holder.img.setVisibility(0);

				
			}else {
				holder.title.setText("站名");
				holder.info.setText("可换乘");
				holder.time.setText("首末车时间");
				holder.title.setTextSize(15);
				holder.info.setTextSize(15);
				holder.time.setTextSize(15);
				
				holder.img.setVisibility(4);

				

			}
			
			return convertView;
		}
		
	}
	
	public final class ViewHolder {
		public ImageView img;
		
		public TextView title;
		
		public TextView info;
		public TextView time;
	}
}
