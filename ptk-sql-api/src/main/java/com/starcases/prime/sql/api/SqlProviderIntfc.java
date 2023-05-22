package com.starcases.prime.sql.api;



import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

public interface SqlProviderIntfc extends SvcProviderBaseIntfc
{
	CmdServerIntfc create(@NonNull final PrimeSourceIntfc primeSrc, final int port);
}
