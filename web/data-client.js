
// Returns a list of available historic runs
async function getRuns(){
    //tbd
}

async function getResults(name){
    return fetch(`results/${name}/results.csv`)
        .then(resp => resp.text())
        .then(body => parseCsv(body))
        .then(data => {
            // console.log(data);
            const aggregated = aggregateRunData(data);
            console.log(aggregated)
            return aggregated;
        });
}
