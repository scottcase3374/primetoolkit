package com.starcases.prime.sql.impl;

import com.starcases.prime.sql.api.PrimeSqlResultIntfc;

import lombok.ToString;

@ToString
public class PrimeSqlResult implements PrimeSqlResultIntfc
{
	private CharSequence result = "<empty>";

	private CharSequence error = null;

	@Override
	public void setResult(final CharSequence result)
	{
		this.result = result;
	}

	@Override
	public void setError(final CharSequence error)
	{
		this.error = error;
	}

	@Override
	public CharSequence getResult()
	{
		return result;
	}

	@Override
	public CharSequence getError()
	{
		return error;
	}
}
