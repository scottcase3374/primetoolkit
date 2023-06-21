#!/bin/bash

projects="$(cat modules.txt)"
targetdirs="$(printf ' ../%s/target' $projects)"

modpath=$(echo $targetdirs | tr '[ ]' '[:]')

jarpaths=$(find $targetdirs -name "*.jar" -print )
jarpaths=$(echo $jarpaths | tr '[ ]' '[:]')


#echo $modpath
#echo $jarpaths

java  --module-path $jarpaths -m PrimeToolKit/com.starcases.prime.PrimeToolKit

