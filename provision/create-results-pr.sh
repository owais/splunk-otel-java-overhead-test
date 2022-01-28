#!/bin/bash

# Creates a PR into gh-pages branch from the results

MYDIR=$(dirname $0)
RESULTS=${MYDIR}/../results
REV=$(ls "${RESULTS}")
NEW_BRANCH="results_${REV}"

set -e

git config --global user.name overhead-results
git config --global user.email overhead-results@users.noreply.github.com

echo "${GITHUB_TOKEN}" > token.txt
gh auth login --with-token < token.txt
rm token.txt

gh repo clone signalfx/splunk-otel-java-overhead-test github-clone
cd github-clone
git checkout -b ${NEW_BRANCH} gh-pages
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
git commit -am "Add test results: ${REV}"
echo "Pushing results to remote branch ${NEW_BRANCH}"
git push -u origin ${NEW_BRANCH}

MSG="[automated] Add test results: ${REV}"

echo "Running PR create command:"
gh pr create --title "${MSG}" --body "${MSG}" --base gh-pages --head ${NEW_BRANCH}
