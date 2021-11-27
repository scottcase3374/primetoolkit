package com.starcases.prime;


import java.util.logging.Level;

import com.starcases.prime.cli.Init;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;



@Command(name = "ptk", subcommands = { Init.class, CommandLine.HelpCommand.class }  , description="Prime Tool Kit")
public class PrimeToolKit  
{	
	@Spec CommandSpec spec;

	public static void main(String [] args)
	{
		PrimeToolKit ptk = new PrimeToolKit();
		CommandLine cl = new CommandLine(ptk);
		cl.registerConverter(java.util.logging.Level.class, Level::parse);
		int exitCode = cl.execute(args);
	
		System.exit(exitCode);
	}	
}

