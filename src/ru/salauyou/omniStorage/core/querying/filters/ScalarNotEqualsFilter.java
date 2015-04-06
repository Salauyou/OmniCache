package ru.salauyou.omnistorage.core.querying.filters;

import ru.salauyou.omnistorage.core.Tuple;


public class ScalarNotEqualsFilter extends AbstractScalarFilter {

	final private Object v;
	
	public ScalarNotEqualsFilter(Object value) {
		v = resolveValue(value);
	}
	
	
	@Override
	public boolean check(Tuple t) {
		Object w = t.getByIndex(index);
		return w == null || !w.equals(v);
	}
	
}
