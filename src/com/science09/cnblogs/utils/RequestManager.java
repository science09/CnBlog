package com.science09.cnblogs.utils;

import com.ab.network.toolbox.RequestQueue;
import com.ab.network.toolbox.Volley;

import android.content.Context;

/**
 * ͨ�õ�����������У�ͳһʹ��һ�����У������ÿ���ļ�������һ�����У����������
 * 
 * @author 10124143
 *
 */
public class RequestManager {
	//Volley�������������
	private static RequestQueue mRequestQueue;
	
	private RequestManager() {
		 // no instances
	} 
	
	//���������������
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
