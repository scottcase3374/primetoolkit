package com.starcases.prime;


import com.starcases.prime.graph.impl.PrimeGrapher;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

public class PrimeToolKit 
{
	@Option(names = {"--max-count"}, description = "Max count of primes to process")
	int maxCount = 1500;
	
	@Option(names = {"--max-reduce"}, description = "Max indicies [0-max) to use for reduction.")
	int maxReduce = 2;
	
	public PrimeToolKit()
	{}
	
	private int executionStrategy(ParseResult parseResult)
	{
		return new CommandLine.RunLast().execute(parseResult);
	}
	
	public static void main(String [] args)
	{
		PrimeToolKit ptk = new PrimeToolKit();
		int exitCode = new CommandLine(ptk)
				.setExecutionStrategy(ptk::executionStrategy)
				.execute(args);
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
		primeGrapher.logReduced(maxReduce);		
	}
	
	@Command void graph()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount);	
		primeGrapher.setNodeLocations();
		primeGrapher.viewDefault();		
	}
}
