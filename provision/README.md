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

# set up hosts in nova

tbd

## configure ssh

By default, nova hosts use password login for ssh, which isn't great.
Let's get our public key set up for ssh access:

```
$ ansible-playbook -i hosts.yml --ask-pass externals-playbook.yml
```

You only have to do this once after creating the hosts in nova.
If they get destroyed, you'll run this again.

# run




```
$ ansible-playbook -i hosts.yml externals-playbook.yml
```