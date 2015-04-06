package ru.salauyou.omnistorage.core.querying.filters;

import ru.salauyou.omnistorage.core.classes.Tuple;

public class ScalarBetweenFilter extends AbstractScalarFilter {

	final private Comparable<Object> vMin;
	final private Comparable<Object> vMax;
	
	
	public ScalarBetweenFilter(Comparable<Object> valueMin, Comparable<Object> valueMax) {
		vMin = valueMin;
		vMax = valueMax;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean check(Tuple t) {
		Comparable<Object> w = (Comparable<Object>) t.getByIndex(index);
		return w != null && vMin.compareTo(w) <= 0 && vMax.compareTo(w) >= 0;
	}

}
