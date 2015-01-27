package com.science09.cnblogs;

import java.util.ArrayList;
import java.util.List;
import com.science09.cnblogs.adapter.RssListAdapter;
import com.science09.cnblogs.adapter.RssListAdapter.EnumSource;
import com.science09.cnblogs.dal.RssListDalHelper;
import com.science09.cnblogs.model.RssList;
import com.science09.cnblogs.view.EmptyLayout;
import com.science09.cnblogsapp.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 订阅界面
 * @author 10124143
 *
 */
public class MyRssFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {
	private List<RssList> listRss = new ArrayList<RssList>();
	private Context mContext;
	private TextView txtNoData; // 没有数据
	private ListView mListView;
	private EmptyLayout mEmptyLayout;
	private int lastItemPosition;
	private RssListAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		//注册广播
		UpdateListViewReceiver receiver = new UpdateListViewReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.science09.cnblogs.update_rsslist");
		mContext.registerReceiver(receiver, filter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.my_rss_layout, container, false);
		mListView = (ListView) view.findViewById(R.id.rss_list);
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		txtNoData = (TextView) view.findViewById(R.id.txtNoData);
		new PageTask().execute();
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		BindEvent();
	}
	
	private void BindEvent() {
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	/*
	 * 网络加载数据
	 */
	class PageTask extends AsyncTask<String, Integer, List<RssList>> {

		
		@Override
		protected List<RssList> doInBackground(String... arg0) {
			RssListDalHelper helper = new RssListDalHelper(mContext);
			return helper.GetRssList();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(mListView.getCount() == 0) {
				mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);	
			}
		}
		
		@Override
		protected void onPostExecute(List<RssList> result) {
			super.onPostExecute(result);
			if(mListView.getCount() == 0) {
				mEmptyLayout.setVisibility(View.GONE);
			}
			if(result == null || result.size() == 0) {
				mEmptyLayout.setErrorType(EmptyLayout.NODATA);
			}else{
				adapter = new RssListAdapter(mContext, listRss, EnumSource.MyRss);
				mListView.setAdapter(adapter);
				mListView.setSelection(lastItemPosition);
			}		
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		TextView tvTitle = (TextView) view.findViewById(R.id.rss_item_title);
		TextView tvUrl = (TextView) view.findViewById(R.id.rss_item_url);
		TextView tvIsCnblogs = (TextView) view.findViewById(R.id.rss_item_is_cnblogs);
		TextView tvAuthor = (TextView) view.findViewById(R.id.rss_item_author);
		String title = tvTitle.getText().toString();
		String url = tvUrl.getText().toString();
		boolean isCnblogs = tvIsCnblogs.getText().toString().equals("1");
		String author = tvAuthor.getText().toString();

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		if (isCnblogs) { // 博客园
			intent.setClass(mContext, AuthorBlogActivity.class);
			bundle.putString("blogName", title);
			bundle.putString("author", author);
		} else {
			intent.setClass(mContext, RssItemsActivity.class);
			bundle.putString("title", title);
			bundle.putString("url", url);
		}
		lastItemPosition = mListView.getSelectedItemPosition();
		intent.putExtras(bundle);
		startActivity(intent);	
	}
	
	/*
	 * 接收删除RSS的广播
	 */
	public class UpdateListViewReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String[] arr = bundle.getStringArray("rsslist");
			if(arr != null) {
				boolean isRss = bundle.getBoolean("isrss");
				RssList entity = new RssList();
				entity.SetAuthor(arr[0]);
				entity.SetDescription(arr[1]);
				entity.SetGuid(arr[2]);
				entity.SetTitle(arr[3]);
				entity.SetImage(arr[4]);
				entity.SetLink(arr[5]);
				entity.SetIsCnblogs(arr[6].equals("1"));
				if(isRss){
					List<RssList> list = new ArrayList<RssList>();
					list.add(entity);
					adapter.AddMoreData(list);
				}else{
					adapter.RemoveData(entity);
				}			
			}
		}	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Intent intent = new Intent();
		intent.setClass(mContext, RssCateActivity.class);
		startActivityForResult(intent, 0);
		return false;
	}
	
}
