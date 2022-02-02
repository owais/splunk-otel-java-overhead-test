#!/bin/bash

# initiates a remote test run of 10 passes.

MYDIR=$(dirname $0)

source ${MYDIR}/env.sh

ssh -f -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" -o "LogLevel=ERROR"\
    -i ~/.orca/id_rsa \
    splunk@${TESTBOX_HOST} \
    'screen -dm bash -c "./run-tests.sh 1; bash"'
