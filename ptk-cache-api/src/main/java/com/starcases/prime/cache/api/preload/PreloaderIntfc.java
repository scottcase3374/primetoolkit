package com.starcases.prime.cache.api.preload;

import java.util.OptionalLong;

public interface PreloaderIntfc
{
	 OptionalLong retrieve(final long idx);
	 long getMaxOffset();
	 boolean primeTextloader();
}
