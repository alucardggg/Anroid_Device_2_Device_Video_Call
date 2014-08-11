package fi.iki.elonen;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.SharedPreferences;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class RtspServer extends NanoHTTPD {

	private SharedPreferences mSharedPreference;

	public RtspServer(int port) {
		super(port);
	}

	@Override
	public Response serve(IHTTPSession session) {
		Method method = session.getMethod();
		String uri = session.getUri();
		System.out.println(method + " '" + uri + "' ");

		// String msg = "<html><body><h1>Hello server</h1>\n";
		String msg = "";
		//
		// Map<String, String> parms = session.getParms();
		// if (parms.get("username") == null)
		// msg += "<form action='?' method='get'>\n"
		// + "  <p>Your name: <input type='text' name='username'></p>\n"
		// + "</form>\n";
		// else
		// msg += "<p>Hello, " + parms.get("username") + "!</p>";
		//
		// msg += "</body></html>\n";
		// @SuppressWarnings("unchecked")
		Map<String, String> all = (Map<String, String>) mSharedPreference
				.getAll();

		Set<Entry<String, String>> set = all.entrySet();
		for (Entry<String, String> e : set) {
			msg += e.getValue() + "\n";
		}

		//
		NanoHTTPD.Response response = new NanoHTTPD.Response(msg);
		response.setMimeType("application/sdp");
		// response.setMimeType("html/xml");
		response.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		response.addHeader("Accept-Encoding", "gzip,deflate,sdch");

		return response;
	}

	public void setmSharedPreference(SharedPreferences mSharedPreference) {
		this.mSharedPreference = mSharedPreference;
	}

}
