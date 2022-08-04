package com.starcases.prime.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

/**
 * This class is derived from picocli online documentation.
 * https://picocli.info/#_cdi_2_0_jsr_365
 *
 */
@ApplicationScoped
public class CDIFactory implements IFactory
{
    @Override
    public <K> K create(Class<K> cls) throws Exception {
        Instance<K> instance = CDI.current().select(cls);
        if (instance.isResolvable()) {
            return instance.get();
        }
        return CommandLine.defaultFactory().create(cls);
    }

}
