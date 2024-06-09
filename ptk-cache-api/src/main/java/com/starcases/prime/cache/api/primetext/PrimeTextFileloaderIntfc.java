package com.starcases.prime.cache.api.primetext;

import java.util.OptionalLong;

public interface PrimeTextFileloaderIntfc
{
	 OptionalLong retrieve(final long idx);
	 boolean primeTextloader();
}
