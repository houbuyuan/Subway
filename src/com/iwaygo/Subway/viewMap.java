
package com.iwaygo.Subway;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

import java.io.InputStream;
import java.net.URL;

public class viewMap extends Activity implements OnTouchListener {
	
	// Define view part
	private ImageView view_main_img;
	
	ImageView view_img;
	
	private LinearLayout zoomLayout, loadingLayout;
	
	private ZoomControls zoomCtrl_main_img;
	
	private Button search_button, input_button, line_button, setting_button;
	
	private static final String TAG = "iWayGo";
	
	// These matrices will be used to move and zoom image
	Matrix matrix = new Matrix();
	
	Matrix savedMatrix = new Matrix();
	
	private Bitmap mapBitmap;
	
	// These for locate and scale image
	float tX = 0, tY = 0, tS = 1;
	
	float postX = 0, postY = 0;
	
	float postS = 1;
	
	float pic_height = 1605;
	
	float pic_width = 2210;
	
	Display display;
	
	// We can be in one of these 3 states
	static final int NONE = 0;
	
	static final int DRAG = 1;
	
	static final int ZOOM = 2;
	
	private static final int DIALOG1_KEY = 0;
	
	private static final int DIALOG2_KEY = 1;
	
	int mode = NONE;
	
	// Remember some things for zoomin
	PointF start = new PointF();
	
	PointF mid = new PointF();
	
	float oldDist = 1f;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.iwaygo_map);
		showDialog(DIALOG2_KEY);
		display = getWindowManager().getDefaultDisplay();
		Log.d(TAG,
				"onCreate getDefaultDisplay:X=" + display.getWidth() + "Y=" + display.getHeight());
		
		findViews();
		updateViews();
		setListener();
		new doLoadTask().execute(null, null, null);
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		display = getWindowManager().getDefaultDisplay();
		Log.d(TAG, "getDefaultDisplay:X=" + display.getWidth() + "Y=" + display.getHeight());
		updateViews();
		/*
		 * if (this.getResources().getConfiguration().orientation ==
		 * Configuration.ORIENTATION_LANDSCAPE) { } else if
		 * (this.getResources().getConfiguration().orientation ==
		 * Configuration.ORIENTATION_PORTRAIT) { }
		 */
	}
	
	private class doLoadTask extends AsyncTask<URL, Integer, Long> {
		protected Long doInBackground(URL... urls) {
			Log.i(TAG, "start readBitMap");
			mapBitmap = readBitMap(viewMap.this, R.drawable.bjsubwaymap);
			Log.i(TAG, "end readBitMap");
			Log.i(TAG, "start matrix");
			postX = -200;
			postY = -40;
			postS = (float) 0.5;
			matrix.setScale((float) 0.5, (float) 0.5);
			matrix.postTranslate(-200, -40);
			Log.i(TAG, "end matrix");
			
			return null;
		}
		
		protected void onProgressUpdate(Integer... progress) {
		}
		
		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理
			//showDialog(DIALOG2_KEY);
		}
		
		protected void onPostExecute(Long result) {
			
			view_main_img.setImageBitmap(mapBitmap);
			view_main_img.setImageMatrix(matrix);
			dismissDialog(DIALOG2_KEY);
		}
	}
	
	private void findViews() {
		view_main_img = (ImageView) findViewById(R.id.imageView);
		view_main_img.setVisibility(1);
		// view_main_img.setImageBitmap(readBitMap(this,
		// R.drawable.bjsubwaymap));
		zoomCtrl_main_img = (ZoomControls) findViewById(R.id.zoomCtrl);
		zoomLayout = (LinearLayout) findViewById(R.id.zoomLayout);
		zoomLayout.setVisibility(1);
		search_button = (Button) findViewById(R.id.search_button);
		input_button = (Button) findViewById(R.id.input_button);
		//input_button.setPressed(true);
		//input_button.setTextColor(Color.WHITE);
		line_button = (Button) findViewById(R.id.line_button);
		setting_button = (Button) findViewById(R.id.setting_button);
		loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
		// loadingLayout.setVisibility(1);
	}
	
	private void updateViews() {
		search_button.setWidth(display.getWidth() / 4);
		input_button.setWidth(display.getWidth() / 4);
		line_button.setWidth(display.getWidth() / 4);
		setting_button.setWidth(display.getWidth() / 4);
	}
	
	private void setListener() {
		view_main_img.setOnTouchListener(this);
		search_button.setOnClickListener(button_search_ClickListener);
		line_button.setOnClickListener(button_line_ClickListener);
		setting_button.setOnClickListener(button_setting_ClickListener);

		// ZoomCtrl Listener
		zoomCtrl_main_img.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scaleImgWithButton((float) 1.2);
				
			}
		});
		zoomCtrl_main_img.setOnZoomOutClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scaleImgWithButton((float) 0.8);
			}
		});
	}
	
	View.OnClickListener button_search_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by search");
			loadingLayout.setVisibility(1);
			Intent intent = new Intent();
			// 第一个参数为 AndroidMinifest.xml文件中配置的package属性，第二个参数为package + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.iWayGoSubway");
			startActivity(intent);
			System.gc();
			viewMap.this.finish();
		}
	};
	View.OnClickListener button_line_ClickListener = new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.d("iWayGo", "onClick by search");
			loadingLayout.setVisibility(1);
			Intent intent = new Intent();
			// 第一个参数为 AndroidMinifest.xml文件中配置的package属性，第二个参数为package + class
			intent.setClassName("com.iwaygo.Subway", "com.iwaygo.Subway.lineAndStation");
			startActivity(intent);
			System.gc();
			viewMap.this.finish();
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
			viewMap.this.finish();

		}
	};
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		view_img = (ImageView) v;
		
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				// 置初始位置
				start.set(event.getX(), event.getY());
				Log.d(TAG, "mode=DRAG");
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				Log.d(TAG, "oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
					Log.d(TAG, "mode=ZOOM");
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				postX += tX;
				postY += tY;
				postS *= tS;
				tX = 0;
				tY = 0;
				tS = 1;
				Log.d(TAG, "mode=NONE");
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					// ...
					matrix.set(savedMatrix);
					tX = (event.getX() - start.x);
					tY = (event.getY() - start.y);
					matrix.postTranslate(tX, tY);
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					Log.d(TAG, "newDist=" + newDist);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						scale = scaleImgThreshold(postS, scale);
						tS = scale;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
		}
		
		Log.d(TAG, "POST:X=" + postX + "Y=" + postY + "S=" + postS);
		view_main_img.setImageMatrix(matrix);
		loadingLayout.setVisibility(4);
		return true; // indicate event was handled
	}
	
	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	
	private void scaleImgWithButton(float scale) {
		savedMatrix.set(matrix);
		matrix.set(savedMatrix);
		scale = scaleImgThreshold(postS, scale);
		postS *= scale;
		matrix.postScale(scale, scale, display.getWidth() / 2, display.getHeight() / 2);
		view_main_img.setImageMatrix(matrix);
	}
	
	private float scaleImgThreshold(float pScale, float scale) {
		if ((pScale * scale) > 1) {
			scale = 1 / pScale;
		}
		if ((pScale * scale * pic_height) < (display.getHeight() - 62)) {
			scale = (display.getHeight() - 62) / (pScale * pic_height);
		}
		return scale;
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
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
