package com.VideoCalling.sample.groupchatwebrtc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Harsha on 11/12/2015.
 */
public class HttpConnectionsTest {

    public HttpURLConnection makeHttpConnection(String url, String method) {

        //  String userJson = gson.toJson(pojoObject);

        HttpURLConnection con = null;
        System.out.println("Test 1");
        //Create connection
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod(method);
            if (method.equalsIgnoreCase("POST")) {
            	con.setDoOutput(true);
            	con.setDoInput(true);
            	con.setRequestProperty("Accept", "application/json");
			}
            con.setRequestProperty("Content-Type", "application/json");

            System.out.println("Connection Information:::>>>"+con.getRequestMethod());
            return con;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String acceptJSON(HttpURLConnection httpURLConnection) throws HttpFailureException {
        //HttpURLConnection httpURLConnection = makeHttpConnection(url,method);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            StringBuffer stringBufferResponse = new StringBuffer();
            while((line = bufferedReader.readLine()) != null) {
                stringBufferResponse.append(line);
            }
            System.out.println("Response from Server::>>"+ stringBufferResponse.toString());
            return stringBufferResponse.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
    }
    
    public String sendAcceptJSON(HttpURLConnection httpURLConnection, Object pojoObject) throws HttpFailureException {
        //HttpURLConnection httpURLConnection = makeHttpConnection(url,method);
        try {
            //sending a JSON object as string as a request

            //writing json request
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(new Gson().toJson(pojoObject).getBytes());

            //reading json response
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            StringBuffer stringBufferResponse = new StringBuffer();
            while((line = bufferedReader.readLine()) != null) {
                stringBufferResponse.append(line);
            }

           // responseUser = gson.fromJson(stringBufferResponse.toString(), User.class);
            System.out.println("Check Check Check:::>>>>"+stringBufferResponse.toString());

            if (httpURLConnection.getResponseCode() == 400){
                throw new HttpFailureException("Failed Request");
            }
            return stringBufferResponse.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
    }
}
