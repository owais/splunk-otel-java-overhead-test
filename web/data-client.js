
// Returns a list of available historic runs
async function getRuns(){
    //tbd
}

async function getResults(name){
    fetch(`results/${name}/results.csv`)
        .then(resp => resp.text())
        .then(body => parseCsv(body))
        .then(data => {
            console.log(data);
        });
}
