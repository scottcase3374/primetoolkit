package com.starcases.prime.cli;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;

import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseTypesLookup;

import picocli.CommandLine.ITypeConverter;

/**
 * Used to convert enum name to OutputableIntfc
 * for use with picocli code.
 */
public class BaseTypesIntfcConverter implements ITypeConverter<BaseTypesIntfc>
{
	private static final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("BASE_TYPES");
	/**
	 * Convert from enum string name to a general interface
	 */
	@Override
	public BaseTypesIntfc convert(final String value) throws Exception
	{
		MutableMap<String, BaseTypesIntfc> baseTypes = Maps.mutable.empty();
		BaseTypesLookup.get(baseTypes, ATTRIBUTES);
		return baseTypes.get(value);
	}
}
