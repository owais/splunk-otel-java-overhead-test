
// Returns a list of available historic runs
async function getRuns(){
    //TODO: Fetch from a static file
    return Promise.resolve(['20220110_132900', '20211214_203544']);
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
