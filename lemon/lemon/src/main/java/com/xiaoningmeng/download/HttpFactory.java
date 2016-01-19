package com.xiaoningmeng.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class HttpFactory {

	private final static String TAG = "HttpFactory";

	private static boolean DEBUG = false;

	/**
	 * Gets the input stream from a response entity. If the entity is gzipped
	 * then this will get a stream over the uncompressed data.
	 * 
	 * @param entity
	 *            the entity whose content should be read
	 * @return the input stream to read from
	 * @throws IOException
	 */
	public static InputStream getUngzippedContent(HttpEntity entity)
			throws IOException {
		InputStream responseStream = entity.getContent();
		if (responseStream == null) {
			return responseStream;
		}
		Header header = entity.getContentEncoding();
		if (header == null) {
			return responseStream;
		}
		String contentEncoding = header.getValue();
		if (contentEncoding == null) {
			return responseStream;
		}
		if (contentEncoding.contains("gzip")) {
			if (DEBUG)
				Log.d(TAG, "getUngzippedContent");
			responseStream = new GZIPInputStream(responseStream);
		}
		return responseStream;
	}

	public static final DefaultHttpClient createHttpClient(int retryCount,
			int timeout) {
		// Shamelessly cribbed from AndroidHttpClient
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// HttpProtocolParams.setUseExpectContinue(params, true);
		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 30 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpClientParams.setRedirecting(params, true);

		ConnManagerParams.setTimeout(params, timeout);
		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(50));
		ConnManagerParams.setMaxTotalConnections(params, 200);

		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(
				params, supportedSchemes);
		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		httpClient
				.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
						retryCount, true));
		// Add gzip header to requests using an interceptor
		// httpClient.addRequestInterceptor(new GzipHttpRequestInterceptor());
		// Add gzip compression to responses using an interceptor
		// httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());

		return httpClient;
	}

	public static final DefaultHttpClient createHttpClient(int retryCount) {
		// Shamelessly cribbed from AndroidHttpClient
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// HttpProtocolParams.setUseExpectContinue(params, true);
		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 30 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, 15 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpClientParams.setRedirecting(params, true);

		ConnManagerParams.setTimeout(params, 5 * 1000);
		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(50));
		ConnManagerParams.setMaxTotalConnections(params, 200);

		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(
				params, supportedSchemes);
		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		httpClient
				.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
						retryCount, true));
		// Add gzip header to requests using an interceptor
		httpClient.addRequestInterceptor(new GzipHttpRequestInterceptor());
		// Add gzip compression to responses using an interceptor
		httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());

		return httpClient;
	}

	/**
	 * Create a thread-safe client. This client does not do redirecting, to
	 * allow us to capture correct "error" codes.
	 * 
	 * @return HttpClient
	 */
	public static final DefaultHttpClient createHttpClient() {
		return createHttpClient(3);
	}

	/**
	 * Create a thread-safe client. This client does not do redirecting, to
	 * allow us to capture correct "error" codes. proxyUri ???????????? port
	 * ????????
	 * 
	 * @return HttpClient
	 */
	public static final DefaultHttpClient createHttpClient(String proxyUri,
			int port) {
		// Shamelessly cribbed from AndroidHttpClient
		HttpParams params = new BasicHttpParams();
		HttpHost host = new HttpHost(proxyUri, port);
		params.setParameter(ConnRouteParams.DEFAULT_PROXY, host);

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		// HttpProtocolParams.setUseExpectContinue(params, true);
		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		// Default connection and socket timeout of 30 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		HttpClientParams.setRedirecting(params, true);

		ConnManagerParams.setTimeout(params, 5 * 1000);
		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(50));
		ConnManagerParams.setMaxTotalConnections(params, 200);

		// Sets up the http part of the service.
		final SchemeRegistry supportedSchemes = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		supportedSchemes.register(new Scheme("http", sf, 80));
		supportedSchemes.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), 443));
		final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(
				params, supportedSchemes);
		DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
		httpClient
				.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
						3, true));
		// Add gzip header to requests using an interceptor
		httpClient.addRequestInterceptor(new GzipHttpRequestInterceptor());
		// Add gzip compression to responses using an interceptor
		httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());

		return httpClient;
	}

	private final static class GzipHttpRequestInterceptor implements
			HttpRequestInterceptor {

		public void process(final HttpRequest request, final HttpContext context)
				throws HttpException, IOException {
			if (DEBUG)
				Log.d(HttpFactory.TAG, "GzipHttpRequestInterceptor");
			// if (!request.containsHeader("Accept-Encoding")) {
			// request.addHeader("Accept-Encoding", "gzip");
			// }
			request.setHeader("Accept-Encoding", "gzip");
		}
	}

	private final static class GzipHttpResponseInterceptor implements
			HttpResponseInterceptor {
		public void process(final HttpResponse response,
				final HttpContext context) throws HttpException, IOException {
			HttpEntity entity = response.getEntity();
			Header header = entity.getContentEncoding();

			// if (DEBUG) Log.d(HttpUtil.TAG,
			// "GzipHttpResponseInterceptor header " + header);
			if (header != null) {
				HeaderElement[] codecs = header.getElements();
				for (int i = 0; i < codecs.length; i++) {
					if (codecs[i].getName().equalsIgnoreCase("gzip")) {
						response.setEntity(new GzipDecompressingEntity(response
								.getEntity()));
						return;
					}
				}
			}
		}
	}

	private final static class GzipDecompressingEntity extends
			HttpEntityWrapper {

		public GzipDecompressingEntity(final HttpEntity entity) {
			super(entity);
		}

		@Override
		public InputStream getContent() throws IOException,
				IllegalStateException {
			InputStream wrappedin = wrappedEntity.getContent();
			if (DEBUG)
				Log.d(HttpFactory.TAG, "GzipDecompressingEntity");
			return new GZIPInputStream(wrappedin);
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}
}
