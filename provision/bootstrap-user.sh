#!/bin/bash

# sets up the initial ssh configuration so that subsequent ansible
# operations use your ssh key

source env.sh

jinja2 \
    -D testbox_host=${TESTBOX_HOST} \
    -D externals_host=${EXTERNALS_HOST} \
    -D ansible_user=root \
    ansible/hosts.yml.jinja > ansible/root.hosts.yml

jinja2 \
    -D testbox_host=${TESTBOX_HOST} \
    -D externals_host=${EXTERNALS_HOST} \
    -D ansible_user=splunk \
    ansible/hosts.yml.jinja > ansible/hosts.yml

ansible-playbook -i ansible/root.hosts.yml ansible/bootstrap-user.yml
