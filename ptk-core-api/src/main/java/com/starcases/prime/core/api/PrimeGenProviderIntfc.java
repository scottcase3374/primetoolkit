package com.starcases.prime.core.api;

import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface PrimeGenProviderIntfc extends SvcProviderBaseIntfc
{
	PrimeGenIntfc create(@NonNull final PrimeSourceIntfc primeSrc, @NonNull final CollectionTrackerIntfc collTracker);
}
