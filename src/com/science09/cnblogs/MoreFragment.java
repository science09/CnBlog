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
 * ����
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
		// ʹ֮���Լ���ͼƬ
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
		String[] texts = { "jQuery�ֲ�", "CSS�ٲ��ֲ�", "������ʽ�ٲ�" };
		String[] descs = { "jQuery�ٷ��ĵ���jQuery1.4�汾�������棬��¼����������",
				"CSS2.0�ٲ��ֲᣬ֧�ַ����������ѯ�����÷�����⼰ʾ����", "����������ʽ�����﷨�����ڿ��ٲ�ѯʹ�á�" };
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

			if (isShowArray[i]) {// ѡ��
				items.add(map);
			}
		}
		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		// ���粻����
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
