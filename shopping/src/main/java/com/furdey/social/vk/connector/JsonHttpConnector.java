package com.furdey.social.vk.connector;

import com.furdey.social.vk.api.Response;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Класс для выполнения запросов к vk.com. Хранит переменные сессии и открытое
 * соединение. Используется для непосредственного выполнения запросов.
 * 
 * @author Stepan Furdey
 */
public final class JsonHttpConnector implements VkConnector {

	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;

	private static final String TAG = JsonHttpConnector.class.getSimpleName();

	private HttpClient client = null;
	private Gson gson = null;

	private StreamHandler handler = null;
	private OutputStream outStream = null;
	private static Logger logger = Logger.getLogger("org.apache.http");

	public JsonHttpConnector() {
		// Включаем логирование HTTP-запросов
		outStream = System.out;
		handler = new StreamHandler(outStream, new SimpleFormatter());
		handler.setLevel(Level.OFF);
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.OFF);
	}

	private HttpClient getNewHttpClient() {
		try {

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new VkSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			HttpClient httpClient = new DefaultHttpClient(ccm, params);

			return httpClient;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.furdey.vk.connector.VkConnector#callVk(android.app.Activity,
	 * com.furdey.vk.api.Request)
	 */
	@Override
	public Response callVk(String request, Class<? extends Response> responseType) {
		if (request == null)
			return null;

		BufferedReader in = null;
		Response apiResponse = null;

		try {
			if (client == null)
				client = getNewHttpClient();

			if (gson == null)
				gson = new Gson();

			HttpPost httpRequest = new HttpPost();
			URL url = new URL(request);
			String nullFragment = null;
			httpRequest.setURI(new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(),
					nullFragment));

			httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY,
					CookiePolicy.BROWSER_COMPATIBILITY);

			HttpResponse response = client.execute(httpRequest);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");

			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}

			in.close();
			String page = sb.toString();
			apiResponse = (Response) gson.fromJson(page, responseType);

		} catch (Exception e) {
			throw new RuntimeException("Error while retriving URL", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// do nothing here, we just tried to shut it down.
                    // error means it has already been shut down
				}
			}
		}

		return apiResponse;
	}
}
