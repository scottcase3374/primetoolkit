#!/bin/sh

java \
-Dcom.level=info \
-Dfile.encoding=UTF-8 \
-XX:+ShowCodeDetailsInExceptionMessages \
--add-reads com.starcases.prime.service.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.logging=ALL-UNNAMED \
--add-reads PrimeToolKit=ALL-UNNAMED \
--add-reads com.starcases.prime.base.nprime.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.base.prefix.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.base.primetree.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.base.triples.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.cache.api=ALL-UNNAMED \
--add-reads com.starcases.prime.cache.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.cli=ALL-UNNAMED \
--add-reads com.starcases.prime.core.api=ALL-UNNAMED \
--add-reads com.starcases.prime.core.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.export.api=ALL-UNNAMED \
--add-reads com.starcases.graph.export.impl.gml=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.visualize.api=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.visualize.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.metrics.api=ALL-UNNAMED \
--add-reads com.starcases.prime.metrics.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.sql.api=ALL-UNNAMED \
--add-reads com.starcases.prime.sql.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.service.api=ALL-UNNAMED \
--add-reads com.starcases.prime.service.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.logging=ALL-UNNAMED \
--add-reads PrimeToolKit=ALL-UNNAMED \
--add-reads com.starcases.prime.base.nprime.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.base.prefix.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.base.primetree.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.base.triples.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.cache.api=ALL-UNNAMED \
--add-reads com.starcases.prime.cache.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.cli=ALL-UNNAMED \
--add-reads com.starcases.prime.core.api=ALL-UNNAMED \
--add-reads com.starcases.prime.core.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.export.api=ALL-UNNAMED \
--add-reads com.starcases.graph.export.impl.gml=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.visualize.api=ALL-UNNAMED \
--add-reads com.starcases.prime.graph.visualize.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.metrics.api=ALL-UNNAMED \
--add-reads com.starcases.prime.metrics.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.sql.api=ALL-UNNAMED \
--add-reads com.starcases.prime.sql.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.sql.csvoutput.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.sql.jsonoutput.impl=ALL-UNNAMED \
--add-reads com.starcases.prime.service.api=ALL-UNNAMED \
--module-path ./lib:./ptk/target/ptk-0.0.1-SNAPSHOT.jar  \
--module PrimeToolKit/com.starcases.prime.PrimeToolKit  \
init \
--max-count=2500 \
--base=PREFIX \
--prefer-parallel=true \
--enable-cmd-listener

#--add-modules java.sql
#--base=PRIME_TREE \
#--output=BASES \
#--use-base-file \
