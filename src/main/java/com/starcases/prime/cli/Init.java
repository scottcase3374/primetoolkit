package com.starcases.prime.cli;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.PrimeBaseWithBitsets;
import com.starcases.prime.base.PrimeBaseWithLists;
import com.starcases.prime.base.nprime.BaseReduceNPrime;
import com.starcases.prime.base.nprime.LogBasesNPrime;
import com.starcases.prime.base.triples.BaseReduceTriple;
import com.starcases.prime.base.triples.LogBases3AllTriples;
import com.starcases.prime.graph.export.ExportGML;
import com.starcases.prime.graph.log.LogGraphStructure;
//import com.starcases.prime.graph.lwjgl.HelloWorld;
import com.starcases.prime.graph.visualize.MetaDataTable;
import com.starcases.prime.graph.visualize.ViewDefault;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.impl.PrimeRefBitSetIndexes;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.LogNodeStructure;

import lombok.NonNull;
import lombok.extern.java.Log;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;

/**
 *
 * Ties all the command line interface options/processing together.
 *
 * The command line parms are parsed and then mapped to PTKKfactory static data members for convenience/consolidation.
 *
 * The command line params determine what actions the toolkit takes and adds a functional "consumer" interface to a list for each "action"
 * to be executed.  Some actions are required as part of initialization so those are added to the action list automatically.
 * Some code added to actions includes references to the PTKFactory - so when the factory method is called, the values
 * of the static PTKFactory data members are used. The parameter to the consumer interface function is just a dummy value - not used.
 *
 */

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

//	@ArgGroup(exclusive = false, validate = false)
//	LWJGLOps jglOps;

	@ArgGroup(exclusive = false, validate = false)
	ExportOpts exportOpts;

	@NonNull
	List<Consumer<String>> actions = new ArrayList<>();

	@Override
	public void run()
	{
		PTKFactory.maxCount = initOpts.maxCount;
		PTKFactory.confidenceLevel = initOpts.confidenceLevel;

		PTKFactory.activeBaseId = BaseTypes.DEFAULT;
		PTKFactory.baseSetPrimeSource = s -> PrimeBaseWithLists.setPrimeSource(s);
		PTKFactory.primeRefSetPrimeSource = s -> PrimeRef.setPrimeSource(s);

		PTKFactory.primeBaseCtor = PrimeBaseWithLists::new;
		PTKFactory.primeRefCtor = (i, base) -> new PrimeRef(i, base, PTKFactory.primeBaseCtor);

		actions.add(s -> {
							FactoryIntfc factory = PTKFactory.getFactory();
							ps = factory.getPrimeSource();
							ps.init();
						});

		if (baseOpts != null)
		{
			if (baseOpts.bases != null)
			{
				switch(baseOpts.bases)
				{
				case NPRIME:
					PTKFactory.activeBaseId = BaseTypes.DEFAULT;
					PTKFactory.primeRefSetPrimeSource = s ->  PrimeRef.setPrimeSource(s);
					PTKFactory.baseSetPrimeSource = s -> PrimeBaseWithLists.setPrimeSource(s);
					PTKFactory.primeBaseCtor = PrimeBaseWithLists::new;
					PTKFactory.primeRefCtor = (i, base) -> new PrimeRef(i, base, PTKFactory.primeBaseCtor);

					actions.add(s ->
									{
										var base = new BaseReduceNPrime(ps);
										base.setLogBaseGeneration(baseOpts.logGenerate);
										base.setMaxReduce(baseOpts.maxReduce);
										base.genBases();
									});
					break;

				case THREETRIPLE:
					PTKFactory.activeBaseId = BaseTypes.THREETRIPLE;
					PTKFactory.primeRefSetPrimeSource = s -> PrimeRefBitSetIndexes.setPrimeSource(s);
					PTKFactory.baseSetPrimeSource = s -> PrimeBaseWithBitsets.setPrimeSource(s);
					PTKFactory.primeBaseCtor = PrimeBaseWithBitsets::new;
					PTKFactory.primeRefCtor = (i, base) -> new PrimeRefBitSetIndexes(i, base, PTKFactory.primeBaseCtor);

					actions.add(s ->
									{
										var base = new BaseReduceTriple(ps);
										base.setLogBaseGeneration(baseOpts.logGenerate);
										base.genBases();
									});
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
				actions.add(s -> (new LogNodeStructure(ps)).log() );
				break;

			case GRAPHSTRUCT:
				actions.add(s -> (new LogGraphStructure(ps, PTKFactory.activeBaseId )).log() );
				break;

			case NPRIME:
				actions.add(s -> (new LogBasesNPrime(ps)).log() );
				break;

			case ALLTHREETRIPLE:
				actions.add(s -> (new LogBases3AllTriples(ps)).log() );
				break;
			}
		}

		if (graphOpts != null && graphOpts.graphType != null && graphOpts.graphType == Graph.DEFAULT)
		{
			actions.add(s -> graph(ps, baseOpts.bases));
		}

//		if (jglOps != null && jglOps.lwjglOper != null && jglOps.lwjglOper == LWJGLOper.HW)
//		{
//			new HelloWorld().run();
//		}

		if (exportOpts != null && exportOpts.exportType != null && exportOpts.exportType == Export.GML)
		{
			actions.add(s -> export(ps));
		}

		actions.stream().forEach(c -> c.accept("execute action"));
	}

	void graph(PrimeSourceIntfc ps, BaseTypes baseType)
	{
		var metaDataView = new MetaDataTable();
		var viewList = new ArrayList<GraphListener<PrimeRefIntfc, DefaultEdge>>();
		viewList.add(metaDataView);
		metaDataView.setSize(400, 320);
		metaDataView.setVisible(true);
		var vd = new ViewDefault(ps,  baseType, viewList);
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
