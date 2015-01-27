package com.science09.cnblogs;

import java.util.ArrayList;
import java.util.List;

import com.ab.task.AbTask;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListListener;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.science09.cnblogs.adapter.BlogListAdapter;
import com.science09.cnblogs.config.Config;
import com.science09.cnblogs.core.BlogHelper;
import com.science09.cnblogs.dal.BlogDalHelper;
import com.science09.cnblogs.model.Blog;
import com.science09.cnblogs.utils.NetUtils;
import com.science09.cnblogs.view.EmptyLayout;
import com.science09.cnblogsapp.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlogFragment extends Fragment implements OnHeaderRefreshListener,
		OnFooterLoadListener {
	public static final String TAG = "BlogFragment";
	List<Blog> mListBlog = new ArrayList<Blog>();
	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;
	private BlogListAdapter adapter;// 数据源
	private EmptyLayout mEmptyLayout;
	BlogDalHelper dbHelper;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		
		// 注册广播
		UpdateListViewReceiver updateReceiver = new UpdateListViewReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.science09.cnblogs.update_bloglist");
		mContext.registerReceiver(updateReceiver, filter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.blog_layout, container, false);
		mAbPullToRefreshView = (AbPullToRefreshView) view
				.findViewById(R.id.mPullRefreshView);
		mListView = (ListView) view.findViewById(R.id.blog_list);
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		dbHelper = new BlogDalHelper(mContext);
		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				RedirectDetailActivity(v);
			}
		});
		
		return view;
	}
	
	/*
	 * 跳转到详情界面
	 */
	private void RedirectDetailActivity(View v) {
		Intent intent = new Intent();
		try {
			// 传递参数
			intent.setClass(mContext, BlogDetailActivity.class);
			Bundle bundle = new Bundle();
			TextView tvBlogId = (TextView) (v
					.findViewById(R.id.recommend_text_id));
			TextView tvBlogTitle = (TextView) (v
					.findViewById(R.id.recommend_text_title));
			TextView tvBlogAuthor = (TextView) (v
					.findViewById(R.id.recommend_text_author));
			TextView tvBlogDate = (TextView) (v
					.findViewById(R.id.recommend_text_date));
			TextView tvBlogUrl = (TextView) (v
					.findViewById(R.id.recommend_text_url));
			TextView tvBlogViewCount = (TextView) (v
					.findViewById(R.id.recommend_text_view));
			TextView tvBlogCommentCount = (TextView) (v
					.findViewById(R.id.recommend_text_comments));
			TextView tvBlogDomain = (TextView) (v
					.findViewById(R.id.recommend_text_domain));

			int blogId = Integer.parseInt(tvBlogId.getText().toString());
			String blogTitle = tvBlogTitle.getText().toString();
			String blogAuthor = tvBlogAuthor.getText().toString();
			String blogDate = tvBlogDate.getText().toString();
			String blogUrl = tvBlogUrl.getText().toString();
			String blogDomain = tvBlogDomain.getText().toString();
			int viewsCount = Integer.parseInt(tvBlogViewCount.getText()
					.toString());
			int commentCount = Integer.parseInt(tvBlogCommentCount.getText()
					.toString());

			bundle.putInt("blogId", blogId);
			bundle.putString("blogTitle", blogTitle);
			bundle.putString("author", blogAuthor);
			bundle.putString("date", blogDate);
			bundle.putString("blogUrl", blogUrl);
			bundle.putInt("view", viewsCount);
			bundle.putInt("comment", commentCount);
			bundle.putString("blogDomain", blogDomain);

			Log.d("blogId", String.valueOf(blogId));
			intent.putExtras(bundle);

			startActivity(intent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		new PageTask(0, true).execute();
	}

	public class PageTask extends AsyncTask<String, Integer, List<Blog>> {
		boolean isRefresh = false;
		int curPageIndex = 0;
		boolean isLocalData = false; // 是否是从本地读取的数据

		public PageTask(int page, boolean isRefresh) {
			curPageIndex = page;
			this.isRefresh = isRefresh;
		}

		@Override
		protected List<Blog> doInBackground(String... arg0) {
			boolean isNetworkAvailable = NetUtils
					.networkIsAvailable(getActivity().getApplicationContext());
			int _pageIndex = curPageIndex;
			if (_pageIndex <= 0) {
				_pageIndex = 1;
			}
			// 优先读取本地数据
			List<Blog> listBlogLocal = dbHelper.GetBlogListByPage(_pageIndex,
					Config.BLOG_PAGE_SIZE);		
			Log.d(TAG, "List Size: "+ listBlogLocal.size());
			
			if (isNetworkAvailable) {
				List<Blog> listBlogNew = BlogHelper.GetBlogList(_pageIndex);
				switch(curPageIndex){
				case -1: //上拉
					List<Blog> listTmp = new ArrayList<Blog>();
					if(listBlogNew != null && listBlogNew.size() > 0){
						int size = listBlogNew.size();
						for(int i = 0; i < size; i++){
							if(!mListBlog.contains(listBlogNew.get(i))){
								listTmp.add(listBlogNew.get(i));
							}
						}
					}
					return listTmp;
				case 0: // 首次加载
				case 1: // 刷新
					if (listBlogNew != null && listBlogNew.size() > 0) {
						return listBlogNew;
					}
					break;
				default:
					List<Blog> listT = new ArrayList<Blog>();
					if (mListBlog != null && mListBlog.size() > 0) { // 避免首页无数据时
						if (listBlogNew != null && listBlogNew.size() > 0) {
							int size = listBlogNew.size();
							for (int i = 0; i < size; i++) {
								if (!mListBlog.contains(listBlogNew.get(i))) { // 避免出现重复
									listT.add(listBlogNew.get(i));
								}
							}
						}
					}
					return listT;
				}
			} else {
				isLocalData = true;
				if (curPageIndex == -1) { //上拉不加载数据
					return null;
				}
				return listBlogLocal;
			}
			return listBlogLocal;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(List<Blog> result) {
			// 网络不可用，并且本地没有缓存数据
			if (result == null || result.size() == 0) {
				mAbPullToRefreshView.onHeaderRefreshFinish();				
				if (!NetUtils.networkIsAvailable(mContext)) {
					if (curPageIndex == 0) {
						mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
					} else if (curPageIndex > 1) {
						Toast.makeText(null, R.string.tip_network_error,
								Toast.LENGTH_SHORT).show();
					}
				}
				return;
			}
			int size = result.size();
			if(size >= Config.BLOG_PAGE_SIZE && mListView.getFooterViewsCount() == 0){
				Log.d(TAG, "size >= Config.BLOG_PAGE_SIZE");
			}		
			// 保存到数据库
			if(!isLocalData){
				dbHelper.SynchronyData2DB(result);
			}
			if(curPageIndex == -1) { //上拉刷新
				adapter.InsertData(result);
			}else if(curPageIndex == 0) { //首次加载
				mListBlog = result;
				mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
				adapter = new BlogListAdapter(mContext, result, mListView);
				mListView.setAdapter(adapter);
			}else if(curPageIndex == 1){
				
			}else {
				adapter.AddMoreData(result);
			}
			
		}

		@Override
		protected void onPreExecute() {
			// 设置EmptyLayout为加载状态
			if (mListView.getCount() == 0 && curPageIndex == 0) {
				Log.d(TAG, "首次加载的情况");
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
			}
			Log.d(TAG, "OnPreExecute....");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	@Override
	public void onFooterLoad(AbPullToRefreshView view) {
		
	}

	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
			refreshData();
	}

	private void refreshData() {
		AbTask mAbTask = new AbTask();
		final AbTaskItem mItem = new AbTaskItem();
		mItem.setListener(new AbTaskListListener() {
			
			@Override
			public void update(List<?> paramList) {
				List<Blog> newList = (List<Blog>) paramList;
				mListBlog.clear();
				if(newList != null && newList.size() > 0) {
					mListBlog.addAll(newList);
					adapter.notifyDataSetChanged();
					newList.clear();
				}
				mAbPullToRefreshView.onHeaderRefreshFinish();
			}
			
			@Override
			public List<?> getList() {
				List<Blog> newList = null;
				
				newList = BlogHelper.GetBlogList(0);
				return newList;
			}
		});
		
		mAbTask.execute(mItem);
	}
	
	
	/*
	 * 更新博客为已读的广播, BlogDetail发送过来的广播消息
	 */
	public class UpdateListViewReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d(TAG, "接收到已读的广播消息");
			int[] blogIdArray = intent.getExtras().getIntArray("blogIdArray");
			for(int i = 0; i < mListView.getChildCount(); i++){
				View view = mListView.getChildAt(i);			
				TextView tvId = (TextView) view.findViewById(R.id.recommend_text_id);
				if(tvId != null){
					
				}		
			}
			for(int i = 0; i < blogIdArray.length; i++){
				for(int j = 0; j < mListBlog.size(); j++){
					if(blogIdArray[i] == mListBlog.get(j).GetBlogId()){
						mListBlog.get(j).SetIsFullText(true);
						mListBlog.get(j).SetIsReaded(true);
					}
				}
			}
		}
		
	}
}
