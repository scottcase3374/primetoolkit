package com.starcases.prime.cli;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.OutputableIntfc;

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
				case "THREETRIPLE" -> BaseTypes.THREETRIPLE;
				case "NPRIME" -> BaseTypes.NPRIME;
				case "DEFAULT" -> BaseTypes.DEFAULT;

				default -> OutputOper.valueOf(value);
			};
	}
}
