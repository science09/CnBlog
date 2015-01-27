package com.science09.cnblogs;

import com.science09.cnblogs.utils.NetUtils;
import com.science09.cnblogs.view.EmptyLayout;
import com.science09.cnblogsapp.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends Activity implements OnClickListener{
	private WebView mWebView;
	private EmptyLayout mEmptyLayout;
	private Button mBtnBack;
	private Button mBtnComment;
	private TextView mTextView;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.web_layout);	
		if(!NetUtils.networkIsAvailable(this)){
			Toast.makeText(this, R.string.sys_network_error, Toast.LENGTH_SHORT).show();
			return;
		}
		initView();
		bindEvent();
	}

	private void bindEvent() {
		url = getIntent().getExtras().getString("url");
		String title = getIntent().getExtras().getString("title");
		mTextView.setText(title);
		mBtnBack.setOnClickListener(this);
		mWebView.findViewById(R.id.bookWebview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setScrollBarStyle(0);
		mWebView.setWebViewClient(new WebViewClient(){   
        	public boolean shouldOverrideUrlLoading(WebView view, String url) {   
                view.loadUrl(url);   
                return true;   
            }  
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebView","onPageStarted");
                super.onPageStarted(view, url, favicon);
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }    
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView","onPageFinished ");
                super.onPageFinished(view, url);
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }); 
//		mWebView.setWebViewClient(new MyWebViewClient());
		loadurl(mWebView, url);//加载网页
	}

	private void initView() {
		mBtnBack = (Button) findViewById(R.id.button_back);
		mBtnComment = (Button) findViewById(R.id.comment_btn);
		mBtnComment.setVisibility(View.INVISIBLE);
		mTextView = (TextView) findViewById(R.id.txtAppTitle);
		mTextView.setText("实用工具箱");
		mWebView = (WebView) findViewById(R.id.bookWebview);
		mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(mWebView.canGoBack()) {
				mWebView.goBack();
			}else {
				WebActivity.this.finish();
			}
		}
		
		return false;
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.button_back:
			if(mWebView.canGoBack()) {
				mWebView.goBack();
			}else {
				WebActivity.this.finish();
			}
			break;
		default:
			break;
		}
	}
	
	final class MyWebViewClient extends WebViewClient {  
        public boolean shouldOverrideUrlLoading(WebView view, String url) {   
            view.loadUrl(url);   
            return true;   
        }  
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	//网络不可用
			if(!NetUtils.networkIsAvailable(getApplicationContext())){
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
				//Toast.makeText(getApplicationContext(), R.string.sys_network_error, Toast.LENGTH_SHORT).show();
				return;
			}
            super.onPageStarted(view, url, favicon);
        }    
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView","onPageFinished ");
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, url);
        }
    }
	
	private void loadurl(final WebView view, String url){
		try{
			//网络不可用
			if(!NetUtils.networkIsAvailable(getApplicationContext())){
				Toast.makeText(getApplicationContext(), R.string.sys_network_error, Toast.LENGTH_SHORT).show();
				return;
			}
			if (mWebView.canGoBack()) {
				url = mWebView.getUrl();
			}
    		view.loadUrl(url); //载入网页
		}catch(Exception ex){
			Toast.makeText(getApplicationContext(), R.string.sys_network_error,Toast.LENGTH_SHORT).show();
			return;
		}
	}
	
	
}
