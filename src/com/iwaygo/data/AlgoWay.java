
package com.iwaygo.data;

import com.iwaygo.util.DynamicArray;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.util.Vector;

public class AlgoWay {
	private static final String TAG = "iWayGo";
	
	public DBManager dbManager;
	
	private Context context;
	
	private SQLiteDatabase database;
	
	private static Vector<String> nodes;
	
	private Boolean isOpenBoolean = false;
	
	private DynamicArray lineNameBylineArray = new DynamicArray();
	
	public AlgoWay(Context context) {
		this.context = context;
	}
	
	public void init() {
		dbManager = new DBManager(this.context);
		dbManager.openDatabase();
		dbManager.closeDatabase();
	}
	
	public void open() {
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME,
				null);
		isOpenBoolean = true;
	}
	
	public void close() {
		database.close();
		isOpenBoolean = false;
	}
	
	public Boolean isOpen() {
		return isOpenBoolean;
	}
	
	public static String[] split(String original, String separator) {
		nodes.removeAllElements();
		// Parse nodes into vector
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		// Get the last node
		nodes.addElement(original);
		// Create splitted string array
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			nodes.copyInto(result);
		}
		return result;
	}
	
	public StringBuffer test(int st) {
		String putin = new String();
		putin = String.valueOf(st);
		Cursor cur = database.rawQuery("SELECT * FROM iwaygo_station_time WHERE _id_line_curr = ?",
				new String[] {
					putin
				});
		if (cur != null) {
			StringBuffer sf = new StringBuffer();
			int i;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				for (i = 0; i < cur.getColumnCount(); i++) {
					sf.append(cur.getString(i)).append("\t");
				}
				sf.append("\n");
				cur.moveToNext();
			}
			if (!cur.isClosed()) {
				cur.close();
			}
			return sf;
		} else {
			return null;
		}
	}
	
	public StringBuffer query(String sql, int st) {
		String putin = new String();
		putin = String.valueOf(st);
		Cursor cur = database.rawQuery(sql, new String[] {
			putin
		});
		if (cur != null) {
			StringBuffer sf = new StringBuffer();
			int i;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				for (i = 0; i < cur.getColumnCount(); i++) {
					sf.append(cur.getString(i));
				}
				cur.moveToNext();
			}
			if (!cur.isClosed()) {
				cur.close();
			}
			return sf;
		} else {
			return null;
		}
	}
	
	public String[] getAllStations() {
		Cursor cur = database.rawQuery("SELECT * FROM iwaygo_station_info", null);
		if (cur.getCount() > 0) {
			String[] str = new String[cur.getCount()];
			int i = 0;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				str[i] = cur.getString(cur.getColumnIndex("line_name")) + "\t"
						+ cur.getString(cur.getColumnIndex("station_name"));
				Log.d(TAG, str[i]);
				i++;
				cur.moveToNext();
			}
			if (!cur.isClosed()) {
				cur.close();
			}
			return str;
		} else {
			return new String[] {};
		}
	}
	
	public String[] getAllLines() {
		Cursor cur = database.rawQuery(
				"SELECT * FROM iwaygo_station_info group by line_name order by _id", null);
		Log.d(TAG, "SELECT * FROM iwaygo_station_info group by line_name order by _id");
		if (cur.getCount() > 0) {
			String[] str = new String[cur.getCount()];
			int i = 0;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				str[i] = cur.getString(cur.getColumnIndex("line_name"));
				Log.d(TAG, str[i]);
				i++;
				cur.moveToNext();
			}
			if (!cur.isClosed()) {
				cur.close();
			}
			return str;
		} else {
			return new String[] {};
		}
	}
	
	public String[] getAllStationsByLine(String[] inputStrings) {
		Cursor cur = database.rawQuery(
				"SELECT * FROM iwaygo_station_info where line_name=? order by _id", inputStrings);
		Log.d(TAG, "SELECT * FROM iwaygo_station_info where line_name=? order by _id::::"
				+ inputStrings.toString());
		if (cur.getCount() > 0) {
			String[] str = new String[cur.getCount()];
			int i = 0;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				str[i] = cur.getString(cur.getColumnIndex("station_name"));
				Log.d(TAG, str[i]);
				i++;
				cur.moveToNext();
			}
			if (!cur.isClosed()) {
				cur.close();
			}
			return str;
		} else {
			return new String[] {};
		}
	}
	
	public int[] getLineByStation(String inputStrings) {
		Cursor cur = database.rawQuery(
				"SELECT _id_line FROM iwaygo_station_info where station_name=? order by _id_line",
				new String[] {
					inputStrings
				});
		
		if (cur.getCount() > 0) {
			int[] str = new int[cur.getCount()];
			int i = 0;
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				str[i] = cur.getInt(cur.getColumnIndex("_id_line"));
				i++;
				cur.moveToNext();
			}
			if (!cur.isClosed()) {
				cur.close();
			}
			return str;
		} else {
			return new int[] {};
		}
	}
	
	public Bundle isSameLine(String start, String end) {
		Bundle bundle = new Bundle();
		DynamicArray arrLineNum = new DynamicArray();
		DynamicArray arrLineName = new DynamicArray();
		int j = 0;
		Cursor curS = database.rawQuery(
				"SELECT _id_line,line_name FROM iwaygo_station_info where station_name=? ",
				new String[] {
					start
				});
		Cursor curE = database.rawQuery(
				"SELECT _id_line,line_name FROM iwaygo_station_info where station_name=? ",
				new String[] {
					end
				});
		Log.d(TAG, "SELECT * FROM iwaygo_station_info where line_name=? order by _id::::" + start
				+ "SELECT _id_line,line_name FROM iwaygo_station_info where station_name=? ::::"
				+ end);
		bundle.putBoolean("state", false);
		bundle.putString("transferTimes", "none");
		if ((curS.getCount() > 0) && (curE.getCount() > 0)) {
			curS.moveToFirst();
			while (!curS.isAfterLast()) {
				for (int i = 0; i < curE.getCount(); i++) {
					curE.moveToPosition(i);
					Log.d(TAG, "start:::" + curS.getString(0) + "\t end:::" + curE.getString(0));
					if (curS.getInt(0) == curE.getInt(0)) {
						arrLineNum.put(j, curS.getInt(curS.getColumnIndex("_id_line")));
						arrLineName.put(j, curS.getString(curS.getColumnIndex("line_name")));
						if (isTheLineLoop(curS.getInt(curS.getColumnIndex("_id_line"))).getBoolean(
								"state")) {
							j++;
							arrLineNum
									.put(j, (curS.getInt(curS.getColumnIndex("_id_line")) + 1000));
							arrLineName.put(j,
									"环线" + (curS.getString(curS.getColumnIndex("line_name"))));
						}
						j++;
						Log.d(TAG, "return true");
						bundle.putBoolean("state", true);
					}
				}
				curS.moveToNext();
			}
			if (bundle.getBoolean("state") == false) {
				DynamicArray arrStartLine = new DynamicArray();
				DynamicArray arrTransfer = new DynamicArray();
				DynamicArray arrEndLine = new DynamicArray();
				DynamicArray arrTransferTwice = new DynamicArray();
				DynamicArray arrLineTwice = new DynamicArray();
				Bundle startLinkBundle = findLineLinksByStation(start);
				Bundle endLinkBundle = findLineLinksByStation(end);
				Boolean transferOnce = false;
				Boolean transferTwice = false;
				if ((startLinkBundle != null) && (endLinkBundle != null)) {
					for (int i = 0; i < startLinkBundle.getIntArray("LineGoto").length; i++) {
						for (int k = 0; k < endLinkBundle.getIntArray("LineGoto").length; k++) {
							if (startLinkBundle.getIntArray("LineGoto")[i] == endLinkBundle
									.getIntArray("ThroughLineGoto")[k]) {
								transferOnce = true;
								Boolean unique = true;
								for (int l = 0; l < arrStartLine.getLength(); l++) {
									if ((arrStartLine.getInt()[l] == startLinkBundle
											.getIntArray("ThroughLineGoto")[i])
											&& (arrTransfer.getInt()[l] == startLinkBundle
													.getIntArray("Transfer")[i])
											&& (arrEndLine.getInt()[l] == startLinkBundle
													.getIntArray("LineGoto")[i])) {
										unique = false;
									}
								}
								if (unique) {
									arrStartLine.put(arrStartLine.getLength(),
											startLinkBundle.getIntArray("ThroughLineGoto")[i]);
									arrTransfer.put(arrTransfer.getLength(),
											startLinkBundle.getIntArray("Transfer")[i]);
									arrEndLine.put(arrEndLine.getLength(),
											startLinkBundle.getIntArray("LineGoto")[i]);
									if (isTheLineLoop(
											startLinkBundle.getIntArray("ThroughLineGoto")[i])
											.getBoolean("state")) {
										arrStartLine
												.put(arrStartLine.getLength(), startLinkBundle
														.getIntArray("ThroughLineGoto")[i] + 1000);
										arrTransfer.put(arrTransfer.getLength(),
												startLinkBundle.getIntArray("Transfer")[i]);
										arrEndLine.put(arrEndLine.getLength(),
												startLinkBundle.getIntArray("LineGoto")[i]);
									}
									if (isTheLineLoop(startLinkBundle.getIntArray("LineGoto")[i])
											.getBoolean("state")) {
										arrStartLine.put(arrStartLine.getLength(),
												startLinkBundle.getIntArray("ThroughLineGoto")[i]);
										arrTransfer.put(arrTransfer.getLength(),
												startLinkBundle.getIntArray("Transfer")[i]);
										arrEndLine.put(arrEndLine.getLength(),
												startLinkBundle.getIntArray("LineGoto")[i] + 1000);
									}
								}
							}
						}
					}
					
					if (transferOnce) {
						bundle.putIntArray("StartLine", arrStartLine.getInt());
						bundle.putIntArray("Transfer", arrTransfer.getInt());
						bundle.putIntArray("EndLine", arrEndLine.getInt());
						
						String[] startLineStrings = new String[bundle.getIntArray("StartLine").length];
						String[] transferStrings = new String[bundle.getIntArray("StartLine").length];
						String[] endLineStrings = new String[bundle.getIntArray("StartLine").length];
						for (int i = 0; i < startLineStrings.length; i++) {
							startLineStrings[i] = getLineName(bundle.getIntArray("StartLine")[i]);
							transferStrings[i] = getStationName(bundle.getIntArray("Transfer")[i]);
							endLineStrings[i] = getLineName(bundle.getIntArray("EndLine")[i]);
						}
						bundle.putStringArray("StartLineName", startLineStrings);
						bundle.putStringArray("TransferName", transferStrings);
						bundle.putStringArray("EndLineName", endLineStrings);
						bundle.putString("transferTimes", "once");
					} else {
						transferTwice = false;
						for (int i = 0; i < startLinkBundle.getIntArray("LineGoto").length; i++) {
							for (int k = 0; k < endLinkBundle.getIntArray("LineGoto").length; k++) {
								if (startLinkBundle.getIntArray("LineGoto")[i] == endLinkBundle
										.getIntArray("LineGoto")[k]) {
									transferTwice = true;
									Boolean unique = true;
									Log.d(TAG, "arrStartLine:" + arrStartLine.getLength());
									Log.d(TAG, "arrEndLine:" + arrEndLine.getLength());
									Log.d(TAG, "arrTransferTwice:" + arrTransferTwice.getLength());
									Log.d(TAG, "arrLineTwice:" + arrLineTwice.getLength());
									for (int l = 0; l < arrStartLine.getLength(); l++) {
										if ((arrStartLine.getInt()[l] == startLinkBundle
												.getIntArray("ThroughLineGoto")[i])
												&& (arrTransfer.getInt()[l] == startLinkBundle
														.getIntArray("Transfer")[i])
												&& (arrLineTwice.getInt()[l] == startLinkBundle
														.getIntArray("LineGoto")[i])
												&& (arrTransferTwice.getInt()[l] == endLinkBundle
														.getIntArray("Transfer")[k])
												&& (arrEndLine.getInt()[l] == endLinkBundle
														.getIntArray("ThroughLineGoto")[k])) {
											unique = false;
										}
									}
									if (unique) {
										arrStartLine.put(arrStartLine.getLength(),
												startLinkBundle.getIntArray("ThroughLineGoto")[i]);
										arrTransfer.put(arrTransfer.getLength(),
												startLinkBundle.getIntArray("Transfer")[i]);
										arrLineTwice.put(arrLineTwice.getLength(),
												startLinkBundle.getIntArray("LineGoto")[i]);
										arrTransferTwice.put(arrTransferTwice.getLength(),
												endLinkBundle.getIntArray("Transfer")[k]);
										arrEndLine.put(arrEndLine.getLength(),
												endLinkBundle.getIntArray("ThroughLineGoto")[k]);
										
										if (isTheLineLoop(
												startLinkBundle.getIntArray("ThroughLineGoto")[i])
												.getBoolean("state")) {
											arrStartLine
													.put(arrStartLine.getLength(),
															startLinkBundle
																	.getIntArray("ThroughLineGoto")[i] + 1000);
											arrTransfer.put(arrTransfer.getLength(),
													startLinkBundle.getIntArray("Transfer")[i]);
											arrLineTwice.put(arrLineTwice.getLength(),
													startLinkBundle.getIntArray("LineGoto")[i]);
											arrTransferTwice.put(arrTransferTwice.getLength(),
													endLinkBundle.getIntArray("Transfer")[k]);
											arrEndLine
													.put(arrEndLine.getLength(), endLinkBundle
															.getIntArray("ThroughLineGoto")[k]);
										}
										if (isTheLineLoop(
												startLinkBundle.getIntArray("LineGoto")[i])
												.getBoolean("state")) {
											arrStartLine
													.put(arrStartLine.getLength(), startLinkBundle
															.getIntArray("ThroughLineGoto")[i]);
											arrTransfer.put(arrTransfer.getLength(),
													startLinkBundle.getIntArray("Transfer")[i]);
											arrLineTwice
													.put(arrLineTwice.getLength(),
															startLinkBundle.getIntArray("LineGoto")[i] + 1000);
											arrTransferTwice.put(arrTransferTwice.getLength(),
													endLinkBundle.getIntArray("Transfer")[k]);
											arrEndLine
													.put(arrEndLine.getLength(), endLinkBundle
															.getIntArray("ThroughLineGoto")[k]);
										}
										if (isTheLineLoop(
												endLinkBundle.getIntArray("ThroughLineGoto")[k])
												.getBoolean("state")) {
											arrStartLine
													.put(arrStartLine.getLength(), startLinkBundle
															.getIntArray("ThroughLineGoto")[i]);
											arrTransfer.put(arrTransfer.getLength(),
													startLinkBundle.getIntArray("Transfer")[i]);
											arrLineTwice.put(arrLineTwice.getLength(),
													startLinkBundle.getIntArray("LineGoto")[i]);
											arrTransferTwice.put(arrTransferTwice.getLength(),
													endLinkBundle.getIntArray("Transfer")[k]);
											arrEndLine
													.put(arrEndLine.getLength(),
															endLinkBundle
																	.getIntArray("ThroughLineGoto")[k] + 1000);
										}
									}
								}
							}
						}
						if (transferTwice) {
							bundle.putIntArray("StartLine", arrStartLine.getInt());
							bundle.putIntArray("Transfer", arrTransfer.getInt());
							bundle.putIntArray("EndLine", arrEndLine.getInt());
							bundle.putIntArray("TransferTwice", arrTransferTwice.getInt());
							bundle.putIntArray("LineTwice", arrLineTwice.getInt());
							
							String[] startLineStrings = new String[bundle.getIntArray("StartLine").length];
							String[] transferStrings = new String[bundle.getIntArray("StartLine").length];
							String[] endLineStrings = new String[bundle.getIntArray("StartLine").length];
							String[] transferTwiceStrings = new String[bundle
									.getIntArray("StartLine").length];
							String[] lineTwiceStrings = new String[bundle.getIntArray("StartLine").length];
							for (int i = 0; i < startLineStrings.length; i++) {
								startLineStrings[i] = getLineName(bundle.getIntArray("StartLine")[i]);
								transferStrings[i] = getStationName(bundle.getIntArray("Transfer")[i]);
								endLineStrings[i] = getLineName(bundle.getIntArray("EndLine")[i]);
								transferTwiceStrings[i] = getStationName(bundle
										.getIntArray("TransferTwice")[i]);
								lineTwiceStrings[i] = getLineName(bundle.getIntArray("LineTwice")[i]);
							}
							bundle.putStringArray("StartLineName", startLineStrings);
							bundle.putStringArray("TransferName", transferStrings);
							bundle.putStringArray("EndLineName", endLineStrings);
							bundle.putStringArray("TransferTwiceName", transferTwiceStrings);
							bundle.putStringArray("LineTwiceName", lineTwiceStrings);
							bundle.putString("transferTimes", "twice");
						}
					}
				}
			}
			if (!curS.isClosed()) {
				curS.close();
			}
			if (!curE.isClosed()) {
				curE.close();
			}
			if ((arrLineName.getLength() > 0) && (arrLineNum.getLength() > 0)) {
				bundle.putStringArray("linenames", arrLineName.getStrings());
				bundle.putIntArray("lines", arrLineNum.getInt());
			}
			
		} else {
			
			if (!curS.isClosed()) {
				curS.close();
			}
			if (!curE.isClosed()) {
				curE.close();
			}
			bundle.putBoolean("state", false);
		}
		return bundle;
	}
	
	public Bundle stationsInTheSameLine(String start, String end, int line) {
		Bundle bundle = new Bundle();
		
		DynamicArray arrStations = new DynamicArray();
		int startStationNum = getStationNum(start, line);
		int endStationNum = getStationNum(end, line);
		int loopLinesCount = 0;
		if ((startStationNum > 0) && (endStationNum > 0)) {
			bundle.putBoolean("isStationsLoop", false);
			if (line > 1000) {// isTheLineLoop(line).getBoolean("state")) {
				int countStations = getCountStationsFromLine(line);// isTheLineLoop(line).getInt("countStations");
				int time = 0;
				if (startStationNum > endStationNum) {
					int j = 0;
					Bundle tempBundle = showPathByPointSameLine(startStationNum, countStations,
							line, "asc");
					for (int i = 0; i < tempBundle.getStringArray("stations").length; i++) {
						arrStations.put(i, tempBundle.getStringArray("stations")[i]);
						j++;
					}
					time += tempBundle.getInt("time");
					tempBundle = showPathByPointSameLine(1, endStationNum, line, "asc");
					for (int i = 0; i < tempBundle.getStringArray("stations").length; i++) {
						arrStations.put(i + j, tempBundle.getStringArray("stations")[i]);
					}
					time += tempBundle.getInt("time");
					String[] outStrings = arrStations.getStrings();
					bundle.putStringArray("stations", outStrings);
					bundle.putString("order", "asc");
					loopLinesCount++;
				} else {
					int j = 0;
					Bundle tempBundle = showPathByPointSameLine(endStationNum, countStations, line,
							"asc");
					for (int i = 0; i < tempBundle.getStringArray("stations").length; i++) {
						arrStations.put(i, tempBundle.getStringArray("stations")[i]);
						j++;
					}
					time += tempBundle.getInt("time");
					tempBundle = showPathByPointSameLine(1, startStationNum, line, "asc");
					for (int i = 0; i < tempBundle.getStringArray("stations").length; i++) {
						arrStations.put(i + j, tempBundle.getStringArray("stations")[i]);
					}
					time += tempBundle.getInt("time");
					String[] outStrings = arrStations.getStringsDesc();
					bundle.putStringArray("stations", outStrings);
					bundle.putString("order", "desc");
					loopLinesCount++;
				}
				bundle.putInt("time", time);
				bundle.putBoolean("isStationsLoop", true);
				
			} else {
				if (startStationNum > endStationNum) {
					bundle = showPathByPointSameLine(endStationNum, startStationNum, line, "desc");
					bundle.putString("order", "desc");
				} else {
					bundle = showPathByPointSameLine(startStationNum, endStationNum, line, "asc");
					bundle.putString("order", "asc");
				}
			}
		}
		bundle.putInt("loopLinesCount", loopLinesCount);
		bundle.putString("inStart", start);
		bundle.putString("inEnd", end);
		bundle.putInt("inLine", line);
		return bundle;
	}
	
	public int getStationNum(String station, int line) {
		int stationNum = 0;
		if (line > 1000) {
			line = line - 1000;
		}
		Cursor curS = database.rawQuery(
				"SELECT * FROM iwaygo_station_info where station_name=? and _id_line=?",
				new String[] {
						station, "" + line
				});
		Log.d(TAG, "SELECT * FROM iwaygo_station_info where station_name=? and _id_line=?::::"
				+ station + " " + line);
		if (curS.getCount() > 0) {
			curS.moveToFirst();
			stationNum = curS.getInt(curS.getColumnIndex("line_num"));
			Log.d(TAG, "getStationNum:" + stationNum);
			if (!curS.isClosed()) {
				curS.close();
			}
		}
		return stationNum;
	}
	
	public String getStationName(int stationId) {
		String stationNameString = null;
		Cursor curS = database.rawQuery(
				"SELECT station_name FROM iwaygo_station_info where _id_station=?", new String[] {
					"" + stationId
				});
		Log.d(TAG, "SELECT station_name FROM iwaygo_station_info where _id_station=?::::"
				+ stationId);
		if (curS.getCount() > 0) {
			curS.moveToFirst();
			stationNameString = curS.getString(curS.getColumnIndex("station_name"));
			Log.d(TAG, "getStationName:" + stationNameString);
			if (!curS.isClosed()) {
				curS.close();
			}
		}
		return stationNameString;
	}
	
	public Bundle getStationInfoByName(String station) {
		Cursor curS = database
				.rawQuery(
						"SELECT _id_line,outs_info,up_first_time,up_last_time,down_first_time,down_last_time FROM iwaygo_station_info where station_name=?",
						new String[] {
							station
						});
		if (curS.getCount() > 0) {
			String[] outs_info = new String[curS.getCount()];
			String[] up_first_time = new String[curS.getCount()];
			String[] up_last_time = new String[curS.getCount()];
			String[] down_first_time = new String[curS.getCount()];
			String[] down_last_time = new String[curS.getCount()];
			int[] _id_line = new int[curS.getCount()];
			
			curS.moveToFirst();
			int i = 0;
			while (!curS.isAfterLast()) {
				_id_line[i] = curS.getInt(curS.getColumnIndex("_id_line"));
				outs_info[i] = curS.getString(curS.getColumnIndex("outs_info"));
				up_first_time[i] = curS.getString(curS.getColumnIndex("up_first_time"));
				up_last_time[i] = curS.getString(curS.getColumnIndex("up_last_time"));
				down_first_time[i] = curS.getString(curS.getColumnIndex("down_first_time"));
				down_last_time[i] = curS.getString(curS.getColumnIndex("down_last_time"));
				curS.moveToNext();
				i++;
			}
			if (!curS.isClosed()) {
				curS.close();
			}
			Bundle outBundle = new Bundle();
			outBundle.putIntArray("_id_line", _id_line);
			outBundle.putStringArray("outs_info", outs_info);
			outBundle.putStringArray("up_first_time", up_first_time);
			outBundle.putStringArray("up_last_time", up_last_time);
			outBundle.putStringArray("down_first_time", down_first_time);
			outBundle.putStringArray("down_last_time", down_last_time);
			return outBundle;
		}
		if (!curS.isClosed()) {
			curS.close();
		}
		return null;
	}
	
	public String getLineName(int LineId) {
		String LineNameString = "";
		Bundle tempBundle = new Bundle();
		tempBundle.putInt("LineId", LineId);
		
		for (int i = 0; i < lineNameBylineArray.getLength(); i++) {
			if (lineNameBylineArray.getBundles()[i].getInt("LineId") == LineId) {
				return lineNameBylineArray.getBundles()[i].getString("LineName");
			}
		}
		
		if (LineId > 1000) {
			LineId -= 1000;
			LineNameString = "环线";
		}
		
		Cursor curS = database.rawQuery(
				"SELECT line_name FROM iwaygo_station_info where _id_line=?", new String[] {
					"" + LineId
				});
		Log.d(TAG, "SELECT line_name FROM iwaygo_station_info where _id_line=?::::" + LineId);
		if (curS.getCount() > 0) {
			curS.moveToFirst();
			LineNameString += curS.getString(curS.getColumnIndex("line_name"));
			Log.d(TAG, "getLineName:" + LineNameString);
			if (!curS.isClosed()) {
				curS.close();
			}
		}
		
		tempBundle.putString("LineName", LineNameString);
		lineNameBylineArray.put(lineNameBylineArray.getLength(), tempBundle);
		return LineNameString;
	}
	
	public void testall() {
		for (int i = 1; i < 19; i++) {
			Cursor curS = database
					.rawQuery(
							"SELECT _id_station,station_name,line_num FROM iwaygo_station_info where _id_line=? order by line_num",
							new String[] {
								"" + i
							});
			if (curS.getCount() > 0) {
				curS.moveToFirst();
				int[] line_num = new int[curS.getCount()];
				int[] _id_station = new int[curS.getCount()];
				String[] station_name = new String[curS.getCount()];
				int[] time = new int[curS.getCount()];
				int n = 0;
				while (!curS.isAfterLast()) {
					line_num[n] = curS.getInt(curS.getColumnIndex("line_num"));
					_id_station[n] = curS.getInt(curS.getColumnIndex("_id_station"));
					station_name[n] = curS.getString(curS.getColumnIndex("station_name"));
					n++;
					
					curS.moveToNext();
				}
				
				for (int j = 0; j < _id_station.length - 1; j++) {
					Cursor curE = database
							.rawQuery(
									"SELECT next_time FROM iwaygo_station_time where _id_line_curr=? and _id_station_start=? and _id_station_next=?",
									new String[] {
											"" + i, "" + _id_station[j], "" + _id_station[j + 1]
									});
					curE.moveToFirst();
					if (curE.getCount() > 0) {
						time[j] = curE.getInt(curE.getColumnIndex("next_time"));
					}
					if (!curE.isClosed()) {
						curE.close();
					}
				}
				
				for (n = 0; n < _id_station.length; n++) {
					Log.d(TAG, "testall\t" + "\tline\t" + i + "\tline_mun\t" + line_num[n]
							+ "\tstation_name\t" + station_name[n] + "\t_id_station\t"
							+ _id_station[n] + "\ttime\t" + time[n]);
				}
				if (!curS.isClosed()) {
					curS.close();
				}
			}
		}
	}
	
	public Bundle showPathByPointSameLine2(int start, int end, int line, String order) {
		int time = 0;
		if (line > 1000) {
			line = line - 1000;
		}
		Log.i(TAG, "::::::::::::::::::::::::showPathByPointSameLine:::::\t" + start + ":::::\t"
				+ end + ":::::\t" + line + ":::::\t" + order);
		String[] stationsStrings = new String[end - start + 1];
		Bundle bundle = new Bundle();
		Cursor curS = database
				.rawQuery(
						"SELECT _id_station,station_name,line_num,time FROM fast_time where _id_line=? order by line_num",
						new String[] {
							"" + line
						});
		
		if (curS.getCount() > 0) {
			curS.moveToFirst();
			int s = 0, curr = 0;
			while (!curS.isAfterLast()) {
				if (start == curS.getInt(curS.getColumnIndex("_id_station"))) {
					s = curr;
				}
				curr++;
				curS.moveToNext();
			}
			
			for (int j = 0; j < stationsStrings.length; j++) {
				curS.moveToPosition(s + j);
				stationsStrings[j] = curS.getString(curS.getColumnIndex("station_name"));
				time += curS.getInt(curS.getColumnIndex("time"));
			}
			
			if (order != "asc") {
				String[] tempStrings = new String[stationsStrings.length];
				for (int j = 0; j < stationsStrings.length; j++) {
					tempStrings[j] = stationsStrings[j];
				}
				for (int j = 0; j < stationsStrings.length; j++) {
					stationsStrings[j] = tempStrings[stationsStrings.length - j-1];
				}
			}
		}
		
		if (!curS.isClosed()) {
			curS.close();
		}
		
		bundle.putStringArray("stations", stationsStrings);
		bundle.putInt("time", time);
		return bundle;
	}
	
	public Bundle showPathByPointSameLine(int start, int end, int line, String order) {
		int time = 0;
		if (line > 1000) {
			line = line - 1000;
		}
		Log.i(TAG, "::::::::::::::::::::::::showPathByPointSameLine:::::\t" + start + ":::::\t"
				+ end + ":::::\t" + line + ":::::\t" + order);
		String[] stationsStrings = new String[end - start + 1];
		Bundle bundle = new Bundle();
		Cursor curS = database.rawQuery(
				"SELECT station_name FROM iwaygo_station_info where line_num=? and _id_line=?",
				new String[] {
						"" + start, "" + line
				});
		Log.d(TAG,
				"SELECT station_name FROM iwaygo_station_info where line_num=? and _id_line=? ::::"
						+ start + " " + line);
		curS.moveToFirst();
		if (order == "asc") {
			stationsStrings[0] = curS.getString(curS.getColumnIndex("station_name"));
		} else {
			stationsStrings[end - start] = curS.getString(curS.getColumnIndex("station_name"));
		}
		if (!curS.isClosed()) {
			curS.close();
		}
		int[] getStation = getStationIdFromLineNum(line);
		Log.d(TAG, "getStation.length" + getStation.length);
		Cursor curT = database
				.rawQuery(
						"SELECT next_time,station_next_name,_id_station_start,_id_station_next FROM iwaygo_station_time where _id_line_curr=?",
						new String[] {
							"" + line
						});
		Log.d(TAG,
				"SELECT next_time,station_next_name,_id_station_start,_id_station_next FROM iwaygo_station_time where _id_line_curr=?::::"
						+ line);
		if (curT.getCount() > 0) {
			for (int i = 0; i < end - start; i++) {
				curT.moveToFirst();
				while (!curT.isAfterLast()) {
					if ((curT.getInt(curT.getColumnIndex("_id_station_start")) == getStation[start
							+ i - 1])
							&& (curT.getInt(curT.getColumnIndex("_id_station_next")) == getStation[start
									+ i])) {
						
						time += curT.getInt(curT.getColumnIndex("next_time"));
						if (order == "asc") {
							stationsStrings[i + 1] = curT.getString(curT
									.getColumnIndex("station_next_name"));
						} else {
							stationsStrings[end - start - i - 1] = curT.getString(curT
									.getColumnIndex("station_next_name"));
						}
					}
					curT.moveToNext();
				}
			}
		}
		if (!curT.isClosed()) {
			curT.close();
		}
		bundle.putStringArray("stations", stationsStrings);
		bundle.putInt("time", time);
		return bundle;
	}
	
	public int[] getStationIdFromLineNum(int line) {
		if (line > 1000) {
			line = line - 1000;
		}
		Cursor curS = database.rawQuery(
				"SELECT _id_station FROM iwaygo_station_info where _id_line=? order by line_num",
				new String[] {
					"" + line
				});
		Log.d(TAG,
				"SELECT _id_station FROM iwaygo_station_info where _id_line=? order by line_num::::"
						+ line);
		int[] out = new int[curS.getCount()];
		if (curS.getCount() > 0) {
			curS.moveToFirst();
			for (int j = 0; j < curS.getCount(); j++) {
				out[j] = curS.getInt(curS.getColumnIndex("_id_station"));
				curS.moveToNext();
				// Log.d(TAG, "in--->>> num:" + j + "\tline:" + line +
				// "\t\tout--->>> id:" + out[j]);
			}
		}
		if (!curS.isClosed()) {
			curS.close();
		}
		return out;
	}
	
	public int getCountStationsFromLine(int line) {
		int countStations;
		if (line > 1000) {
			line = line - 1000;
		}
		Cursor curS = database.rawQuery(
				"SELECT _id_line FROM iwaygo_station_info where _id_line=?", new String[] {
					"" + line
				});
		Log.d(TAG, "SELECT _id_line FROM iwaygo_station_info where _id_line=?::::" + line);
		countStations = curS.getCount();
		if (!curS.isClosed()) {
			curS.close();
		}
		return countStations;
	}
	
	public Bundle findLineLinksByLine(int line) {
		DynamicArray arrStations = new DynamicArray();
		Log.i(TAG, "findLineLinksByLine::::" + line);
		// Find transfer by line
		Cursor curE = database.rawQuery(
				"SELECT _id_station FROM iwaygo_station_info where _id_line=? and transfer=1",
				new String[] {
					"" + line
				});
		Log.d(TAG, "Find transfer by line(getCount):TEST:" + curE.getCount());
		if (curE.getCount() > 0) {
			curE.moveToFirst();
			while (!curE.isAfterLast()) {
				arrStations.put(arrStations.getLength(),
						curE.getInt(curE.getColumnIndex("_id_station")));
				curE.moveToNext();
			}
			
			Log.d(TAG, "Find transfer by lines:END:");
			
			// Find transfer can go to lines
			int[] transfers = arrStations.getInt();
			DynamicArray arrStationsTransfer = new DynamicArray();
			DynamicArray arrLineGoto = new DynamicArray();
			for (int l = 0; l < transfers.length; l++) {
				Log.d(TAG, "SELECT _id_line FROM iwaygo_station_info where _id_station=?");
				Cursor curT = database.rawQuery(
						"SELECT _id_line FROM iwaygo_station_info where _id_station=?",
						new String[] {
							"" + transfers[l]
						});
				if (curT.getCount() > 0) {
					curT.moveToFirst();
					while (!curT.isAfterLast()) {
						arrLineGoto.put(arrLineGoto.getLength(),
								curT.getInt(curT.getColumnIndex("_id_line")));
						arrStationsTransfer.put(arrStationsTransfer.getLength(), transfers[l]);
						
						curT.moveToNext();
					}
				}
				if (!curT.isClosed()) {
					curT.close();
				}
			}
			Log.d(TAG, "Find transfer can go to lines:END:");
			if (!curE.isClosed()) {
				curE.close();
			}
			Bundle outBundle = new Bundle();
			outBundle.putIntArray("LineGoto", arrLineGoto.getInt());
			outBundle.putIntArray("Transfer", arrStationsTransfer.getInt());
			return outBundle;
		}
		if (!curE.isClosed()) {
			curE.close();
		}
		return null;
	}
	
	public Bundle findLineLinksByStation(String station) {
		// Find line by station
		Cursor curS = database.rawQuery(
				"SELECT _id_line FROM iwaygo_station_info where station_name=? group by _id_line",
				new String[] {
					station
				});
		if (curS.getCount() > 0) {
			int[] lines = new int[curS.getCount()];
			curS.moveToFirst();
			int i = 0;
			while (!curS.isAfterLast()) {
				lines[i] = curS.getInt(curS.getColumnIndex("_id_line"));
				curS.moveToNext();
				i++;
			}
			if (!curS.isClosed()) {
				curS.close();
			}
			DynamicArray arrStationsTransfer = new DynamicArray();
			DynamicArray arrThroughLineGoto = new DynamicArray();
			DynamicArray arrLineGoto = new DynamicArray();
			Boolean isNull = true;
			for (int j = 0; j < lines.length; j++) {
				Bundle byLineBundle = findLineLinksByLine(lines[j]);
				if (byLineBundle != null) {
					isNull = false;
					for (int k = 0; k < byLineBundle.getIntArray("LineGoto").length; k++) {
						arrLineGoto.put(arrLineGoto.getLength(),
								byLineBundle.getIntArray("LineGoto")[k]);
						arrStationsTransfer.put(arrStationsTransfer.getLength(),
								byLineBundle.getIntArray("Transfer")[k]);
						arrThroughLineGoto.put(arrThroughLineGoto.getLength(), lines[j]);
					}
				}
			}
			if (isNull) {
				return null;
			}
			Bundle outBundle = new Bundle();
			outBundle.putIntArray("LineGoto", arrLineGoto.getInt());
			outBundle.putIntArray("Transfer", arrStationsTransfer.getInt());
			outBundle.putIntArray("ThroughLineGoto", arrThroughLineGoto.getInt());
			return outBundle;
		}
		if (!curS.isClosed()) {
			curS.close();
		}
		return null;
	}
	
	public Bundle isTheLineLoop(int line) {
		// int countStations;
		if (line > 1000) {
			line = line - 1000;
		}
		Bundle outsBundle = new Bundle();
		outsBundle.putBoolean("state", false);
		
		if ((line == 3) || (line == 4)) {
			outsBundle.putBoolean("state", true);
		}
		Log.d(TAG, "isTheLineLoop:" + outsBundle.getBoolean("state"));
		// Log.d(TAG, "countStations:" + outsBundle.getInt("countStations"));
		return outsBundle;
	}
}
