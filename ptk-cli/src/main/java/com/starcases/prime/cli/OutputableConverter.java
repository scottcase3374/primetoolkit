package com.starcases.prime.cli;

import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.common.api.OutputOper;
import com.starcases.prime.core.api.OutputableIntfc;

import picocli.CommandLine.ITypeConverter;

/**
 * Used to convert enum name to OutputableIntfc
 * for use with picocli code.
 */
public class OutputableConverter implements ITypeConverter<OutputableIntfc>
{
	/**
	 * Convert from enum string name to a general interface
	 */
	@Override
	public OutputableIntfc convert(final String value) throws Exception
	{
		return	switch(value)
			{
				case "PRIME_TREE" -> BaseTypes.PRIME_TREE;
				case "PREFIX" -> BaseTypes.PREFIX;
				case "TRIPLE" -> BaseTypes.TRIPLE;
				case "NPRIME" -> BaseTypes.NPRIME;
				case "DEFAULT" -> BaseTypes.DEFAULT;

				default -> OutputOper.valueOf(value);
			};
	}
}
