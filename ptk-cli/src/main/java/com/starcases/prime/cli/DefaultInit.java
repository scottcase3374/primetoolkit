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
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.ImmutableMultimap;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.MutableMapFactoryImpl;
import org.jgrapht.event.GraphListener;
import org.jgrapht.graph.DefaultEdge;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import com.starcases.prime.base.api.BaseProviderIntfc;
import com.starcases.prime.base.api.BaseTypesProviderIntfc;
import com.starcases.prime.base.api.LogPrimeDataProviderIntfc;
import com.starcases.prime.base.impl.BaseTypes;
import com.starcases.prime.cache.api.primetext.PrimeTextFileLoaderProviderIntfc;
import com.starcases.prime.core.api.PrimeRefFactoryIntfc;
import com.starcases.prime.core.api.PrimeRefIntfc;
import com.starcases.prime.core.api.PrimeSourceFactoryIntfc;
import com.starcases.prime.core.api.PrimeSourceIntfc;
import com.starcases.prime.core.impl.PrimeRef;
import com.starcases.prime.core.impl.PrimeSource;
import com.starcases.prime.datamgmt.api.CollectionTrackerIntfc;
import com.starcases.prime.datamgmt.api.CollectionTrackerProviderIntfc;
import com.starcases.prime.graph.export.api.ExportsProviderIntfc;
import com.starcases.prime.graph.visualize.impl.ViewDefault;
import com.starcases.prime.kern.api.BaseTypesIntfc;
import com.starcases.prime.kern.api.OutputableIntfc;
import com.starcases.prime.kern.api.PtkException;
import com.starcases.prime.kern.api.StatusHandlerProviderIntfc;
import com.starcases.prime.kern.api.StatusHandlerIntfc;
import com.starcases.prime.logging.LogGraphStructure;
import com.starcases.prime.logging.LogNodeStructure;
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
	private static final Predicate2<BaseTypesIntfc, String> baseMatchPred = (base, outputType) -> base.name().equals(outputType);

	private static DB ptkDB;
	private static final MutableMap<String, DB> baseDBS = MutableMapFactoryImpl.INSTANCE.empty();

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

		actionInitBaseGenerators();

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

	/**
	 * Normalize the path and insert identification info into the filename.
	 * @param base
	 * @param fileName
	 * @param extension
	 * @return
	 */
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

	private void actionEnableCmdListener()
	{
		if (LOG.isLoggable(Level.INFO))
		{
			LOG.info("CLI - Check SQL listener enablement.");
		}
		if (baseOpts != null && initOpts.isEnableCmmandListener())
		{
			final ImmutableCollection<String> attributes = Lists.immutable.of("SQLPRIME");
			final SvcLoader<SqlProviderIntfc, Class<SqlProviderIntfc>> sqlCmdProviders = new SvcLoader< >(SqlProviderIntfc.class);

			actions.add(s -> {

					var pRef = primeSrc.getPrimeRefForIdx(32).get();
					System.out.println(String.format("##### Index: %d, Prime: %d, BaseType: %s, Bases: %s ",
							pRef.getPrimeRefIdx(),
							pRef.getPrime(),
							BASE_TYPES.select(b -> b.name().equals("PREFIX")).getOnly(),
							Arrays.toString(pRef.getPrimeBases(BASE_TYPES.select(b -> b.name().equals("PREFIX")).getOnly() ))));

					if (LOG.isLoggable(Level.INFO))
					{
						LOG.info("Starting SQL command listener - port:" + initOpts.getCmdListenerPort());
					}

					sqlCmdProviders
						.provider(attributes)
						.map(p -> p.create(primeSrc, initOpts.getCmdListenerPort()))
						.ifPresentOrElse(p ->
									{
										try
										{
											p.run();
										}
										catch(final InterruptedException e)
										{
											throw new PtkException(e);
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

			//
			//  Primes setup
			//

			// Create cache instance and if requested - clear out existing primes [persisted]; no in-memory primes should
			// exist yet since we haven't loaded the raw primes nor have we tried to load persisted primes.
			final String CACHE_NAME = "primes";

			final String inputFolderPath = initOpts.getInputDataFolder();
			final var inputFoldExist = ensureFolderExist(inputFolderPath);

			final Path homePath =  Path.of(replaceTildeHome(inputFolderPath)).getParent();
			final Path dbPath = Path.of(homePath.normalize().toString(), "ptkdb.mapdb");

			ptkDB = DBMaker
		    .fileDB(dbPath.normalize().toString())
		    .fileMmapEnable()            // Always enable mmap
		    .fileMmapPreclearDisable()   // Make mmap file faster
		    .allocateStartSize(5L * 1024 * 1024 * 1024) // 5 GB
		    .allocateIncrement(1024L * 1024 * 1024) // 1 GB
		    .checksumHeaderBypass()
		    .make();

			final boolean loadRawPrimes = initOpts.isLoadPrimes();
			var primeCache = ptkDB
								.treeMap(CACHE_NAME)
								.keySerializer(Serializer.LONG)
								.valueSerializer(Serializer.LONG)
								.createOrOpen();

			if (loadRawPrimes)
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("CREATING PrimeSrc : Loading cache from raw files ; Input folder exists: [%b], do-load-raw-primes[%b]", inputFoldExist, loadRawPrimes));
				}

				final SvcLoader<PrimeTextFileLoaderProviderIntfc, Class<PrimeTextFileLoaderProviderIntfc>> primePreloadProvider =
						new SvcLoader< >(PrimeTextFileLoaderProviderIntfc.class);

				// Constructor calls methods to load data.
				primePreloadProvider
						.provider(Lists.immutable.of("PRELOADER"))
						.map(p -> p.create(primeCache, Path.of(replaceTildeHome(inputFolderPath)), null).orElse(null))
						.ifPresentOrElse(
								 preloader -> 	{
									 				LOG.fine("Raw source primes loaded.");
								 				}
								, () -> LOG.warning("No Prime Raw Text preloader found."));
			}
			else
			{
				if (LOG.isLoggable(Level.INFO))
				{
					LOG.info(String.format("CREATING PrimeSrc: NOT loading Cache ; Input folder exists: [%b], do-load-raw-primes[%b]", inputFoldExist, loadRawPrimes));
				}
			}

			primeSrc = getPrimeSource(primeCache);

			System.out.println(String.format("***** Prime for index 4: [%d]", primeSrc.getPrimeForIdx(4L).orElse(-1)));
		});
	}

	private PrimeSourceFactoryIntfc getPrimeSource(@NonNull final BTreeMap<Long, Long> primeCache)
	{
		final Consumer<PrimeSourceIntfc> c = PrimeRef::setPrimeSource;
		final ImmutableList<Consumer<PrimeSourceIntfc>> consumers = Lists.immutable.of(c);
		final Function<Long, PrimeRefFactoryIntfc>  f = PrimeRef::new;

		var pSrc = new PrimeSource(initOpts.getMaxCount()
				, consumers
				, f
				,collTracker
				,primeCache
				);

		return pSrc;
	}

	private void actionInitPrimeSourceData()
	{
		actions.add(s -> {
			primeSrc.setCreateBases(baseOpts != null ? baseOpts.isCreateBases() : false);
			primeSrc.init();
			baseDBS.forEach(b -> b.commit());
		});
	}

	private void actionInitBaseGenerators()
	{
		statusHandler.dbgOutput("%s", "CLI - Check enablement of bases.");
		if (baseOpts != null && baseOpts.getBases() != null)
		{
			final SvcLoader<BaseProviderIntfc, Class<BaseProviderIntfc>> baseProvider = new SvcLoader< >(BaseProviderIntfc.class);

			baseOpts.getBases().forEach(
					baseType ->
			{
				setupBaseLogConfig(baseType);

				// base cache setup
				final String cacheNameForBaseType = baseType.name();
				final Path cachePathForBaseType = Path.of(replaceTildeHome(initOpts.getOutputFolder()), cacheNameForBaseType);
				ensureFolderExist(cachePathForBaseType.toString());

				// base generator setup
				final ImmutableList<String> baseProviderAttributes = Lists.immutable.of(baseType.name(), "DEFAULT");

				statusHandler.dbgOutput("CLI - Prep base: %s", baseType.name());

				final String inputFolderPath = initOpts.getInputDataFolder();
				final Path homePath =  Path.of(replaceTildeHome(inputFolderPath)).getParent();
				final Path dbPath = Path.of(homePath.normalize().toString(), baseType.name() + ".mapdb");

				final DB db = DBMaker
					    .fileDB(dbPath.normalize().toString())
					    .fileMmapEnable()            // Always enable mmap
					    .fileMmapPreclearDisable()   // Make mmap file faster
					    .allocateStartSize(5L * 1024 * 1024 * 1024) // 5 GB
					    .allocateIncrement(1024L * 1024 * 1024) // 1 GB
					    .transactionEnable()
					    .checksumHeaderBypass()
					    .make();

				baseDBS.put(baseType.name(), db);

				final var baseSrc = db.treeMap(cacheNameForBaseType, Serializer.LONG, Serializer.LONG_ARRAY).createOrOpen();
				PrimeRef.setPrimeBases(baseType, baseSrc);

				ImmutableMap<String, Object> settings = Maps.immutable.empty();
				if (baseType.name().equals("NPRIME"))
				{
					settings = Maps.immutable.of("maxReduce", baseOpts.getMaxReduce());
				}
				else if (baseType.name().equals("PRIME_TREE"))
				{
					settings = Maps.immutable.of("collTracker", collTracker);
				}
				else
				{
					settings = Maps.immutable.empty();
				}

				// System provided: TRIPLE, TRIPLENG, PREFIX_PRIME + user provided
				final ImmutableMap<String, Object> settingsFinal = settings;
				baseProvider
					.provider(baseProviderAttributes)
					.ifPresentOrElse
						(
							p ->
								actions.add(s -> primeSrc
												 .addBaseGenerator(
																	p.create(settingsFinal)
																	 .assignPrimeSrc(primeSrc)
																	 .doPreferParallel(initOpts.isPreferParallel())
																	)
										   )
								, () -> statusHandler.errorOutput(baseType, "ERROR: No provider for %s", baseType.toString() // OrElse
									)
						);
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
													break;

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

		if (graphOpts != null && graphOpts.getGraphType() != null)
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
