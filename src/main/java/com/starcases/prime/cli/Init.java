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
import java.util.stream.Collectors;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.BaseTypes;
import com.starcases.prime.base.PrimeMultiBaseContainer;
import com.starcases.prime.base.nprime.BaseReduceNPrime;
import com.starcases.prime.base.nprime.LogBasesNPrime;
import com.starcases.prime.base.prefix.BasePrefixes;
import com.starcases.prime.base.prefix.LogBasePrefixes;
import com.starcases.prime.base.primetree.LogPrimeTree;
import com.starcases.prime.base.primetree.PrimeTree;
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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;

/**
 *
 * Ties all the command line interface options/processing together.
 *
 * The command line parms are parsed and then mapped to PTKKfactory
 *  static data members for convenience/consolidation.
 *
 * The command line params determine what actions the toolkit takes
 * and adds a functional "consumer" interface to a list for each "action"
 * to be executed.  Some actions are required as part of initialization so
 * those are added to the action list automatically.
 * Some code added to actions includes references to the PTKFactory - so when
 * the factory method is called, the values
 * of the static PTKFactory data members are used. The parameter to the consumer
 * interface function is just a dummy value - not used.
 *
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CommentSize", "PMD.AvoidFinalLocalVariable"})
@Command(name = "init", description = "initial setup")
public class Init implements Runnable
{
	private static final Logger LOG = Logger.getLogger(Init.class.getName());

	@Getter
	@Setter
	private PrimeSourceIntfc ps;

	@Getter
	@ArgGroup(exclusive = false, validate = false)
	private final InitOpts initOpts = new InitOpts();

	@Getter
	@Setter
	@ArgGroup(exclusive = false, validate = false)
	private BaseOpts baseOpts;

	/**
	 * flags for which data to output.
	 */
	@Getter
	@Setter
	@ArgGroup(exclusive = false, validate = false)
	private OutputOpts outputOpts;

	/**
	 * flags indicating a graph type to produce
	 */
	@Getter
	@Setter
	@ArgGroup(exclusive = false, validate = false)
	private GraphOpts graphOpts;

	/**
	 * flags indicating to export GML
	 */
	@Getter
	@Setter
	@ArgGroup(exclusive = false, validate = false)
	private ExportOpts exportOpts;

	/**
	 * list/container for actions to execute - is never null or replaced.
	 */
	@Getter
	@NonNull
	private final List<Consumer<String>> actions = new FastList<>();

	/**
	 * Pull all the settings together and execute all the desired functionality.
	 */
	@Override
	public void run()
	{
		ensureOutputFolder();

		setFactoryDefaults();

		actionInitDefaultPrimeContent();

		actionHandleAdditionalBases();

		actionHandleOutputs();

		actionHandleGraphing();

		actionHandleExports();

		executeActions();
	}

	/**
	 * default graph setup
	 *
	 * @param ps
	 * @param baseType
	 */
	private void graph(final PrimeSourceIntfc ps, final BaseTypes baseType)
	{
		final var metaDataView = new MetaDataTable();
		final var viewList = new ArrayList<GraphListener<PrimeRefIntfc, DefaultEdge>>();
		viewList.add(metaDataView);
		metaDataView.setSize(400, 320);
		metaDataView.setVisible(true);
		final var vd = new ViewDefault(ps,  baseType, viewList);
		vd.viewDefault();
	}

	/**
	 * default export setup.
	 *
	 * @param ps
	 * @param exportFileDef
	 */
	private void export(final PrimeSourceIntfc ps, final String exportFileDef)
	{
		try
		{
			String exportFile;
			if (exportFileDef == null)
			{
				exportFile = "export.gml";
			}
			else
			{
				exportFile = exportFileDef;
			}
			try (var pw = new PrintWriter(exportFile))
			{
				final var e = new ExportGML(ps, pw);
				e.export();
				pw.flush();
			}
		}
		catch(Exception e)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("Exception "+ e);
			}
		}
	}

	private void setFactoryDefaults()
	{
		PTKFactory.setMaxCount(initOpts.getMaxCount());
		PTKFactory.setConfidenceLevel(initOpts.getConfidenceLevel());

		PTKFactory.setBaseSetPrimeSource(PrimeMultiBaseContainer::setPrimeSource);
		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeMultiBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base) );
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private void ensureOutputFolder()
	{
		if (!initOpts.getOutputFolder().exists())
		{
			if (!initOpts.getOutputFolder().mkdirs() && LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("ERROR: could not create base folder: " + initOpts.getOutputFolder());
				return;
			}
			stdOutRedirect();
		}
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private void stdOutRedirect()
	{
		String cpath = "";
		try
		{
			File f = createFile("stdout-");
			cpath = f.getCanonicalPath();
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info("Created outputfile: " + cpath);
			}

			// Point standard out to our selected output file.
			PrimeToolKit.setOut(new PrintStream(f));
		}
		catch(final IOException e)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("ERROR: could not set standard out to file: " + cpath);
				LOG.severe(e.toString());
			}
		}
	}

	private File createFile(final String prefix) throws IOException
	{
		return File.createTempFile(prefix, null, initOpts.getOutputFolder());
	}

	private void actionInitDefaultPrimeContent()
	{
		actions.add(s -> {
			final FactoryIntfc factory = PTKFactory.getFactory();
			ps = factory.getPrimeSource();
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info("Init::actionInitDefaultPrimeContent - primeSource init");
			}

			ps.setDisplayProgress(outputOpts.getOutputOpers().contains(OutputOper.PROGRESS));

			ps.setDisplayPrimeTreeMetrics(outputOpts.getOutputOpers().contains(OutputOper.PRIMETREE_METRICS));

			ps.init();
		});
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private void actionHandleAdditionalBases()
	{
		@SuppressWarnings("PMD.AvoidFinalLocalVariable")
		final var method = "Init::actionHandleAdditionalBases - base ";

		final var trackGenTime = true;

		if (baseOpts != null && baseOpts.getBases() != null)
		{

			baseOpts.getBases().forEach(baseType ->
			{
				switch(baseType)
				{
					case NPRIME:
						actions.add(s ->
										{
											if (LOG.isLoggable(Level.INFO))
											{
												LOG.info(method + baseType.name());
											}
											final var base = new BaseReduceNPrime(ps);
											base.setTrackTime(trackGenTime);
											base.doPreferParallel(initOpts.isPreferParallel());
											base.setBaseGenerationOutput(outputOpts.getOutputOpers().contains(OutputOper.CREATE));
											base.setMaxReduce(baseOpts.getMaxReduce());
											base.genBases();
										});
						break;

					case THREETRIPLE:
						actions.add(s ->
										{
											if (LOG.isLoggable(Level.INFO))
											{
												LOG.info(method + baseType.name());
											}
											final var base = new BaseReduceTriple(ps);
											base.setTrackTime(trackGenTime);
											base.doPreferParallel(initOpts.isPreferParallel());
											base.setBaseGenerationOutput(outputOpts.getOutputOpers().contains(OutputOper.CREATE));
											base.genBases();
										});
						break;

					case PREFIX:
						actions.add(s ->
										{
											if (LOG.isLoggable(Level.INFO))
											{
												LOG.info(method + baseType.name());
											}

											final var base = new BasePrefixes(ps);
											base.setTrackTime(trackGenTime);
											base.doPreferParallel(initOpts.isPreferParallel());
											base.setBaseGenerationOutput(outputOpts.getOutputOpers().contains(OutputOper.CREATE));
											base.genBases();
										});
						break;

					case PRIME_TREE:
						actions.add(s ->
										{
											if (LOG.isLoggable(Level.INFO))
											{
												LOG.info(method + baseType.name());
											}

											final var base = new PrimeTree(ps, PTKFactory.getCollTrack());
											base.setTrackTime(trackGenTime);
											base.doPreferParallel(initOpts.isPreferParallel());
											base.setBaseGenerationOutput(outputOpts.getOutputOpers().contains(OutputOper.CREATE));
											base.genBases();
										});
						break;

					default:
						if(LOG.isLoggable(Level.INFO))
						{
							LOG.info(String.format("%s%s",method, baseOpts.getBases()));
						}
						break;
				}
			}
		);
		}
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private void actionHandleOutputs()
	{
		final var method = "Init::actionHandleLogging - logOper ";

		if (outputOpts != null && outputOpts.getOutputOpers() != null && !outputOpts.getOutputOpers().isEmpty())
		{
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("%s%s", method, outputOpts.getOutputOpers().stream().map(Object::toString).collect(Collectors.joining(","))));
			}
			outputOpts.getOutputOpers().forEach(oo ->
			{
				switch(oo.toString())
				{
				case "THREETRIPLE":
					if (isBaseSelected(BaseTypes.THREETRIPLE))
					{
						actions.add(s -> new LogBases3AllTriples(ps).doPreferParallel(initOpts.isPreferParallel()).l() );
					}
					break;

				case "NPRIME":
					if (isBaseSelected(BaseTypes.NPRIME))
					{
						actions.add(s -> new LogBasesNPrime(ps).doPreferParallel(initOpts.isPreferParallel()).l() );
					}
					break;

				case "PREFIX":
					if (isBaseSelected(BaseTypes.PREFIX))
					{
						actions.add(s -> new LogBasePrefixes(ps).doPreferParallel(false).l() );
					}
					break;

				case "PRIME_TREE":
					if (isBaseSelected(BaseTypes.PRIME_TREE))
					{
						actions.add(s -> new LogPrimeTree(ps).doPreferParallel(false).l() );
					}
					break;

				case "BASES":
					if (isBaseSelected(BaseTypes.THREETRIPLE))
					{
						actions.add(s -> new LogBases3AllTriples(ps).doPreferParallel(initOpts.isPreferParallel()).l() );
					}
					if (isBaseSelected(BaseTypes.NPRIME))
					{
						actions.add(s -> new LogBasesNPrime(ps).doPreferParallel(initOpts.isPreferParallel()).l() );
					}
					if (isBaseSelected(BaseTypes.PREFIX))
					{
						actions.add(s -> new LogBasePrefixes(ps).doPreferParallel(false).l() );
					}
					if (isBaseSelected(BaseTypes.PRIME_TREE))
					{
						actions.add(s -> new LogPrimeTree(ps).doPreferParallel(false).l() );
					}
					break;

				case "GRAPHSTRUCT":
					actions.add(s -> new LogGraphStructure(ps, BaseTypes.DEFAULT ).doPreferParallel(initOpts.isPreferParallel()).l() );
					break;

				case "NODESTRUCT":
					actions.add(s -> new LogNodeStructure(ps).doPreferParallel(initOpts.isPreferParallel()).l() );
					break;


				case "PROGRESS",
					 "CREATE",
					 "PRIMETREE_METRICS":
				default:
					break;
				}
			});
		}
	}

	@SuppressWarnings("PMD.LawOfDemeter")
	private boolean isBaseSelected(final BaseTypes baseType)
	{
		return baseOpts != null && baseOpts.getBases().contains(baseType);
	}

	private void actionHandleGraphing()
	{
		if (graphOpts != null && graphOpts.getGraphType() != null && graphOpts.getGraphType() == Graph.DEFAULT)
		{
			actions.add(s -> graph(ps, BaseTypes.DEFAULT));
		}
	}

	private void actionHandleExports()
	{
		if (exportOpts != null && exportOpts.getExportType() != null && exportOpts.getExportType() == Export.GML)
		{
			actions.add(s -> export(ps, exportOpts.getExportFile()));
		}
	}

	private void executeActions()
	{
		actions.forEach(c -> c.accept("execute action"));
	}
}
