google.charts.load('current', {'packages':['corechart']});

google.charts.setOnLoadCallback(graficoPrincipal);

function graficoPrincipal() {
      $.ajax({
        url: "api/dashboard/tarjetastotales",
        dataType: "json",
      }).done(function (jsonData) {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'tipo');
        data.addColumn('number', 'saldo');
        

        jsonData.forEach(function (row) {
          data.addRow([
            row.tipo,
            row.saldo
          ]);
        });

        var options = {
          chart: {
            width: 600,
            height: 400,
            title: 'Reporte de  Ventas',
            legend: { position: 'top'}
          }
        };

        var formatter = new google.visualization.NumberFormat({fractionDigits: 2} );
        formatter.format(data, 1);

        var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
        chart.draw(data, options );
      }).fail(function (jq, text, err) {
        console.log(text + ' - ' + err);
      });
}