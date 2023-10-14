package com.starcases.prime.sql.impl;

import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.sql.api.OutputServiceIntfc;

import lombok.NonNull;

public interface SqlOutputProviderIntfc
{
	OutputServiceIntfc create();
}
