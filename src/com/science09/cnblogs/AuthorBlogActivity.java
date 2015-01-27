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
 * ��ҳ�������ߵĲ���ҳ
 *
 */
public class AuthorBlogActivity extends Activity{
	List<Blog> listBlog = new ArrayList<Blog>();

	int pageIndex=1; //ҳ��
	ListView listView;
	
	ProgressBar blogBody_progressBar;//����
	ImageButton blog_refresh_btn;//ˢ�°�ť
	private Button blog_button_back;//����
	ProgressBar blog_progress_bar;//���ذ�ť
	
	private LinearLayout viewFooter;//footer view
	TextView tvFooterMore;//�ײ�������ʾ
	ProgressBar list_footer_progress;//�ײ�������
	
	private String author;//�����û���
	private String blogName;//������
	private int blogCount;//�������� 

	Button btn_rss;//���İ�ť
	private int lastItem;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.author_blog_layout);	 
	}	
		
}
