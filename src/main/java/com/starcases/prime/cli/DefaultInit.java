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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.infinispan.Cache;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.PTKFactory;
import com.starcases.prime.PrimeToolKit;
import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.impl.AbstractPrimeBaseGenerator;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;
import com.starcases.prime.base.nprime.impl.LogBasesNPrime;
import com.starcases.prime.base.prefix.impl.LogBasePrefixes;
import com.starcases.prime.base.primetree.impl.LogPrimeTree;
import com.starcases.prime.base.triples.impl.LogBases3AllTriples;
import com.starcases.prime.cache.api.CacheProviderIntfc;
import com.starcases.prime.cli.MetricsOpts.MetricOpt;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.ProgressProviderIntfc;
import com.starcases.prime.core.impl.PrimeRef;
import com.starcases.prime.graph.export.api.ExportsProviderIntfc;
import com.starcases.prime.graph.log.LogGraphStructure;
import com.starcases.prime.graph.visualize.impl.MetaDataTable;
import com.starcases.prime.graph.visualize.impl.ViewDefault;
import com.starcases.prime.log.LogNodeStructure;
import com.starcases.prime.metrics.api.MetricsProviderIntfc;
import com.starcases.prime.preload.api.PreloaderIntfc;
import com.starcases.prime.preload.api.PreloaderProviderIntfc;
import com.starcases.prime.preload.impl.PrimeSubset;
import com.starcases.prime.service.SvcLoader;
import com.starcases.prime.sql.api.SqlProviderIntfc;

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
	private PrimeSourceFactoryIntfc primeSrc;

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
	 * flags indicating metrics to manage
	 */
	@Getter
	@Setter
	@ArgGroup(exclusive = false, validate = false)
	private MetricsOpts metricOpts;

	/**
	 * flags indicating to export GML
	 */
	@Getter
	@Setter
	@ArgGroup(exclusive = false, validate = false)
	private ExportOpts exportOpts;

	private static final  MetricOpt [] NULL_OPTS = new MetricOpt[0];

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
		final var outputFolderOk = ensureFolderExist(initOpts.getOutputFolder());
		if (outputFolderOk)
		{
			stdOutRedirect();
		}

		setFactoryDefaults();

		actionInitDefaultPrimeContent();

		actionEnableMetrics();

		actionHandleAdditionalBases();

		actionHandleOutputs();

		actionHandleExports();

		actionEnableCmdListener();

		actionHandleGraphing();

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
		try (var exportWriter = new PrintWriter(
				Files.newBufferedWriter(decorateFileName("default", "export", "gml"),
										StandardOpenOption.CREATE,
										StandardOpenOption.TRUNCATE_EXISTING,
										StandardOpenOption.WRITE)))
		{
			final SvcLoader<ExportsProviderIntfc, Class<ExportsProviderIntfc>> exportProvider = new SvcLoader< >(ExportsProviderIntfc.class);
			final ImmutableCollection<String> attributes = Lists.immutable.of("GML", "DEFAULT");
			final var exporter = exportProvider.provider(attributes).create(primeSrc, exportWriter, null);
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

	private String replaceTildeHome(final String path)
	{
		return path.replaceFirst("^~", System.getenv("HOME"));
	}

	private Path decorateFileName(final String base, final String fileName, final String extension)
	{
		Path ret = null;
		try
		{
			final File folder = new File(replaceTildeHome(initOpts.getOutputFolder()));
			ret = Path.of(folder.getCanonicalPath().toLowerCase(Locale.getDefault()), String.format("%s-%s-%s.%s", fileName, base ,DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) , extension).toLowerCase(Locale.getDefault()) );
		}
		catch(final IOException e)
		{
			// nothing to do; returns null
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe(e.toString());
			}
		}
		return ret;
	}

	private void setFactoryDefaults()
	{
		PTKFactory.setMaxCount(initOpts.getMaxCount());
		PTKFactory.setConfidenceLevel(initOpts.getConfidenceLevel());

		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeMultiBaseContainer::new);
		PTKFactory.setPrimeRefRawCtor( (i, base) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor(), base) );
	}

	private boolean ensureFolderExist(final String folderPath)
	{
		Optional<File> optFolder = Optional.empty();
		final File folder = new File(replaceTildeHome(folderPath));
		if (folder.exists() || folder.mkdirs())
		{
			optFolder = Optional.ofNullable(folder);
		}

		if (optFolder.isEmpty() && LOG.isLoggable(Level.SEVERE))
		{
			LOG.severe("ERROR: could not create base folder: " + initOpts.getOutputFolder());
		}
		return optFolder.isPresent();
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

	private void actionEnableMetrics()
	{

		if (Arrays.asList((metricOpts != null) ? metricOpts.getMetricType() : NULL_OPTS).contains(MetricOpt.ALL))
		{
			final SvcLoader<MetricsProviderIntfc, Class<MetricsProviderIntfc>> registryProviders = new SvcLoader< >(MetricsProviderIntfc.class);
			final ImmutableCollection<String> attributes = Lists.immutable.of("METRICS");
			actions.add(s ->  registryProviders.providers(attributes).forEach(p -> p.create(null).enable(true)));
		}
	}

	private void actionEnableCmdListener()
	{
		if (baseOpts != null && baseOpts.isEnableCmmandListener())
		{
			final ImmutableCollection<String> attributes = Lists.immutable.of("SQLPRIME");
			final SvcLoader<SqlProviderIntfc, Class<SqlProviderIntfc>> sqlCmdProviders = new SvcLoader< >(SqlProviderIntfc.class);

			actions.add(s -> {
				try
				{
					if (LOG.isLoggable(Level.INFO))
					{
						LOG.info("DefaultInit::actionEnableCmdListener - SQL command listener port:" + baseOpts.getCmdListenerPort());
					}
					sqlCmdProviders.provider(attributes).create(primeSrc, baseOpts.getCmdListenerPort()).run();
				}
				catch(final InterruptedException e)
				{
					throw new RuntimeException(e);
				}
			});
		}
	}

	private void actionInitDefaultPrimeContent()
	{
		final SvcLoader<CacheProviderIntfc, Class<CacheProviderIntfc>> cacheProvider = new SvcLoader< >(CacheProviderIntfc.class);
		final ImmutableList<String> cacheAttributes = Lists.immutable.of("CACHE");

		LOG.info("enter actionInitDefaultPrimeContent");
		actions.add(s -> {

			final FactoryIntfc factory = PTKFactory.getFactory();

			@SuppressWarnings({"PMD.LocalVariableNamingConventions"})
			final String CACHE_NAME = "primes";
			final String inputFolderPath = initOpts.getInputDataFolder();
			final Cache<Long,PrimeSubset> cache = cacheProvider.provider(cacheAttributes).create(CACHE_NAME, null);

			// Use idx 2 which would be prime 3 for determining if cache was loaded / pre-loaded propertly. Since Primes 1 and 2 may be hard coded in
			// places, it is safer to use the 1st item which is never hardcoded.
			final var cacheIdx0 = cache.get(0L);
			final var cacheIdx0idx2 = cacheIdx0 != null ? cacheIdx0.get(2) : -1L;
			final var alreadyLoaded = cacheIdx0idx2 == 3;
			final var inputFoldExist = ensureFolderExist(inputFolderPath);
			final var loadRawPrimes = initOpts.isLoadPrimes();

			if (alreadyLoaded || !inputFoldExist || !loadRawPrimes)
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("Cache NOT loading raw primes ; Cache Loaded: [%b], Input folder exists: [%b], load-raw-primes[%b]",  alreadyLoaded, inputFoldExist, loadRawPrimes));
				}
				primeSrc = factory.getPrimeSource();
			}
			else if (loadRawPrimes)
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("Cache primes from raw files ; Cache Loaded: [%b], Input folder exists: [%b], load-raw-primes[%b]",  alreadyLoaded, inputFoldExist, loadRawPrimes));
				}

				final SvcLoader<PreloaderProviderIntfc, Class<PreloaderProviderIntfc>> preloadProvider = new SvcLoader< >(PreloaderProviderIntfc.class);
				final ImmutableCollection<String> attributes = Lists.immutable.of("PRELOADER");

				final PreloaderIntfc primePreloader = preloadProvider
						.provider(attributes)
						.create(cache, Path.of(replaceTildeHome(inputFolderPath)), null);

				primePreloader.load();

				primeSrc = factory.getPrimeSource(primePreloader);
			}

			if (outputOpts.getOutputOpers().contains(OutputOper.PROGRESS))
			{
				final SvcLoader<ProgressProviderIntfc, Class<ProgressProviderIntfc>> progressProvider = new SvcLoader< >(ProgressProviderIntfc.class);
				final ImmutableList<String> attributes = Lists.immutable.of("PROGRESS");
				primeSrc.setDisplayProgress(progressProvider.provider(attributes).create(null));
			}

			primeSrc.setDisplayPrimeTreeMetrics(outputOpts.getOutputOpers().contains(OutputOper.PRIMETREE_METRICS));

			if (LOG.isLoggable(Level.FINE))
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.fine("DefaultInit::actionInitDefaultPrimeContent - primeSource init");
				}
			}
			primeSrc.init();
		});
	}

	private void actionHandleAdditionalBases()
	{
		if (baseOpts != null && baseOpts.getBases() != null)
		{
			final SvcLoader<BaseProviderIntfc, Class<BaseProviderIntfc>> baseProvider = new SvcLoader< >(BaseProviderIntfc.class);
			baseOpts.getBases().forEach(baseType ->
			{
				final var trackGenTime = true;
				final var method = "DefaultInit::actionHandleAdditionalBases - base :" + baseType.name();
				final Supplier<AbstractPrimeBaseGenerator> baseSupplier;
				final ImmutableList<String> baseProviderAttributes = Lists.immutable.of(baseType.name(), "DEFAULT");

				switch(baseType)
				{
					case NPRIME:
						{	// braces creates a local scope for "settings" variable here and in PRIME_TREE option below.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("maxReduce", baseOpts.getMaxReduce());
							baseSupplier = () -> baseProvider.provider(baseProviderAttributes).create(primeSrc, settings);
						}
						break;

					case THREETRIPLE:
						baseSupplier = () -> baseProvider.provider(baseProviderAttributes).create(primeSrc, null);
						break;

					case PREFIX:
						baseSupplier = () -> baseProvider.provider(baseProviderAttributes).create(primeSrc, null);
						break;

					case PRIME_TREE:
						{ 	// braces creates a local scope for "settings" variable here and in NPRIME option above.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("collTracker", PTKFactory.getCollTracker());
							baseSupplier = () -> baseProvider.provider(baseProviderAttributes).create(primeSrc, settings);
						}
						break;

					default:
						baseSupplier = null;
						if(LOG.isLoggable(Level.FINE))
						{
							LOG.fine(String.format("%s%s",method, baseOpts.getBases()));
						}
						break;
				}
				addBaseSupplierAction(baseSupplier, trackGenTime, baseType);
			}
		);
		}
	}

	private void addBaseSupplierAction(final Supplier<AbstractPrimeBaseGenerator> baseSupplier, final boolean trackGenTime, final BaseTypes baseType)
	{
		final var method = "DefaultInit::addBaseSupplierAction - base ";
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

				// TODO Some revamp needed - use logger instead of console.
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
