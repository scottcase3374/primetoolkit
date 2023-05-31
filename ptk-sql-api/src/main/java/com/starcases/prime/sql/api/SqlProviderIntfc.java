package com.starcases.prime.sql.api;



import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public interface SqlProviderIntfc extends SvcProviderBaseIntfc
{
	/**
	 * Create target service.
	 *
	 * @param primeSrc
	 * @param port
	 * @return
	 */
	CmdServerIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final int port);
}
