package ru.salauyou.omnistorage.core.querying;

import ru.salauyou.omnistorage.core.Tuple;


public interface Filter {
	
	public boolean check(Tuple t);
	
}
