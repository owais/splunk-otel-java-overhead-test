#!/bin/bash

# Creates a PR into gh-pages branch from the results

MYDIR=$(dirname $0)
RESULTS=${MYDIR}/../results
REV=$(ls "${RESULTS}")
NEW_BRANCH="results_${REV}"

set -e

git config --global user.name overhead-results
git config --global user.email olone+gdi-bot@splunk.com
git config --global gpg.program gpg2

echo "Creating a signing key"
cat <<EOF > /tmp/key.txt
Key-Type: RSA
Key-Length: 2048
Subkey-Type: ELG-E
Subkey-Length: 2048
Name-Real: overhead-results
Name-Email: olone+gdi-bot@splunk.com
Expire-Date: 0
Passphrase: abc
%commit
EOF
gpg2 --batch --passphrase '' --quick-gen-key /tmp/key.txt
KEY_ID=$(gpg2 -K --keyid-format SHORT | grep '^ ' | tr -d ' ')
git config --global user.signingKey ${KEY_ID}

git clone https://splunk-o11y-gdi-bot:"${GITHUB_TOKEN}"@github.com/signalfx/splunk-otel-java-overhead-test.git github-clone
cd github-clone
git checkout gh-pages
git checkout -b ${NEW_BRANCH}
cd ..

echo "Setting up a new pull request for results data: ${REV} results"

rsync -avv --progress "${RESULTS}/${REV}" github-clone/results/
cd github-clone
echo "Results list: " && ls -l results/
ls -1 results/ | grep -v README > results/index.txt
echo "Adding new files to changelist"
git add results/index.txt
git add results/${REV}/*
echo "Committing changes..."
git commit -S -am "Add test results: ${REV}"
echo "Pushing results to remote branch ${NEW_BRANCH}"
git push https://splunk-o11y-gdi-bot:"${GITHUB_TOKEN}"@github.com/signalfx/splunk-otel-java-overhead-test.git ${NEW_BRANCH}

MSG="[automated] Add test results: ${REV}"

echo "Running PR create command:"
gh pr create --title "${MSG}" --body "${MSG}" --base gh-pages --head ${NEW_BRANCH}
