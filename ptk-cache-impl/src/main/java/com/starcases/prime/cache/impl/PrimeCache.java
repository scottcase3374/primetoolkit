package com.starcases.prime.cache.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

public class PrimeCache<K,V> implements Cache<K,V>
{
	private Logger LOG = Logger.getLogger(PrimeCache.class.getName());

	private static final FileAttribute<Set<PosixFilePermission>> fileAttrs = PosixFilePermissions
			.asFileAttribute(Set.of(new PosixFilePermission []{
											PosixFilePermission.OWNER_WRITE,
											PosixFilePermission.OWNER_READ}));

	private final Path pathToCacheDir;
	private final MutableMap<K,V> keysToValue = Maps.mutable.empty();

	public PrimeCache(final String cacheName, final Path pathToCacheDirs)
	{
		try
		{
			LOG.info(String.format("Cache ctor : cacheName [%s] pathToCacheDirs: [%s]", cacheName, pathToCacheDirs.toString()));
			pathToCacheDir = Files.createDirectories(Path.of(pathToCacheDirs.toString(), cacheName).normalize());
		}
		catch(final IOException e)
		{
			throw new RuntimeException("can't create cache directory");
		}
	}

	@Override
	public V get(K key)
	{
		return keysToValue.get(key);
	}

	@Override
	public Map<K, V> getAll(Set<? extends K> keys)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsKey(K key)
	{
		return keysToValue.containsKey(key);
	}

	@Override
	public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void put(K key, V value)
	{
		keysToValue.put(key, value);
	}

	@Override
	public V getAndPut(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean putIfAbsent(K key, V value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(K key)
	{
		return keysToValue.removeIf((k,v) -> containsKey(key));
	}

	@Override
	public boolean remove(K key, V oldValue)
	{
		return keysToValue.remove(key, oldValue);
	}

	@Override
	public V getAndRemove(K key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(K key, V value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V getAndReplace(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAll(Set<? extends K> keys) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll()
	{
		clear();
	}

	@Override
	public void clear()
	{
		keysToValue.clear();
	}

	@Override
	public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments)
			throws EntryProcessorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor,
			Object... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		return pathToCacheDir.getFileName().toString();
	}

	@Override
	public CacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isClosed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}