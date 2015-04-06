package ru.salauyou.omnistorage.core.querying.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.salauyou.omnistorage.core.OmniStorage;
import ru.salauyou.omnistorage.core.classes.Schema.SchemaType;
import ru.salauyou.omnistorage.core.classes.Tuple;
import ru.salauyou.omnistorage.core.querying.Filter;

public final class EntityFilter implements Filter {

	final List<Filter> scalarFilters = new ArrayList<>();
	final List<EntityFilter> entityFilters = new ArrayList<>();
	final Map<String, EntityFilter> entityFilterMap = new HashMap<>();
	final OmniStorage storage;
	final SchemaType schemaType;
	final int index;
	
	
	
	public EntityFilter(OmniStorage storage, SchemaType schemaType, int index) {
		this.storage = storage;
		this.schemaType = schemaType;
		this.index = index;
	}
	
	
	
	@Override
	public boolean check(Tuple t) {
		for (Filter f : scalarFilters) {
			if (!f.check(t))
				return false;
		}
		for (EntityFilter f : entityFilters) {
			Tuple tt = storage.getTuple(schemaType.type, t.getByIndex(index));
			if (!f.check(tt))
				return false;
		}
		return true;
	}

}
