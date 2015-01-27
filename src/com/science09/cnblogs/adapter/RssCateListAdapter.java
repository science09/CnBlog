package com.science09.cnblogs.adapter;

import java.util.List;

import com.science09.cnblogs.model.RssCat;
import com.science09.cnblogs.utils.ImageLoad;
import com.science09.cnblogsapp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RssCateListAdapter extends BaseAdapter {
	private List<RssCat> list;
	private LayoutInflater mInflater;

	public RssCateListAdapter(Context context, List<RssCat> list,
			ListView listView) {
		this.list = list;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		RssCat entity = list.get(position);
		if (convertView != null && convertView.getId() == R.id.rss_cate_list) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.rsscate_list_item, null);
			viewHolder.rss_cate_icon = (ImageView) convertView
					.findViewById(R.id.rss_cate_icon);
			viewHolder.rss_cate_title = (TextView) convertView
					.findViewById(R.id.rss_cate_title);
			viewHolder.rss_cate_summary = (TextView) convertView
					.findViewById(R.id.rss_cate_summary);
			viewHolder.rss_cate_id = (TextView) convertView
					.findViewById(R.id.rss_cate_id);
		}
		String tag = entity.GetIcon();
		if (tag.contains("?")) {// 截断?后的字符串，避免无效图片
			tag = tag.substring(0, tag.indexOf("?"));
		}
		viewHolder.rss_cate_icon.setTag(tag);
		ImageLoad.getInstance().display(viewHolder.rss_cate_icon, tag);

		viewHolder.rss_cate_title.setText(entity.GetCatName());
		viewHolder.rss_cate_summary.setText(entity.GetSummary());
		viewHolder.rss_cate_id.setText(String.valueOf(entity.GetCatId()));
		convertView.setTag(viewHolder);
		return convertView;
	}

	/**
	 * 得到数据
	 * 
	 * @return
	 */
	public List<RssCat> GetData() {
		return list;
	}

	/**
	 * 插入
	 * 
	 * @param list
	 */
	public void InsertData(List<RssCat> list) {
		this.list.addAll(0, list);
		this.notifyDataSetChanged();
	}

	/**
	 * 增加数据
	 * 
	 * @param list
	 */
	public void AddMoreData(List<RssCat> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		ImageView rss_cate_icon;
		TextView rss_cate_title;
		TextView rss_cate_summary;
		TextView rss_cate_id;
	}
}
