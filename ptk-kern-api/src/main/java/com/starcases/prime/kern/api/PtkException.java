package com.starcases.prime.kern.api;

public class PtkException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public PtkException(final Exception e)
	{
		super(e);
	}

	public PtkException(final String msg)
	{
		super(msg);
	}

}
