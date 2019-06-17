package com.shuangling.software.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.shuangling.software.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttpUtils {
	private static Handler mHandler=new Handler(Looper.getMainLooper());
	public static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
			.readTimeout(20, TimeUnit.SECONDS).cookieJar(new CookieJar() {
				private final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

				@Override
				public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
					cookieStore.put(url.host(), cookies);
				}

				@Override
				public List<Cookie> loadForRequest(HttpUrl url) {
					List<Cookie> cookies = cookieStore.get(url.host());
					return cookies != null ? cookies : new ArrayList<Cookie>();
				}
			}).build();



	public static void post(String url, Map<String,String> params, List<File> files, final OkHttpCallback callback){
		Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if(params!=null&&params.size()>0){
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.addFormDataPart(entry.getKey(),entry.getValue()) ;
			}
		}
		for(int i=0;files!=null&&i<files.size();i++){
			File file=files.get(i);
			if(file.exists()){
				if (file.getName().endsWith(".png")) {
					RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
					String fileName = file.getName();
					builder.addFormDataPart("" + i, fileName, fileBody);
				} else if (file.getName().endsWith(".jpg")) {
					RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
					String fileName = file.getName();
					builder.addFormDataPart("" + i, fileName, fileBody);
				} else if (file.getName().endsWith(".mp4")) {
					RequestBody fileBody = RequestBody.create(MediaType.parse("video/mp4"), file);
					String fileName = file.getName();
					builder.addFormDataPart(""+i, fileName, fileBody);
				}else if (file.getName().endsWith(".mp3")) {
					RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mp3"), file);
					String fileName = file.getName();
					builder.addFormDataPart(""+i, fileName, fileBody);
				}else {
					RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
					String fileName = file.getName();
					builder.addFormDataPart("" + i, fileName, fileBody);
				}
			}
		}
		MultipartBody body =builder.build();
		Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(url).addHeader("Authorization", "-1").post(body).build();
		}else {
			request= new Request.Builder().url(url).addHeader("Authorization", User.getInstance().getAuthorization()).post(body).build();
		}

		okHttpClient.newCall(request).enqueue(new Callback() {


			@Override
			public void onResponse(Call call, Response response) throws IOException {
				int code=response.code();
				callback.onResponse(call,response);

			}

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}
		});

	}



	public static void postWithFile(String url, Map<String, String> textMap, Map<String, String> fileMap, final OkHttpCallback callback){
		Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if(textMap!=null&&textMap.size()>0){
			for (Map.Entry<String, String> entry : textMap.entrySet()) {
				builder.addFormDataPart(entry.getKey(),entry.getValue()) ;
			}
		}
		if(fileMap!=null&&fileMap.size()>0){
			for (Map.Entry<String, String> entry : fileMap.entrySet()) {
				String key=entry.getKey();
				String value=entry.getValue();
				File file=new File(value);
				if(file.exists()){
					if (file.getName().endsWith(".png")) {
						RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
						String fileName = file.getName();
						builder.addFormDataPart(key, fileName, fileBody);
					} else if (file.getName().endsWith(".jpg")) {
						RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
						String fileName = file.getName();
						builder.addFormDataPart(key, fileName, fileBody);
					} else if (file.getName().endsWith(".mp4")) {
						RequestBody fileBody = RequestBody.create(MediaType.parse("video/mp4"), file);
						String fileName = file.getName();
						builder.addFormDataPart(key, fileName, fileBody);
					}else if (file.getName().endsWith(".mp3")) {
						RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mp3"), file);
						String fileName = file.getName();
						builder.addFormDataPart(key, fileName, fileBody);
					}else {
						RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
						String fileName = file.getName();
						builder.addFormDataPart(key, fileName, fileBody);
					}

				}
			}
		}


		MultipartBody body =builder.build();
		Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(url).addHeader("Authorization", "-1").post(body).build();
		}else {
			request= new Request.Builder().url(url).addHeader("Authorization", User.getInstance().getAuthorization()).post(body).build();
		}

		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				int code=response.code();


				callback.onResponse(call,response);

			}
		});
	}




	public static void post(String url, Map<String,String> params, final OkHttpCallback callback){
		FormBody.Builder builder = new FormBody.Builder();
		if(params!=null&&params.size()>0){
			for (Map.Entry<String, String> entry : params.entrySet()) {
				builder.add(entry.getKey(),entry.getValue()) ;
			}
		}
		FormBody body = builder.build();
		Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(url).addHeader("Authorization", "-1").post(body).build();
		}else {
			request= new Request.Builder().url(url).addHeader("Authorization", User.getInstance().getAuthorization()).post(body).build();
		}
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				int code=response.code();


				callback.onResponse(call,response);

			}
		});
	}

	



	public static void get(String url, Map<String,String> params, final OkHttpCallback callback){
		String newUrl=url;
		if(params!=null&&params.size()>0){
			newUrl+="?";
			for (Map.Entry<String, String> entry : params.entrySet()) {
				newUrl+=(entry.getKey()+"="+entry.getValue());
				newUrl+="&";
			}
		}
		if(newUrl.endsWith("&")){
			newUrl=newUrl.substring(0, newUrl.length()-1);
		}

		Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(newUrl).addHeader("Authorization", "-1").get().build();
		}else {
			request= new Request.Builder().url(newUrl).addHeader("Authorization", User.getInstance().getAuthorization()).get().build();
		}
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				int code=response.code();


				callback.onResponse(call,response);

			}
		});

	}
	

	
	
	
}
