package com.starcases.prime.preload.api;

import java.util.OptionalLong;

public interface PreloaderIntfc
{
	 OptionalLong retrieve(final long idx);
	 long getMaxOffset();
	 boolean primeTextloader();
}
