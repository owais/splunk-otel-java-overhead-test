#!/bin/bash

# uses orca to create "externals" and "testbox" instances.
# creates env.sh that contains the IP of each

# assumes orca is set up through a very complicated shell environment in user profile. yep.
source ${HOME}/.bash_profile

echo "!! PROVISIONING externals with orca !!"
orca --cloud aws create \
    --no-provision \
    --aws-instance-type m4.large \
    --prefix externals

echo "!! PROVISIONING testbox with orca !!"
orca --cloud aws create \
    --no-provision \
    --aws-instance-type m4.xlarge \
    --prefix testbox

function getIP() {
  PREFIX=$1
  IP=$(orca --cloud aws show containers | grep -A 2 ${PREFIX} | grep Splunkd | sed -e "s/.*https:..//" | sed -e "s/:.*//")
  echo $IP
}

# Get the IP of the externals box
TESTBOX_HOST=$(getIP "testbox")
EXTERNALS_HOST=$(getIP "externals")

MYDIR=$(dirname $0)
echo "export EXTERNALS_HOST=${EXTERNALS_HOST}" > "${MYDIR}/env.sh"
echo "export TESTBOX_HOST=${TESTBOX_HOST}" >> "${MYDIR}/env.sh"

