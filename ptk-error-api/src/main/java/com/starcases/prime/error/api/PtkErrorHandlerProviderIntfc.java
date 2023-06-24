package com.starcases.prime.error.api;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

/**
 *
 * @author scott
 *
 */
public interface PtkErrorHandlerProviderIntfc extends SvcProviderBaseIntfc
{
	PtkErrorHandlerIntfc create();
}
