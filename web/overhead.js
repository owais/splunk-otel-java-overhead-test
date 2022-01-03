async function startOverhead() {
    console.log('overhead started');
    document.getElementById('test-run')
        .addEventListener("change", testRunChosen);
    getRuns()
        .then(runNames => {
            console.log(runNames);
            populateRunsDropDown(runNames);
            document.getElementById('test-run').value = runNames[0];
            testRunChosen(runNames[0]);
        });
}

function testRunChosen() {
    //TODO: Consider clearing/removing existing graphs first
    const value = document.getElementById('test-run').value;
    console.log(`selection changed ${value}`);
    getResults(value)
        .then(addCharts)
}

function populateRunsDropDown(runNames) {
    const sel = document.getElementById('test-run');
    runNames.forEach(name => {
        const option = document.createElement("option");
        option.text = name;
        option.value = name;
        sel.add(option);
    });
}

function addCharts(aggregated) {
    makeChart(aggregated, 'startupDurationMs', "seconds", x => x / 1000);
    makeChart(aggregated, 'averageCpuUser', "percent (%)");
    makeChart(aggregated, 'maxCpuUser', "% CPU load");
    makeChart(aggregated, 'maxHeapUsed', "megabytes", x => x / (1024 * 1024));
    makeChart(aggregated, 'totalAllocatedMB', "Gigabytes", x => x / (1024));
    makeChart(aggregated, 'totalGCTime', "seconds", x => x / (1000 * 1000 * 1000));
    makeChart(aggregated, 'gcPauseMs', "milliseconds");
    makeChart(aggregated, 'iterationAvg', "milliseconds");
    makeChart(aggregated, 'iterationP95', "milliseconds");
    makeChart(aggregated, 'requestAvg', "milliseconds");
    makeChart(aggregated, 'requestP95', "milliseconds");
    makeChart(aggregated, 'netReadAvg', "MiB/s", x => x / (1024 * 1024));
    makeChart(aggregated, 'netWriteAvg', "MiB/s", x => x / (1024 * 1024));
    makeChart(aggregated, 'peakThreadCount', "MiB/s");
    makeChart(aggregated, 'maxThreadContextSwitchRate', "switches/s");
    makeChart(aggregated, 'runDurationMs', "seconds", x => x / 1000);
}

function makeChart(aggregated, resultType, axisTitle, scaleFunction = x => x) {
    const agents = aggregated['agents']
    const initialResults = agents.map(agent => aggregated['results'][resultType][agent]);
    const results = initialResults.map(scaleFunction);
    new Chartist.Bar(`#${resultType}-chart`, {
            labels: agents,
            series: [results]
        },
        {
            seriesBarDistance: 10,
            axisX: {
                offset: 60
            },
            axisY: {
                offset: 60,
                scaleMinSpace: 20
            },
            plugins: [
                Chartist.plugins.ctBarLabels({
                    labelClass: 'ct-bar-label',
                    labelInterpolationFnc: function (text) {
                        return text.toFixed(2);
                    }
                }),
                Chartist.plugins.ctAxisTitle({
                    axisY: {
                        axisTitle: axisTitle,
                        axisClass: "ct-axis-title",
                        offset: {
                            x: 0,
                            y: 15
                        },
                        flipTitle: true
                    }
                })
            ]
        },
    );
}
