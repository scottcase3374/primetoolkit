package com.starcases.prime.base.api;

import com.starcases.prime.service.api.SvcProviderBaseIntfc;

import lombok.NonNull;

/**
 *
 * @author scott
 *
 */
public interface BaseGenDecorProviderIntfc extends SvcProviderBaseIntfc
{
	BaseGenIntfc create(@NonNull final BaseGenIntfc baseGen);
}
