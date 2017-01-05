package com.VideoCalling.sample.groupchatwebrtc.util;

import com.google.gson.Gson;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class performs all the httpconnections between android app and
 * web-services. The data receiving and send is also handled in this class. Json
 * data is being received in this class. 
 * 
 * Created by Harsha on 14/12/2015.
 */
public class HttpConnection {

	public HttpURLConnection makeHttpConnection(String url, String method) {

		// String userJson = gson.toJson(pojoObject);

		HttpURLConnection con = null;
		System.out.println("Test 1");
		// Create connection
		try {
			con = (HttpURLConnection) (new URL(url)).openConnection();
			con.setRequestMethod(method);
			if (method.equalsIgnoreCase("POST")) {
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Accept", "application/json");

			}
			con.setRequestProperty("Content-Type", "application/json");

			System.out.println("Connection Information:::>>>" + con.getRequestMethod());
			return con;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String acceptJSON(HttpURLConnection httpURLConnection) throws HttpFailureException {
		// HttpURLConnection httpURLConnection = makeHttpConnection(url,method);
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpURLConnection.getInputStream()));
			String line;
			StringBuffer stringBufferResponse = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				stringBufferResponse.append(line);
			}
			System.out.println("Response from Server::>>" + stringBufferResponse.toString());
			return stringBufferResponse.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
	}

	public String sendAcceptJSON(HttpURLConnection httpURLConnection, Object pojoObject) throws HttpFailureException {
		// HttpURLConnection httpURLConnection = makeHttpConnection(url,method);
		try {
			// sending a JSON object as string as a request

			// writing json request
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(new Gson().toJson(pojoObject).getBytes());

			// reading json response
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpURLConnection.getInputStream()));
			String line;
			StringBuffer stringBufferResponse = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				stringBufferResponse.append(line);
			}

			// responseUser = gson.fromJson(stringBufferResponse.toString(),
			// User.class);
			System.out.println("Check Check Check:::>>>>" + stringBufferResponse.toString());

			if (httpURLConnection.getResponseCode() == 400) {
				throw new HttpFailureException("Failed Request");
			}
			return stringBufferResponse.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
				return null;
			}
		}
	}

	/*
	 * public HttpConnection() {
	 * 
	 * }
	 * 
	 *//**
		 * This methods connects with the server/ restful web-service and will
		 * accept any response that is being returned through that url/ restful
		 * web-service. It reads the response from the server and based on the
		 * response checks if the call to the server was successful. It converts
		 * the response from the server to a string and then to a JSON.
		 * 
		 * @param url
		 * @param method
		 * @return
		 * @throws HttpFailureException
		 *//*
		 * public String acceptJSON(String url, String method) throws
		 * HttpFailureException { HttpURLConnection httpURLConnection =
		 * makeHttpConnection(url,method); try { BufferedReader bufferedReader =
		 * new BufferedReader(new
		 * InputStreamReader(httpURLConnection.getInputStream())); String line;
		 * StringBuffer stringBufferResponse = new StringBuffer(); while((line =
		 * bufferedReader.readLine()) != null) {
		 * stringBufferResponse.append(line); } System.out.println(
		 * "Response Products"+ stringBufferResponse.toString()); return
		 * stringBufferResponse.toString(); } catch (IOException e) {
		 * e.printStackTrace(); return null; }finally { if (httpURLConnection !=
		 * null){ httpURLConnection.disconnect(); } } }
		 * 
		 * public void sendJSON(String url, String method, Object pojoObject)
		 * throws HttpFailureException { HttpURLConnection httpURLConnection =
		 * makeHttpConnection(url,method); try { OutputStream os =
		 * httpURLConnection.getOutputStream(); os.write(new
		 * Gson().toJson(pojoObject).getBytes());
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }finally { if
		 * (httpURLConnection != null){ httpURLConnection.disconnect(); } }
		 * 
		 * }
		 * 
		 * public String sendAcceptJSON(String url, String method, Object
		 * pojoObject) throws HttpFailureException { HttpURLConnection
		 * httpURLConnection = makeHttpConnection(url,method); try { //sending a
		 * JSON object as string as a request
		 * 
		 * //writing json request OutputStream outputStream =
		 * httpURLConnection.getOutputStream(); outputStream.write(new
		 * Gson().toJson(pojoObject).getBytes());
		 * 
		 * //reading json response BufferedReader bufferedReader = new
		 * BufferedReader(new
		 * InputStreamReader(httpURLConnection.getInputStream())); String line;
		 * StringBuffer stringBufferResponse = new StringBuffer(); while((line =
		 * bufferedReader.readLine()) != null) {
		 * stringBufferResponse.append(line); }
		 * 
		 * // responseUser = gson.fromJson(stringBufferResponse.toString(),
		 * User.class); System.out.println("Check Check Check:::>>>>"
		 * +stringBufferResponse.toString());
		 * 
		 * if (httpURLConnection.getResponseCode() == 400){ throw new
		 * HttpFailureException("Failed Request"); } return
		 * stringBufferResponse.toString(); } catch (IOException e) {
		 * e.printStackTrace(); return null; } finally { if (httpURLConnection
		 * != null){ httpURLConnection.disconnect(); } } }
		 * 
		 * private HttpURLConnection makeHttpConnection(String url,String
		 * method) { HttpURLConnection con = null; System.out.println("Test 1");
		 * //Create connection try { con = (HttpURLConnection) ( new
		 * URL(url)).openConnection(); con.setRequestMethod(method);
		 * con.setDoOutput(true); con.setDoInput(true);
		 * 
		 * if (method.equalsIgnoreCase("POST")) {
		 * con.setRequestProperty("Accept", "application/json"); }
		 * con.setRequestProperty("Content-Type", "application/json");
		 * 
		 * return con; } catch (IOException e) { e.printStackTrace(); return
		 * null; } }
		 */
}
