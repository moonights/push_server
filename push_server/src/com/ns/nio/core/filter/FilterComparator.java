package com.ns.nio.core.filter;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 过滤器比较器
 */
public class FilterComparator implements Comparator<Filter>,Serializable  {
	private static final long serialVersionUID = -6433253691990065751L;

	public int compare(Filter o1, Filter o2) {
		return o1.getOrder()-o2.getOrder();
	}
}