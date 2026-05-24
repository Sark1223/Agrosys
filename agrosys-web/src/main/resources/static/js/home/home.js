//Configuracion de los botones
/*$(document).ready(function() {
    $('#btnRegistrarAsistencia').click(function() {
        window.location.href = '/agrosys/workers';
    });
});*/

// ===============================
// CONFIGURACIÓN DEL CLIMA
// ===============================
$.fn.getClima = function () {

    const latitud = 20.5888;
    const longitud = -100.3899;

    $.ajax({
        url: `https://api.open-meteo.com/v1/forecast?latitude=${latitud}&longitude=${longitud}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1`,
        type: "GET",
        success: function (response) {
            const climaContainer = $('#climaContenido');
            climaContainer.html('');

            if (response.current && response.daily) {
                console.log('Clima obtenido:', response);

                const temperatura = Math.round(response.current.temperature_2m);
                const humedad = response.current.relative_humidity_2m;
                const viento = response.current.wind_speed_10m;
                const codigoClima = response.current.weather_code;

                const maxima = Math.round(response.daily.temperature_2m_max[0]);
                const minima = Math.round(response.daily.temperature_2m_min[0]);

                const descripcion = getDescripcionClima(codigoClima);
                const icono = getIconoClima(codigoClima);

                const climaCard = $(`
                    <div class="clima-contenido-info">
                        <div class="clima-icono">${icono}</div>
                        <h2>Querétaro</h2>
                        <p class="clima-temperatura">${temperatura}°C</p>
                        <p class="clima-descripcion">${descripcion}</p>
                        <div class="clima-detalles">
                            <p>Máxima: ${maxima}°C</p>
                            <p>Mínima: ${minima}°C</p>
                            <p>Humedad: ${humedad}%</p>
                            <p>Viento: ${viento} km/h</p>
                        </div>
                    </div>
                `);
                climaContainer.append(climaCard);
            } else {
                climaContainer.html(`
                    <p class="clima-error">No se pudo cargar la información del clima.</p>
                `);

                mostrarAlertaError('No se pudo obtener la información del clima');
            }
        },
        error: function (xhr, status, error) {
            console.error('Error en la solicitud AJAX del clima:', error);

            $('#climaContenido').html(`
                <p class="clima-error">No se pudo cargar el clima.</p>
            `);

            mostrarAlertaError('Ocurrió un error al comunicarse con la API del clima');
        }
    });
};

// ===============================
// FUNCIONES AUXILIARES DEL CLIMA
// ===============================
function getDescripcionClima(codigo) {
    const codigos = {
        0: 'Despejado',
        1: 'Mayormente despejado',
        2: 'Parcialmente nublado',
        3: 'Nublado',
        45: 'Niebla',
        48: 'Niebla con escarcha',
        51: 'Llovizna ligera',
        53: 'Llovizna moderada',
        55: 'Llovizna intensa',
        61: 'Lluvia ligera',
        63: 'Lluvia moderada',
        65: 'Lluvia intensa',
        80: 'Chubascos ligeros',
        81: 'Chubascos moderados',
        82: 'Chubascos fuertes',
        95: 'Tormenta eléctrica'
    };

    return codigos[codigo] || 'Clima no disponible';
}

function getIconoClima(codigo) {
    if (codigo === 0) {
        return '☀️';
    }

    if (codigo === 1 || codigo === 2) {
        return '🌤️';
    }

    if (codigo === 3) {
        return '☁️';
    }

    if (codigo === 45 || codigo === 48) {
        return '🌫️';
    }

    if (
        codigo === 51 ||
        codigo === 53 ||
        codigo === 55 ||
        codigo === 61 ||
        codigo === 63 ||
        codigo === 65 ||
        codigo === 80 ||
        codigo === 81 ||
        codigo === 82
    ) {
        return '🌧️';
    }

    if (codigo === 95) {
        return '⛈️';
    }

    return '🌡️';
}


// ===============================
// CONFIGURACIÓN DE FILTROS
// ===============================
$.fn.initFiltros = function () {
    const anioSelect = $('#filtroAnio');
    const mesSelect = $('#filtroMes');

    anioSelect.html('');
    mesSelect.html('');

    for (let anio = 2026; anio <= 2031; anio++) {
        anioSelect.append(`
            <option value="${anio}">${anio}</option>
        `);
    }

    const meses = [
        { valor: 1, nombre: 'Enero' },
        { valor: 2, nombre: 'Febrero' },
        { valor: 3, nombre: 'Marzo' },
        { valor: 4, nombre: 'Abril' },
        { valor: 5, nombre: 'Mayo' },
        { valor: 6, nombre: 'Junio' },
        { valor: 7, nombre: 'Julio' },
        { valor: 8, nombre: 'Agosto' },
        { valor: 9, nombre: 'Septiembre' },
        { valor: 10, nombre: 'Octubre' },
        { valor: 11, nombre: 'Noviembre' },
        { valor: 12, nombre: 'Diciembre' }
    ];

    meses.forEach(function (mes) {
        mesSelect.append(`
            <option value="${mes.valor}">${mes.nombre}</option>
        `);
    });

    anioSelect.val('2026');
    mesSelect.val('4');

    $.fn.cargarDiasFiltro();
};

$.fn.cargarDiasFiltro = function () {
    const anio = $('#filtroAnio').val();
    const mes = $('#filtroMes').val();
    const diaSelect = $('#filtroDia');

    const diaSeleccionado = diaSelect.val();

    diaSelect.html('');

    const totalDias = new Date(anio, mes, 0).getDate();

    for (let dia = 1; dia <= totalDias; dia++) {
        diaSelect.append(`
            <option value="${dia}">${dia}</option>
        `);
    }

    if (diaSeleccionado && diaSeleccionado <= totalDias) {
        diaSelect.val(diaSeleccionado);
    } else {
        diaSelect.val('1');
    }
};

$.fn.getFiltrosSeleccionados = function () {
    const filtros = {
        anio: $('#filtroAnio').val(),
        mes: $('#filtroMes').val(),
        dia: $('#filtroDia').val()
    };

    console.log('Filtros seleccionados:', filtros);

    return filtros;
};
// ===============================
// EVENTOS
// ===============================
$.fn.initEventos = function () {

    $('#filtroAnio, #filtroMes').on('change', function () {
        $.fn.cargarDiasFiltro();
    });

    $('#btnAplicarFiltros').on('click', function () {
        const filtros = $.fn.getFiltrosSeleccionados();

        console.log('Aplicando filtros:', filtros);

        /*
            $.fn.getIngresos(filtros);
            $.fn.getGastos(filtros);
            $.fn.getInformeTareas(filtros);
            $.fn.getIngresosVsGastos(filtros);
        */
    });
};
// ===============================
// ALERTAS
// ===============================
function mostrarAlertaError(mensaje) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: mensaje
        });
    } else {
        console.error(mensaje);
    }
}
// ===============================
// INICIALIZACIÓN GENERAL
// ===============================
$(document).ready(function () {
    $.fn.initFiltros();
    $.fn.initEventos();
    $.fn.getClima();
});