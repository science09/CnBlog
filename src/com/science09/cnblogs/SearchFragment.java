package com.science09.cnblogs;

import java.util.ArrayList;
import java.util.List;

import com.science09.cnblogs.adapter.UserListAdapter;
import com.science09.cnblogs.core.UserHelper;
import com.science09.cnblogs.model.Users;
import com.science09.cnblogs.utils.NetUtils;
import com.science09.cnblogs.view.EmptyLayout;
import com.science09.cnblogsapp.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends Fragment implements OnKeyListener,
		OnClickListener, OnItemClickListener {
	private static final String TAG = "SearchFragment";
	private List<Users> listUser = new ArrayList<Users>();
	private UserListAdapter adapter;
	private Context mContext;
	private ListView mListView = null;
	private EmptyLayout mEmptyLayout = null;
	private EditText mTxtSearch = null;
	private ImageButton mSearchBtn;
	private ImageButton mItemBtn;
	private SharedPreferences mSharePfs;
	private String mSearchStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_layout, container, false);
		mListView = (ListView) view.findViewById(R.id.search_list);
		mListView.setOnItemClickListener(this);
		mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
		mTxtSearch = (EditText) view.findViewById(R.id.txtSearch);
		mTxtSearch.clearFocus();
		mSearchBtn = (ImageButton) view.findViewById(R.id.search_btn);
		mSearchBtn.setOnClickListener(this);
		// mItemBtn = (ImageButton) view.findViewById(R.id.btnItem);
		mSharePfs = mContext.getSharedPreferences(
				getString(R.string.preferences_key), Context.MODE_PRIVATE);
		String lastSearch = mSharePfs.getString(
				getString(R.string.preference_last_search_keyword), "");
		mTxtSearch.setText(lastSearch);
		mTxtSearch.setOnKeyListener(this);

		return view;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_ENTER == keyCode) {
			startSearch();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		startSearch();
	}

	private void startSearch() {
		mSearchStr = mTxtSearch.getText().toString();
		if ("".equals(mSearchStr)) {
			Toast.makeText(mContext, R.string.sys_input_empty,
					Toast.LENGTH_SHORT).show();
			mTxtSearch.setFocusable(true);
			return;
		}
		String str = getString(R.string.preference_last_search_keyword);
		mSharePfs.edit().putString(str, mSearchStr).commit();
		new PageTask().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		Intent intent = new Intent();
		intent.setClass(mContext, AuthorBlogActivity.class);
		Bundle bundle = new Bundle();
		TextView tvBlogTitle = (TextView) (view
				.findViewById(R.id.author_list_title));
		TextView tvBlogAuthor = (TextView) (view
				.findViewById(R.id.author_list_username));
		String blogTitle = tvBlogTitle.getText().toString();
		String blogAuthor = tvBlogAuthor.getText().toString();
		bundle.putString("blogName", blogTitle);
		bundle.putString("author", blogAuthor);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	class PageTask extends AsyncTask<String, Integer, List<Users>> {
		public PageTask() {

		}

		@Override
		protected List<Users> doInBackground(String... params) {
			try {
				List<Users> listTmp = new ArrayList<Users>();
				List<Users> listUserNew = UserHelper.GetUserList(mSearchStr);
				int size = listUserNew.size();
				for (int i = 0; i < size; i++) {
					if (!listUser.contains(listUserNew.get(i))) { // ±ÜÃâ³öÏÖÖØ¸´
						listTmp.add(listUserNew.get(i));
					}
				}
				return listTmp;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mListView.getCount() == 0) {
				if (!NetUtils.networkIsAvailable(mContext)) {
					Toast.makeText(mContext, R.string.sys_network_error,
							Toast.LENGTH_SHORT).show();
				} else {
					mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
				}
			}
		}

		@Override
		protected void onPostExecute(List<Users> result) {
			if (result == null || result.size() == 0) {
				return;
			}
			listUser.addAll(result);
			result.clear();
			mEmptyLayout.setVisibility(View.GONE);
			adapter = new UserListAdapter(mContext, listUser);
			mListView.setAdapter(adapter);
		}
	}

}
