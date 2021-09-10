#!/bin/bash

# sets up the initial ssh configuration so that subsequent ansible
# operations use your ssh key

source env.sh

jinja2 -D testbox_host=${TESTBOX_HOST} -D externals_host=${EXTERNALS_HOST} \
    ansible/hosts.yml.jinja > ansible/hosts.yml

ansible-playbook -i ansible/hosts.yml --ask-pass ansible/bootstrap-ssh.yml