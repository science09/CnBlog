package com.science09.cnblogs;

import java.util.ArrayList;
import java.util.List;

import com.science09.cnblogs.model.Blog;
import com.science09.cnblogsapp.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 此页包含作者的博客页
 *
 */
public class AuthorBlogActivity extends Activity{
	List<Blog> listBlog = new ArrayList<Blog>();

	int pageIndex=1; //页码
	ListView listView;
	
	ProgressBar blogBody_progressBar;//加载
	ImageButton blog_refresh_btn;//刷新按钮
	private Button blog_button_back;//返回
	ProgressBar blog_progress_bar;//加载按钮
	
	private LinearLayout viewFooter;//footer view
	TextView tvFooterMore;//底部更多显示
	ProgressBar list_footer_progress;//底部进度条
	
	private String author;//博主用户名
	private String blogName;//博客名
	private int blogCount;//博客数量 

	Button btn_rss;//订阅按钮
	private int lastItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.author_blog_layout);	 
	}	
		
}
