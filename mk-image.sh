#!/bin/sh

#image=ubuntu
#workingctr=ubuntu-working-container

image=gcr.io/distroless/java17-debian11
workingctr=java17-debian11-working-container

buildah from $image
buildah copy $workingctr run-ctr.sh lib/* ptk/target/*.jar  /tmp
buildah config --entrypoint ["/bin/java @tmp/run-ctr.sh"] $workingctr
buildah commit $workingctr ptk
