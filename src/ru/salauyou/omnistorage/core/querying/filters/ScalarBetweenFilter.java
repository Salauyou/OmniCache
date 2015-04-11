package ru.salauyou.omnistorage.core.querying.filters;

import ru.salauyou.omnistorage.core.Tuple;


public class ScalarBetweenFilter extends AbstractScalarFilter {

	final private Comparable<Object> vMin;
	final private Comparable<Object> vMax;
	
	
	@SuppressWarnings("unchecked")
	public ScalarBetweenFilter(Comparable<Object> valueMin, Comparable<Object> valueMax) {
		vMin = (Comparable<Object>) resolveValue(valueMin);
		vMax = (Comparable<Object>) resolveValue(valueMax);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean check(Tuple t) {
		Comparable<Object> w = (Comparable<Object>) t.getByIndex(index);
		return w != null && vMin.compareTo(w) <= 0 && vMax.compareTo(w) >= 0;
	}

}
