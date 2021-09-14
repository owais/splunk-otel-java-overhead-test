#!/bin/bash

# Fetches remote results file and reformats them.

source env.sh

FIELD_SEQ=$(for f in `seq 0 15` ; do for a in `seq 0 5` ; do echo -n "\$$(($f+2+15*$a)) \",\" "; done; done )
AWKPROG=$(echo "{ print \$1 \",\" ${FIELD_SEQ} }" | sed -e "s/.....}/}/")

echo $AWKPROG

for dir in $( ssh splunker@${TESTBOX_HOST} ls results) ; do
  rsync -avv --progress splunker@${TESTBOX_HOST}:./results/$dir/results.csv results_${dir}.tmp.csv
  echo awk -F, ${AWKPROG} results_${dir}.tmp.csv > results_${dir}.csv
  awk -F, ${AWKPROG} results_${dir}.tmp.csv > results_${dir}.csv
#  rm results_${dir}.tmp.csv
done