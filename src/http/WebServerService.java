package http;

import java.io.IOException;

import fi.iki.elonen.RtspServer;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.ServerRunner;
import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class WebServerService extends Service {
	private NanoHTTPD httpd;

	public WebServerService() {
		super();
		httpd = new RtspServer(8080);
		// httpd = new RtspServer(8080, null);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			((RtspServer) httpd).setmSharedPreference(getSharedPreferences(
					"SDP", Context.MODE_PRIVATE));
			httpd.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		Log.i("HTTPSERVICE", "Creating and starting httpService");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i("HTTPSERVICE", "Destroying httpService");
		httpd.stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}