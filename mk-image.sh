#!/bin/sh

#image=ubuntu
#workingctr=ubuntu-working-container

image=gcr.io/distroless/java17-debian11
workingctr=java17-debian11-working-container

buildah from $image
buildah copy $workingctr Podman-java-args lib/* ptk/target/*.jar  /tmp
buildah config --entrypoint ["/bin/java","@Podman-java-args"] $workingctr
buildah commit $workingctr ptk
