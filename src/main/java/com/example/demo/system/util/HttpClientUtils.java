package com.example.demo.system.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author threenoodles
 *
 */
@Slf4j
public class HttpClientUtils {

	/**
	 * 连接超时时间 毫秒
	 */
	private static final int CONNECTION_TIMEOUT_MS = 50000;
	
	private static final int READ_TIMEOUT_MS = 80000;

	private static final String UTF8_CHARSET = "UTF-8";

	private static final Charset UTF_8 = Charset.forName(UTF8_CHARSET);

	/**
	 * 创建HttpClient
	 * 
	 * @param isMultiThread
	 * @return
	 */
	public static HttpClient buildHttpClient(boolean isMultiThread) {

		CloseableHttpClient client = null;

		if (isMultiThread) {
			client = HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
		} else {
			client = HttpClientBuilder.create().build();
		}
		return client;
	}

	/**
	 * get 调用，如果要封装请在这个方法基础上进行封装
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static HttpResponse getInvoke(String url, Map<String, String> params, Map<String, String> headers)
			throws ClientProtocolException, IOException, URISyntaxException {

		HttpClient client = buildHttpClient(false);
		// 设置params
		HttpGet get = new HttpGet(buildGetUrl(url, params));
		// 设置请求和传输超时时间
		get.setConfig(generateRequestConfig());
		get.setHeader(HTTP.CONTENT_ENCODING, UTF8_CHARSET);
		// 设置header
		if (!MapUtil.isEmpty(headers)) {
			for (Entry<String, String> kv : headers.entrySet()) {
				if (StringUtils.isNotEmpty(kv.getKey())) {
					//logger.info("params key : " + kv.getKey() + " , value : " + kv.getValue());
					get.setHeader(kv.getKey(), kv.getValue());
				}
			}
		}
		return client.execute(get);
	}

	/**
	 * post 调用，如果要封装请在这个方法基础上进行封装
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResponse postInvoke(String url, Map<String, String> params, Map<String, String> headers)
			throws URISyntaxException, ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost post = new HttpPost(url);
		// 设置请求和传输超时时间
		post.setConfig(generateRequestConfig());
		post.setHeader(HTTP.CONTENT_ENCODING, UTF8_CHARSET);
		UrlEncodedFormEntity he = null;
		if (params != null) {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				formparams.add(new BasicNameValuePair(key, params.get(key)));
				//logger.info("params key : " + key + " , value : " + params.get(key));
			}
			he = new UrlEncodedFormEntity(formparams, UTF_8);
			post.setEntity(he);
		}
		// 设置header
		if (!MapUtil.isEmpty(headers)) {
			for (Entry<String, String> kv : headers.entrySet()) {
				if (StringUtils.isNotEmpty(kv.getKey())) {
					//logger.info("params key : " + kv.getKey() + " , value : " + kv.getValue());
					post.setHeader(kv.getKey(), kv.getValue());
				}
			}
		}
		return client.execute(post);
	}
	
	/**
	 * 
	 * @param url
	 * @param headers
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	
	public static HttpResponse postFileInvoke(String url, String accessToken, CommonsMultipartFile file, Map<String, String> headers)
			throws URISyntaxException, ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost post = new HttpPost(url);
		// 设置请求和传输超时时间
		post.setConfig(generateRequestConfig());
		post.setHeader(HTTP.CONTENT_ENCODING, UTF8_CHARSET);
		 
		// 设置header
		if (!MapUtil.isEmpty(headers)) {
			for (Entry<String, String> kv : headers.entrySet()) {
				if (StringUtils.isNotEmpty(kv.getKey())) {
					//logger.info("params key : " + kv.getKey() + " , value : " + kv.getValue());
					post.setHeader(kv.getKey(), kv.getValue());
				}
			}
		}
		
		DiskFileItem fi = (DiskFileItem)file.getFileItem();
		File f = fi.getStoreLocation();
//		FileBody bin = new FileBody(f);
//		StringBody accessTokenBody = new StringBody(accessToken, ContentType.create("text/plain", Consts.UTF_8));
		 HttpEntity reqEntity = MultipartEntityBuilder.create()
				 .addBinaryBody("file", f)
				 .addTextBody("accessToken", accessToken, ContentType.TEXT_PLAIN.withCharset("UTF-8"))
				 .build();
		 post.setEntity(reqEntity);
		return client.execute(post);
	}
	

	/**
	 * 创建一个json提交的post
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	public static HttpPost buildJSONHttpPost(String url, JSONObject params, Map<String, String> headers)
			throws UnsupportedEncodingException, URISyntaxException {
		HttpPost post = new HttpPost(url);
		// 设置请求和传输超时时间
		post.setConfig(generateRequestConfig());
		post.setHeader(HTTP.CONTENT_ENCODING, UTF8_CHARSET);
		if (params != null) {
			StringEntity entry = new StringEntity(params.toString(), UTF8_CHARSET);
			entry.setContentEncoding(UTF8_CHARSET);
			post.setEntity(entry);
		}
		// 设置header
		if (!MapUtil.isEmpty(headers)) {
			for (Entry<String, String> kv : headers.entrySet()) {
				if (StringUtils.isNotEmpty(kv.getKey())) {
					//logger.info("params key : " + kv.getKey() + " , value : " + kv.getValue());
					post.setHeader(kv.getKey(), kv.getValue());
				}
			}
		}
		return post;
	}

	/**
	 * 创建一个xml提交的post
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	public static HttpPost buildXMLHttpPost(String url, Map<String, String> params, Map<String, String> headers)
			throws UnsupportedEncodingException, URISyntaxException {
		HttpPost post = new HttpPost(url);
		// 设置请求和传输超时时间
		post.setConfig(generateRequestConfig());
		post.setHeader(HTTP.CONTENT_ENCODING, UTF8_CHARSET);
		if (params != null) {
			StringEntity entry = new StringEntity(mapToXml(params), UTF8_CHARSET);
			entry.setContentEncoding(UTF8_CHARSET);
			post.setEntity(entry);
		}
		// 设置header
		if (!MapUtil.isEmpty(headers)) {
			for (Entry<String, String> kv : headers.entrySet()) {
				if (StringUtils.isNotEmpty(kv.getKey())) {
					//logger.info("params key : " + kv.getKey() + " , value : " + kv.getValue());
					post.setHeader(kv.getKey(), kv.getValue());
				}
			}
		}
		return post;
	}

	/**
	 * @param params
	 * @return
	 */
	public static String mapToXml(Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return StringUtils.EMPTY;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (Entry<String, String> entry : params.entrySet()) {
			sb.append("<" + entry.getKey() + ">");
			sb.append(entry.getValue());
			sb.append("</" + entry.getKey() + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}

	private static RequestConfig generateRequestConfig() {
		RequestConfig requestConfig = RequestConfig.custom()
				// 设置连接请求超时
				.setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
				// 设置连接超时
				.setConnectTimeout(CONNECTION_TIMEOUT_MS)
				// 设置回应超时
				.setSocketTimeout(READ_TIMEOUT_MS).build();
		return requestConfig;
	}

	/**
	 * build getUrl str
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private static String buildGetUrl(String url, Map<String, String> params) {
		StringBuffer uriStr = new StringBuffer(url);
		if (params != null) {
			List<NameValuePair> ps = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				ps.add(new BasicNameValuePair(key, params.get(key)));
			}
			uriStr.append("?");
			uriStr.append(URLEncodedUtils.format(ps, UTF_8));
		}
		return uriStr.toString();
	}

	private HttpClientUtils() {
	}

	/**
	 * 从HTTP请求中取出请求的XML
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getXmlFromRequest(HttpServletRequest request) throws IOException {
		String reqXml = "";
		reqXml = request.getQueryString(); // GET请求
		if ("post".equalsIgnoreCase(request.getMethod())) { // POST请求
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			reqXml = sb.toString();
		}
		return reqXml;
	}

	//=====================================应用方法======================================================
	/**
	 * GET调用
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static String simpleGetInvoke(String url, 
			Map<String, String> params,Map<String, String> headers) throws ClientProtocolException,
			IOException, URISyntaxException {
		// 获取response
		HttpResponse response = getInvoke(url, params,headers);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	public static String simpleGetInvoke(String url, 
			Map<String, String> params) throws ClientProtocolException,
	IOException, URISyntaxException {
		// 获取response
		HttpResponse response = getInvoke(url, params,null);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * post调用
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String simplePostInvoke(String url,
			Map<String, String> params,Map<String, String> headers)
			throws URISyntaxException, ClientProtocolException, IOException {

		// post
		HttpResponse response = postInvoke(url, params, headers);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	public static String simplePostInvoke(String url,
			Map<String, String> params)
					throws URISyntaxException, ClientProtocolException, IOException {
		
		// post
		HttpResponse response = postInvoke(url, params, null);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	

	/**post 调用，json传参
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String simplePostJSONInvoke(String url, JSONObject params, Map<String, String> headers)
			throws URISyntaxException, ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost postMethod = buildJSONHttpPost(url, params,headers);
		HttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	public static String simplePostJSONInvoke(String url, JSONObject params)
			throws URISyntaxException, ClientProtocolException, IOException {
		
		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost postMethod = buildJSONHttpPost(url, params,null);
		HttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * XML调用
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String simplePostXMLInvoke(String url,
			Map<String, String> params,Map<String, String> headers) throws URISyntaxException,
            ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost postMethod = buildXMLHttpPost(url, params,headers);
		HttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	public static String simplePostXMLInvoke(String url,
			Map<String, String> params) throws URISyntaxException,
            ClientProtocolException, IOException {
		
		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost postMethod = buildXMLHttpPost(url, params,null);
		HttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}
	

	/**
	 * 带证书的post调用
	 * 
	 * @param url
	 * @param params
	 * @param keyPath
	 * @param keyPassword
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws URISyntaxException
	 */
	public static String simpleSSlKeyPostXMLInvoke(String url,
			Map<String, String> params, String keyPath, String keyPassword)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, KeyManagementException,
			UnrecoverableKeyException, URISyntaxException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(keyPath));
		try {
			keyStore.load(instream, keyPassword.toCharArray());
		} finally {
			instream.close();
		}
		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf).build();
		HttpPost httppost = buildXMLHttpPost(url, params,null);
		CloseableHttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}

	public static HttpPost buildJSONStringHttpPost(String url, String requestJson, Map<String, String> headers) {
		HttpPost post = new HttpPost(url);
		// 设置请求和传输超时时间
		post.setConfig(generateRequestConfig());
		post.setHeader(HTTP.CONTENT_ENCODING, UTF8_CHARSET);
		if (requestJson != null) {
			StringEntity entry = new StringEntity(requestJson, UTF8_CHARSET);
			entry.setContentEncoding(UTF8_CHARSET);
			post.setEntity(entry);
		}
		// 设置header
		if (!MapUtil.isEmpty(headers)) {
			for (Entry<String, String> kv : headers.entrySet()) {
				if (StringUtils.isNotEmpty(kv.getKey())) {
					//logger.info("params key : " + kv.getKey() + " , value : " + kv.getValue());
					post.setHeader(kv.getKey(), kv.getValue());
				}
			}
		}
		return post;
	}
	
	/**
	 * json 字符串请求
	 * @param url
	 * @param params
	 * @param headers
	 * @return
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String simpleBuildJSONStringHttpPost(String url,
			String params,Map<String, String> headers) throws URISyntaxException,
            ClientProtocolException, IOException {

		HttpClient client = buildHttpClient(false);
		// 创建post
		HttpPost postMethod = buildJSONStringHttpPost(url, params,headers);
		HttpResponse response = client.execute(postMethod);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			return EntityUtils.toString(entity, UTF8_CHARSET);
		}
		return StringUtils.EMPTY;
	}

	public static String getRealIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress != null && (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1"))) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}
