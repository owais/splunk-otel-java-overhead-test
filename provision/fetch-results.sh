#!/bin/bash

# fetches the remote results. Assumes there is only one results directory.

MYDIR=$(dirname $0)
source ${MYDIR}/env.sh

RDIR=$(ssh -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" -o "LogLevel=ERROR" -i ~/.orca/id_rsa  splunk@${TESTBOX_HOST} ls results)
echo Remote dir is ${RDIR}
TS=$(ssh -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" -o "LogLevel=ERROR" -i ~/.orca/id_rsa  splunk@${TESTBOX_HOST} "date -r results/${RDIR} '+%Y%m%d_%H%M%S'")
echo Timestamp dir will be ${TS}

RESULTS=${MYDIR}/../results/${TS}
mkdir -p $RESULTS

rsync -avv --progress -e \
  'ssh -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" -o "LogLevel=ERROR" -i ~/.orca/id_rsa' \
   "splunk@${TESTBOX_HOST}:results/${RDIR}/" "${RESULTS}/"