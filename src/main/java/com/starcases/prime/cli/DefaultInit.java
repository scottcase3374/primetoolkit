package com.starcases.prime.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.list.mutable.FastList;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.AbstractPrimeBaseGenerator;
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
import com.starcases.prime.impl.GenerationProgress;
import com.starcases.prime.impl.PrimeRef;
import com.starcases.prime.intfc.FactoryIntfc;
import com.starcases.prime.intfc.PrimeRefIntfc;
import com.starcases.prime.intfc.PrimeSourceIntfc;
import com.starcases.prime.log.LogNodeStructure;
import com.starcases.prime.metrics.MetricMonitor;

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
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
@Command(name = "init", description = "Default initial setup")
public class DefaultInit implements Runnable
{
	/**
	 * default logger
	 */
	private static final Logger LOG = Logger.getLogger(DefaultInit.class.getName());

	/**
	 * prime source - for prime/prime ref lookups
	 */
	@Getter
	@Setter
	private PrimeSourceIntfc primeSrc;

	/**
	 * DefaultInit opts info from picocli
	 */
	@Getter
	@ArgGroup(exclusive = false, validate = false)
	private final InitOpts initOpts = new InitOpts();

	/**
	 * Base type selections
	 */
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
	private OutputOpts outputOpts = new OutputOpts();

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
		final var outputFolderOk = ensureOutputFolder();
		if (outputFolderOk)
		{
			stdOutRedirect();
		}

		setFactoryDefaults();

