package com.science09.cnblogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.science09.cnblogs.utils.NetUtils;
import com.science09.cnblogsapp.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

/**
 * 更多
 */
public class MoreFragment extends Fragment implements OnItemClickListener {
	private Context mContext;
	private ListView mListView;
	private SimpleAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity().getApplicationContext();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.more_layout, container, false);
		mListView = (ListView) view.findViewById(R.id.more_tools_list);
		adapter = new SimpleAdapter(mContext, getData(),
				R.layout.more_list_item, new String[] { "PIC", "TITLE", "DESC",
						"URL" }, new int[] { R.id.more_tools_icon,
						R.id.more_tools_title, R.id.more_tools_desc,
						R.id.more_tools_url });
		// 使之可以加载图片
		adapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);

		return view;
	}

	private List<Map<String, Object>> getData() {
		Integer[] images = { R.drawable.jquery, R.drawable.stylesheet,
				R.drawable.regular };
		String[] texts = { "jQuery手册", "CSS速查手册", "正则表达式速查" };
		String[] descs = { "jQuery官方文档（jQuery1.4版本）汉化版，收录基本完整。",
				"CSS2.0速查手册，支持分类浏览及查询，含用法、详解及示例。", "包含正则表达式基本语法，便于快速查询使用。" };
		String[] urls = { "http://m.walkingp.com/handbook/jquery/",
				"http://m.walkingp.com/handbook/css/",
				"http://m.walkingp.com/handbook/regular/" };
		boolean[] isShowArray = { true, true, true };
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < texts.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", images[i]);
			map.put("TITLE", texts[i]);
			map.put("DESC", descs[i]);
			map.put("URL", urls[i]);

			if (isShowArray[i]) {// 选项
				items.add(map);
			}
		}
		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		// 网络不可用
		if (!NetUtils.networkIsAvailable(mContext)) {
			Toast.makeText(mContext, R.string.sys_network_error,
					Toast.LENGTH_SHORT).show();
			return;
		}
		TextView tvTitle = (TextView) (view.findViewById(R.id.more_tools_title));
		TextView tvUrl = (TextView) (view.findViewById(R.id.more_tools_url));
		String url = tvUrl.getText().toString();
		String title = tvTitle.getText().toString();
		Intent intent = new Intent();
		intent.setClass(mContext, WebActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putString("title", title);

		intent.putExtras(bundle);
		startActivity(intent);
	}

}
