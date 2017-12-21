package com.example.sysinfo;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	WindowManager wManager;
	TextView rootLayout;
	Handler handler;
	Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2002;
		wmParams.format = 1;
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
		// WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
		// WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
		// WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.x = 10;
		wmParams.y = 100;
		rootLayout = new TextView(this);
		rootLayout.setTextSize(20f);
		rootLayout.setTextColor(Color.WHITE);
		wManager.addView(rootLayout, wmParams);
		
		rootLayout.setClickable(true);
		rootLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				try {
					rootLayout.setText(msg.obj.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};

		handler.sendEmptyMessage(1);

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				for (;!thread.isInterrupted();) {
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = getInfo();

					handler.sendMessage(msg);

					try {
						Thread.sleep(300);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.start();
	}

	private String getInfo() {
		StringBuffer info = new StringBuffer();
		info.append(CPU.getCpuName());
		info.append("\n");
		for (int i = 1; i <= CPU.getCpuNum(); i++) {
			info.append("CPU" + i + ":");
			info.append(CPU.getMinCpuFreq(i - 1));
			info.append("/");
			info.append(CPU.getMaxCpuFreq(i - 1));
			info.append(" CUR:" + CPU.getCurCpuFreq());
			info.append("\n");
		}

		info.append("MEM: "
				+ Formatter.formatFileSize(getApplicationContext(), 1024 * MEM.getmem_UNUSED(getApplicationContext()))
				+ "/" + Formatter.formatFileSize(getApplicationContext(), 1024 * MEM.getmem_TOLAL()) + " USE:"
				+ Formatter.formatFileSize(getApplicationContext(),
						1024 * MEM.getmem_TOLAL() - 1024 * MEM.getmem_UNUSED(getApplicationContext())));

		return info.toString();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		wManager.removeView(rootLayout);
		handler.removeMessages(1);
		thread.interrupt();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
