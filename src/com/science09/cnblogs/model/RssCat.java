package com.science09.cnblogs.model;

public class RssCat {
	private int _catId;
	private String _catName;
	private String _icon;
	private String _summary;

	public void SetCatId(int catId) {
		_catId = catId;
	}

	public int GetCatId() {
		return _catId;
	}

	public void SetCatName(String catName) {
		_catName = catName;
	}

	public String GetCatName() {
		return _catName;
	}

	public void SetIcon(String icon) {
		_icon = icon;
	}

	public String GetIcon() {
		return _icon;
	}

	public void SetSummary(String summary) {
		_summary = summary;
	}

	public String GetSummary() {
		return _summary;
	}
}
