package com.starcases.prime.cli;

import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.intfc.OutputableIntfc;

import picocli.CommandLine.ITypeConverter;

public class OutputableConverter implements ITypeConverter<OutputableIntfc>
{
	@Override
	public OutputableIntfc convert(String value) throws Exception
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