		actionInitMetrics();

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
	 * @param primeSrc
	 * @param baseType
	 */
	private void graph(final PrimeSourceIntfc primeSrc, final BaseTypes baseType)
	{
		try
		{
			final var metaDataView = new MetaDataTable();
			final var viewList = new ArrayList<GraphListener<PrimeRefIntfc, DefaultEdge>>();
			viewList.add(metaDataView);
			metaDataView.setSize(400, 320);
			metaDataView.setVisible(true);
			final var viewDefault = new ViewDefault(primeSrc,  baseType, viewList);
			viewDefault.viewDefault();
		}
		catch(IOException except)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("IOExcetion: " + except.toString());
			}
		}
	}

	/**
	 * default export setup.
	 *
	 * @param primeSrc
	 * @param exportFileDef
	 */
	private void export(final PrimeSourceIntfc primeSrc)
	{
		try (var exportWriter = new PrintWriter(Files.newBufferedWriter(decorateFileName("default", "export", "gml"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)))
		{
			final var exporter = new ExportGML(primeSrc, exportWriter);
			exporter.export();
			exportWriter.flush();
		}
		catch(IOException except)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("Exception "+ except);
			}
		}
	}

	private Path decorateFileName(final String base, final String fileName, final String extension)
	{
		Path ret = null;
		try
		{
			final File folder = new File(initOpts.getOutputFolder().replaceFirst("^.*~", System.getenv("HOME")));
			ret = Path.of(folder.getCanonicalPath().toLowerCase(Locale.getDefault()), String.format("%s-%s-%s.%s", fileName, base ,DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) , extension).toLowerCase(Locale.getDefault()) );
		}
		catch(final IOException e)
		{
			// nothing to do; returns null
		}
		return ret;
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

	private boolean ensureOutputFolder()
	{
		boolean ret;
		final File folder = new File(initOpts.getOutputFolder().replaceFirst("^.*~", System.getenv("HOME")));
		if (folder.exists() || folder.mkdirs())
		{
			ret = true;
		}
		else
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("ERROR: could not create base folder: " + initOpts.getOutputFolder());
			}
			ret = false;
		}
		return ret;
	}

	private void stdOutRedirect()
	{
		if (!initOpts.isStdOuputRedir())
		{
			return;
		}

		final Path stdOutPath = this.decorateFileName("std", "out", "log");
		if (stdOutPath != null)
		{
			// Point standard out to our selected output file.
			PrimeToolKit.setOutput("stdout", stdOutPath);
		}
		else
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("ERROR: could not set standard out to provided destination. " );
			}
		}
	}

	private void actionInitDefaultPrimeContent()
	{
		actions.add(s -> {
			final FactoryIntfc factory = PTKFactory.getFactory();
			primeSrc = factory.getPrimeSource();
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info("DefaultInit::actionInitDefaultPrimeContent - primeSource init");
			}

			if (outputOpts.getOutputOpers().contains(OutputOper.PROGRESS))
			{
				// Just temporary - want more configurability still
				primeSrc.setDisplayProgress(new GenerationProgress());
			}

			primeSrc.setDisplayPrimeTreeMetrics(outputOpts.getOutputOpers().contains(OutputOper.PRIMETREE_METRICS));

			if (initOpts.getLoadPrimes() != null)
			{
				primeSrc.load(initOpts.getLoadPrimes());
			}
			primeSrc.init();

			if (initOpts.getStorePrimes() != null)
			{
				primeSrc.store(initOpts.getStorePrimes());
			}
		});
	}

	private void actionInitMetrics()
	{
		MetricMonitor.startReport();
	}

	private void actionHandleAdditionalBases()
	{
		if (baseOpts != null && baseOpts.getBases() != null)
		{
			baseOpts.getBases().forEach(baseType ->
			{
				final var trackGenTime = true;
				final var method = "DefaultInit::actionHandleAdditionalBases - base ";
				final Supplier<AbstractPrimeBaseGenerator> baseSupplier;
				switch(baseType)
				{
					case NPRIME:
						baseSupplier = () -> new BaseReduceNPrime(primeSrc).assignMaxReduce(baseOpts.getMaxReduce());
						break;

					case THREETRIPLE:
						baseSupplier = () -> new BaseReduceTriple(primeSrc);
						break;

					case PREFIX:
						baseSupplier = () -> new BasePrefixes(primeSrc);
						break;

					case PRIME_TREE:
						baseSupplier = () -> new PrimeTree(primeSrc, PTKFactory.getCollTrack());
						break;

					default:
						baseSupplier = null;
						if(LOG.isLoggable(Level.INFO))
						{
							LOG.info(String.format("%s%s",method, baseOpts.getBases()));
						}
						break;
				}

				if (null != baseSupplier)
				{
					actions.add(s ->
									{
										if (LOG.isLoggable(Level.INFO))
										{
											LOG.info(method + baseType.name());
										}

										if (baseOpts.isUseBaseFile())
										{
											PrimeToolKit.setOutput(baseType.name(), this.decorateFileName(baseType.name(), "base", "log"));
										}
										final var base = baseSupplier.get();
										base.setTrackTime(trackGenTime);
										base.doPreferParallel(initOpts.isPreferParallel());
										base.setBaseGenerationOutput(outputOpts.getOutputOpers().contains(OutputOper.CREATE));

										base.genBases();
									});
				}
			}
		);
		}
	}

	private void actionHandleOutputs()
	{
		if (outputOpts != null && outputOpts.getOutputOpers() != null && !outputOpts.getOutputOpers().isEmpty())
		{
			if (LOG.isLoggable(Level.INFO))
			{
				final var method = "DefaultInit::actionHandleLogging - logOper ";
				LOG.info(String.format("%s%s", method, outputOpts.getOutputOpers().stream().map(Object::toString).collect(Collectors.joining(","))));
			}
			outputOpts.getOutputOpers().forEach(oo ->
			{
				switch(oo.toString())
				{
				case "THREETRIPLE":
					if (isBaseSelected(BaseTypes.THREETRIPLE))
					{
						actions.add(s -> new LogBases3AllTriples(primeSrc).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
					}
					break;

				case "NPRIME":
					if (isBaseSelected(BaseTypes.NPRIME))
					{
						actions.add(s -> new LogBasesNPrime(primeSrc).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
					}
					break;

				case "PREFIX":
					if (isBaseSelected(BaseTypes.PREFIX))
					{
						actions.add(s -> new LogBasePrefixes(primeSrc).doPreferParallel(false).outputLogs() );
					}
					break;

				case "PRIME_TREE":
					if (isBaseSelected(BaseTypes.PRIME_TREE))
					{
						actions.add(s -> new LogPrimeTree(primeSrc).doPreferParallel(false).outputLogs() );
					}
					break;

				case "BASES":
					if (isBaseSelected(BaseTypes.THREETRIPLE))
					{
						actions.add(s -> new LogBases3AllTriples(primeSrc).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
					}
					if (isBaseSelected(BaseTypes.NPRIME))
					{
						actions.add(s -> new LogBasesNPrime(primeSrc).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
					}
					if (isBaseSelected(BaseTypes.PREFIX))
					{
						actions.add(s -> new LogBasePrefixes(primeSrc).doPreferParallel(false).outputLogs() );
					}
					if (isBaseSelected(BaseTypes.PRIME_TREE))
					{
						actions.add(s -> new LogPrimeTree(primeSrc).doPreferParallel(false).outputLogs() );
					}
					break;

				case "GRAPHSTRUCT":
					actions.add(s -> new LogGraphStructure(primeSrc, BaseTypes.DEFAULT ).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
					break;

				case "NODESTRUCT":
					actions.add(s -> new LogNodeStructure(primeSrc).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
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

	private boolean isBaseSelected(final BaseTypes baseType)
	{
		return baseOpts != null && baseOpts.getBases().contains(baseType);
	}

	private void actionHandleGraphing()
	{
		if (graphOpts != null && graphOpts.getGraphType() != null && graphOpts.getGraphType() == Graph.DEFAULT)
		{
			actions.add(s -> graph(primeSrc, BaseTypes.DEFAULT));
		}
	}

	private void actionHandleExports()
	{
		if (exportOpts != null && exportOpts.getExportType() != null && exportOpts.getExportType() == Export.GML)
		{
			actions.add(s -> export(primeSrc));
		}
	}

	private void executeActions()
	{
		actions.forEach(c -> c.accept("execute action"));
	}
}
