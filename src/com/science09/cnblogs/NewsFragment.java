package com.science09.cnblogs;

import java.util.ArrayList;
import java.util.List;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.science09.cnblogs.adapter.NewsListAdapter;
import com.science09.cnblogs.config.Config;
import com.science09.cnblogs.core.NewsHelper;
import com.science09.cnblogs.dal.NewsDalHelper;
import com.science09.cnblogs.model.News;
import com.science09.cnblogs.utils.NetUtils;
import com.science09.cnblogs.view.EmptyLayout;
import com.science09.cnblogsapp.R;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NewsFragment extends Fragment implements OnHeaderRefreshListener,
		OnFooterLoadListener {
	private List<News> mListNews = new ArrayList<News>();
	private ListView mListView;
	private NewsListAdapter adapter;
	private AbPullToRefreshView mAbPullRefresh;
	private EmptyLayout mEmptyLayout;
	private Context mContext;
	private NewsDalHelper mNewsDao;

	static final int MENU_DETAIL = Menu.FIRST; // 查看详细
	static final int MENU_COMMENT = Menu.FIRST + 1; // 查看评论
	static final int MENU_VIEW_BROWSER = Menu.FIRST + 2; // 在浏览器中查看
	static final int MENU_SHARE_TO = Menu.FIRST + 3; // 分享到

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		mNewsDao = new NewsDalHelper(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_layout, container, false);
		mAbPullRefresh = (AbPullToRefreshView) view
				.findViewById(R.id.mPullRefreshView);
		mListView = (ListView) view.findViewById(R.id.news_list);
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		mAbPullRefresh.setOnHeaderRefreshListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				RedirectDetailActivity(view);
			}
		});
		new PageTask(mContext, 0, true).execute();

		return view;
	}

	//上拉加载更多数据
	@Override
	public void onFooterLoad(AbPullToRefreshView view) {

	}

	//下拉刷新ListView列表
	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {

	}

	/*
	 * 跳转到详情页面
	 */
	private void RedirectDetailActivity(View mView) {
		Intent intent = new Intent();
		try {
			// 传递参数
			intent.setClass(mContext, NewsDetailActivity.class);
			Bundle bundle = new Bundle();
			TextView tvNewsId = (TextView) (mView
					.findViewById(R.id.news_text_id));
			TextView tvNewsTitle = (TextView) (mView
					.findViewById(R.id.news_text_title));
			TextView tvNewsDate = (TextView) (mView
					.findViewById(R.id.news_text_date));
			TextView tvNewsUrl = (TextView) (mView
					.findViewById(R.id.news_text_url));
			TextView tvNewsComment = (TextView) (mView
					.findViewById(R.id.news_text_comments));
			TextView tvNewsView = (TextView) (mView
					.findViewById(R.id.news_text_view));

			String newsId = tvNewsId.getText().toString();
			String newsTitle = tvNewsTitle.getText().toString();
			String newsDate = tvNewsDate.getText().toString();
			String newsUrl = tvNewsUrl.getText().toString();
			int view = Integer.parseInt(tvNewsView.getText().toString());
			int comment = Integer.parseInt(tvNewsComment.getText().toString());

			bundle.putString("newsId", newsId);
			bundle.putString("newsTitle", newsTitle);
			bundle.putString("date", newsDate);
			bundle.putString("newsUrl", newsUrl);
			bundle.putInt("view", view);
			bundle.putInt("comment", comment);
			Log.d("newsId", newsId.toString());
			intent.putExtras(bundle);

			startActivityForResult(intent, 0);
			tvNewsTitle.setTextColor(mContext.getResources().getColor(
					R.color.gray));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 异步加载网络数据
	 */
	public class PageTask extends AsyncTask<String, Integer, List<News>> {
		Context mContext;
		boolean isRefresh = false;
		int curPageIndex = 0;
		boolean isLocalData = false;// 是否是从本地读取的数据

		public PageTask(Context context, int page, boolean isRefresh) {
			mContext = context;
			curPageIndex = page;
			this.isRefresh = isRefresh;
		}

		@Override
		protected List<News> doInBackground(String... params) {
			boolean isNetworkAvailable = NetUtils.networkIsAvailable(mContext);
			int _pageIndex = curPageIndex;
			if (_pageIndex <= 0) {
				_pageIndex = 1;
			}
			// 先读取本地数据
			List<News> mListNewsLocal = mNewsDao.GetNewsListByPage(_pageIndex,
					Config.NEWS_PAGE_SIZE);
			if (isNetworkAvailable) {
				List<News> mListNewsNew = NewsHelper.GetNewsList(_pageIndex);
				switch (curPageIndex) {
				case -1: // 上拉
					List<News> listTmp = new ArrayList<News>();
					if (mListNewsNew != null && mListNewsNew.size() > 0) {
						int size = mListNewsNew.size();
						for (int i = 0; i < size; i++) {
							if (!mListNews.contains(mListNewsNew.get(i))) {
								listTmp.add(mListNewsNew.get(i));
							}
						}
					}
					return listTmp;
				case 0: // 首次加载
				case 1: // 刷新
					if (mListNewsNew != null && mListNewsNew.size() > 0) {
						return mListNewsNew;
					}
					break;
				default:
					List<News> listT = new ArrayList<News>();
					if (mListNews != null && mListNews.size() > 0) {
						if (mListNewsNew != null && mListNewsNew.size() > 0) {
							for (int i = 0, size = mListNewsNew.size(); i < size; i++) {
								if (!mListNews.contains(mListNewsNew.get(i))) {
									listT.add(mListNewsNew.get(i));
								}
							}

						}
					}
					return listT;
				}
			} else {
				isLocalData = true;
				if (curPageIndex == -1) { // 上拉不加载数据
					return null;
				}
				return mListNewsLocal;
			}

			return mListNewsLocal;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 设置EmptyLayout为加载状态
			if (mListView.getCount() == 0 && curPageIndex == 0) {
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
			}
		}

		@Override
		protected void onPostExecute(List<News> result) {
			super.onPostExecute(result);
			if (result == null || result.size() == 0) {
				mAbPullRefresh.onHeaderRefreshFinish();
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
			if (size >= Config.BLOG_PAGE_SIZE
					&& mListView.getFooterViewsCount() == 0) {
				Log.d("NewsFragment", "size >= Config.BLOG_PAGE_SIZE");
			}
			if (!isLocalData) { // 保存数据到数据库
				mNewsDao.SynchronyData2DB(result);
			}
			if (curPageIndex == -1) { // 上拉加载
				adapter.InsertData(result);
			} else if (curPageIndex == 0) { // 首次加载
				mListNews = result;
				mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
				adapter = new NewsListAdapter(mContext, result);
				mListView.setAdapter(adapter);
			} else if (curPageIndex == 1) {

			} else {
				adapter.AddMoreData(result);
			}
		}

	}

}
