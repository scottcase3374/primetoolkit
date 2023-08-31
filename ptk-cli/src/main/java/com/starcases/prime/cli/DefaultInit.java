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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.base.api.LogPrimeDataProviderIntfc;
import com.starcases.prime.base.api.BaseGenDecorProviderIntfc;
import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.base.impl.BaseTypes;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;

import com.starcases.prime.cache.api.CacheProviderIntfc;
import com.starcases.prime.cache.api.PersistedCacheIntfc;
import com.starcases.prime.cache.api.persistload.PersistLoaderProviderIntfc;
import com.starcases.prime.cache.api.primetext.PrimeTextFileLoaderProviderIntfc;
import com.starcases.prime.cache.api.primetext.PrimeTextFileloaderIntfc;
import com.starcases.prime.cache.api.subset.PrimeSubsetProviderIntfc;
import com.starcases.prime.cache.impl.prime.PrimeSubsetCacheImpl;
import com.starcases.prime.cli.MetricsOpts.MetricOpt;
import com.starcases.prime.common.api.OutputOper;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.ProgressProviderIntfc;
import com.starcases.prime.core.impl.PrimeRef;
import com.starcases.prime.core.impl.PrimeSource;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc;
import com.starcases.prime.graph.export.api.ExportsProviderIntfc;
import com.starcases.prime.graph.visualize.impl.ViewDefault;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.OutputableIntfc;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.logging.LogGraphStructure;
import com.starcases.prime.logging.LogNodeStructure;
import com.starcases.prime.metrics.api.MetricProviderIntfc;
import com.starcases.prime.metrics.api.MetricsRegistryProviderIntfc;
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
	 * for matching output type names to base-type names
	 */
	private static Predicate2<BaseTypesIntfc, String> baseMatchPred = (base, outputType) -> base.name().equals(outputType);

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


	private final  ImmutableCollection<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider(Lists.immutable.of("GLOBAL_BASE_TYPES")).orElseThrow().create();

	private final  StatusHandlerIntfc statusHandler =
			new SvcLoader<StatusHandlerProviderIntfc, Class<StatusHandlerProviderIntfc>>(StatusHandlerProviderIntfc.class)
				.provider(Lists.immutable.of("STATUS_HANDLER")).orElseThrow().create();

	private static final SvcLoader<CollectionTrackerProviderIntfc, Class<CollectionTrackerProviderIntfc>> collTreeProvider = new SvcLoader< >(CollectionTrackerProviderIntfc.class);
	private static final CollectionTrackerIntfc collTracker = collTreeProvider
					.provider(Lists.immutable.of("COLLECTION_TRACKER"))
					.map(p -> p.create(null))
					.orElse(null);

	private static final SvcLoader<MetricProviderIntfc, Class<MetricProviderIntfc>> metricProviderSvc = new SvcLoader< >(MetricProviderIntfc.class);
	private static final MetricProviderIntfc metricProvider = metricProviderSvc
					.provider(Lists.immutable.of("METRIC_PROVIDER"))
					.map(p -> p.create(null))
					.orElse(null);

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

		actionEnableMetrics();

		actionCreatePrimeSrc();

		actionPrepAdditionalBases();

		actionInitPrimeSourceData();

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
		try
		{
//			final SvcLoader<VisualizationProviderIntfc, Class<VisualizationProviderIntfc>> visualizationProvider = new SvcLoader< >(VisualizationProviderIntfc.class);
//
//			final MutableList<VisualizationProviderIntfc> list = visualizationProvider
//				.providers(Lists.immutable.of("VISUALIZATION"))
//				.collectIf(f -> f.countAttributesMatch( Lists.immutable.of("CIRCULAR_LAYOUT", "COMPACT_TREE_LAYOUT", "METADATA_TABLE")) > 0, p -> p)
//				.toList()
//				;

			final var viewList = new ArrayList<GraphListener<PrimeRefIntfc, DefaultEdge>>();
//			list.flatCollect(p -> p.create(null, null)).forEach( i ->
//					{
//						i.setSize(400, 320);
//						i.setVisible(true);
//						viewList.add(i);
//					}
//					);

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
			exportProvider
				.provider(attributes)
				.map(p -> p.create(primeSrc, exportWriter, null))
				.ifPresentOrElse( p ->
								{
									p.export();
									exportWriter.flush();
								}
							, () -> statusHandler.handleError(() -> "No Export GML provider", Level.SEVERE, false)
						);
		}
		catch(final IOException except)
		{
			statusHandler.handleError(
					  () -> "Exception during export"
					, Level.SEVERE
					, except,
					false,
					true);
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
			// Point standard-out to our pre-generated output filename.
			this.statusHandler.setOutput("stdout", stdOutPath);
		}
		else
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("ERROR: could not set stdout to provided destination. " );
			}
		}
	}

	private void actionEnableMetrics()
	{
		// TODO allow enablement of individual metrics

		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check metrics enablement.");
		}
		if (Arrays.asList((metricOpts != null) ? metricOpts.getMetricType() : NULL_OPTS).contains(MetricOpt.ALL))
		{
			final SvcLoader<MetricsRegistryProviderIntfc, Class<MetricsRegistryProviderIntfc>> registryProviders = new SvcLoader< >(MetricsRegistryProviderIntfc.class);
			final ImmutableCollection<String> attributes = Lists.immutable.of("METRICS");
			actions.add(s ->  registryProviders
								.providers(attributes)
								.tap(p -> LOG.info("metric: " + p.toString()))
								.forEach(p -> p.create(null).create(null)));
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
						.ifPresentOrElse(p ->
									{
										try
										{
											p.run();
										}
										catch(final InterruptedException e)
										{
											throw new RuntimeException(e);
										}
									},
									() -> statusHandler.handleError(() -> "No SQLCommand Provider", Level.SEVERE, false));
			});
		}
	}

	private void actionCreatePrimeSrc()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Prep init of default prime content.");
		}

		actions.add(s -> {

			// Create cache instance and if requested - clear out existing primes [persisted]; no in-memory primes should
			// exist yet since we haven't loaded the raw primes nor have we tried to load persisted primes.
			@SuppressWarnings({"PMD.LocalVariableNamingConventions"})
			final String CACHE_NAME = "primes";
			final String inputFolderPath = initOpts.getInputDataFolder();
			final boolean loadRawPrimes = initOpts.isLoadPrimes();
			final Path CACHE_PATH = Path.of(replaceTildeHome(initOpts.getOutputFolder()), CACHE_NAME);
			final PersistedCacheIntfc<Long> cache = new SvcLoader< >(CacheProviderIntfc.class)
													.provider(Lists.immutable.of("CACHE", "PRIMES"))
													.map(p -> p.create(
																CACHE_PATH,
																loadRawPrimes /* clear any existing prime cache first */
																) )
													.orElseThrow();

			final var inputFoldExist = ensureFolderExist(inputFolderPath);

			// Setup for persistent data load
			final PrimeSubsetProviderIntfc subsetProvider = new SvcLoader<PrimeSubsetProviderIntfc, Class<PrimeSubsetProviderIntfc>>(PrimeSubsetProviderIntfc.class)
					.provider(Lists.immutable.of("PRIMESUBSET")).orElseThrow();

			// Load previously persisted primes (primes previously cached - NOT the raw text prime data)
			new SvcLoader< >(PersistLoaderProviderIntfc.class)
				.provider(Lists.immutable.of("PERSISTLOADER", "PRIMES"))
				.ifPresent(p -> p.create(cache, CACHE_PATH, subsetProvider, null).process());

//			new SvcLoader< >(PersistLoaderProviderIntfc.class)
//				.provider(Lists.immutable.of("PERSISTLOADER", "BASES"))
//				.ifPresent(p -> p.create(cache, CACHE_PATH, subsetProvider, null).process());

			if (loadRawPrimes && (cache.get(0L) == null))
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("CREATING PrimeSrc : Loading cache from raw files ; Input folder exists: [%b], do-load-raw-primes[%b]", inputFoldExist, loadRawPrimes));
				}

				final SvcLoader<PrimeTextFileLoaderProviderIntfc, Class<PrimeTextFileLoaderProviderIntfc>> preloadProvider =
						new SvcLoader< >(PrimeTextFileLoaderProviderIntfc.class);

				// Constructor calls required methods to load data.
				final PrimeTextFileloaderIntfc primePreloader = preloadProvider
						.provider(Lists.immutable.of("PRELOADER"))
						.map(p -> p.create(cache, Path.of(replaceTildeHome(inputFolderPath)), null).orElse(null))
						.orElse(null);

				primeSrc = getPrimeSource(cache);
				cache.unwrap(PrimeSubsetCacheImpl.class).persistAll();
			}
			else
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("CREATING PrimeSrc: NOT loading Cache ; Input folder exists: [%b], do-load-raw-primes[%b]", inputFoldExist, loadRawPrimes));
				}

				primeSrc = getPrimeSource(cache);
			}

			if (outputOpts.getOutputOpers().contains(OutputOper.PROGRESS))
			{
				final SvcLoader<ProgressProviderIntfc, Class<ProgressProviderIntfc>> progressProvider = new SvcLoader< >(ProgressProviderIntfc.class);
				final ImmutableList<String> attributes = Lists.immutable.of("PROGRESS");

				progressProvider
					.provider(attributes)
					.map(p -> p.create(null))
					.ifPresentOrElse(p -> primeSrc.setDisplayProgress(p),
							() -> statusHandler.handleError(() -> "No Progress provider", Level.SEVERE, false));
			}

			primeSrc.setDisplayDefaultBaseMetrics(outputOpts.getOutputOpers().contains(OutputOper.PRIMETREE_METRICS));
		});
	}

	private PrimeSourceFactoryIntfc getPrimeSource(@NonNull PersistedCacheIntfc<Long> cache)
	{
		final Consumer<PrimeSourceIntfc> c = PrimeRef::setPrimeSource;
		final ImmutableList<Consumer<PrimeSourceIntfc>> consumers = Lists.immutable.of(c);
		final Function<Long, PrimeRefFactoryIntfc>  f = i -> new PrimeRef(i).init(PrimeMultiBaseContainer::new);

		return new PrimeSource(initOpts.getMaxCount()
				, consumers
				, f
				,collTracker
				,cache
				,metricProvider
				);
	}

	private void actionInitPrimeSourceData()
	{

		actions.add(s -> primeSrc.init());
	}

	private void actionPrepAdditionalBases()
	{
		statusHandler.dbgOutput("%s", "CLI - Check enablement of bases.");
		if (baseOpts != null && baseOpts.getBases() != null)
		{
			final SvcLoader<BaseProviderIntfc, Class<BaseProviderIntfc>> baseProvider = new SvcLoader< >(BaseProviderIntfc.class);

			baseOpts.getBases().forEach(baseType ->
			{
				setupBaseLogConfig(baseType);

				final var trackGenTime = true;
				final var method = "DefaultInit::actionHandleAdditionalBases - base :" + baseType.name();
				final ImmutableList<String> baseProviderAttributes = Lists.immutable.of(baseType.name(), "DEFAULT");

				statusHandler.dbgOutput("CLI - Prep base: %s", baseType.name());
				switch(baseType.name())
				{
					case "TRIPLE":
					case "PREFIX":
						baseProvider
							.provider(baseProviderAttributes)
							.ifPresentOrElse
								(
									p -> actions.add(s -> primeSrc
															.addBaseGenerator(
																	addBaseDecorators(
																			p.create(null)
																			 .assignPrimeSrc(primeSrc)
																			 .doPreferParallel(initOpts.isPreferParallel())
																			,trackGenTime, baseType)))
									, () -> statusHandler.errorOutput(baseType, "ERROR: No provider for %s", baseType.toString())
								);
						break;

					case "NPRIME":
						{	// braces creates a local scope for "settings" variable here and in PRIME_TREE option below.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("maxReduce", baseOpts.getMaxReduce());
							baseProvider
								.provider(baseProviderAttributes)
								.ifPresentOrElse
									(
										p -> actions.add(s -> primeSrc.addBaseGenerator(addBaseDecorators(p.create(settings).assignPrimeSrc(primeSrc), trackGenTime, baseType)))
										, () -> statusHandler.dbgOutput(baseType, "ERROR: No provider for %s", baseType.toString())
									);
						}
						break;

					case "PRIME_TREE":
						{ 	// braces creates a local scope for "settings" variable here and in NPRIME option above.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("collTracker", collTracker);
							baseProvider
							.provider(baseProviderAttributes)
							.ifPresentOrElse
								(
									p -> actions.add(s -> primeSrc
																.addBaseGenerator(
																		addBaseDecorators(
																				p.create(settings)
																					.assignPrimeSrc(primeSrc)
																				, trackGenTime, baseType)))
									, () -> statusHandler.dbgOutput(baseType, "ERROR: No provider for %s", baseType.toString())
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

	private void setupBaseLogConfig(@NonNull final BaseTypesIntfc baseType)
	{
		if (baseOpts.isUseBaseFile())
		{
			statusHandler.setOutput(baseType.name(), this.decorateFileName(baseType.name(), "base", "log"));
		}
	}

	private BaseGenIntfc addBaseDecorators(@NonNull final BaseGenIntfc base, final boolean trackGenTime, @NonNull final BaseTypesIntfc baseType)
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info(String.format("DECORATE base supplier - base[%s] track-gen-time[%b]", baseType.name(), trackGenTime));
		}

		final BaseGenIntfc decoratedBase [] = {base};

		if (trackGenTime)
		{
			final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("METRIC_BASE_GENERATOR_DECORATOR");
			final SvcLoader<BaseGenDecorProviderIntfc, Class<BaseGenDecorProviderIntfc>> metricDecorProvider =
					new SvcLoader< >(BaseGenDecorProviderIntfc.class);

			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("DECORATE base supplier [%s] with Timer", baseType.name()));
			}

			metricDecorProvider.provider(ATTRIBUTES).ifPresentOrElse(p -> decoratedBase[0] = p.create(decoratedBase[0]), () -> statusHandler.errorOutput(baseType, "ERROR: No Metric-decor-provider"));
		}

		if (outputOpts.getOutputOpers().contains(OutputOper.CREATE_PRIMES))
		{
			final ImmutableList<String> ATTRIBUTES = Lists.immutable.of("LOG_BASE_GENERATOR_DECORATOR");
			final SvcLoader<BaseGenDecorProviderIntfc, Class<BaseGenDecorProviderIntfc>> logDecorProvider =
					new SvcLoader< >(BaseGenDecorProviderIntfc.class);

			if (LOG.isLoggable(Level.INFO))
			{
				LOG.info(String.format("DECORATE base supplier [%s] with Logger", baseType.name()));
			}
			logDecorProvider.provider(ATTRIBUTES).ifPresentOrElse(p -> decoratedBase[0] = p.create(decoratedBase[0]), () -> statusHandler.dbgOutput(baseType, "ERROR: No Log-decor-provider"));
		}
		return decoratedBase[0];
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

				LOG.info(String.format("%s%s", "CLI - Prep logger outputs: ",
						outputOpts
							.getOutputOpers()
							.stream()
							.map(Object::toString)
							.collect(Collectors.joining(","))));
			}

			final SvcLoader<LogPrimeDataProviderIntfc, Class<LogPrimeDataProviderIntfc>> logBaseDataProvider = new SvcLoader< >(LogPrimeDataProviderIntfc.class);

			final ImmutableMultimap<Boolean, OutputableIntfc> baseNotBaseColl =
					outputOpts
					.getOutputOpers()
					.groupBy(o -> BASE_TYPES.anySatisfyWith(baseMatchPred, o.toString()));

			baseNotBaseColl.forEachKeyMultiValues((b, oVals) ->
						{
							if (b) // meaning individual bases specified
							{
								oVals.forEach(o ->
									{
										final ImmutableList<String> attributes = Lists.immutable.of(o.toString());

										actions.add(s ->
										logBaseDataProvider
										.provider(attributes)
										.ifPresentOrElse(p ->
															p.create(primeSrc, null)
															 .doPreferParallel(initOpts.isPreferParallel())
															 .outputLogs()
															,() -> statusHandler.errorOutput("ERROR: No %s provider", o.toString())
														)
												);
									});
							}
							else // meaning either non-base specified or the value "bases" indicating each active base.
							{
								oVals.forEach(o ->
											{
												switch(o.toString())
												{
												case "BASES":

													if (baseOpts != null)
													{
														baseOpts.getBases().forEach(base ->
																{
																	final ImmutableList<String> attributes = Lists.immutable.of(base.toString());

																	actions.add(s ->
																		logBaseDataProvider
																		.provider(attributes)
																		.ifPresentOrElse(p ->
																				p.create(primeSrc, null)
																				 .doPreferParallel(initOpts.isPreferParallel())
																				 .outputLogs()
																			   ,() -> statusHandler.dbgOutput("ERROR: No TripleLog provider")));
																 }
																);
													}

												case "GRAPHSTRUCT":
													actions.add(s -> new LogGraphStructure(primeSrc, BaseTypes.DEFAULT ).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
													break;

												case "NODESTRUCT":
													actions.add(s -> new LogNodeStructure(primeSrc).doPreferParallel(initOpts.isPreferParallel()).outputLogs() );
													break;

												default:
													break;
												}
											}
										);
							}
					});
			}
		}

	private void actionHandleGraphing()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check Graph enablement.");
		}

		if (graphOpts != null && graphOpts.getGraphType() != null && graphOpts.getGraphType() != null)
		{
			System.out.println("**** Graphing enabled");
			actions.add(s -> graph(primeSrc, BASE_TYPES.select(p -> p.name().equals(graphOpts.getGraphType().name())).getOnly() ) );
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
