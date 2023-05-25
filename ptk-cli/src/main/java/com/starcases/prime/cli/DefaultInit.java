package com.starcases.prime.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.cache.Cache;

import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.list.mutable.FastList;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypes;
import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.LogPrimeDataProviderIntfc;
import com.starcases.prime.base.api.PrimeBaseGeneratorIntfc;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;

import com.starcases.prime.cache.api.CacheProviderIntfc;
import com.starcases.prime.cli.MetricsOpts.MetricOpt;
import com.starcases.prime.common.api.OutputOper;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.ProgressProviderIntfc;
import com.starcases.prime.core.impl.PTKLogger;
import com.starcases.prime.core.impl.PrimeRef;
import com.starcases.prime.graph.export.api.ExportsProviderIntfc;

import com.starcases.prime.logging.LogGraphStructure;
import com.starcases.prime.logging.LogNodeStructure;
import com.starcases.prime.metrics.api.MetricsProviderIntfc;
import com.starcases.prime.preload.api.PrimeSubset;
import com.starcases.prime.service.impl.SvcLoader;
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

		actionCreatePrimeSrc();

		actionPrepAdditionalBases();

		actionInitPrimeSourceData();

		actionEnableMetrics();

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
	private void graph(final PrimeSourceIntfc primeSrc, final BaseTypesIntfc baseType)
	{
//		try
//		{
//			final var metaDataView = new MetaDataTable();
//			final var viewList = new ArrayList<GraphListener<PrimeRefIntfc, DefaultEdge>>();
//			viewList.add(metaDataView);
//			metaDataView.setSize(400, 320);
//			metaDataView.setVisible(true);
//			final var viewDefault = new ViewDefault(primeSrc,  baseType, viewList);
//			viewDefault.viewDefault();
//		}
//		catch(IOException except)
//		{
//			if (LOG.isLoggable(Level.SEVERE))
//			{
//				LOG.severe("IOExcetion: " + except.toString());
//			}
//		}
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
			exportProvider
				.provider(attributes)
				.map(p -> p.create(primeSrc, exportWriter, null))
				.ifPresent( p ->
								{
									p.export();
									exportWriter.flush();
								}
						);
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
		LOG.info("CLI - Setting defaults");
		PTKFactory.setMaxCount(initOpts.getMaxCount());
		PTKFactory.setConfidenceLevel(initOpts.getConfidenceLevel());

		PTKFactory.setPrimeRefSetPrimeSource(PrimeRef::setPrimeSource);

		PTKFactory.setPrimeBaseCtor(PrimeMultiBaseContainer::new);

		PTKFactory.setPrimeRefRawCtor( (i) -> new PrimeRef(i).init(PTKFactory.getPrimeBaseCtor()));
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
			PTKLogger.setOutput("stdout", stdOutPath);
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
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check metrics enablement.");
		}
		if (Arrays.asList((metricOpts != null) ? metricOpts.getMetricType() : NULL_OPTS).contains(MetricOpt.ALL))
		{
			final SvcLoader<MetricsProviderIntfc, Class<MetricsProviderIntfc>> registryProviders = new SvcLoader< >(MetricsProviderIntfc.class);
			final ImmutableCollection<String> attributes = Lists.immutable.of("METRICS");
			actions.add(s ->  registryProviders.providers(attributes).forEach(p -> p.create(null).enable(true)));
		}
	}

	private void actionEnableCmdListener()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check SQL listener enablement.");
		}
		if (baseOpts != null && baseOpts.isEnableCmmandListener())
		{
			final ImmutableCollection<String> attributes = Lists.immutable.of("SQLPRIME");
			final SvcLoader<SqlProviderIntfc, Class<SqlProviderIntfc>> sqlCmdProviders = new SvcLoader< >(SqlProviderIntfc.class);

			actions.add(s -> {

					if (LOG.isLoggable(Level.INFO))
					{
						LOG.info("Starting SQL command listener - port:" + baseOpts.getCmdListenerPort());
					}

					sqlCmdProviders
						.provider(attributes)
						.map(p -> p.create(primeSrc, baseOpts.getCmdListenerPort()))
						.ifPresent(p ->
									{
										try
										{
											p.run();
										}
										catch(final InterruptedException e)
										{
											throw new RuntimeException(e);
										}
									});
			});
		}
	}

	private void actionCreatePrimeSrc()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Prep init of default prime content.");
		}
		final SvcLoader<CacheProviderIntfc, Class<CacheProviderIntfc>> cacheProvider =
				new SvcLoader< >(CacheProviderIntfc.class,
								new Class[] {}, //{javax.transaction.xa.XAResource.class},
								new Module[] {} //{ javax.transaction.xa.XAResource.class.getModule()
												);

		final ImmutableList<String> cacheAttributes = Lists.immutable.of("CACHE");

		actions.add(s -> {

			final FactoryIntfc factory = PTKFactory.getFactory();

			@SuppressWarnings({"PMD.LocalVariableNamingConventions"})
			final String CACHE_NAME = "primes";
			final String inputFolderPath = initOpts.getInputDataFolder();
			final Cache<Long,PrimeSubset> cache = cacheProvider
													.provider(cacheAttributes)
													.map(p -> p.<Long, PrimeSubset>create(Path.of(initOpts.getOutputFolder(), CACHE_NAME), null))
													.orElseThrow();

			PTKFactory.setCache(cache);

			LOG.info("actionCreatePrimeSrc *** cache:" + PTKFactory.getCache().getName());
			// Use idx 2 which would be prime 3 for determining if cache was loaded / pre-loaded propertly. Since Primes 1 and 2 may be hard coded in
			// places, it is safer to use the 1st item which is never hardcoded.
			final var cacheIdx0 = cache.get(0L);
			final var cacheIdx0idx2 = cacheIdx0 != null ? cacheIdx0.get(2) : -1L;
			final var alreadyLoaded = cacheIdx0idx2 == 3;

			final var inputFoldExist = ensureFolderExist(inputFolderPath);
			final var loadRawPrimes = initOpts.isLoadPrimes();

			if (loadRawPrimes && !alreadyLoaded && inputFoldExist)
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("CREATING PrimeSrc : Loading cache from raw files ; Cache already loaded: [%b], Input folder exists: [%b], do-load-raw-primes[%b]",  alreadyLoaded, inputFoldExist, loadRawPrimes));
				}

				PTKFactory.setInputFolderPath(Path.of(replaceTildeHome(inputFolderPath)));

				primeSrc = factory.getPrimeSource(true);
			}
			else
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("CREATING PrimeSrc: NOT loading Cache ; Cache already loaded: [%b], Input folder exists: [%b], do-load-raw-primes[%b]",  alreadyLoaded, inputFoldExist, loadRawPrimes));
				}
				primeSrc = factory.getPrimeSource(false);
			}

			if (outputOpts.getOutputOpers().contains(OutputOper.PROGRESS))
			{
				final SvcLoader<ProgressProviderIntfc, Class<ProgressProviderIntfc>> progressProvider = new SvcLoader< >(ProgressProviderIntfc.class);
				final ImmutableList<String> attributes = Lists.immutable.of("PROGRESS");

				progressProvider
					.provider(attributes)
					.map(p -> p.create(null))
					.ifPresent(p -> primeSrc.setDisplayProgress(p));
			}

			primeSrc.setDisplayDefaultBaseMetrics(outputOpts.getOutputOpers().contains(OutputOper.PRIMETREE_METRICS));
		});
	}

	private void actionInitPrimeSourceData()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Prep prime source init.");
		}
		actions.add(s -> primeSrc.init());
	}

	private void actionPrepAdditionalBases()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check enablement of bases.");
		}

		if (baseOpts != null && baseOpts.getBases() != null)
		{
			final SvcLoader<BaseProviderIntfc, Class<BaseProviderIntfc>> baseProvider = new SvcLoader< >(BaseProviderIntfc.class);
			baseOpts.getBases().forEach(baseType ->
			{
				final var trackGenTime = false;
				final var method = "DefaultInit::actionHandleAdditionalBases - base :" + baseType.name();
				final ImmutableList<String> baseProviderAttributes = Lists.immutable.of(baseType.name(), "DEFAULT");
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info("CLI - Prep base: " + baseType.name());
				}

				switch(baseType.name())
				{
					case "THREETRIPLE":
					case "PREFIX":
						baseProvider
							.provider(baseProviderAttributes)
							.ifPresent
								(
									p -> actions.add(s -> primeSrc.addBaseGenerator(addBaseDecorators(p.create(null).assignPrimeSrc(primeSrc), trackGenTime, baseType)))
								);
						break;

					case "NPRIME":
						{	// braces creates a local scope for "settings" variable here and in PRIME_TREE option below.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("maxReduce", baseOpts.getMaxReduce());
							baseProvider
								.provider(baseProviderAttributes)
								.ifPresent
									(
										p -> actions.add(s -> primeSrc.addBaseGenerator(addBaseDecorators(p.create(settings).assignPrimeSrc(primeSrc), trackGenTime, baseType)))
									);
						}
						break;

					case "PRIME_TREE":
						{ 	// braces creates a local scope for "settings" variable here and in NPRIME option above.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("collTracker", PTKFactory.getCollTracker());
							baseProvider
							.provider(baseProviderAttributes)
							.ifPresent
								(
									p -> actions.add(s -> primeSrc.addBaseGenerator(addBaseDecorators(p.create(settings).assignPrimeSrc(primeSrc), trackGenTime, baseType)))
								);
						}
						break;

					default:
						if(LOG.isLoggable(Level.FINE))
						{
							LOG.fine(String.format("%s%s",method, baseOpts.getBases()));
						}
						break;
				}
			}
		);
		}
	}

	private PrimeBaseGeneratorIntfc addBaseDecorators(@NonNull final PrimeBaseGeneratorIntfc base, final boolean trackGenTime, final BaseTypesIntfc baseType)
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("DECORATE base supplier - " + baseType.name());
		}

		var decoratedBase = base;

		if (baseOpts.isUseBaseFile())
		{
			PTKLogger.setOutput(baseType.name(), this.decorateFileName(baseType.name(), "base", "log"));
		}

		if (trackGenTime)
		{
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("DECORATE base supplier [%s] with Timer", baseType.name()));
			}
			//decoratedBase = new LogTimerDecorator(decoratedBase);
		}

		if (outputOpts.getOutputOpers().contains(OutputOper.CREATE))
		{
			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("DECORATE base supplier [%s] with Logger", baseType.name()));
			}
			// FIXME
			//decoratedBase = new LogBaseGenDecorator(decoratedBase);
		}

		//FIXME Handle prefer parallel ; base.doPreferParallel(initOpts.isPreferParallel());

		return decoratedBase;
	}
	private void actionHandleOutputs()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check base logging enablement.");
		}

		if (outputOpts != null && outputOpts.getOutputOpers() != null && !outputOpts.getOutputOpers().isEmpty())
		{
			if (LOG.isLoggable(Level.INFO))
			{

				LOG.info(String.format("%s%s", "CLI - Prep logger outputs: ", outputOpts.getOutputOpers().stream().map(Object::toString).collect(Collectors.joining(","))));
			}

			final SvcLoader<LogPrimeDataProviderIntfc, Class<LogPrimeDataProviderIntfc>> logBaseDataProvider = new SvcLoader< >(LogPrimeDataProviderIntfc.class);

			outputOpts.getOutputOpers().forEach(oo ->
			{
				final ImmutableList<String> attributes = Lists.immutable.of(oo.toString());

				switch(oo.toString())
				{
				case "THREETRIPLE":
					if (isBaseSelected(BaseTypes.THREETRIPLE))
					{
						actions.add(s ->
							logBaseDataProvider
							.provider(attributes)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(initOpts.isPreferParallel()).outputLogs()) );
					}
					break;

				case "NPRIME":
					if (isBaseSelected(BaseTypes.NPRIME))
					{
						actions.add(s ->
							logBaseDataProvider
							.provider(attributes)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(initOpts.isPreferParallel()).outputLogs()) );
					}
					break;

				case "PREFIX":
					if (isBaseSelected(BaseTypes.PREFIX))
					{
						actions.add(s ->
							logBaseDataProvider
							.provider(attributes)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(false).outputLogs()) );
					}
					break;

				case "PRIME_TREE":
					if (isBaseSelected(BaseTypes.PRIME_TREE))
					{
						actions.add(s ->
							logBaseDataProvider
							.provider(attributes)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(false).outputLogs()) );
					}
					break;

				case "BASES":
					if (isBaseSelected(BaseTypes.THREETRIPLE))
					{
						final ImmutableList<String> attributesAll = Lists.immutable.of(BaseTypes.THREETRIPLE.name());
						actions.add(s ->
							logBaseDataProvider
							.provider(attributesAll)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(initOpts.isPreferParallel()).outputLogs()) );
					}
					if (isBaseSelected(BaseTypes.NPRIME))
					{
						final ImmutableList<String> attributesAll = Lists.immutable.of(BaseTypes.NPRIME.name());
						actions.add(s ->
							logBaseDataProvider
							.provider(attributesAll)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(initOpts.isPreferParallel()).outputLogs()) );
					}
					if (isBaseSelected(BaseTypes.PREFIX))
					{
						final ImmutableList<String> attributesAll = Lists.immutable.of(BaseTypes.PREFIX.name());
						actions.add(s ->
							logBaseDataProvider
							.provider(attributesAll)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(false).outputLogs()) );
					}
					if (isBaseSelected(BaseTypes.PRIME_TREE))
					{
						final ImmutableList<String> attributesAll = Lists.immutable.of(BaseTypes.PRIME_TREE.name());
						actions.add(s ->
							logBaseDataProvider
							.provider(attributesAll)
							.ifPresent(p -> p.create(primeSrc, null).doPreferParallel(false).outputLogs()) );
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

	private boolean isBaseSelected(final BaseTypesIntfc baseType)
	{
		return baseOpts != null && baseOpts.getBases().contains(baseType);
	}

	private void actionHandleGraphing()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check Graph enablement.");
		}

		if (graphOpts != null && graphOpts.getGraphType() != null && graphOpts.getGraphType() == Graph.DEFAULT)
		{
			actions.add(s -> graph(primeSrc, BaseTypes.DEFAULT));
		}
	}

	private void actionHandleExports()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check exports enablement.");
		}
		if (exportOpts != null && exportOpts.getExportType() != null && exportOpts.getExportType() == Export.GML)
		{
			actions.add(s -> export(primeSrc));
		}
	}

	private void executeActions()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Execute configured actions.");
		}
		actions.forEach(c -> c.accept("execute action"));
	}
}
