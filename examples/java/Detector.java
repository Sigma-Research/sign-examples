
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Detector {

    public static final int CONNECTION_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;

	private static final String APP_KEY = "<your appKey>";
	private static final String APP_SECRET = "<your appSercet>";

	private static String readFileAsBase64(String filename){
        File originalFile = new File(filename);
        String encodedBase64 = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
            byte[] bytes = new byte[(int)originalFile.length()];
            fileInputStreamReader.read(bytes);
            encodedBase64 = new String(Base64.encodeBase64(bytes));
            return encodedBase64;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return encodedBase64;
    }

	private static String md5(String str){
      	MessageDigest md = null;;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}
        return sb.toString();
    }

	private static String getSaltString(int len) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < len) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static String sendRequest() {

        String error = null;
        try {
        	String urlStr = "http://rbs.hexinedu.com/api/open/task/detect";
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(urlStr);


            long timestamp = System.currentTimeMillis();
            String nonstr = getSaltString(8);
            String appSign = md5(APP_KEY+":"+APP_SECRET+":"+nonstr+":"+timestamp);
            //
            // 设置参数
            client.getParams().setContentCharset("UTF-8");
            client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEOUT);
            client.getHttpConnectionManager().getParams().setSoTimeout(READ_TIMEOUT);
            method.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    		method.setRequestHeader("app-timestamp", String.valueOf(timestamp));
            method.setRequestHeader("app-key", APP_KEY);
            method.setRequestHeader("app-nonstr", nonstr);
            method.setRequestHeader("app-signature", appSign);
            //
            // 0或1均为简体中文，其他值不支持
            // 设置post数据
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("type", "express");
            jsonParam.put("data", readFileAsBase64("../test.jpg"));

            StringRequestEntity sre = new StringRequestEntity(jsonParam.toString(), "application/json", "UTF-8");
            method.setRequestEntity(sre);
            //
            // 发送post请求
            client.executeMethod(method);

            //
            // 获取返回结果并转码
            String responseDefaultCharset = method.getResponseBodyAsString();
            String charSet = method.getResponseCharSet();
            String msgUtf8 = new String(responseDefaultCharset.getBytes(charSet), "UTF-8");
            System.out.println(msgUtf8);
            return msgUtf8;

        } catch (HttpException e) {
            e.printStackTrace();
            error = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return "";
    }

	public static void main(String[] args) throws Exception {
		Detector detector = new Detector();
		System.out.println(" Send Http post request!");
		detector.sendRequest();
	}
}
