$(document).ready(function () {

    // ===============================
    // DETECCIÓN DE VISTA
    // ===============================
    const esAdmin = $('main[th\\:if]').length > 0 || $('#btnRegistrarNuevoLote').length > 0;
    const esUsuario = $('#cardReloj').length > 0;

    // ===============================
    // BOTÓN REGISTRAR ASISTENCIA (compartido)
    // ===============================
    $('#btnRegistrarAsistencia').click(function (e) {
        e.preventDefault();

        const workerId = $(this).data('worker-id');

        $.ajax({
            url: '/agrosys/workers/attendance/asistence',
            type: 'GET',
            data: workerId ? { workerId: workerId } : {},
            success: function (response) {
                if (response.data > 0) {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Asistencia',
                        text: 'Ya se registró la asistencia del día de hoy.'
                    });
                } else {
                    window.location.href = '/agrosys/workers';
                }
            },
            error: function () {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo validar la asistencia.'
                });
            }
        });
    });

    // ===============================
    // CLIMA — admin (simple, 1 día)
    // ===============================
    $.fn.getClima = function () {
        const latitud  = 20.5888;
        const longitud = -100.3899;

        $.ajax({
            url: `https://api.open-meteo.com/v1/forecast?latitude=${latitud}&longitude=${longitud}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1`,
            type: 'GET',
            success: function (response) {
                const climaContainer = $('#climaContenido');
                climaContainer.html('');

                if (response.current && response.daily) {
                    const temperatura = Math.round(response.current.temperature_2m);
                    const humedad    = response.current.relative_humidity_2m;
                    const viento     = response.current.wind_speed_10m;
                    const codigoClima = response.current.weather_code;
                    const maxima     = Math.round(response.daily.temperature_2m_max[0]);
                    const minima     = Math.round(response.daily.temperature_2m_min[0]);
                    const descripcion = getDescripcionClima(codigoClima);
                    const icono       = getIconoClima(codigoClima);

                    climaContainer.append(`
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
                } else {
                    climaContainer.html('<p class="clima-error">No se pudo cargar la información del clima.</p>');
                    mostrarAlertaError('No se pudo obtener la información del clima');
                }
            },
            error: function (xhr, status, error) {
                console.error('Error clima admin:', error);
                $('#climaContenido').html('<p class="clima-error">No se pudo cargar el clima.</p>');
                mostrarAlertaError('Ocurrió un error al comunicarse con la API del clima');
            }
        });
    };

    // ===============================
    // CLIMA — usuario (extendido: horas + 7 días)
    // ===============================
    $.fn.getClimaExtendido = function () {
        const latitud  = 20.5888;
        const longitud = -100.3899;

        const url = `https://api.open-meteo.com/v1/forecast`
            + `?latitude=${latitud}&longitude=${longitud}`
            + `&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m`
            + `&hourly=temperature_2m,weather_code`
            + `&daily=temperature_2m_max,temperature_2m_min,weather_code`
            + `&timezone=auto&forecast_days=7`;

        $.ajax({
            url,
            type: 'GET',
            success: function (res) {
                const container = $('#climaContenido');
                container.html('');

                if (!res.current || !res.daily) {
                    container.html('<p class="clima-error">No se pudo cargar el clima.</p>');
                    return;
                }

                /* Datos actuales */
                const temp        = Math.round(res.current.temperature_2m);
                const humedad     = res.current.relative_humidity_2m;
                const viento      = res.current.wind_speed_10m;
                const codigo      = res.current.weather_code;
                const maxHoy      = Math.round(res.daily.temperature_2m_max[0]);
                const minHoy      = Math.round(res.daily.temperature_2m_min[0]);
                const descripcion = getDescripcionClima(codigo);
                const icono       = getIconoClima(codigo);

                /* Próximas 8 horas desde ahora */
                const ahora        = new Date();
                const horasTimes   = res.hourly.time;
                const horasTemps   = res.hourly.temperature_2m;
                const horasCodigos = res.hourly.weather_code;
                let horasProximas  = [];

                for (let i = 0; i < horasTimes.length && horasProximas.length < 8; i++) {
                    const fecha = new Date(horasTimes[i]);
                    if (fecha >= ahora) {
                        horasProximas.push({
                            hora:  fecha.getHours(),
                            temp:  Math.round(horasTemps[i]),
                            icono: getIconoClima(horasCodigos[i])
                        });
                    }
                }

                /* 7 días */
                const diasSemana = ['Dom','Lun','Mar','Mié','Jue','Vie','Sáb'];
                const diasDiario = res.daily.time.map((t, idx) => {
                    const d = new Date(t + 'T12:00:00');
                    return {
                        nombre: idx === 0 ? 'Hoy' : diasSemana[d.getDay()],
                        max:    Math.round(res.daily.temperature_2m_max[idx]),
                        min:    Math.round(res.daily.temperature_2m_min[idx]),
                        icono:  getIconoClima(res.daily.weather_code[idx])
                    };
                });

                container.html(`
                    <div class="clima-top">
                        <div class="clima-ciudad">Querétaro</div>
                        <div class="clima-actual-row">
                            <span class="clima-temp-grande">${temp}°C</span>
                            <div class="clima-icono-desc">
                                <span class="clima-emoji">${icono}</span>
                                <span class="clima-desc">${descripcion}</span>
                            </div>
                        </div>
                        <div class="clima-sub-datos">
                            <span>↑ ${maxHoy}°  ↓ ${minHoy}°</span>
                            <span>💧 ${humedad}%</span>
                            <span>💨 ${viento} km/h</span>
                        </div>
                    </div>

                    <div class="clima-divider"></div>

                    <div class="clima-cuerpo">

                        <div class="clima-horas-scroll">
                            ${horasProximas.map(h => `
                                <div class="clima-hora-item">
                                    <span class="clima-hora-lbl">${formatHoraClima(h.hora)}</span>
                                    <span class="clima-hora-emoji">${h.icono}</span>
                                    <span class="clima-hora-temp">${h.temp}°</span>
                                </div>
                            `).join('')}
                        </div>

                        <div class="clima-semana">
                            ${diasDiario.map(d => `
                                <div class="clima-dia-item">
                                    <span class="clima-dia-nombre">${d.nombre}</span>
                                    <span class="clima-dia-emoji">${d.icono}</span>
                                    <span class="clima-dia-temps">
                                        <span class="clima-dia-max">${d.max}°</span>
                                        <span class="clima-dia-min">${d.min}°</span>
                                    </span>
                                </div>
                            `).join('')}
                        </div>

                    </div>
                `);
            },
            error: function (xhr, status, error) {
                console.error('Error clima usuario:', error);
                $('#climaContenido').html('<p class="clima-error">No se pudo cargar el clima.</p>');
            }
        });
    };

    function formatHoraClima(h) {
        const ampm = h >= 12 ? 'PM' : 'AM';
        const h12  = h % 12 || 12;
        return `${h12}:00 ${ampm}`;
    }

    function getDescripcionClima(codigo) {
        const codigos = {
            0:  'Despejado',
            1:  'Mayormente despejado',
            2:  'Parcialmente nublado',
            3:  'Nublado',
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
        if (codigo === 0)                    return '☀️';
        if (codigo === 1 || codigo === 2)    return '🌤️';
        if (codigo === 3)                    return '☁️';
        if (codigo === 45 || codigo === 48)  return '🌫️';
        if ([51,53,55,61,63,65,80,81,82].includes(codigo)) return '🌧️';
        if (codigo === 95)                   return '⛈️';
        return '🌡️';
    }

    // ===============================
    // ALERTAS (compartido)
    // ===============================
    function mostrarAlertaError(mensaje) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({ icon: 'error', title: 'Error', text: mensaje });
        } else {
            console.error(mensaje);
        }
    }

    // ===============================
    // LÓGICA EXCLUSIVA DEL ADMIN
    // ===============================
    if (esAdmin) {

        // Navegación de botones
        $('#btnRegistrarNuevoLote').click(function () {
            window.location.href = '/agrosys/plots';
        });

        $('#btnRegistrarNuevaTransaccion').click(function () {
            window.location.href = '/';
        });

        $('#btnVerSalarios').click(function () {
            window.location.href = '/';
        });

        // Filtros
        $.fn.initFiltros = function () {
            const anioSelect = $('#filtroAnio');
            const mesSelect  = $('#filtroMes');

            anioSelect.html('');
            mesSelect.html('');

            for (let anio = 2026; anio <= 2031; anio++) {
                anioSelect.append(`<option value="${anio}">${anio}</option>`);
            }

            const meses = [
                { valor: 1,  nombre: 'Enero' },
                { valor: 2,  nombre: 'Febrero' },
                { valor: 3,  nombre: 'Marzo' },
                { valor: 4,  nombre: 'Abril' },
                { valor: 5,  nombre: 'Mayo' },
                { valor: 6,  nombre: 'Junio' },
                { valor: 7,  nombre: 'Julio' },
                { valor: 8,  nombre: 'Agosto' },
                { valor: 9,  nombre: 'Septiembre' },
                { valor: 10, nombre: 'Octubre' },
                { valor: 11, nombre: 'Noviembre' },
                { valor: 12, nombre: 'Diciembre' }
            ];

            meses.forEach(function (mes) {
                mesSelect.append(`<option value="${mes.valor}">${mes.nombre}</option>`);
            });

            anioSelect.val('2026');
            mesSelect.val('4');

            $.fn.cargarDiasFiltro();
        };

        $.fn.cargarDiasFiltro = function () {
            const anio = $('#filtroAnio').val();
            const mes  = $('#filtroMes').val();
            const diaSelect = $('#filtroDia');
            const diaSeleccionado = diaSelect.val();

            diaSelect.html('');

            const totalDias = new Date(anio, mes, 0).getDate();

            for (let dia = 1; dia <= totalDias; dia++) {
                diaSelect.append(`<option value="${dia}">${dia}</option>`);
            }

            diaSelect.val((diaSeleccionado && diaSeleccionado <= totalDias) ? diaSeleccionado : '1');
        };

        $.fn.getFiltrosSeleccionados = function () {
            return {
                anio: $('#filtroAnio').val(),
                mes:  $('#filtroMes').val(),
                dia:  $('#filtroDia').val()
            };
        };

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

        // Inicialización admin
        $.fn.initFiltros();
        $.fn.initEventos();
        $.fn.getClima();
    }

    // ===============================
    // LÓGICA EXCLUSIVA DEL USUARIO
    // ===============================
    if (esUsuario) {

        // Reloj
        $.fn.iniciarRelojUsuario = function () {
            const relojHoras    = $('#relojHoras');
            const relojMinutos  = $('#relojMinutos');
            const relojSegundos = $('#relojSegundos');
            const relojAmPm     = $('#relojAmPm');

            if (!relojHoras.length) return;

            function actualizarReloj() {
                const ahora = new Date();
                let horas   = ahora.getHours();
                const minutos  = String(ahora.getMinutes()).padStart(2, '0');
                const segundos = String(ahora.getSeconds()).padStart(2, '0');
                const ampm  = horas >= 12 ? 'PM' : 'AM';

                horas = horas % 12 || 12;
                horas = String(horas).padStart(2, '0');

                relojHoras.text(horas);
                relojMinutos.text(minutos);
                relojSegundos.text(segundos);
                relojAmPm.text(ampm);
            }

            actualizarReloj();
            setInterval(actualizarReloj, 1000);
        };

        // Fecha
        function initFecha() {
            const dias  = ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'];
            const meses = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto',
                           'Septiembre','Octubre','Noviembre','Diciembre'];
            const hoy = new Date();

            $('#fechaDia').text(hoy.getDate());
            $('#fechaNombre').text(dias[hoy.getDay()] + ', ' + meses[hoy.getMonth()] + ' ' + hoy.getFullYear());
        }

        // Semana de asistencia
        function renderSemana(semana) {
            const container = document.getElementById('semanaDotsContainer');
            if (!container) return;
            container.innerHTML = '';

            semana.forEach(({ dia, estado }) => {
                const col = document.createElement('div');
                col.className = 'semana-col';

                const dot = document.createElement('div');
                dot.className = 'semana-dot semana-dot--' + estado;

                if (estado === 'ok')     dot.innerHTML = '<i class="ri-check-line"></i>';
                else if (estado === 'falta') dot.innerHTML = '<i class="ri-close-line"></i>';
                else if (estado === 'hoy')   dot.textContent = 'Hoy';
                else                         dot.textContent = dia.charAt(0);

                const lbl = document.createElement('span');
                lbl.className = 'semana-dot-lbl';
                lbl.textContent = dia;
                if (estado === 'futuro') lbl.style.opacity = '0';

                col.appendChild(dot);
                col.appendChild(lbl);
                container.appendChild(col);
            });
        }

        // Inicialización usuario
        $.fn.iniciarRelojUsuario();
        $.fn.getClimaExtendido();
        initFecha();

        /*
            TODO: reemplazar el arreglo estático por datos reales del controller,
            ya sea via Thymeleaf data-attribute o un fetch a tu API.
        */
        renderSemana([
            { dia: 'Lun', estado: 'ok' },
            { dia: 'Mar', estado: 'ok' },
            { dia: 'Mié', estado: 'hoy' },
            { dia: 'Jue', estado: 'futuro' },
            { dia: 'Vie', estado: 'futuro' },
        ]);
    }

});