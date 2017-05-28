#!/bin/bash

declare -a arr
max=$(nproc)
for i in $(seq $max -1 1)
do
`pwd`/run.sh "$i"
done

