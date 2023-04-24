package com.starcases.prime.base_api;

import java.util.Map;

import com.starcases.prime.base_impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.core_api.PrimeSourceIntfc;
import com.starcases.prime.service_api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface BaseProviderIntfc extends SvcProviderBaseIntfc
{
	AbstractPrimeBaseGenerator create( @NonNull final PrimeSourceIntfc primeSrc, final Map<String, Object> settings);
}
