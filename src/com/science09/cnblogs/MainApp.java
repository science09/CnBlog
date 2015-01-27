package com.science09.cnblogs;

import com.science09.cnblogs.utils.ImageCacheManager;
import com.science09.cnblogs.utils.ImageCacheManager.CacheType;
import com.science09.cnblogs.utils.ImageLoad;
import com.science09.cnblogs.utils.RequestManager;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;

public class MainApp extends Application {
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;   //缓存到SD卡上的空间块大小
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	private static int DISK_IMAGECACHE_QUALITY = 100; //PNG图片保存像素	
	
	@Override
	public void onCreate() {
		super.onCreate();
		RequestManager.init(this);
		ImageLoad.init(this);
		createImageCache();
	}

	private void createImageCache() {
		ImageCacheManager.getInstance().init(this,
				this.getPackageCodePath()
				, DISK_IMAGECACHE_SIZE
				, DISK_IMAGECACHE_COMPRESS_FORMAT
				, DISK_IMAGECACHE_QUALITY
				, CacheType.MEMORY);	
	}	
	
}
