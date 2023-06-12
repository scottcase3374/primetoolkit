package com.starcases.prime.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.cache.Cache;

import org.eclipse.collections.api.block.predicate.Predicate2;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.impl.list.mutable.FastList;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypesIntfc;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.base.api.LogPrimeDataProviderIntfc;
import com.starcases.prime.base.api.BaseGenDecorProviderIntfc;
import com.starcases.prime.base.api.BaseGenIntfc;
import com.starcases.prime.base.impl.BaseTypes;
import com.starcases.prime.base.impl.PrimeMultiBaseContainer;

import com.starcases.prime.cache.api.CacheProviderIntfc;
import com.starcases.prime.cli.MetricsOpts.MetricOpt;
import com.starcases.prime.common.api.OutputOper;
import com.starcases.prime.common.api.PTKLogger;
import com.starcases.prime.core.api.FactoryIntfc;
import com.starcases.prime.core.api.OutputableIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.api.ProgressProviderIntfc;
import com.starcases.prime.core.impl.PrimeRef;
import com.starcases.prime.graph.export.api.ExportsProviderIntfc;

import com.starcases.prime.logging.LogGraphStructure;
import com.starcases.prime.logging.LogNodeStructure;
import com.starcases.prime.metrics.api.MetricsRegistryProviderIntfc;
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

	private static final BiFunction<Long, Path, PrimeSubset> loader = (K, P) -> null; //Paths.get(P.toString(), K.toString()).toFile();

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


	final  ImmutableCollection<BaseTypesIntfc> BASE_TYPES =
			new SvcLoader<BaseTypesProviderIntfc, Class<BaseTypesProviderIntfc>>(BaseTypesProviderIntfc.class)
				.provider(Lists.immutable.of("GLOBAL_BASE_TYPES")).orElseThrow().create();

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
				.ifPresentOrElse( p ->
								{
									p.export();
									exportWriter.flush();
								}
							, () -> PTKLogger.dbgOutput("ERROR: %s", "No Export GML provider")
						);
		}
		catch(IOException except)
		{
			if (LOG.isLoggable(Level.SEVERE))
			{
				LOG.severe("Exception "+ except);
				PTKLogger.output("Exception: %s", except.toString());
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
								.tap(p -> System.out.println("metric: " + p.toString()))
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
									() -> PTKLogger.dbgOutput("ERROR: %s" , "No SQLCommand Provider"));
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
				new SvcLoader< >(CacheProviderIntfc.class);

		final ImmutableList<String> cacheAttributes = Lists.immutable.of("CACHE");

		actions.add(s -> {

			final FactoryIntfc factory = PTKFactory.getFactory();

			@SuppressWarnings({"PMD.LocalVariableNamingConventions"})
			final String CACHE_NAME = "primes";
			final String inputFolderPath = initOpts.getInputDataFolder();
			final Cache<Long,PrimeSubset> cache = cacheProvider
													.provider(cacheAttributes)
													.map(p -> p.<Long, PrimeSubset>create(Path.of(replaceTildeHome(initOpts.getOutputFolder()), CACHE_NAME), loader, null))
													.orElseThrow();

			PTKFactory.setCache(cache);

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
					.ifPresentOrElse(p -> primeSrc.setDisplayProgress(p),
							() -> PTKLogger.dbgOutput("ERROR: %s", "No Progress provider"));
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
				setupBaseLogConfig(baseType);

				final var trackGenTime = true;
				final var method = "DefaultInit::actionHandleAdditionalBases - base :" + baseType.name();
				final ImmutableList<String> baseProviderAttributes = Lists.immutable.of(baseType.name(), "DEFAULT");
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info("CLI - Prep base: " + baseType.name());
				}

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
									, () -> PTKLogger.dbgOutput(baseType, "ERROR: No provider for %s", baseType.toString())
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
										, () -> PTKLogger.dbgOutput(baseType, "ERROR: No provider for %s", baseType.toString())
									);
						}
						break;

					case "PRIME_TREE":
						{ 	// braces creates a local scope for "settings" variable here and in NPRIME option above.
							final ImmutableMap<String, Object> settings = Maps.immutable.of("collTracker", PTKFactory.getCollTracker());
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
									, () -> PTKLogger.dbgOutput(baseType, "ERROR: No provider for %s", baseType.toString())
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
			PTKLogger.setOutput(baseType.name(), this.decorateFileName(baseType.name(), "base", "log"));
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

			// PTKFactory.getMetricProvider ()
			metricDecorProvider.provider(ATTRIBUTES).ifPresentOrElse(p -> decoratedBase[0] = p.create(decoratedBase[0]), () -> PTKLogger.dbgOutput(baseType, "ERROR: No Metric-decor-provider"));
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
			logDecorProvider.provider(ATTRIBUTES).ifPresentOrElse(p -> decoratedBase[0] = p.create(decoratedBase[0]), () -> PTKLogger.dbgOutput(baseType, "ERROR: No Log-decor-provider"));
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
															,() -> PTKLogger.dbgOutput("ERROR: No %s provider", o.toString())
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
																			   ,() -> PTKLogger.dbgOutput("ERROR: No TripleLog provider")));
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