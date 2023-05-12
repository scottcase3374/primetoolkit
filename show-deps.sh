#!/bin/bash

CP="."

mods="ptk-base-nprime-impl ptk-base-prefix-impl ptk-base-primetree-impl ptk-base-triples-impl ptk-cache-api ptk-cache-impl ptk-core-api ptk-core-impl ptk-graph-export-api ptk-graph-export-gml ptk-graph-impl ptk-graph-visualize-api ptk-graph-visualize-impl ptk-main ptk-preload-api ptk-preload-impl ptk-service-api ptk-sql-svc-api ptk-sql-svc-impl"
# ptk-metrics-api ptk-metrics-impl 

for modname in $mods ; 
do
  echo "$modname"
  CP="$modname/target/$modname-0.0.1-SNAPSHOT.jar:$CP";
done

echo

echo "classpath=$CP"
echo

MAIN=ptk-main
jdeps -dotoutput deps.dot -p=$CP --ignore-missing-deps $MAIN/target/$MAIN-0.0.1-SNAPSHOT.jar
