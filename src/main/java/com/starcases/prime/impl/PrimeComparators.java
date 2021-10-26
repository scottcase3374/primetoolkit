package com.starcases.prime.impl;

import java.util.Comparator;
import java.util.NavigableSet;

import com.starcases.prime.intfc.PrimeComparatorIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;

import lombok.Getter;

@Getter
public  class PrimeComparators<E extends Number & Comparator<E>> implements PrimeComparatorIntfc<E>
{
	 Comparator<PrimeRefIntfc<E>> primeFactorComparator = (PrimeRefIntfc<E> o1, PrimeRefIntfc<E> o2) -> 
	{
		if (o1 == null)
			return -1;
		
		return o1.compareTo(o2); 					
	};

	Comparator<NavigableSet<PrimeRefIntfc<E>>> primeSetComparator = 
		(NavigableSet<PrimeRefIntfc<E>> ss1, NavigableSet<PrimeRefIntfc<E>> ss2) 
			-> {
					if (ss1 == ss2)
						return 0;
					else if (ss2 == null)
						return 1;
					else if (ss1 == null)
						return -1;
					
					boolean ss1HasAll = ss1.containsAll(ss2);
					boolean ss2HasAll = ss2.containsAll(ss1);
					
					if (ss1HasAll && !ss2HasAll)
						return 1;
					else if (!ss1HasAll  && ss2HasAll)
						return -1;
					else if (!ss1HasAll && !ss2HasAll)
						return -1;
					else
						return 0;
				};
}
