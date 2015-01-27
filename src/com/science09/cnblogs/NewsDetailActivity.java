package com.science09.cnblogs;

import java.io.InputStream;
import com.ab.network.StringRequest;
import com.ab.network.toolbox.Response;
import com.ab.network.toolbox.Response.ErrorListener;
import com.ab.network.toolbox.VolleyError;
import com.science09.cnblogs.config.Config;
import com.science09.cnblogs.core.NewsHelper;
import com.science09.cnblogs.utils.AppUtil;
import com.science09.cnblogs.utils.NetUtils;
import com.science09.cnblogs.utils.RequestManager;
import com.science09.cnblogs.view.EmptyLayout;
import com.science09.cnblogsapp.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewsDetailActivity extends Activity implements OnClickListener,
		OnTouchListener, OnGestureListener {
	private int newsId; // 博客编号
	private String newsTitle; // 标题
	private String newsDate; // 发表时间
	private String newsUrl; // 文章链接
	private int newsViews; // 浏览次数
	private int newsComemnt; // 评论次数
	static final int I_MENU_BACK = Menu.FIRST;// 返回
	static final int I_MENU_REFRESH = Menu.FIRST + 1;// 刷新
	static final int I_MENU_COMMENT = Menu.FIRST + 2;// 查看评论
	static final int I_MENU_VIEW_BROWSER = Menu.FIRST + 3;// 查看网页
	static final int I_MENU_SHARE = Menu.FIRST + 4;// 分享到
	private LinearLayout mLinearLayout;
	private Button comment_btn;
	private Button back_btn;
	private TextView mTextView;
	private WebView mWebView;
	private EmptyLayout mEmptyLayout;
	private GestureDetector gestureScanner;// 手势
	private SharedPreferences mSharePre;
	private boolean isFullScreen = false; // 是否全屏

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_detail);
		mSharePre = getSharedPreferences(
				getResources().getString(R.string.preferences_key),
				MODE_PRIVATE);
		initView();
		initWebView();
	}

	private void initView() {
		newsId = Integer.parseInt(getIntent().getStringExtra("newsId"));
		newsTitle = getIntent().getStringExtra("newsTitle");
		newsDate = getIntent().getStringExtra("date");
		newsUrl = getIntent().getStringExtra("newsUrl");
		newsViews = getIntent().getIntExtra("view", 0);
		newsComemnt = getIntent().getIntExtra("comment", 0);
		mTextView = (TextView) findViewById(R.id.txtAppTitle);
		mTextView.setText("消息");
		// 打开评论
		comment_btn = (Button) findViewById(R.id.comment_btn);
		String commentsCountString = (newsComemnt == 0) ? "暂无" : newsComemnt
				+ "条";
		comment_btn.setText(commentsCountString + "评论");
		comment_btn.setOnClickListener(this);
		back_btn = (Button) findViewById(R.id.button_back);
		back_btn.setOnClickListener(this);
		mLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
		mLinearLayout.setOnTouchListener(this);
	}

	@SuppressLint("NewApi")
	private void initWebView() {
		mWebView = (WebView) findViewById(R.id.news_body_webview_content);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		// mWebView.addJavascriptInterface(this, "javatojs");
		mWebView.setScrollBarStyle(0);
		WebSettings webSetting = mWebView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setNeedInitialFocus(false);
		webSetting.setSupportZoom(true);
		webSetting.setCacheMode(WebSettings.LOAD_DEFAULT
				| WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.setOnTouchListener(this);
		mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); //关闭硬件加速
		// 上一次保存的缩放比例
		int scalePercent = 110;
		float webviewScale = mSharePre.getFloat(
				getResources().getString(
						R.string.preferences_webview_zoom_scale), (float) 1.1);
		scalePercent = (int) (webviewScale * 100);
		mWebView.setInitialScale(scalePercent);
		String url = Config.URL_GET_BLOG_DETAIL.replace("{0}",
				String.valueOf(newsId)); // 网址
		mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
		StringRequest strReq = new StringRequest(url, responseListener(),
				errorResponse());
		RequestManager.getRequestQueue().add(strReq);
		mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

		gestureScanner = new GestureDetector(this);
		gestureScanner
				.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

					@Override
					public boolean onSingleTapConfirmed(MotionEvent arg0) {
						return false;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent arg0) {
						return false;
					}

					@Override
					public boolean onDoubleTap(MotionEvent event) {
						if (!isFullScreen) {
							setFullScreen();
						} else {
							quitFullScreen();
						}
						mSharePre.edit().putBoolean(
								getResources().getString(
										R.string.preferences_is_fullscreen),
								isFullScreen);

						return false;
					}
				});
		isFullScreen = mSharePre.getBoolean(
				getResources().getString(R.string.preferences_is_fullscreen),
				false);
		if (isFullScreen) {
			setFullScreen();
		}
	}

	private Response.Listener<String> responseListener() {
		return new Response.Listener<String>() {
			@Override
			public void onResponse(String _newsContent) {
				_newsContent = NewsHelper.ParseNewsString(_newsContent);
				String htmlContent = "";
				try {
					InputStream in = getAssets().open("NewsDetail.html");
					byte[] temp = NetUtils.readInputStream(in);
					htmlContent = new String(temp);
				} catch (Exception e) {
					Log.e("error", e.toString());
				}
				_newsContent = AppUtil.FormatContent(getBaseContext(),
						_newsContent);
				String newsInfo = "发表时间:" + newsDate + " 查看:" + newsViews;
				htmlContent = htmlContent.replace("#title#", newsTitle)
						.replace("#time#", newsInfo)
						.replace("#content#", _newsContent);
				mEmptyLayout.setVisibility(View.GONE);
				LoadWebViewContent(mWebView, htmlContent);
			}
		};
	}

	private void LoadWebViewContent(WebView webView, String content) {
		webView.loadDataWithBaseURL(Config.LOCAL_PATH, content, "text/html",
				Config.ENCODE_TYPE, null);
	}

	private ErrorListener errorResponse() {
		return new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(NewsDetailActivity.this, "网络访问出错!",
						Toast.LENGTH_SHORT).show();
			}
		};
	}

	private void OnDoubleTapListene() {
		if (!isFullScreen) {
			setFullScreen();
		} else {
			quitFullScreen();
		}
		isFullScreen = !isFullScreen;
	}

	/*
	 * 退出全屏
	 */
	private void quitFullScreen() {
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		getWindow()
				.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		// 显示导航
		mLinearLayout.setVisibility(View.VISIBLE);
	}

	/*
	 * 进入全屏模式
	 */
	private void setFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 隐藏导航
		mLinearLayout.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.comment_btn:
			RedirectCommentActivity();
			break;
		case R.id.button_back:
			NewsDetailActivity.this.finish();
			break;
		}

	}

	private void RedirectCommentActivity() {
		//还没有评论
		if(newsComemnt==0){
			Toast.makeText(getApplicationContext(), R.string.sys_empty_comment, Toast.LENGTH_SHORT).show();
			return;
		}
		
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		return gestureScanner.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent event, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent event) {

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent event, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
}
