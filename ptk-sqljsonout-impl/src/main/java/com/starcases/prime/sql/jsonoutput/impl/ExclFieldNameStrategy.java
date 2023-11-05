package com.starcases.prime.sql.jsonoutput.impl;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import lombok.Getter;

/**
 * Class which enables excluding specific field names from json output.
 */
public class ExclFieldNameStrategy implements ExclusionStrategy
{
	@Getter
	private final MutableList<String> fieldNames = Lists.mutable.empty();

	/**
	 * Constructors for the field name exclusion class.
	 *
	 * @param fieldName
	 */
	public ExclFieldNameStrategy()
	{}

	public void addExcludedField(final String fieldName)
	{
		this.fieldNames.add(fieldName);
	}

	@Override
	public boolean shouldSkipClass(final Class<?> clazz)
	{
		return false;
	}

	@Override
	public boolean shouldSkipField(final FieldAttributes f)
	{
		return fieldNames.stream().anyMatch(fn -> f.getName().equals(fn));
	}
}
