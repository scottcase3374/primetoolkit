#!/bin/bash

CP="."

mods="ptk-base-nprime-impl ptk-base-prefix-impl ptk-base-primetree-impl ptk-base-triples-impl ptk-cache-api ptk-cache-impl ptk-core-api ptk-core-impl ptk-graph-export-api ptk-graph-export-gml ptk-graph-impl ptk-graph-visualize-api ptk-graph-visualize-impl ptk-main ptk-preload-api ptk-preload-impl ptk-service-api ptk-sql-svc-api ptk-sql-svc-impl"
# ptk-metrics-impl ptk-metrics-api 
for modname in $mods ; 
do
  echo "$modname"
  jar --file="$modname/target/$modname-0.0.1-SNAPSHOT.jar" --describe-module;
done

echo

