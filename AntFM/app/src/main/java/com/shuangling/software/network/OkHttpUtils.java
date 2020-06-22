package com.shuangling.software.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.shuangling.software.activity.LoginActivity;
import com.shuangling.software.activity.NewLoginActivity;
import com.shuangling.software.entity.User;
import com.shuangling.software.utils.ServerInfo;
import com.shuangling.software.utils.SharedPreferencesUtils;

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



	public static void post(final String url, final Map<String,String> params, final List<File> files, final OkHttpCallback callback){
		Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if(params!=null&&params.size()>0){
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(entry.getValue()!=null){
					builder.addFormDataPart(entry.getKey(),entry.getValue()) ;
				}

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
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//
//								Map<String,String> par=new HashMap<>();
//								par.put("refresh_token",User.getInstance().getRefresh_token());
//								post(ServerInfo.serviceIP + ServerInfo.refreshTokenLogin,par,);
//
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
						postRefreshTokenLogin(url, params, files, callback);



					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}


			}

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}
		});

	}



	public static void postWithFile(final String url, final Map<String, String> textMap, final Map<String, String> fileMap, final OkHttpCallback callback){
		Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if(textMap!=null&&textMap.size()>0){
			for (Map.Entry<String, String> entry : textMap.entrySet()) {
				if(entry.getValue()!=null){
					builder.addFormDataPart(entry.getKey(),entry.getValue()) ;
				}

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
//				try{
//					int code=response.code();
//					String resp=response.body().string();
//					JSONObject jsonObject = JSONObject.parseObject(resp);
//					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
//						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
//
//					}else{
//						callback.onResponse(call,resp);
//					}
//				}catch (Exception e){
//
//				}
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//
//								Map<String,String> par=new HashMap<>();
//								par.put("refresh_token",User.getInstance().getRefresh_token());
//								post(ServerInfo.serviceIP + ServerInfo.refreshTokenLogin,par,);
//
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
						postRefreshTokenLogin(url, textMap, fileMap,callback);

					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});
	}




	public static void post(final String url, final Map<String,String> params, final OkHttpCallback callback){
		FormBody.Builder builder = new FormBody.Builder();
		if(params!=null&&params.size()>0){
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(url).addHeader("Content-Type","application/json").addHeader("Authorization", "-1").post(body).build();
		}else {
			request= new Request.Builder().url(url).addHeader("Content-Type","application/json").addHeader("Authorization", User.getInstance().getAuthorization()).post(body).build();
		}
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
//				try{
//					int code=response.code();
//					String resp=response.body().string();
//					JSONObject jsonObject = JSONObject.parseObject(resp);
//					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
//						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
//
//					}else{
//						callback.onResponse(call,resp);
//					}
//				}catch (Exception e){
//
//				}

				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {

						postRefreshTokenLogin(url,params,callback);

					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});
	}


	public static void put(final String url, final Map<String,String> params, final OkHttpCallback callback){
		FormBody.Builder builder = new FormBody.Builder();
		if(params!=null&&params.size()>0){
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		final Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(url).addHeader("Content-Type","application/json").addHeader("Authorization", "-1").put(body).build();
		}else {
			request= new Request.Builder().url(url).addHeader("Content-Type","application/json").addHeader("Authorization", User.getInstance().getAuthorization()).put(body).build();
		}
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
//				try{
//					int code=response.code();
//					String resp=response.body().string();
//					JSONObject jsonObject = JSONObject.parseObject(resp);
//					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
//						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
//
//					}else{
//						callback.onResponse(call,resp);
//					}
//				}catch (Exception e){
//
//				}


				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {

						putRefreshTokenLogin(url,params,callback);

					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});
	}



	public static void delete(final String url, final Map<String,String> params, final OkHttpCallback callback){
		FormBody.Builder builder = new FormBody.Builder();
		if(params!=null&&params.size()>0){
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request;
		if(User.getInstance()==null){
			request= new Request.Builder().url(url).addHeader("Content-Type","application/json").addHeader("Authorization", "-1").delete(body).build();
		}else {
			request= new Request.Builder().url(url).addHeader("Content-Type","application/json").addHeader("Authorization", User.getInstance().getAuthorization()).delete(body).build();
		}
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
//				try{
//					int code=response.code();
//					String resp=response.body().string();
//					JSONObject jsonObject = JSONObject.parseObject(resp);
//					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
//						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
//
//					}else{
//						callback.onResponse(call,resp);
//					}
//				}catch (Exception e){
//
//				}
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {

						deleteRefreshTokenLogin(url,params,callback);

					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});
	}



	public static void get(final String url, final Map<String,String> params, final OkHttpCallback callback){
		String newUrl=url;
		if(params!=null&&params.size()>0){
			newUrl+="?";
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(entry.getValue()!=null){
					newUrl+=(entry.getKey()+"="+entry.getValue());
					newUrl+="&";
				}

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
//				try{
//					int code=response.code();
//					String resp=response.body().string();
//					JSONObject jsonObject = JSONObject.parseObject(resp);
//					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
//						//授权失败，token过期
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								ToastUtils.show("您的登录信息已失效，请重新登录");
//								Intent it=new Intent(callback.getContext(),LoginActivity.class);
//								callback.getContext().startActivity(it);
//							}
//						});
//
//					}else{
//						callback.onResponse(call,resp);
//					}
//				}catch (Exception e){
//
//				}

				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 303001) {
						getRefreshTokenLogin(url,params,callback);
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}


			}
		});

	}


	public static void getNotAuthorization(String url, Map<String,String> params, final OkHttpCallback callback){
		String newUrl=url;
		if(params!=null&&params.size()>0){
			newUrl+="?";
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if(entry.getValue()!=null){
					newUrl+=(entry.getKey()+"="+entry.getValue());
					newUrl+="&";
				}

			}
		}
		if(newUrl.endsWith("&")){
			newUrl=newUrl.substring(0, newUrl.length()-1);
		}

		Request request= new Request.Builder().url(newUrl).get().build();

		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call, e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					callback.onResponse(call,resp);

				}catch (Exception e){
					callback.onFailure(call,e);
				}



			}
		});

	}





	public static void postRefreshTokenLogin(final String url, final Map<String,String> params, final List<File> files, final OkHttpCallback callback){
		String loginUrl = ServerInfo.serviceIP + ServerInfo.refreshTokenLogin;
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("refresh_token", User.getInstance().getRefresh_token());

		FormBody.Builder builder = new FormBody.Builder();
		if(loginParams!=null&&loginParams.size()>0){
			for (Map.Entry<String, String> entry : loginParams.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request= new Request.Builder().url(loginUrl).addHeader("Content-Type","application/json").post(body).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
						User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
						user.setRefresh_token(User.getInstance().getRefresh_token());
						user.setLogin_type(User.getInstance().getLogin_type());
						User.setInstance(user);
						SharedPreferencesUtils.saveUser(user);
						post(url, params, files, callback);

					}else if(jsonObject != null && jsonObject.getIntValue("code") == 303001){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ToastUtils.show("您的登录信息已失效，请重新登录");
								Intent it=new Intent(callback.getContext(),NewLoginActivity.class);
								callback.getContext().startActivity(it);
							}
						});
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});


	}



	public static void postRefreshTokenLogin(final String url, final Map<String, String> textMap, final Map<String, String> fileMap, final OkHttpCallback callback){
		String loginUrl = ServerInfo.serviceIP + ServerInfo.refreshTokenLogin;
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("refresh_token", User.getInstance().getRefresh_token());

		FormBody.Builder builder = new FormBody.Builder();
		if(loginParams!=null&&loginParams.size()>0){
			for (Map.Entry<String, String> entry : loginParams.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request= new Request.Builder().url(loginUrl).addHeader("Content-Type","application/json").post(body).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
						User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
						user.setRefresh_token(User.getInstance().getRefresh_token());
						user.setLogin_type(User.getInstance().getLogin_type());
						User.setInstance(user);
						SharedPreferencesUtils.saveUser(user);
						postWithFile(url,textMap,fileMap,callback);

					}else if(jsonObject != null && jsonObject.getIntValue("code") == 303001){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ToastUtils.show("您的登录信息已失效，请重新登录");
								Intent it=new Intent(callback.getContext(),NewLoginActivity.class);
								callback.getContext().startActivity(it);
							}
						});
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});


	}

	public static void postRefreshTokenLogin(final String url, final Map<String,String> params, final OkHttpCallback callback){
		String loginUrl = ServerInfo.serviceIP + ServerInfo.refreshTokenLogin;
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("refresh_token", User.getInstance().getRefresh_token());

		FormBody.Builder builder = new FormBody.Builder();
		if(loginParams!=null&&loginParams.size()>0){
			for (Map.Entry<String, String> entry : loginParams.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request= new Request.Builder().url(loginUrl).addHeader("Content-Type","application/json").post(body).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
						User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
						user.setRefresh_token(User.getInstance().getRefresh_token());
						user.setLogin_type(User.getInstance().getLogin_type());
						User.setInstance(user);
						SharedPreferencesUtils.saveUser(user);
						post(url,params,callback);

					}else if(jsonObject != null && jsonObject.getIntValue("code") == 303001){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ToastUtils.show("您的登录信息已失效，请重新登录");
								Intent it=new Intent(callback.getContext(),NewLoginActivity.class);
								callback.getContext().startActivity(it);
							}
						});
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});


	}

	public static void putRefreshTokenLogin(final String url, final Map<String,String> params, final OkHttpCallback callback){
		String loginUrl = ServerInfo.serviceIP + ServerInfo.refreshTokenLogin;
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("refresh_token", User.getInstance().getRefresh_token());

		FormBody.Builder builder = new FormBody.Builder();
		if(loginParams!=null&&loginParams.size()>0){
			for (Map.Entry<String, String> entry : loginParams.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request= new Request.Builder().url(loginUrl).addHeader("Content-Type","application/json").post(body).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
						User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
						user.setRefresh_token(User.getInstance().getRefresh_token());
						user.setLogin_type(User.getInstance().getLogin_type());
						User.setInstance(user);
						SharedPreferencesUtils.saveUser(user);
						put(url,params,callback);

					}else if(jsonObject != null && jsonObject.getIntValue("code") == 303001){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ToastUtils.show("您的登录信息已失效，请重新登录");
								Intent it=new Intent(callback.getContext(),NewLoginActivity.class);
								callback.getContext().startActivity(it);
							}
						});
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});


	}



	public static void deleteRefreshTokenLogin(final String url, final Map<String,String> params, final OkHttpCallback callback){
		String loginUrl = ServerInfo.serviceIP + ServerInfo.refreshTokenLogin;
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("refresh_token", User.getInstance().getRefresh_token());

		FormBody.Builder builder = new FormBody.Builder();
		if(loginParams!=null&&loginParams.size()>0){
			for (Map.Entry<String, String> entry : loginParams.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request= new Request.Builder().url(loginUrl).addHeader("Content-Type","application/json").post(body).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
						User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
						user.setRefresh_token(User.getInstance().getRefresh_token());
						user.setLogin_type(User.getInstance().getLogin_type());
						User.setInstance(user);
						SharedPreferencesUtils.saveUser(user);
						delete(url,params,callback);

					}else if(jsonObject != null && jsonObject.getIntValue("code") == 303001){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ToastUtils.show("您的登录信息已失效，请重新登录");
								Intent it=new Intent(callback.getContext(),NewLoginActivity.class);
								callback.getContext().startActivity(it);
							}
						});
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});


	}



	public static void getRefreshTokenLogin(final String url, final Map<String,String> params, final OkHttpCallback callback){
		String loginUrl = ServerInfo.serviceIP + ServerInfo.refreshTokenLogin;
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("refresh_token", User.getInstance().getRefresh_token());

		FormBody.Builder builder = new FormBody.Builder();
		if(loginParams!=null&&loginParams.size()>0){
			for (Map.Entry<String, String> entry : loginParams.entrySet()) {
				if(entry.getValue()!=null){
					builder.add(entry.getKey(),entry.getValue()) ;
				}

			}
		}
		FormBody body = builder.build();
		Request request= new Request.Builder().url(loginUrl).addHeader("Content-Type","application/json").post(body).build();

		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				callback.onFailure(call,e);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try{
					int code=response.code();
					String resp=response.body().string();
					JSONObject jsonObject = JSONObject.parseObject(resp);
					if (jsonObject != null && jsonObject.getIntValue("code") == 100000) {
						User user = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), User.class);
						user.setRefresh_token(User.getInstance().getRefresh_token());
						user.setLogin_type(User.getInstance().getLogin_type());
						User.setInstance(user);
						SharedPreferencesUtils.saveUser(user);
						get(url,params,callback);

					}else if(jsonObject != null && jsonObject.getIntValue("code") == 303001){
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								ToastUtils.show("您的登录信息已失效，请重新登录");
								Intent it=new Intent(callback.getContext(),NewLoginActivity.class);
								callback.getContext().startActivity(it);
							}
						});
					}else{
						callback.onResponse(call,resp);
					}
				}catch (Exception e){
					callback.onFailure(call,e);
				}

			}
		});


	}
	
	
}
