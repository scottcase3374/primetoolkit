package com.starcases.prime.core.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.collections.api.LongIterable;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.kern.api.BaseTypesIntfc;

import lombok.NonNull;

public interface PrimeRefFactoryIntfc extends PrimeRefIntfc
{
	PrimeRefFactoryIntfc init( @NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier);

	PrimeRefFactoryIntfc generateBases(@NonNull final Consumer<PrimeRefFactoryIntfc> basesGenerate);

	void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final LongIterable primeBase);
	void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final long [] basePrimes);
	void addPrimeBases(@NonNull final BaseTypesIntfc baseType, @NonNull final PrimeRefIntfc [] basePrimes);
}
