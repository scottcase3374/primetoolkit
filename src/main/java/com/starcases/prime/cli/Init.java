package com.starcases.prime.cli;

import java.io.PrintWriter;

import com.starcases.prime.PrimeSourceFactory;
import com.starcases.prime.base.BaseReduce3Triple;
import com.starcases.prime.base.BaseReduceNPrime;
import com.starcases.prime.graph.export.ExportGML;
import com.starcases.prime.graph.log.LogGraphStructure;
import com.starcases.prime.graph.visualize.MetaDataTable;
import com.starcases.prime.graph.visualize.ViewDefault;
import com.starcases.prime.intfc.BaseTypes;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.LogBases3Triple;
import com.starcases.prime.log.LogBasesNPrime;
import com.starcases.prime.log.LogNodeStructure;

import lombok.extern.java.Log;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;

@Log
@Command(name = "init", description = "initial setup")
public class Init implements Runnable
{
	PrimeSourceIntfc ps;	
	
	@ArgGroup(exclusive = false, validate = false)
	InitOpts initOpts = new InitOpts();
	
	@ArgGroup(exclusive = false, validate = false)
	BaseOpts baseOpts;
	
	@ArgGroup(exclusive = false, validate = false)
	LogOpts logOpts;
	
	@ArgGroup(exclusive = false, validate = false)
	GraphOpts graphOpts;
	
	@ArgGroup(exclusive = false, validate = false)
	ExportOpts exportOpts;
	
	
	@Override
	public void run() 
	{
		ps = PrimeSourceFactory.primeSource(initOpts.maxCount, initOpts.confidenceLevel);	
		ps.init();
		var baseType = BaseTypes.DEFAULT;
		
		if (baseOpts != null)	
		{
			if (baseOpts.bases != null)
			{
				baseType = baseOpts.bases;
				switch(baseType)
				{
				case NPRIME:
					optBaseNPrime(ps, baseOpts);					
					break;
					
				case THREETRIPLE:
					optBaseThreetriple(ps);
					break;	
					
				default:
					break;
				}
			}			
		}
		
		if (logOpts != null && logOpts.logOper != null)
		{
			switch (logOpts.logOper)
			{
			case NODESTRUCT:
				this.logNodeStructure(ps);
				break;
				
			case GRAPHSTRUCT:
				this.logGraphStructure(ps, baseType);
				break;
				
			case NPRIME:
				this.logNPrime(ps);
				break;
				
			case THREETRIPLE:
				this.logTripleBase(ps);
				break;
			}
		}
		
		if (graphOpts != null && graphOpts.graphType != null)
		{
			switch(graphOpts.graphType)
			{
			case DEFAULT:
				graph(ps, baseType);
				break;
			}
		}
		
		if (exportOpts != null && exportOpts.exportType != null)
		{
			switch(exportOpts.exportType)
			{
			case GML:
				export(ps);
				break;
			}
		}
	}	
	
	void optBaseNPrime(PrimeSourceIntfc ps, BaseOpts baseOpts)
	{
		var base = new BaseReduceNPrime(ps);
		base.setMaxReduce(baseOpts.maxReduce);
		if (baseOpts.logGenerate)
		{
			base.setLogBaseGeneration(baseOpts.logGenerate);
		}
		
		base.genBases();		
	}	
	
	void optBaseThreetriple(PrimeSourceIntfc ps)
	{
		var base = new BaseReduce3Triple(ps);
		if (baseOpts != null && baseOpts.logGenerate)
		{
			base.setLogBaseGeneration(baseOpts.logGenerate);
		}

		base.genBases();		
	}
	
	private void logNodeStructure(PrimeSourceIntfc ps)
	{		
		var lns = new LogNodeStructure(ps);
		lns.log();
	}
 
	void logGraphStructure(PrimeSourceIntfc ps, BaseTypes baseType)
	{
		var lgs = new LogGraphStructure(ps, baseType );	
		lgs.log();		
	}
		 
	void logNPrime(PrimeSourceIntfc ps)
	{
		var lbnp = new LogBasesNPrime(ps);
		lbnp.log();
	}
		
	void logTripleBase(PrimeSourceIntfc ps)
	{
		var lb3t = new LogBases3Triple(ps);
		lb3t.log();
	}
	
	void graph(PrimeSourceIntfc ps, BaseTypes baseType)
	{
		var metaDataView = new MetaDataTable();
		metaDataView.setSize(400, 320);
		metaDataView.setVisible(true);
		var vd = new ViewDefault(ps,  baseType, metaDataView);
		vd.viewDefault();
	}
	
	void export(PrimeSourceIntfc ps)
	{
		try
		{
			try (var pw = new PrintWriter("/home/scott/graph.gml"))
			{
				var e = new ExportGML(ps, pw);
				e.export();
				pw.flush();
			}			
		}
		catch(Exception e)
		{
			log.severe("Exception "+ e);
		}
	}
}
