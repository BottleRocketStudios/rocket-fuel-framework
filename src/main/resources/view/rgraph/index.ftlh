<!DOCTYPE html >
<html lang="en">
<head>
    <title>${title}</title>
    <script src="https://www.rgraph.net/libraries/RGraph.common.core.js" ></script>
    <script src="https://www.rgraph.net/libraries/RGraph.common.dynamic.js" ></script>
    <script src="https://www.rgraph.net/libraries/RGraph.line.js" ></script>
    <script src="https://www.rgraph.net/libraries/RGraph.common.tooltips.js" ></script>
    <script src="https://www.rgraph.net/libraries/RGraph.common.key.js"></script>
    <meta name="description" content="Alcon MARLO Perfomance Information" />
    <meta name="robots" content="noindex, nofollow" />
    <style type="text/css">
    body {
        font-family: Arial;
    }
  </style>
</head>
<body>
<h2>${title}</h2>

<h3>Test Durations (in seconds)</h3>
<canvas id="summary1" width="1400" height="400">[No canvas support]</canvas><br/>

<h3>Test Durations Sorted (in seconds)</h3>
<canvas id="summary2" width="1400" height="400">[No canvas support]</canvas><br/>

<script>

        ${json1}

        ${json2}

        var line1 = new RGraph.Line({
            id: 'summary1',
            data: json.data,
            options: {
                xaxisLabels: json.labels,
                textAccessible: true,
                colors: ['#7CB5EC','#C67171'],
                tickmarksStyle: 'filledcircle',
                hmargin: 10,
                linewidth: 2,
                shadow: true,
                backgroundGridVlines: false,
                ymax: 100,
                textSize: 9,
                yaxiScaleDecimals: 2,
                labels: json.label,
                gutterLeft: 35,
                titleSize: 11
            }
        }).draw();

        var line2 = new RGraph.Line({
            id: 'summary2',
            data: json2.data,
            options: {
                xaxisLabels: json2.labels,
                textAccessible: true,
                colors: ['#7CB5EC','#C67171'],
                tickmarksStyle: 'filledcircle',
                hmargin: 10,
                linewidth: 2,
                shadow: true,
                backgroundGridVlines: false,
                ymax: 100,
                textSize: 9,
                yaxiScaleDecimals: 2,
                labels: json.label,
                gutterLeft: 35,
                titleSize: 11
            }
        }).draw();

    </script>
</body>
</html>
