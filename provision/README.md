The overhead tests attempt to reduce the impact of noisy neighbors by provisioning
the collector and postgres services on a separate cloud instance.

This directory contains automation tooling used to provision and configure 
the EC2 instances used for this test. It uses an internal Slunk tool 
called [Orca](https://core-ee.splunkdev.page/orca/) to do the provisioning 
and ansible to automatically configure the VMs.

At the time of this writing, provisioning takes about 12 minutes.

# setup

* You need to be on the corporate VPN.
* You need to be a member of the `ssg-orca-aws-users` LDAP group. Follow the docs and links here [to request access](https://core-ee.splunkdev.page/orca/docs/providers/aws#through-cli).

## install orca

Orca is a Splunk-internal tool for provisioning cloud instances.
[Go here to learn how to set it up](https://core-ee.splunkdev.page/orca/docs/setup).

## install ansible

The ansible docs say to use pip, but that didn't work out. Just use homebrew:
```
$ brew install ansible
```

## install jinja2 template engine

```
$ brew install jinja2-cli
```

# provisioning

Provisioning will create one instance called "testbox" and one instance called "externals".

Orca vault auth tokens are only good for maybe 24 hours. Before provisioning, you
probably want to reset the token by running:
```
orca config auth
```
and entering your user/pass (you can leave the team blank).

Next, run `./provision` and wait about 12 minutes.

If all is successful, your two instances should be set up and ready to use. You
can verify with `orca --cloud aws show containers`

# run tests

To start the tests on the `TESTBOX_HOST` backgrounded in `screen`:
```
source env.sh
ssh -f -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" \    
    -i ~/.orca/id_rsa splunk@${TESTBOX_HOST} \
    'screen -dm bash -c "./run-tests.sh 10; bash"'
```

The `10` in the command is the number of passes to perform.

# misc

make strings that can be fed to awk....
```
for f in `seq 0 15` ; do for a in `seq 0 5` ; do echo -n "\$$(($f+2+15*$a)) \",\" "; done; done
```

then 
```
awk -F, '{ print $1 "," <bigstring> }' results.csv
```

# orca cheatsheet

Look at existing deployments:

```
orca --cloud aws show deployments
```

Look at existing containers:

```
orca --cloud aws show containers
```

Create a bare ec2 instance. See `bootstrap-orca.sh` for better examples.
```
orca --cloud aws create --no-provision \
    --prefix externals \
    --aws-instance-type m4.large \
    --labels retention_time=86400 \ 
 
```

Get a shell on the testbox:
```
ssh -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" \
    -i ~/.orca/id_rsa \
    splunk@$(orca --cloud aws show containers | grep -A 2 testbox | grep Splunkd | sed -e "s/.*https:..//" | sed -e "s/:.*//")
```

Get a shell on the externals box:
```
ssh -o "StrictHostKeyChecking=no" -o "UserKnownHostsFile=/dev/null" \
    -i ~/.orca/id_rsa \
    splunk@$(orca --cloud aws show containers | grep -A 2 externals | grep Splunkd | sed -e "s/.*https:..//" | sed -e "s/:.*//")
```