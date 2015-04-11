package ru.salauyou.omnistorage.core.querying.filters;

import ru.salauyou.omnistorage.core.classes.Entity;
import ru.salauyou.omnistorage.core.querying.Filter;

public abstract class AbstractScalarFilter implements Filter {

	int index;
	
	static protected Object resolveValue(Object value) {
		return (value instanceof Entity) ? ((Entity) value).getId() : value;
	}
	
}
