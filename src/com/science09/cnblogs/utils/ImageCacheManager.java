package com.science09.cnblogs.utils;

import com.ab.image.toolbox.ImageLoader;
import com.ab.image.toolbox.ImageLoader.ImageCache;
import com.ab.image.toolbox.ImageLoader.ImageListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class ImageCacheManager {
	
	//Õ¯¬Áª∫¥Ê«Î«Û¿‡–Õ: SDø®ª∫¥Ê∫Õƒ⁄¥Êª∫¥Ê
	public enum CacheType {
		DISK, MEMORY
	}
	private static ImageCacheManager mInstance;
	private ImageLoader mImageLoader;  //Volley Image Loarder
	private ImageCache  mImageCache;
	
	public ImageCacheManager() {
		
	}
	
	public static ImageCacheManager getInstance(){
		if(mInstance == null)
			mInstance = new ImageCacheManager();		
		return mInstance;
	}
	public void init(Context context, String uniqueName, int cacheSize, CompressFormat compressFormat, int quality, CacheType type){
		switch (type) {
		case DISK:
			mImageCache= new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
			break;
		case MEMORY:
			mImageCache = new BitmapLruImageCache(cacheSize);
		default:
			mImageCache = new BitmapLruImageCache(cacheSize);
			break;
		}
		
		mImageLoader = new ImageLoader(RequestManager.getRequestQueue(), mImageCache);
	}
	
	public Bitmap getBitmap(String url) {
		try {
			return mImageCache.getBitmap(createKey(url));
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}

	public void putBitmap(String url, Bitmap bitmap) {
		try {
			mImageCache.putBitmap(createKey(url), bitmap);
		} catch (NullPointerException e) {
			throw new IllegalStateException("Disk Cache Not initialized");
		}
	}
	
	public void getImage(String url, ImageListener listener){
		mImageLoader.get(url, listener);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}
	
	
	private String createKey(String url){
		return String.valueOf(url.hashCode());
	}
}
