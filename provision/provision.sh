#!/bin/bash

# top-level provisioning script.
#
# * sets up two instances via orca: externals and testbox
# * gives the splunk user sudo and ansible perms
# * provisions externals via ansible
# * provisions testbox via ansible

MYDIR=$(dirname $0)
ANSIBLE_DIR="${MYDIR}/ansible"
${MYDIR}/bootstrap-orca.sh
${MYDIR}/bootstrap-user.sh

echo Confguring externals via ansible
ansible-playbook -i ${ANSIBLE_DIR}/hosts.yml ${ANSIBLE_DIR}/externals-playbook.yml

echo Configuring testbox via ansible
ansible-playbook -i ${ANSIBLE_DIR}/hosts.yml ${ANSIBLE_DIR}/testbox-playbook.yml

