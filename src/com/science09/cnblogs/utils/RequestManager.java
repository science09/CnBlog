package com.science09.cnblogs.utils;

import com.ab.network.toolbox.RequestQueue;
import com.ab.network.toolbox.Volley;

import android.content.Context;

/**
 * 通用的网络请求队列，统一使用一个队列，免得在每个文件都创建一个队列，不方便管理
 * 
 * @author 10124143
 *
 */
public class RequestManager {
	//Volley的网络请求队列
	private static RequestQueue mRequestQueue;
	
	private RequestManager() {
		 // no instances
	} 
	
	//创建网络请求队列
	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}
	
	public static RequestQueue getRequestQueue() {
	    if (mRequestQueue != null) {
	        return mRequestQueue;
	    } else {
	        throw new IllegalStateException("Not initialized");
	    }
	}
}
