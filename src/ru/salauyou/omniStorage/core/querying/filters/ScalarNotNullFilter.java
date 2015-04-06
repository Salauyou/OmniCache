package ru.salauyou.omnistorage.core.querying.filters;

import ru.salauyou.omnistorage.core.Tuple;


public class ScalarNotNullFilter extends AbstractScalarFilter {

	@Override
	public boolean check(Tuple t) {
		return t.getByIndex(index) != null;
	}

}
