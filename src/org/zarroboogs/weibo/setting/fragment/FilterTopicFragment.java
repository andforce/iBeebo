package org.zarroboogs.weibo.setting.fragment;

import org.zarroboogs.weibo.db.task.FilterDBTask;

import java.util.Collection;
import java.util.List;

/**
 * User: qii Date: 13-6-16
 */
public class FilterTopicFragment extends AbstractFilterFragment {

	@Override
	protected List<String> getDBDataImpl() {
		return FilterDBTask.getFilterKeywordList(FilterDBTask.TYPE_TOPIC);
	}

	@Override
	protected void addFilterImpl(Collection<String> set) {
		FilterDBTask.addFilterKeyword(FilterDBTask.TYPE_TOPIC, set);
	}

	@Override
	protected List<String> removeAndGetFilterListImpl(Collection<String> set) {
		return FilterDBTask.removeAndGetNewFilterKeywordList(FilterDBTask.TYPE_TOPIC, set);
	}

}
