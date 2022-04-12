package com.starcases.prime.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.PrimeBaseContainer;
import com.starcases.prime.base.nprime.BaseReduceNPrime;
import com.starcases.prime.base.nprime.LogBasesNPrime;
import com.starcases.prime.base.prefix.BasePrefixes;
import com.starcases.prime.base.prefix.LogBasePrefixes;
import com.starcases.prime.base.prefixtree.BasePrefixTree;
import com.starcases.prime.base.prefixtree.LogBasePrefixTree;
import com.starcases.prime.base.triples.BaseReduceTriple;
import com.starcases.prime.base.triples.LogBases3AllTriples;
import com.starcases.prime.graph.export.ExportGML;
import com.starcases.prime.graph.log.LogGraphStructure;
import com.starcases.prime.graph.visualize.MetaDataTable;
import com.starcases.prime.graph.visualize.ViewDefault;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.LogNodeStructure;

import lombok.NonNull;
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
@Command(name = "init", description = "initial setup")
public class Init implements Runnable
{
	private static final Logger log = Logger.getLogger(Init.class.getName());

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
	List<Consumer<String>> actions = new FastList<>();

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

	void export(PrimeSourceIntfc ps, String exportFile)
	{
		try
		{
			if (exportFile == null)
				exportFile = "/tmp/export.gml";

			try (var pw = new PrintWriter(exportFile))
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
		PTKFactory.setBaseSetPrimeSource(PrimeBaseContainer::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base) );
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
						if (log.isLoggable(Level.INFO))
							log.info(String.format("Renamed output file: %s"  + " to " ,  initOpts.outputFile, ren.getCanonicalPath()));
					}
					else
					{
						if (log.isLoggable(Level.SEVERE))
							log.severe(String.format("FAILED: Rename output file: %s to %s",  initOpts.outputFile,  ren.getCanonicalPath()));
					}
				}
				else
				{
					if (log.isLoggable(Level.INFO))
						log.info("Created outputfile: " + initOpts.outputFile.getCanonicalPath() + " :" +    initOpts.outputFile.createNewFile());
				}

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
		//final var load = initOpts != null && initOpts.loadPrimes != null;
		//final var store = initOpts != null && initOpts.storePrimes != null;

		actions.add(s -> {
			FactoryIntfc factory = PTKFactory.getFactory();
			ps = factory.getPrimeSource();

			////if (load)
			//	ps.load(initOpts.loadPrimes);

			log.info("Init::actionInitDefaultPrimeContent - primeSource init");
			ps.init();

			//if (store)
			//	ps.store(initOpts.storePrimes);

			ps.setActiveBaseId(PTKFactory.getActiveBaseId());
		});
	}

	void actionHandleAdditionalBases()
	{
		final var method = "Init::actionHandleAdditionalBases - base ";

		final var trackGenTime = true;

		if (baseOpts != null && baseOpts.bases != null)
		{

			switch(baseOpts.bases)
			{
			case NPRIME:
				PTKFactory.setActiveBaseId(BaseTypes.NPRIME);
				PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);
				PTKFactory.setBaseSetPrimeSource(PrimeBaseContainer::setPrimeSource);
				PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
				PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base) );

				actions.add(s ->
								{
									log.info(method + baseOpts.bases);
									var base = new BaseReduceNPrime(ps);
									base.doPreferParallel(initOpts.preferParallel);
									base.setLogBaseGeneration(baseOpts.logGenerate);
									base.setMaxReduce(baseOpts.maxReduce);
									base.genBases(trackGenTime);
								});
				break;

			case THREETRIPLE:
				PTKFactory.setActiveBaseId(BaseTypes.THREETRIPLE);
				PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);
				PTKFactory.setBaseSetPrimeSource( PrimeBaseContainer::setPrimeSource);
				PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
				PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base) );

				actions.add(s ->
								{
									log.info(method + baseOpts.bases);
									var base = new BaseReduceTriple(ps);
									base.doPreferParallel(initOpts.preferParallel);
									base.setLogBaseGeneration(baseOpts.logGenerate);
									base.genBases(trackGenTime);
								});
				break;

			case PREFIX:
				PTKFactory.setActiveBaseId(BaseTypes.PREFIX);
				PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);
				PTKFactory.setBaseSetPrimeSource( PrimeBaseContainer::setPrimeSource);
				PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
				PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base) );

				actions.add(s ->
								{
									log.info(method + baseOpts.bases);
									var base = new BasePrefixes(ps);
									base.doPreferParallel(initOpts.preferParallel);
									base.setLogBaseGeneration(baseOpts.logGenerate);
									base.genBases(trackGenTime);
								});
				break;

			case PREFIX_TREE:
				PTKFactory.setActiveBaseId(BaseTypes.PREFIX_TREE);
				PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);
				PTKFactory.setBaseSetPrimeSource( PrimeBaseContainer::setPrimeSource);
				PTKFactory.setPrimeBaseCtor(PrimeBaseContainer::new);
				PTKFactory.setPrimeRefRawCtor( (i, base) -> (new PrimeRef(i)).init(PTKFactory.getPrimeBaseCtor(), base));


				actions.add(s ->
								{
									log.info(method + baseOpts.bases);
									var base = new BasePrefixTree(ps);
									base.doPreferParallel(initOpts.preferParallel);
									base.setLogBaseGeneration(baseOpts.logGenerate);
									base.genBases(trackGenTime);
								});
				break;

			default:
				log.info(method + baseOpts.bases);
				break;
			}
		}
	}

	void actionHandleLogging()
	{
		final var method = "Init::actionHandleLogging - logOper ";

		if (logOpts != null && logOpts.logOper != null)
		{
			log.info(method + logOpts.logOper);
			switch (logOpts.logOper)
			{
			case NODESTRUCT:
				actions.add(s -> (new LogNodeStructure(ps)).doPreferParallel(initOpts.preferParallel).l() );
				break;

			case GRAPHSTRUCT:
				actions.add(s -> (new LogGraphStructure(ps, PTKFactory.getActiveBaseId() )).doPreferParallel(initOpts.preferParallel).l() );
				break;

			case BASES:
				switch(PTKFactory.getActiveBaseId())
				{
					case THREETRIPLE:
						actions.add(s -> (new LogBases3AllTriples(ps)).doPreferParallel(initOpts.preferParallel).l() );
						break;

					case NPRIME:
						actions.add(s -> (new LogBasesNPrime(ps)).doPreferParallel(initOpts.preferParallel).l() );
						break;

					case PREFIX:
						actions.add(s -> (new LogBasePrefixes(ps)).doPreferParallel(false).l() );
						break;

					case PREFIX_TREE:
						actions.add(s -> (new LogBasePrefixTree(ps)).doPreferParallel(false).l() );
						break;

					case  DEFAULT:
						actions.add(s -> (new LogNodeStructure(ps)).doPreferParallel(initOpts.preferParallel).l() );
						break;
				}
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
			actions.add(s -> export(ps, exportOpts.exportFile));
		}
	}

	void executeActions()
	{
		actions.stream().forEach(c -> c.accept("execute action"));
	}
}
