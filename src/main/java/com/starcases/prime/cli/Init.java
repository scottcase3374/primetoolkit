package com.starcases.prime.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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

	@ArgGroup(exclusive = false, validate = false)
	ExportOpts exportOpts;

	@NonNull
	List<Consumer<String>> actions = new ArrayList<>();

	@Override
	public void run()
	{
		stdOutRedirect();

		setFactoryDefaults();

		actionInitDefaultPrimeContent();

		actionHandleAdditionalBases();

		actionHandleLogging();

		actionHandleGraphing();

		actionHandleExports();

		executeActions();
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

	void setFactoryDefaults()
	{
		PTKFactory.setMaxCount(initOpts.maxCount);
		PTKFactory.setConfidenceLevel(initOpts.confidenceLevel);

		PTKFactory.setActiveBaseId(BaseTypes.DEFAULT);
		PTKFactory.setBaseSetPrimeSource(PrimeBaseWithLists::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseWithLists::new);
		PTKFactory.setPrimeRefCtor( (i, base) -> new PrimeRef(i, base, PTKFactory.getPrimeBaseCtor()) );
	}

	void stdOutRedirect()
	{
		if (initOpts.outputFile != null)
		{
			try
			{
				if (initOpts.outputFile.exists())
				{
					File ren = File.createTempFile("ptk", ".old", new File(initOpts.outputFile.getParent()));
					if (initOpts.outputFile.renameTo(ren))
					{
						log.info("Renamed output file: " + initOpts.outputFile + " to " + ren.getCanonicalPath());
					}
					else
					{
						log.severe("FAILED: Rename output file: " + initOpts.outputFile + " to " + ren.getCanonicalPath());
					}
				}
				else
					log.info("Created outputfile: " + initOpts.outputFile.getCanonicalPath() + " :" +    initOpts.outputFile.createNewFile());

				// Point standard out to our selected output file.
				System.setOut(new PrintStream(initOpts.outputFile));
			}
			catch(IOException e)
			{
				log.severe("ERROR: could not set standard out to file: " + initOpts.outputFile);
				log.severe(e.toString());
			}
		}
	}

	void actionInitDefaultPrimeContent()
	{
		actions.add(s -> {
			FactoryIntfc factory = PTKFactory.getFactory();
			ps = factory.getPrimeSource();
			ps.init();
			ps.setActiveBaseId(PTKFactory.getActiveBaseId());
		});
	}

	void actionHandleAdditionalBases()
	{
		if (baseOpts != null && baseOpts.bases != null)
		{

			switch(baseOpts.bases)
			{
			case NPRIME:
				PTKFactory.setActiveBaseId(BaseTypes.NPRIME);
				PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);
				PTKFactory.setBaseSetPrimeSource(PrimeBaseWithLists::setPrimeSource);
				PTKFactory.setPrimeBaseCtor(PrimeBaseWithLists::new);
				PTKFactory.setPrimeRefCtor( (i, base) -> new PrimeRef(i, base, PTKFactory.getPrimeBaseCtor()) );

				actions.add(s ->
								{
									var base = new BaseReduceNPrime(ps);
									base.setLogBaseGeneration(baseOpts.logGenerate);
									base.setMaxReduce(baseOpts.maxReduce);
									base.genBases();
								});
				break;

			case THREETRIPLE:
				PTKFactory.setActiveBaseId(BaseTypes.THREETRIPLE);
				PTKFactory.setPrimeRefSetPrimeSource(PrimeRefBitSetIndexes::setPrimeSource);
				PTKFactory.setBaseSetPrimeSource( PrimeBaseWithBitsets::setPrimeSource);
				PTKFactory.setPrimeBaseCtor(PrimeBaseWithBitsets::new);
				PTKFactory.setPrimeRefCtor( (i, base) -> new PrimeRefBitSetIndexes(i, base, PTKFactory.getPrimeBaseCtor() ));

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

	void actionHandleLogging()
	{
		if (logOpts != null && logOpts.logOper != null)
		{
			switch (logOpts.logOper)
			{
			case NODESTRUCT:
				actions.add(s -> (new LogNodeStructure(ps)).log() );
				break;

			case GRAPHSTRUCT:
				actions.add(s -> (new LogGraphStructure(ps, PTKFactory.getActiveBaseId() )).log() );
				break;

			case BASES:
				if (PTKFactory.getActiveBaseId() == BaseTypes.THREETRIPLE)
				{
					actions.add(s -> (new LogBases3AllTriples(ps)).log() );
				}
				else if (PTKFactory.getActiveBaseId() == BaseTypes.NPRIME)
				{
					actions.add(s -> (new LogBasesNPrime(ps)).log() );
				}
				break;

			default:
				break;
			}
		}
	}

	void actionHandleGraphing()
	{
		if (graphOpts != null && graphOpts.graphType != null && graphOpts.graphType == Graph.DEFAULT)
		{

			actions.add(s -> graph(ps, PTKFactory.getActiveBaseId() != null ? PTKFactory.getActiveBaseId() : BaseTypes.DEFAULT));
		}
	}

	void actionHandleExports()
	{
		if (exportOpts != null && exportOpts.exportType != null && exportOpts.exportType == Export.GML)
		{
			actions.add(s -> export(ps));
		}
	}

	void executeActions()
	{
		actions.stream().forEach(c -> c.accept("execute action"));
	}
}
