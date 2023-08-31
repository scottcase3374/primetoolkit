FROM gcr.io/distroless/java17-debian11

VOLUME /data
VOLUME /output

WORKDIR /
COPY  Podman-java-args  lib/* ptk/target/ptk-0.0.1-SNAPSHOT.jar /
ENTRYPOINT ["/usr/bin/java", "@/Podman-java-args"]

