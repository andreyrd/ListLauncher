package com.amagital.launcher.list;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* Created by Andrey on 9/2/2014.
*/
class LauncherAdapter extends BaseAdapter implements SectionIndexer {
	private Context context;
	private List<AppInfo> appInfoList;

	private HashMap<String, Integer> positionMap;
	private String[] sections = new String[0];

	public LauncherAdapter(Context context, List<AppInfo> appInfoList) {
		this.context = context;
		this.appInfoList = appInfoList;
	}

	public void notifyDataSetChanged(List<AppInfo> appInfoList) {
		this.appInfoList = appInfoList;

		positionMap = new HashMap<>();
		Set<String> lettersSet = new HashSet<>();
		List<String> lettersList = new ArrayList<>();

		for (int i=0; i<appInfoList.size(); i++) {
			String letter = appInfoList.get(i).getName().substring(0, 1).toUpperCase();

			if (!lettersSet.contains(letter)) {
				lettersSet.add(letter);
				lettersList.add(letter);
				positionMap.put(letter, i);
			}
		}

		sections = lettersList.toArray(new String[lettersList.size()]);

		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return appInfoList.size();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(context, R.layout.item, null);
		}

		TextView nameView = (TextView) view.findViewById(R.id.item_name);
		ImageView iconView = (ImageView) view.findViewById(R.id.item_icon);

		AppInfo info = appInfoList.get(position);

		nameView.setText(info.getName());
		iconView.setImageDrawable(info.getIcon());

		return view;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public String[] getSections() {
		return sections;
	}

	@Override
	public int getPositionForSection(int i) {
		if (i >= sections.length) {
			return getCount() - 1;
		} else {
            return positionMap.get(sections[i]);
		}
	}

	@Override
	public int getSectionForPosition(int i) {
		// Slower but will be replaced in the future with AlphabetIndexer and Cursor
		String letter = appInfoList.get(i).getName().substring(0, 1).toUpperCase();
		int index = Arrays.binarySearch(sections, letter);
		return index > 0 && index < sections.length ? index : 0;
	}
}
