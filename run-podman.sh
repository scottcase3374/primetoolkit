#!/bin/sh
podman run -d -p 8690:8690  -v /home/scott/ptk/input-data:/data -v  /home/scott/ptk/output:/output localhost/ptk \
init \
--input-data-folder=/data \
--output-folder=/output \
--max-count=2500 \
--base=PRIME_TREE \
--base=PREFIX \
--output=BASES \
--use-base-file \
--prefer-parallel=true \
--enable-cmd-listener
