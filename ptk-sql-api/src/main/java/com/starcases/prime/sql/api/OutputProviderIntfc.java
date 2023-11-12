package com.starcases.prime.sql.api;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

public interface OutputProviderIntfc extends SvcProviderBaseIntfc
{
	OutputServiceIntfc create(final PrimeSourceIntfc primeSrc, final PrimeResultIntfc result);
}
