package com.starcases.prime.core.api;

import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.collections.api.LongIterable;

import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.NonNull;

public interface PrimeRefFactoryIntfc extends PrimeRefIntfc
{
	PrimeRefFactoryIntfc generateBases(@NonNull final Consumer<PrimeRefFactoryIntfc> basesGenerate);

	void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final LongIterable primeBase);
	void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final long [] basePrimes);
	void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final PrimeRefIntfc [] basePrimes);

	Set<BaseTypesIntfc> getBaseTypes();
}
