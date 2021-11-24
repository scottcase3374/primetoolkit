package com.starcases.prime;

import java.io.PrintWriter;

import com.starcases.prime.base.BaseReduce3Triple;
import com.starcases.prime.base.BaseReduceNPrime;
import com.starcases.prime.graph.export.ExportGML;
import com.starcases.prime.graph.log.LogBases3Triple;
import com.starcases.prime.graph.log.LogBasesNPrime;
import com.starcases.prime.graph.log.LogGraphStructure;
import com.starcases.prime.graph.log.LogNodeStructure;
import com.starcases.prime.graph.visualize.ViewDefault;
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
	
	@Option(names = {"--test-iterations"} , description = "Run for number of iterations and report time")
	int testIterations = 0;
	
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
		PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
		LogNodeStructure lns = new LogNodeStructure(ps);
		lns.log();
	}

	@Command(name = "logGraphStructure") 
	void logGraphStructure()
	{
		PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
		LogGraphStructure lgs = new LogGraphStructure(ps);	
		lgs.log();		
	}
	
	@Command(name = "logReduced") 
	void logReduced()
	{
		PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
		BaseReduceNPrime ld = new BaseReduceNPrime(ps);
		ld.genBases(maxReduce);
		LogBasesNPrime lbnp = new LogBasesNPrime(ps);
		lbnp.log();
	}
	
	@Command(name = "log3Base")
	void log3Base()
	{
		PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
		BaseReduce3Triple ld3 = new BaseReduce3Triple(ps);
		ld3.genBases(activeBaseId);
		LogBases3Triple lb3t = new LogBases3Triple(ps);
		lb3t.log();
	}
	
	@Command(name = "defaultGraph") 
	void graph()
	{
		PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
		ViewDefault vd = new ViewDefault(ps);
		vd.viewDefault();
	}
	
	@Command(name = "export")
	void export()
	{
		try
		{
			try (PrintWriter pw = new PrintWriter("/home/scott/graph.gml"))
			{
				PrimeSourceIntfc ps = PrimeSourceFactory.primeSource(maxCount, confidenceLevel);
				ExportGML e = new ExportGML(ps, pw);
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
