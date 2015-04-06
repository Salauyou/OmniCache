package ru.salauyou.omnistorage.core.querying.filters;

import ru.salauyou.omnistorage.core.classes.Tuple;

public class ScalarNotEqualsFilter extends AbstractScalarFilter {

	final private Object v;
	
	public ScalarNotEqualsFilter(Object value) {
		v = value;
	}
	
	
	@Override
	public boolean check(Tuple t) {
		Object w = t.getByIndex(index);
		return w == null || !w.equals(v);
	}
	
}
