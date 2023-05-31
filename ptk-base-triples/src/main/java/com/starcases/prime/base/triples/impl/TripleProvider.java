package com.starcases.prime.base.triples.impl;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.BaseGenFactoryIntfc;

/**
 *
 * @author scott
 *
 */
public class TripleProvider implements BaseProviderIntfc
{
	/**
	 * default provider attributes
	 */
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("TRIPLE", "DEFAULT");

	/**
	 * create target service instance
	 */
	@Override
	public BaseGenFactoryIntfc create(final ImmutableMap<String,Object> settings)
	{
		return new BaseReduceTriple(BaseTypes.TRIPLE);
	}

	/**
	 * get provider attributes
	 */
	@Override
	public ImmutableList<String> getProviderAttributes()
	{
		return ATTRIBUTES;
	}
}
