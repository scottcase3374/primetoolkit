package com.starcases.prime.core.impl;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.starcases.prime.base.api.PrimeBaseIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;

import lombok.NonNull;

public interface PrimeRefFactoryIntfc extends PrimeRefIntfc
{
	PrimeRefFactoryIntfc init( @NonNull final Supplier<PrimeBaseIntfc> primeBaseSupplier);

	PrimeRefFactoryIntfc generateBases(@NonNull final Consumer<PrimeRef> basesGenerate);

}
