This is automation tooling used to provision and configure 
the EC2 instances used for this test.

It is assumed that ec2 instances are stood up in https://splunkit.io/ec2 (by hand)
but can be configured via automation (ansible).

# setup

* You need to be on the corporate VPN.
* You need an ssh keypair in `~/.ssh/id_rsa.pub` and `~/.ssh/id_rsa`
* You probably want to `ssh-add ~/.ssh/id_rsa.pub` to cache your key pass

## install ansible

The ansible docs say to use pip, but that didn't work out. Just use homebrew:
```
$ brew install ansible
```

## install jinja2 template engine

```
$ brew install jinja2-cli
```

## create hosts in nova

Visit: https://splunkit.io/ec2

Create one instance called "testbox" and one instance called "testbox". 

## set up env.sh

This small script will be used to template the ansible inventory.
There may be a smarter way of doing this, but for now, create a file
named `env.sh` and replace the two hostnames with the hosts
from your nova setup.

```
# Contains specific ephemeral environment information

export TESTBOX_HOST=<your testbox hostname>
export EXTERNALS_HOST=<your externals box hostname>
```

It stinks that this step is manual, but it should be the last thing.

## configure ssh

By default, nova hosts use password login for ssh, which isn't great.
Let's get our public key set up for ssh access:

```
$ ./bootstrap.sh
```

Enter the interactive password for the `splunker` user when prompted.
You only have to do this once after creating the hosts in nova.
If they get destroyed, you'll run this again.

# run




```
$ ansible-playbook -i hosts.yml externals-playbook.yml
```