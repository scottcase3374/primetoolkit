package com.starcases.prime;

import java.util.concurrent.Callable;

import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.PrimeSourceIntfc;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

public class PrimeToolKit 
{
	@Option(names = {"--max-count"}, description = "Max count of primes to process")
	int maxCount = 1500;
	
	public PrimeToolKit()
	{}
	
	public static void main(String [] args)
	{
		int exitCode = new CommandLine(new PrimeToolKit()).execute(args);
		System.exit(exitCode);
	}

	@Command
	private void logNodeStructure()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount);	
		primeGrapher.logNodeStructure();
	}

	@Command void logGraphStructure()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount);	
		primeGrapher.logGraphStructure();		
	}
	
	@Command void logReduced()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount);	
		primeGrapher.logReduced();		
	}
	
	@Command void graph()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount);	
		primeGrapher.setNodeLocations();
		primeGrapher.viewDefault();		
	}
}
