
package com.iwaygo.Subway;

import com.iwaygo.data.AlgoWay;
import com.iwaygo.util.DynamicArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class showWay extends Activity {
	private static final String TAG = "iWayGo";
	
	// 声明窗体控件
	private TextView textView_test, textView_test2;
	
	private ScrollView showWayScrollView;
	
	private ExpandableListView listView;
	
	public ExpandInfoAdapter adapter;
	
	private LinearLayout loadingLayout;
	
	private Button outButton, backButton;
	
	// private Button search_button, input_button, line_button, setting_button;
	
	// 数据库
	AlgoWay algoWay = new AlgoWay(this);
	
	// 获取屏幕信息
	Display display;
	
	// 信息列表
	private String startStationString, endStationString;
	
	private String text1 = null, text2 = null;
	
	public List<String> group;
	
	public List<List<String>> child;
	
	// private String[] stationsStrings,linesStrings;
	private static final int DIALOG1_KEY = 0;
	
	private static final int DIALOG2_KEY = 1;
	
	DynamicArray bundleArray = new DynamicArray();
	
	// private Handler handler = null;
	
	// 首次加载时载入
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 载入窗体XML
		setContentView(R.layout.iwaygo_show);
		showDialog(DIALOG2_KEY);
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
		
		registerForContextMenu(listView);
		loadingLayout.setVisibility(4);
		
		new doCalculateTask().execute(null, null, null);
		
	}
	
	private class doCalculateTask extends AsyncTask<URL, Integer, Long> {
		protected Long doInBackground(URL... urls) {
			if (getStations()) {
				calculateTask();
			} else {
				text1 = "请填写两个站点的名称，谢谢。";
			}
			return null;
		}
		
		protected void onProgressUpdate(Integer... progress) {
		}
		
		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			showDialog(DIALOG2_KEY);
		}
		
		protected void onPostExecute(Long result) {
			
			textView_test.setText(Html.fromHtml(text1, imageGetter, null));
			textView_test2.setText(Html.fromHtml(text2, imageGetter, null));
			// TODO
			adapter = new ExpandInfoAdapter(showWay.this);
			listView.setAdapter(adapter);
			listView.setGroupIndicator(null);
			dismissDialog(DIALOG2_KEY);
		}
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
	}
	
	private void findViews() {
		listView = (ExpandableListView) findViewById(R.id.expandable_list_view);
		showWayScrollView = (ScrollView) findViewById(R.id.showWayScrollView);
		showWayScrollView.setVisibility(1);
		textView_test = (TextView) findViewById(R.id.test_textView);
		textView_test2 = (TextView) findViewById(R.id.test_textView2);
		loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
		outButton = (Button) findViewById(R.id.outbutton);
		backButton = (Button) findViewById(R.id.backbutton);
	}
	
	private void setListener() {
		setListViewOnChildClickListener();
		outButton.setOnTouchListener(outButton_OnTouchListener);
		backButton.setOnTouchListener(backButton_OnTouchListener);
		
	}
	
	private void setDatabase() {
		algoWay.init();
		group = new ArrayList<String>();
		child = new ArrayList<List<String>>();
		
	}
	
	private void setAdapter() {
	}
	
	public void setListViewOnChildClickListener() {
		listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
					int childPosition, long id) {
				Log.d(TAG, "groupPosition:" + groupPosition);
				Log.d(TAG, "childPosition:" + childPosition);
				Log.d(TAG, "id:" + id);
				return true;
			}
		});
	}
	
	OnTouchListener outButton_OnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					outButton.setBackgroundResource(R.drawable.go_g);
					break;
				case MotionEvent.ACTION_UP:
					// loadingLayout.setVisibility(1);
					Intent intent = new Intent();
					
					intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.stationInfo");
					intent.putExtra("startstation", startStationString);
					intent.putExtra("endstation", endStationString);
					startActivity(intent);
					showDialog(DIALOG2_KEY);
					System.gc();
					// overridePendingTransition(R.anim.slide_top_to_bottom,
					// R.anim.my_alpha_action);
					
				default:
					outButton.setBackgroundResource(R.drawable.go);
					break;
			}
			
			return true;
		}
	};
	
	OnTouchListener backButton_OnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					backButton.setBackgroundResource(R.drawable.go_g);
					break;
				case MotionEvent.ACTION_UP:
					Intent intent = new Intent();
					intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.iWayGoSubway");
					startActivity(intent);
					System.gc();
					showWay.this.finish();
					overridePendingTransition(R.anim.slide_top_to_bottom, R.anim.my_alpha_action);
				default:
					backButton.setBackgroundResource(R.drawable.go);
					break;
			}
			
			return true;
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
	
	private void calculateTask() {
		algoWay.open();
		StringBuffer textStringBuffer = new StringBuffer();
		String[] pathStrings, lineStrings;
		int[] pathTimes, order;
		Bundle isSameLineBundle, wayBundle = null;
		isSameLineBundle = algoWay.isSameLine(startStationString, endStationString);
		if (isSameLineBundle.getBoolean("state")) {
			// 两个站点在同一条线路上
			textStringBuffer.append("<p>");
			textStringBuffer.append("您选择的两站同时位于:<br />");
			if (isSameLineBundle.getIntArray("lines").length > 0) {
				pathStrings = new String[isSameLineBundle.getIntArray("lines").length];
				lineStrings = new String[isSameLineBundle.getIntArray("lines").length];
				pathTimes = new int[isSameLineBundle.getIntArray("lines").length];
				order = new int[isSameLineBundle.getIntArray("lines").length];
				List<List<String>> itemList = new ArrayList<List<String>>();
				String[] sfAll = new String[isSameLineBundle.getIntArray("lines").length];
				
				for (int i = 0; i < isSameLineBundle.getIntArray("lines").length; i++) {
					StringBuffer sf = new StringBuffer();
					List<String> item = new ArrayList<String>();
					Log.d(TAG, "They are in the line:" + isSameLineBundle.getIntArray("lines")[i]);
					wayBundle = algoWay.stationsInTheSameLine(startStationString, endStationString,
							isSameLineBundle.getIntArray("lines")[i]);
					if (isSameLineBundle.getIntArray("lines")[i] > 1000) {
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							sf.append("<font color='#0059a8'>乘坐该线环线上行</font><br />");
							order[i] = 3;
						} else {
							// 降序排列
							sf.append("<font color='#0059a8'>乘坐该线环线下行</font><br />");
							order[i] = 2;
						}
					} else {
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							sf.append("<font color='#0059a8'>上行</font><br />");
							order[i] = 1;
						} else {
							// 降序排列
							sf.append("<font color='#0059a8'>下行</font><br />");
							order[i] = 0;
						}
					}
					for (int j = 0; j < wayBundle.getStringArray("stations").length; j++) {
						sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
						item.add(wayBundle.getStringArray("stations")[j].toString());
					}
					itemList.add(item);
					pathStrings[i] = sf.toString();
					pathTimes[i] = wayBundle.getInt("time");
					lineStrings[i] = isSameLineBundle.getStringArray("linenames")[i];
					textStringBuffer.append(lineStrings[i]);
					textStringBuffer.append("<br />");
					Log.d(TAG, "Use Time:" + pathTimes[i] + "\n stations:" + pathStrings[i]);
				}
				textStringBuffer.append("以下方案均不需要换乘<hr>");
				for (int i = 0; i < isSameLineBundle.getIntArray("lines").length; i++) {
					StringBuffer sfBuffer = new StringBuffer();
					// TODO Add IMG
					int st = isSameLineBundle.getIntArray("lines")[i];
					if (st > 1000) {
						st -= 1000 - 1;
					}
					int resID = getResources().getIdentifier("xs" + st, "drawable",
							"com.iwaygo.Subway");
					
					sfBuffer.append("起点：" + startStationString);
					int[] stline = algoWay.getLineByStation(startStationString);
					for (int j = 0; j < stline.length; j++) {
						int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
								"com.iwaygo.Subway");
						sfBuffer.append("  <img src='" + resIDs + "'>");
					}
					sfBuffer.append("<br />终点：" + endStationString);
					stline = algoWay.getLineByStation(endStationString);
					for (int j = 0; j < stline.length; j++) {
						int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
								"com.iwaygo.Subway");
						sfBuffer.append("  <img src='" + resIDs + "'>");
					}
					sfBuffer.append("<br />预计所用时间:<font color='#000000'>" + pathTimes[i] / 60
							+ "</font>min");
					sfBuffer.append("步骤1：请步行至" + "<img src='" + resID + "'> " + "站台,");
					switch (order[i]) {
						case 3:
							sfBuffer.append("乘坐<font color='#0059a8'>环线上行</font>列车");
							break;
						case 2:
							sfBuffer.append("乘坐<font color='#0059a8'>环线下行</font>列车");
							break;
						case 1:
							sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
							break;
						case 0:
							sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
							break;
						
						default:
							break;
					}
					sfBuffer.append("至<font color='#ff6600'>" + endStationString + "</font>站。");
					sfAll[i] = sfBuffer.toString();
					textStringBuffer.append(sfBuffer.toString());
					textStringBuffer.append("<br /><br />所经过的站点:<br />" + pathStrings[i]
							+ "<br /><br />");
				}
				textStringBuffer.append("</p>");
				int small = 0, taget = 0;
				small = pathTimes[0];
				for (int j = 0; j < pathTimes.length; j++) {
					if (pathTimes[j] < small) {
						small = pathTimes[j];
						taget = j;
					}
				}
				child.add(itemList.get(taget));
				group.add(sfAll[taget]);
				text1 = sfAll[taget].substring(0, sfAll[taget].indexOf("<br />预计", 0));
				text2 = sfAll[taget].substring(sfAll[taget].indexOf("预计", 0),
						sfAll[taget].indexOf("步骤", 0))
						+ "<br />票价：2元";
				
			}
			
		} else {
			Log.d(TAG, "They are not in the Same line! We can not know the way yet!");
			text1 = "您的输入有误！\n或者您所选择的两站超过2次换乘。请重新选择。谢谢。";
			if (isSameLineBundle.getString("transferTimes") == "once") {
				List<List<String>> itemList = new ArrayList<List<String>>();
				String[] sfAll = new String[isSameLineBundle.getStringArray("StartLineName").length];
				List<List<String>> itemList1 = new ArrayList<List<String>>();
				String[] sfAll1 = new String[isSameLineBundle.getStringArray("StartLineName").length];
				
				pathStrings = new String[isSameLineBundle.getStringArray("StartLineName").length];
				lineStrings = new String[isSameLineBundle.getStringArray("StartLineName").length];
				pathTimes = new int[isSameLineBundle.getStringArray("StartLineName").length];
				int[] order0 = new int[isSameLineBundle.getStringArray("StartLineName").length];
				int[] order1 = new int[isSameLineBundle.getStringArray("StartLineName").length];
				// StringBuffer sf = new StringBuffer();
				for (int i = 0; i < isSameLineBundle.getStringArray("StartLineName").length; i++) {
					StringBuffer sf = new StringBuffer();
					List<String> item = new ArrayList<String>();
					
					/************************************************************/
					wayBundle = algoWay.stationsInTheSameLine(startStationString,
							isSameLineBundle.getStringArray("TransferName")[i],
							isSameLineBundle.getIntArray("StartLine")[i]);
					
					if (wayBundle.getString("order") == "asc") {
						// 升序排列
						sf.append(isSameLineBundle.getStringArray("StartLineName")[i]);
						sf.append("上行<br />");
						order0[i] = 1;
					} else {
						// 降序排列
						sf.append(isSameLineBundle.getStringArray("StartLineName")[i]);
						sf.append("下行<br />");
						order0[i] = 1;
					}
					for (int j = 0; j < wayBundle.getStringArray("stations").length - 1; j++) {
						sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
						item.add(wayBundle.getStringArray("stations")[j].toString());
					}
					item.add(wayBundle.getStringArray("stations")[wayBundle
							.getStringArray("stations").length - 1].toString());
					itemList.add(item);
					item = new ArrayList<String>();
					pathTimes[i] = wayBundle.getInt("time");
					
					/************************************************************/
					wayBundle = algoWay.stationsInTheSameLine(
							isSameLineBundle.getStringArray("TransferName")[i], endStationString,
							isSameLineBundle.getIntArray("EndLine")[i]);
					if (wayBundle.getString("order") == "asc") {
						// 升序排列
						sf.append(isSameLineBundle.getStringArray("EndLineName")[i]);
						sf.append("上行<br />");
						order1[i] = 1;
					} else {
						// 降序排列
						sf.append(isSameLineBundle.getStringArray("EndLineName")[i]);
						sf.append("下行<br />");
						order1[i] = 0;
					}
					for (int j = 0; j < wayBundle.getStringArray("stations").length; j++) {
						Log.i(TAG, wayBundle.getStringArray("stations")[j].toString());
						sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
						item.add(wayBundle.getStringArray("stations")[j].toString());
					}
					// TODO
					// XXX
					itemList1.add(item);
					pathStrings[i] = sf.toString();
					pathTimes[i] += wayBundle.getInt("time");
					lineStrings[i] = isSameLineBundle.getStringArray("StartLineName")[i];
					Log.d(TAG, "Use Time:" + pathTimes[i] + "<br /> stations:" + pathStrings[i]);
				}
				for (int i = 0; i < isSameLineBundle.getStringArray("StartLineName").length; i++) {
					StringBuffer sfBuffer = new StringBuffer();
					
					// TODO Add IMG
					int st = isSameLineBundle.getIntArray("StartLine")[i];
					if (st > 1000) {
						st -= 1000 - 1;
					}
					int resID = getResources().getIdentifier("xs" + st, "drawable",
							"com.iwaygo.Subway");
					sfBuffer.append("起点：" + startStationString);
					int[] stline = algoWay.getLineByStation(startStationString);
					for (int j = 0; j < stline.length; j++) {
						int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
								"com.iwaygo.Subway");
						sfBuffer.append("  <img src='" + resIDs + "'>");
					}
					sfBuffer.append("<br />终点：" + endStationString);
					stline = algoWay.getLineByStation(endStationString);
					for (int j = 0; j < stline.length; j++) {
						int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
								"com.iwaygo.Subway");
						sfBuffer.append("  <img src='" + resIDs + "'>");
					}
					sfBuffer.append("<br />预计所用时间:<font color='#000000'>" + pathTimes[i] / 60
							+ "</font>min");
					sfBuffer.append("步骤1：请步行至" + "<img src='" + resID + "'> " + "站台,");
					switch (order0[i]) {
						case 1:
							sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
							break;
						case 0:
							sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
							break;
						
						default:
							break;
					}
					sfBuffer.append("至<font color='#ff6600'>"
							+ isSameLineBundle.getStringArray("TransferName")[i] + "</font>站。");
					sfAll[i] = sfBuffer.toString();
					sfBuffer = new StringBuffer();
					// TODO Add IMG
					st = isSameLineBundle.getIntArray("EndLine")[i];
					if (st > 1000) {
						st -= 1000 - 1;
					}
					resID = getResources()
							.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
					sfBuffer.append("步骤2：请换乘至" + "<img src='" + resID + "'> " + ",");
					switch (order1[i]) {
						case 1:
							sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
							break;
						case 0:
							sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
							break;
						
						default:
							break;
					}
					sfBuffer.append("至<font color='#ff6600'>" + endStationString + "</font>站。");
					
					// TODO
					// XXX
					sfAll1[i] = sfBuffer.toString();
					textStringBuffer.append(sfBuffer.toString());
					textStringBuffer.append("<br /><br />所经过的站点:<br />" + pathStrings[i]
							+ "<br /><br />");
				}
				int small = 0, taget = 0;
				small = pathTimes[0];
				for (int j = 0; j < isSameLineBundle.getStringArray("StartLineName").length; j++) {
					if (pathTimes[j] < small) {
						small = pathTimes[j];
						taget = j;
					}
				}
				child.add(itemList.get(taget));
				group.add(sfAll[taget]);
				child.add(itemList1.get(taget));
				group.add(sfAll1[taget]);
				text1 = sfAll[taget].substring(0, sfAll[taget].indexOf("<br />预计", 0));
				text2 = sfAll[taget].substring(sfAll[taget].indexOf("预计", 0),
						sfAll[taget].indexOf("步骤", 0))
						+ "<br />票价：2元";
				
			} else if (isSameLineBundle.getString("transferTimes") == "twice") {
				bundleArray = new DynamicArray();
				
				List<List<String>> itemList = new ArrayList<List<String>>();
				String[] sfAll = new String[isSameLineBundle.getStringArray("StartLineName").length];
				List<List<String>> itemList1 = new ArrayList<List<String>>();
				String[] sfAll1 = new String[isSameLineBundle.getStringArray("StartLineName").length];
				List<List<String>> itemList2 = new ArrayList<List<String>>();
				String[] sfAll2 = new String[isSameLineBundle.getStringArray("StartLineName").length];
				
				/*******************************************************************************************/
				int count = isSameLineBundle.getStringArray("StartLineName").length;
				
				pathStrings = new String[count];
				lineStrings = new String[count];
				pathTimes = new int[count];
				int[] order0 = new int[count];
				int[] order1 = new int[count];
				int[] order2 = new int[count];
				// StringBuffer sf = new StringBuffer();
				for (int i = 0; i < isSameLineBundle.getStringArray("StartLineName").length; i++) {
					StringBuffer sf = new StringBuffer();
					List<String> item = new ArrayList<String>();
					/************************************************************/
					Boolean unique = true;
					for (int j = 0; j < bundleArray.getLength(); j++) {
						if ((bundleArray.getBundles()[j].getString("inStart").compareTo(
								startStationString) == 0)
								&& (bundleArray.getBundles()[j].getString("inEnd").compareTo(
										isSameLineBundle.getStringArray("TransferName")[i]) == 0)
								&& (bundleArray.getBundles()[j].getInt("inLine") == isSameLineBundle
										.getIntArray("StartLine")[i])) {
							wayBundle = bundleArray.getBundles()[j];
							unique = false;
						}
						
					}
					
					if (unique) {
						wayBundle = algoWay.stationsInTheSameLine(startStationString,
								isSameLineBundle.getStringArray("TransferName")[i],
								isSameLineBundle.getIntArray("StartLine")[i]);
						bundleArray.put(bundleArray.getLength(), wayBundle);
					}
					
					if (wayBundle.getString("order") == "asc") {
						// 升序排列
						order0[i] = 1;
					} else {
						// 降序排列
						order0[i] = 0;
					}
					for (int j = 0; j < wayBundle.getStringArray("stations").length - 1; j++) {
						sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
						item.add(wayBundle.getStringArray("stations")[j].toString());
					}
					item.add(wayBundle.getStringArray("stations")[wayBundle
							.getStringArray("stations").length - 1].toString());
					itemList.add(item);
					item = new ArrayList<String>();
					pathTimes[i] = wayBundle.getInt("time");
					/************************************************************/
					Log.d(TAG, "TransferName:" + isSameLineBundle.getStringArray("TransferName")[i]);
					Log.d(TAG,
							"TransferTwiceName:"
									+ isSameLineBundle.getStringArray("TransferTwiceName")[i]);
					Log.d(TAG, "LineTwice:" + isSameLineBundle.getIntArray("LineTwice")[i]);
					Boolean unique1 = true;
					for (int j = 0; j < bundleArray.getLength(); j++) {
						if ((bundleArray.getBundles()[j].getString("inStart").compareTo(
								isSameLineBundle.getStringArray("TransferName")[i]) == 0)
								&& (bundleArray.getBundles()[j].getString("inEnd").compareTo(
										isSameLineBundle.getStringArray("TransferTwiceName")[i]) == 0)
								&& (bundleArray.getBundles()[j].getInt("inLine") == isSameLineBundle
										.getIntArray("LineTwice")[i])) {
							wayBundle = bundleArray.getBundles()[j];
							unique1 = false;
						}
					}
					if (unique1) {
						wayBundle = algoWay.stationsInTheSameLine(
								isSameLineBundle.getStringArray("TransferName")[i],
								isSameLineBundle.getStringArray("TransferTwiceName")[i],
								isSameLineBundle.getIntArray("LineTwice")[i]);
						bundleArray.put(bundleArray.getLength(), wayBundle);
					}
					if (wayBundle.getString("order") == "asc") {
						// 升序排列
						sf.append(isSameLineBundle.getStringArray("LineTwiceName")[i]);
						sf.append("上行<br />");
						order1[i] = 1;
					} else {
						// 降序排列
						sf.append(isSameLineBundle.getStringArray("LineTwiceName")[i]);
						sf.append("下行<br />");
						order1[i] = 0;
					}
					for (int j = 0; j < wayBundle.getStringArray("stations").length - 1; j++) {
						sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
						item.add(wayBundle.getStringArray("stations")[j].toString());
					}
					item.add(wayBundle.getStringArray("stations")[wayBundle
							.getStringArray("stations").length - 1].toString());
					itemList1.add(item);
					item = new ArrayList<String>();
					pathTimes[i] += wayBundle.getInt("time");
					
					/************************************************************/
					Boolean unique2 = true;
					for (int j = 0; j < bundleArray.getLength(); j++) {
						if ((bundleArray.getBundles()[j].getString("inStart").compareTo(
								isSameLineBundle.getStringArray("TransferTwiceName")[i]) == 0)
								&& (bundleArray.getBundles()[j].getString("inEnd") == endStationString)
								&& (bundleArray.getBundles()[j].getInt("inLine") == isSameLineBundle
										.getIntArray("EndLine")[i])) {
							wayBundle = bundleArray.getBundles()[j];
							unique2 = false;
						}
					}
					if (unique2) {
						wayBundle = algoWay.stationsInTheSameLine(
								isSameLineBundle.getStringArray("TransferTwiceName")[i],
								endStationString, isSameLineBundle.getIntArray("EndLine")[i]);
						bundleArray.put(bundleArray.getLength(), wayBundle);
					}
					if (wayBundle.getString("order") == "asc") {
						// 升序排列
						sf.append(isSameLineBundle.getStringArray("EndLineName")[i]);
						sf.append("上行<br />");
						order2[i] = 1;
					} else {
						// 降序排列
						sf.append(isSameLineBundle.getStringArray("EndLineName")[i]);
						sf.append("下行<br />");
						order2[i] = 0;
					}
					for (int j = 0; j < wayBundle.getStringArray("stations").length; j++) {
						sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
						item.add(wayBundle.getStringArray("stations")[j].toString());
					}
					// TODO
					// XXX
					itemList2.add(item);
					pathStrings[i] = sf.toString();
					pathTimes[i] += wayBundle.getInt("time");
					lineStrings[i] = isSameLineBundle.getStringArray("StartLineName")[i];
					
					Log.d(TAG, "Use Time:" + pathTimes[i] + "<br /> stations:" + pathStrings[i]);
					Log.i(TAG, "************unique=" + unique);
					Log.i(TAG, "************unique1=" + unique1);
					Log.i(TAG, "************unique2=" + unique2);
				}
				for (int i = 0; i < isSameLineBundle.getStringArray("StartLineName").length; i++) {
					StringBuffer sfBuffer = new StringBuffer();
					// TODO Add IMG
					int st = isSameLineBundle.getIntArray("StartLine")[i];
					if (st > 1000) {
						st -= 1000 - 1;
					}
					int resID = getResources().getIdentifier("xs" + st, "drawable",
							"com.iwaygo.Subway");
					
					sfBuffer.append("起点：" + startStationString);
					int[] stline = algoWay.getLineByStation(startStationString);
					for (int j = 0; j < stline.length; j++) {
						int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
								"com.iwaygo.Subway");
						sfBuffer.append("  <img src='" + resIDs + "'>");
					}
					sfBuffer.append("<br />终点：" + endStationString);
					stline = algoWay.getLineByStation(endStationString);
					for (int j = 0; j < stline.length; j++) {
						int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
								"com.iwaygo.Subway");
						sfBuffer.append("  <img src='" + resIDs + "'> ");
					}
					sfBuffer.append("<br />预计所用时间:<font color='#000000'>" + pathTimes[i] / 60
							+ "</font>min");
					sfBuffer.append("步骤1：请步行至" + " <img src='" + resID + "'> " + "站台,");
					switch (order0[i]) {
						case 1:
							sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
							break;
						case 0:
							sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
							break;
						
						default:
							break;
					}
					
					sfBuffer.append("至<font color='#ff6600'>"
							+ isSameLineBundle.getStringArray("TransferName")[i] + "</font>站。");
					/****************/
					sfAll[i] = sfBuffer.toString();
					sfBuffer = new StringBuffer();
					// TODO Add IMG
					st = isSameLineBundle.getIntArray("LineTwice")[i];
					if (st > 1000) {
						st -= 1000 - 1;
					}
					resID = getResources()
							.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
					sfBuffer.append("步骤2：请换乘至" + " <img src='" + resID + "'> " + ",");
					switch (order1[i]) {
						case 1:
							sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
							break;
						case 0:
							sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
							break;
						
						default:
							break;
					}
					sfBuffer.append("至<font color='#ff6600'>"
							+ isSameLineBundle.getStringArray("TransferTwiceName")[i] + "</font>站。");
					/****************/
					sfAll1[i] = sfBuffer.toString();
					sfBuffer = new StringBuffer();
					// TODO Add IMG
					st = isSameLineBundle.getIntArray("EndLine")[i];
					if (st > 1000) {
						st -= 1000 - 1;
					}
					resID = getResources()
							.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
					sfBuffer.append("步骤3：请换乘至" + " <img src='" + resID + "'> " + ",");
					switch (order2[i]) {
						case 1:
							sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
							break;
						case 0:
							sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
							break;
						
						default:
							break;
					}
					sfBuffer.append("至<font color='#ff6600'>" + endStationString + "</font>站。");
					/****************/
					
					// TODO
					// XXX
					sfAll2[i] = sfBuffer.toString();
					
					textStringBuffer.append(sfBuffer.toString());
					textStringBuffer.append("<br /><br />所经过的站点:<br />" + pathStrings[i]
							+ "<br /><br />");
				}
				int small = 0, taget = 0;
				small = pathTimes[0];
				for (int j = 0; j < isSameLineBundle.getStringArray("StartLineName").length; j++) {
					if (pathTimes[j] < small) {
						small = pathTimes[j];
						taget = j;
					}
				}
				child.add(itemList.get(taget));
				group.add(sfAll[taget]);
				child.add(itemList1.get(taget));
				group.add(sfAll1[taget]);
				child.add(itemList2.get(taget));
				group.add(sfAll2[taget]);
				text1 = sfAll[taget].substring(0, sfAll[taget].indexOf("<br />预计", 0));
				text2 = sfAll[taget].substring(sfAll[taget].indexOf("预计", 0),
						sfAll[taget].indexOf("步骤", 0))
						+ "<br />票价：2元";
				
			} else {
				// 三次换乘
				String tranfser = new String();
				int lineto = 0;
				Boolean startBoolean = true;
				int[] getline = algoWay.getLineByStation(startStationString);
				if (getline.length > 0) {
					if (getline[0] == 2) {
						tranfser = "四惠";
						lineto = 1;
					}
					if (getline[0] == 12) {
						tranfser = "西二旗";
						lineto = 10;
					}
					if (getline[0] == 13) {
						tranfser = "西二旗";
						lineto = 10;
					}
					if (getline[0] == 14) {
						tranfser = "北土城";
						lineto = 9;
					}
					if (getline[0] == 16) {
						tranfser = "望京西";
						lineto = 10;
					}
					if (getline[0] == 17) {
						tranfser = "望京西";
						lineto = 10;
					}
					if (getline[0] == 8) {
						tranfser = "宋家庄";
						lineto = 7;
					}
				}
				if (lineto == 0) {
					startBoolean = false;
					getline = algoWay.getLineByStation(endStationString);
					if (getline.length > 0) {
						if (getline[0] == 2) {
							tranfser = "四惠";
							lineto = 1;
						}
						if (getline[0] == 12) {
							tranfser = "西二旗";
							lineto = 10;
						}
						if (getline[0] == 13) {
							tranfser = "西二旗";
							lineto = 10;
						}
						if (getline[0] == 14) {
							tranfser = "北土城";
							lineto = 9;
						}
						if (getline[0] == 16) {
							tranfser = "望京西";
							lineto = 10;
						}
						if (getline[0] == 17) {
							tranfser = "望京西";
							lineto = 10;
						}
						if (getline[0] == 8) {
							tranfser = "宋家庄";
							lineto = 7;
						}
					}
				}
				Log.d(TAG, "#########3333333######:::" + tranfser + ":::" + lineto);
				if (lineto != 0) {
					String tempStart = startStationString, tempEnd = endStationString;
					
					List<List<String>> itemList3 = new ArrayList<List<String>>();
					
					String sfAll3 = "";
					int time3 = 0;
					int order3 = 0;
					
					if (startBoolean) {
						List<String> item = new ArrayList<String>();
						wayBundle = algoWay.stationsInTheSameLine(startStationString, tranfser,
								getline[0]);
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							order3 = 1;
						} else {
							// 降序排列
							order3 = 0;
						}
						
						for (int j = 0; j < wayBundle.getStringArray("stations").length; j++) {
							item.add(wayBundle.getStringArray("stations")[j].toString());
						}
						// item.add(wayBundle.getStringArray("stations")[wayBundle
						// .getStringArray("stations").length].toString());
						itemList3.add(item);
						time3 = wayBundle.getInt("time");
						
						startStationString = tranfser;
					} else {
						List<String> item = new ArrayList<String>();
						wayBundle = algoWay.stationsInTheSameLine(tranfser, endStationString,
								getline[0]);
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							order3 = 1;
						} else {
							// 降序排列
							order3 = 0;
						}
						for (int j = 0; j < wayBundle.getStringArray("stations").length; j++) {
							item.add(wayBundle.getStringArray("stations")[j].toString());
						}
						// item.add(wayBundle.getStringArray("stations")[wayBundle
						// .getStringArray("stations").length].toString());
						itemList3.add(item);
						time3 = wayBundle.getInt("time");
						endStationString = tranfser;
					}
					isSameLineBundle = algoWay.isSameLine(startStationString, endStationString);
					
					bundleArray = new DynamicArray();
					
					List<List<String>> itemList = new ArrayList<List<String>>();
					String[] sfAll = new String[isSameLineBundle.getStringArray("StartLineName").length];
					List<List<String>> itemList1 = new ArrayList<List<String>>();
					String[] sfAll1 = new String[isSameLineBundle.getStringArray("StartLineName").length];
					List<List<String>> itemList2 = new ArrayList<List<String>>();
					String[] sfAll2 = new String[isSameLineBundle.getStringArray("StartLineName").length];
					/*******************************************************************************************/
					int count = isSameLineBundle.getStringArray("StartLineName").length;
					
					pathStrings = new String[count];
					lineStrings = new String[count];
					pathTimes = new int[count];
					int[] order0 = new int[count];
					int[] order1 = new int[count];
					int[] order2 = new int[count];
					// StringBuffer sf = new StringBuffer();
					for (int i = 0; i < isSameLineBundle.getStringArray("StartLineName").length; i++) {
						StringBuffer sf = new StringBuffer();
						List<String> item = new ArrayList<String>();
						/************************************************************/
						Boolean unique = true;
						for (int j = 0; j < bundleArray.getLength(); j++) {
							if ((bundleArray.getBundles()[j].getString("inStart").compareTo(
									startStationString) == 0)
									&& (bundleArray.getBundles()[j].getString("inEnd").compareTo(
											isSameLineBundle.getStringArray("TransferName")[i]) == 0)
									&& (bundleArray.getBundles()[j].getInt("inLine") == isSameLineBundle
											.getIntArray("StartLine")[i])) {
								wayBundle = bundleArray.getBundles()[j];
								unique = false;
							}
							
						}
						
						if (unique) {
							wayBundle = algoWay.stationsInTheSameLine(startStationString,
									isSameLineBundle.getStringArray("TransferName")[i],
									isSameLineBundle.getIntArray("StartLine")[i]);
							bundleArray.put(bundleArray.getLength(), wayBundle);
						}
						
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							order0[i] = 1;
						} else {
							// 降序排列
							order0[i] = 0;
						}
						for (int j = 0; j < wayBundle.getStringArray("stations").length - 1; j++) {
							sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
							item.add(wayBundle.getStringArray("stations")[j].toString());
						}
						item.add(wayBundle.getStringArray("stations")[wayBundle
								.getStringArray("stations").length - 1].toString());
						itemList.add(item);
						item = new ArrayList<String>();
						pathTimes[i] = wayBundle.getInt("time");
						/************************************************************/
						Boolean unique1 = true;
						for (int j = 0; j < bundleArray.getLength(); j++) {
							if ((bundleArray.getBundles()[j].getString("inStart").compareTo(
									isSameLineBundle.getStringArray("TransferName")[i]) == 0)
									&& (bundleArray.getBundles()[j]
											.getString("inEnd")
											.compareTo(
													isSameLineBundle
															.getStringArray("TransferTwiceName")[i]) == 0)
									&& (bundleArray.getBundles()[j].getInt("inLine") == isSameLineBundle
											.getIntArray("LineTwice")[i])) {
								wayBundle = bundleArray.getBundles()[j];
								unique1 = false;
							}
						}
						if (unique1) {
							wayBundle = algoWay.stationsInTheSameLine(
									isSameLineBundle.getStringArray("TransferName")[i],
									isSameLineBundle.getStringArray("TransferTwiceName")[i],
									isSameLineBundle.getIntArray("LineTwice")[i]);
							bundleArray.put(bundleArray.getLength(), wayBundle);
						}
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							sf.append(isSameLineBundle.getStringArray("LineTwiceName")[i]);
							sf.append("上行<br />");
							order1[i] = 1;
						} else {
							// 降序排列
							sf.append(isSameLineBundle.getStringArray("LineTwiceName")[i]);
							sf.append("下行<br />");
							order1[i] = 0;
						}
						for (int j = 0; j < wayBundle.getStringArray("stations").length - 1; j++) {
							sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
							item.add(wayBundle.getStringArray("stations")[j].toString());
						}
						item.add(wayBundle.getStringArray("stations")[wayBundle
								.getStringArray("stations").length - 1].toString());
						itemList1.add(item);
						item = new ArrayList<String>();
						pathTimes[i] += wayBundle.getInt("time");
						
						/************************************************************/
						Boolean unique2 = true;
						for (int j = 0; j < bundleArray.getLength(); j++) {
							if ((bundleArray.getBundles()[j].getString("inStart").compareTo(
									isSameLineBundle.getStringArray("TransferTwiceName")[i]) == 0)
									&& (bundleArray.getBundles()[j].getString("inEnd") == endStationString)
									&& (bundleArray.getBundles()[j].getInt("inLine") == isSameLineBundle
											.getIntArray("EndLine")[i])) {
								wayBundle = bundleArray.getBundles()[j];
								unique2 = false;
							}
						}
						if (unique2) {
							wayBundle = algoWay.stationsInTheSameLine(
									isSameLineBundle.getStringArray("TransferTwiceName")[i],
									endStationString, isSameLineBundle.getIntArray("EndLine")[i]);
							bundleArray.put(bundleArray.getLength(), wayBundle);
						}
						if (wayBundle.getString("order") == "asc") {
							// 升序排列
							sf.append(isSameLineBundle.getStringArray("EndLineName")[i]);
							sf.append("上行<br />");
							order2[i] = 1;
						} else {
							// 降序排列
							sf.append(isSameLineBundle.getStringArray("EndLineName")[i]);
							sf.append("下行<br />");
							order2[i] = 0;
						}
						for (int j = 0; j < wayBundle.getStringArray("stations").length; j++) {
							sf.append(wayBundle.getStringArray("stations")[j]).append("<br />");
							item.add(wayBundle.getStringArray("stations")[j].toString());
						}
						// TODO
						// XXX
						itemList2.add(item);
						pathStrings[i] = sf.toString();
						pathTimes[i] += wayBundle.getInt("time");
						lineStrings[i] = isSameLineBundle.getStringArray("StartLineName")[i];
						
						Log.d(TAG, "Use Time:" + pathTimes[i] + "<br /> stations:" + pathStrings[i]);
						Log.i(TAG, "************unique=" + unique);
						Log.i(TAG, "************unique1=" + unique1);
						Log.i(TAG, "************unique2=" + unique2);
					}
					for (int i = 0; i < isSameLineBundle.getStringArray("StartLineName").length; i++) {
						StringBuffer sfBuffer = new StringBuffer();
						// TODO Add IMG
						int st = isSameLineBundle.getIntArray("StartLine")[i];
						if (st > 1000) {
							st -= 1000 - 1;
						}
						int resID = getResources().getIdentifier("xs" + st, "drawable",
								"com.iwaygo.Subway");
						
						sfBuffer.append("起点：" + tempStart);
						int[] stline = algoWay.getLineByStation(tempStart);
						for (int j = 0; j < stline.length; j++) {
							int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
									"com.iwaygo.Subway");
							sfBuffer.append("  <img src='" + resIDs + "'>");
						}
						sfBuffer.append("<br />终点：" + tempEnd);
						stline = algoWay.getLineByStation(tempEnd);
						for (int j = 0; j < stline.length; j++) {
							int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
									"com.iwaygo.Subway");
							sfBuffer.append("  <img src='" + resIDs + "'> ");
						}
						sfBuffer.append("<br />预计所用时间:<font color='#000000'>" + (pathTimes[i]+time3) / 60
								+ "</font>min");
						if(startBoolean){

							sfBuffer = new StringBuffer();
							// TODO Add IMG
							st = getline[0];
							if (st > 1000) {
								st -= 1000 - 1;
							}
							resID = getResources()
									.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
							sfBuffer.append("步骤1：请步行至" + " <img src='" + resID + "'> 站台" + ",");
							switch (order3) {
								case 1:
									sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
									break;
								case 0:
									sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
									break;
								
								default:
									break;
							}
							sfBuffer.append("至<font color='#ff6600'>" + tranfser + "</font>站。");
							/****************/
							
							sfAll3 = sfBuffer.toString();
							
							sfBuffer = new StringBuffer();
							// TODO Add IMG
							st = isSameLineBundle.getIntArray("StartLine")[i];
							if (st > 1000) {
								st -= 1000 - 1;
							}
							resID = getResources().getIdentifier("xs" + st, "drawable",
									"com.iwaygo.Subway");
							
							sfBuffer.append("起点：" + tempStart);
							stline = algoWay.getLineByStation(tempStart);
							for (int j = 0; j < stline.length; j++) {
								int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
										"com.iwaygo.Subway");
								sfBuffer.append("  <img src='" + resIDs + "'>");
							}
							sfBuffer.append("<br />终点：" + tempEnd);
							stline = algoWay.getLineByStation(tempEnd);
							for (int j = 0; j < stline.length; j++) {
								int resIDs = getResources().getIdentifier("x" + stline[j], "drawable",
										"com.iwaygo.Subway");
								sfBuffer.append("  <img src='" + resIDs + "'> ");
							}
							sfBuffer.append("<br />预计所用时间:<font color='#000000'>" + (pathTimes[i]+time3) / 60
									+ "</font>min");
							sfBuffer.append("步骤2：请换乘至" + " <img src='" + resID + "'> " + "站台,");
							switch (order0[i]) {
								case 1:
									sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
									break;
								case 0:
									sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
									break;
								
								default:
									break;
							}
							
							sfBuffer.append("至<font color='#ff6600'>"
									+ isSameLineBundle.getStringArray("TransferName")[i] + "</font>站。");
							/****************/
							sfAll[i] = sfBuffer.toString();
							sfBuffer = new StringBuffer();
							// TODO Add IMG
							st = isSameLineBundle.getIntArray("LineTwice")[i];
							if (st > 1000) {
								st -= 1000 - 1;
							}
							resID = getResources()
									.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
							sfBuffer.append("步骤3：请换乘至" + " <img src='" + resID + "'> " + ",");
							switch (order1[i]) {
								case 1:
									sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
									break;
								case 0:
									sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
									break;
								
								default:
									break;
							}
							sfBuffer.append("至<font color='#ff6600'>"
									+ isSameLineBundle.getStringArray("TransferTwiceName")[i] + "</font>站。");
							/****************/
							sfAll1[i] = sfBuffer.toString();
							sfBuffer = new StringBuffer();
							// TODO Add IMG
							st = isSameLineBundle.getIntArray("EndLine")[i];
							if (st > 1000) {
								st -= 1000 - 1;
							}
							resID = getResources()
									.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
							sfBuffer.append("步骤4：请换乘至" + " <img src='" + resID + "'> " + ",");
							switch (order3) {
								case 1:
									sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
									break;
								case 0:
									sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
									break;
								
								default:
									break;
							}
							sfBuffer.append("至<font color='#ff6600'>" + endStationString + "</font>站。");
							/****************/
							
							// TODO
							// XXX
							sfAll2[i] = sfBuffer.toString();
							
						}else {
			
							sfBuffer.append("步骤1：请步行至" + " <img src='" + resID + "'> " + "站台,");
						switch (order0[i]) {
							case 1:
								sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
								break;
							case 0:
								sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
								break;
							
							default:
								break;
						}
						
						sfBuffer.append("至<font color='#ff6600'>"
								+ isSameLineBundle.getStringArray("TransferName")[i] + "</font>站。");
						/****************/
						sfAll[i] = sfBuffer.toString();
						sfBuffer = new StringBuffer();
						// TODO Add IMG
						st = isSameLineBundle.getIntArray("LineTwice")[i];
						if (st > 1000) {
							st -= 1000 - 1;
						}
						resID = getResources()
								.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
						sfBuffer.append("步骤2：请换乘至" + " <img src='" + resID + "'> " + ",");
						switch (order1[i]) {
							case 1:
								sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
								break;
							case 0:
								sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
								break;
							
							default:
								break;
						}
						sfBuffer.append("至<font color='#ff6600'>"
								+ isSameLineBundle.getStringArray("TransferTwiceName")[i] + "</font>站。");
						/****************/
						sfAll1[i] = sfBuffer.toString();
						sfBuffer = new StringBuffer();
						// TODO Add IMG
						st = isSameLineBundle.getIntArray("EndLine")[i];
						if (st > 1000) {
							st -= 1000 - 1;
						}
						resID = getResources()
								.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
						sfBuffer.append("步骤3：请换乘至" + " <img src='" + resID + "'> " + ",");
						switch (order2[i]) {
							case 1:
								sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
								break;
							case 0:
								sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
								break;
							
							default:
								break;
						}
						sfBuffer.append("至<font color='#ff6600'>" + endStationString + "</font>站。");
						/****************/
						
						// TODO
						// XXX
						sfAll2[i] = sfBuffer.toString();
						sfBuffer = new StringBuffer();
						// TODO Add IMG
						st = getline[0];
						if (st > 1000) {
							st -= 1000 - 1;
						}
						resID = getResources()
								.getIdentifier("xs" + st, "drawable", "com.iwaygo.Subway");
						sfBuffer.append("步骤4：请换乘至" + " <img src='" + resID + "'> " + ",");
						switch (order2[i]) {
							case 1:
								sfBuffer.append("乘坐<font color='#0059a8'>上行</font>列车");
								break;
							case 0:
								sfBuffer.append("乘坐<font color='#0059a8'>下行</font>列车");
								break;
							
							default:
								break;
						}
						sfBuffer.append("至<font color='#ff6600'>" + tempEnd + "</font>站。");
						/****************/
						
						sfAll3 = sfBuffer.toString();
					}
						textStringBuffer.append(sfBuffer.toString());
						textStringBuffer.append("<br /><br />所经过的站点:<br />" + pathStrings[i]
								+ "<br /><br />");
					}
					int small = 0, taget = 0;
					small = pathTimes[0];
					for (int j = 0; j < isSameLineBundle.getStringArray("StartLineName").length; j++) {
						if (pathTimes[j] < small) {
							small = pathTimes[j];
							taget = j;
						}
					}
					if (startBoolean) {
						child.add(itemList3.get(taget));
						group.add(sfAll3);
						child.add(itemList.get(taget));
						group.add(sfAll[taget]);
						child.add(itemList1.get(taget));
						group.add(sfAll1[taget]);
						child.add(itemList2.get(taget));
						group.add(sfAll2[taget]);
					} else {
						child.add(itemList.get(taget));
						group.add(sfAll[taget]);
						child.add(itemList1.get(taget));
						group.add(sfAll1[taget]);
						child.add(itemList2.get(taget));
						group.add(sfAll2[taget]);
						child.add(itemList3.get(taget));
						group.add(sfAll3);
					}
					
					text1 = sfAll[taget].substring(0, sfAll[taget].indexOf("<br />预计", 0));
					text2 = sfAll[taget].substring(sfAll[taget].indexOf("预计", 0),
							sfAll[taget].indexOf("步骤", 0))
							+ "<br />票价：2元";
					
				}
			}
			
		}
		algoWay.close();
	}
	
	/**
	 * A simple adapter which maintains an ArrayList of photo resource Ids. Each
	 * photo is displayed as an image. This adapter supports clearing the list
	 * of photos and adding a new photo.
	 */
	public class ExpandInfoAdapter extends BaseExpandableListAdapter {
		// LayoutInflater mInflater;
		
		// Bitmap mIcon1;
		
		Activity activity;
		
		public ExpandInfoAdapter(Activity a) {
			activity = a;
		}
		
		public Object getChild(int groupPosition, int childPosition) {
			return child.get(groupPosition).get(childPosition);// children[groupPosition][childPosition];
		}
		
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}
		
		public int getChildrenCount(int groupPosition) {
			return child.get(groupPosition).size();// children[groupPosition].length;
		}
		
		public TextView getGenericView() {
			// Layout parameters for the ExpandableListView
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			
			TextView textView = new TextView(showWay.this);
			textView.setLayoutParams(lp);
			// Center the text vertically
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView.setPadding(5, 5, 5, 5);
			textView.setTextColor(R.color.colorstatelist);
			textView.setTextSize(22);
			return textView;
		}
		
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(showWay.this);
			textView.setText(Html.fromHtml(getChild(groupPosition, childPosition).toString(),
					imageGetter, null));
			textView.setPadding(36, 0, 0, 0);
			textView.setHeight(30);
			textView.setTextSize(15);
			
			textView.setTextColor(R.color.colorstatelist);
			return textView;
		}
		
		public Object getGroup(int groupPosition) {
			return group.get(groupPosition);// groups[groupPosition];
		}
		
		public int getGroupCount() {
			return group.size();// groups.length;
		}
		
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}
		
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
				ViewGroup parent) {
			LinearLayout mainLayout = new LinearLayout(showWay.this);
			StringBuffer sf = new StringBuffer();
			sf.append(getGroup(groupPosition).toString());
			
			int resIDs = getResources().getIdentifier(
					"n" + sf.substring(sf.indexOf("骤", 0) + 1, sf.indexOf("请", 0) - 1), "drawable",
					"com.iwaygo.Subway");
			
			TextView textView1 = new TextView(showWay.this);
			textView1.setText(Html
					.fromHtml("步骤" + " <img src='" + resIDs + "'>", imageGetter, null));
			// Center the text vertically
			textView1.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			// Set the text starting position
			textView1.setPadding(5, 5, 5, 5);
			textView1.setTextColor(R.color.colorstatelist);
			textView1.setBackgroundColor(Color.argb(100, 247, 247, 247));
			textView1.setTextSize(22);
			
			StringBuffer titleBuffer = new StringBuffer();
			titleBuffer.append(sf.substring(sf.indexOf("请", 0)).toString());
			TextView textView = getGenericView();
			textView.setText(Html.fromHtml(titleBuffer.toString(), imageGetter, null));
			mainLayout.setOrientation(1);
			mainLayout.addView(textView1);
			mainLayout.addView(textView);
			return mainLayout;
		}
		
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		public boolean hasStableIds() {
			return true;
		}
		
	}
	
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
			Intent intent = new Intent();
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.iWayGoSubway");
			startActivity(intent);
			System.gc();
			showWay.this.finish();
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
				dialog.setMessage("正在载入,请稍后...");
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
