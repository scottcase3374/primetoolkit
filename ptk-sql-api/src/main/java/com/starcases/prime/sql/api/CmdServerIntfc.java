package com.starcases.prime.sql.api;

/**
 * Service listens on a dedicated port.
 *
 * @author scott
 *
 */
public interface CmdServerIntfc
{
	void run() throws InterruptedException;
}
