/**
 * {
 *   agents: [a1, a2, a3],
 *   runs: [
 *       {
 *           timestamp: 123456,
 *           a1: {
 *               f1: v1,
 *               f2: v2,
 *               f3: v3
 *           },
 *           a2: {
 *               f1: v1,
 *               f2: v2,
 *               ...
 *           }
 *       }
 *   ]
 * }
 */
function parseCsv(body) {
    const runs = parseRuns(body);
    const agents = body.split('\n')[0].split(',').slice(1).map(x => x.replace(/:.*/, '')).slice(0,3);
    console.log(agents);
    return {
        agents: agents,
        runs: runs
    }
}

function parseRuns(body){
    const lines = body.trim().split("\n");
    const fieldNames = lines.shift().split(",");

    return lines.map(line => {
        const fields = line.split(",");
        const timestamp = fields.shift();
        const fieldTuples = fields.map((elem, i) => {
            const agent = fieldNames[i + 1].replace(/:.*/, '');
            const fieldName = fieldNames[i + 1].replace(/.*:/, '');
            return [agent, fieldName, elem];
        });
        const obj = fieldTuples.reduce((acc, tuple) => {
            const agent = tuple[0];
            const obj = acc[agent] || {};
            const fieldName = tuple[1];
            const fieldValue = tuple[2];
            obj[fieldName] = Number(fieldValue);
            acc[agent] = obj;
            return acc;
        }, {});
        obj['timestamp'] = new Date(Number(timestamp)*1000);
        return obj;
    });
}
