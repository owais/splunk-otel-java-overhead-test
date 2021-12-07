#!/bin/bash

# checks the progress of the remote test run

MYDIR=$(dirname $0)

source ${MYDIR}/env.sh

ssh -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" -o "LogLevel=ERROR" \
    -i ~/.orca/id_rsa \
    splunk@${TESTBOX_HOST} "cat /tmp/passnum.txt /tmp/progress.txt"
