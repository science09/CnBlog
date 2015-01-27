package com.science09.cnblogs.utils;

import android.content.Context;

import com.ab.image.AbImageLoader;

/*
 * Õº∆¨œ¬‘ÿ¿‡
 */
public class ImageLoad {
	private static AbImageLoader mAbImageLoader = null;

	private ImageLoad() {
		
	}
	
	public static AbImageLoader getInstance() {
		if(mAbImageLoader != null) {
			return mAbImageLoader;
		}else {
			 throw new IllegalStateException("Not initialized");
		}
	}

	public static void init(Context context) {
		mAbImageLoader = AbImageLoader.newInstance(context);
	}
}
