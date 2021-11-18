package com.starcases.prime;

import java.io.PrintWriter;

import com.starcases.prime.graph.impl.Export;
import com.starcases.prime.graph.impl.PrimeGrapher;
import com.starcases.prime.intfc.PrimeSourceIntfc;

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
	
	@Option(names = {"--confidence-level"}, description = "Drives confidence level of primality check.")
	int confidenceLevel = 100;
		
	@Option(names = {"--active-base-id"}, description = "ID (0 to n) of the active base set (out of multiple possible bases)")
	int activeBaseId = 0;
	
	public PrimeToolKit()
	{
		// Nothing to do here
	}
	
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

	@Command(name = "logNodeStructure")
	private void logNodeStructure()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount, confidenceLevel);	
		primeGrapher.logNodeStructure();
	}

	@Command(name = "logGraphStructure") 
	void logGraphStructure()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount, confidenceLevel);	
		primeGrapher.logGraphStructure();		
	}
	
	@Command(name = "logReduced") 
	void logReduced()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount, confidenceLevel);	
		primeGrapher.logReduced(maxReduce);		
	}
	
	@Command(name = "log3Base")
	void log3Base()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount, confidenceLevel);
		primeGrapher.log3Base(activeBaseId);
	}
	
	@Command(name = "defaultGraph") 
	void graph()
	{
		PrimeGrapher primeGrapher = PrimeSourceFactory.primeGrapher(maxCount, confidenceLevel);	
		//primeGrapher.setNodeLocations();
		primeGrapher.viewDefault();		
	}
	
	@Command(name = "export")
	void export()
	{
		try
		{
			try (PrintWriter pw = new PrintWriter("/home/scott/graph.gml"))
			{
				PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
				Export e = new Export(ps, pw);
				e.export();
				pw.flush();
			}			
		}
		catch(Exception e)
		{
			System.out.println("Exception "+ e);
		}
	}
}
