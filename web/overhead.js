async function startOverhead(){
    console.log('overhead started');
    getResults('20211214_203544')
        .then(addCharts)
}

function addCharts(aggregated){
    makeChart(aggregated, 'startupDurationMs', "seconds", x => x / 1000);
    makeChart(aggregated, 'averageCpuUser', "percent (%)");
    makeChart(aggregated, 'maxCpuUser', "percent (%)");
    makeChart(aggregated, 'maxHeapUsed', "megabytes", x => x / (1024*1024));
    makeChart(aggregated, 'totalAllocatedMB', "gigabytes", x => x / (1024));
    makeChart(aggregated, 'totalGCTime', "seconds", x => x / (1000*1000*1000));
    makeChart(aggregated, 'gcPauseMs', "milliseconds");
    makeChart(aggregated, 'iterationAvg', "milliseconds");
    makeChart(aggregated, 'iterationP95', "milliseconds");
    makeChart(aggregated, 'requestAvg', "milliseconds");
    makeChart(aggregated, 'requestP95', "milliseconds");
    makeChart(aggregated, 'netReadAvg', "MiB/s", x => x / (1024*1024));
    makeChart(aggregated, 'netWriteAvg', "MiB/s", x => x / (1024*1024));
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
                offset: 50,
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
