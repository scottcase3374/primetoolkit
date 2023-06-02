package com.starcases.prime.cli;

import java.util.Optional;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.common.api.OutputOper;
import com.starcases.prime.core.api.OutputableIntfc;
import com.starcases.prime.service.impl.SvcLoader;

import picocli.CommandLine.ITypeConverter;

/**
 * Used to convert enum name to OutputableIntfc
 * for use with picocli code.
 */
public class OutputableConverter implements ITypeConverter<OutputableIntfc>
{
	private static final ImmutableList<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider( Lists.immutable.of("GLOBAL_BASE_TYPES"))
				.orElseThrow()
				.create();
	/**
	 * Convert from enum string name to a general interface
	 */
	@Override
	public OutputableIntfc convert(final String name) throws Exception
	{
		final Optional<OutputableIntfc> baseType =  Optional.<OutputableIntfc>ofNullable(BASE_TYPES.select(base -> base.name().equals(name)).getFirst());
		return	baseType.orElse(OutputOper.valueOf(name));
	}
}
