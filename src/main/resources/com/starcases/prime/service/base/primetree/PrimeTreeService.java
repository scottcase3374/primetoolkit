package com.starcases.prime.service.base.primetree;

import com.starcases.prime.base.primetree_impl.PrimeTree;
import com.starcases.prime.core_api.CollectionTrackerIntfc;
import com.starcases.prime.core_api.PrimeSourceIntfc;

import lombok.NonNull;

public class PrimeTreeService extends PrimeTree {

	public PrimeTreeService(@NonNull PrimeSourceIntfc primeSrc, @NonNull CollectionTrackerIntfc collectionTracker) {
		super(primeSrc, collectionTracker);
		// TODO Auto-generated constructor stub
	}

}
