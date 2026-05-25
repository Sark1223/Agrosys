$(document).ready(function () {

    // ===============================
    // DETECCIÓN DE VISTA
    // ===============================
    const esAdmin = $('main[th\\:if]').length > 0 || $('#btnRegistrarNuevoLote').length > 0;
    const esUsuario = $('#cardReloj').length > 0;

    // ===============================
    // BOTÓN REGISTRAR ASISTENCIA
    // ===============================
    $('#btnRegistrarAsistencia').click(function (e) {
        e.preventDefault();
        const workerId = $(this).data('worker-id');
        $.ajax({
            url: '/agrosys/workers/attendance/asistence',
            type: 'GET',
            data: { workerId: workerId },
            success: function (response) {
                if (response.data > 0) {
                    Swal.fire({
                        icon: 'warning', title: 'Asistencia', text: 'Ya se registró la asistencia del día de hoy.'
                    });
                } else {
                    window.location.href = '/agrosys/workers';
                }
            },
            error: function () {
                Swal.fire({
                    icon: 'error', title: 'Error', text: 'No se pudo validar la asistencia.'
                });
            }
        });
    });

    // ===============================
    // CLIMA — admin 
    // ===============================
    $.fn.getClima = function () {
        const latitud = 20.5888;
        const longitud = -100.3899;

        $.ajax({
            url: `https://api.open-meteo.com/v1/forecast?latitude=${latitud}&longitude=${longitud}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&daily=temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=1`,
            type: 'GET',
            success: function (response) {
                const climaContainer = $('#climaContenido');
                climaContainer.html('');

                if (response.current && response.daily) {
                    const temperatura = Math.round(response.current.temperature_2m);
                    const humedad = response.current.relative_humidity_2m;
                    const viento = response.current.wind_speed_10m;
                    const codigoClima = response.current.weather_code;
                    const maxima = Math.round(response.daily.temperature_2m_max[0]);
                    const minima = Math.round(response.daily.temperature_2m_min[0]);
                    const descripcion = getDescripcionClima(codigoClima);
                    const icono = getIconoClima(codigoClima);

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
    // CLIMA — usuario 
    // ===============================
    $.fn.getClimaExtendido = function () {
        const latitud = 20.5888;
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

                const temp = Math.round(res.current.temperature_2m);
                const humedad = res.current.relative_humidity_2m;
                const viento = res.current.wind_speed_10m;
                const codigo = res.current.weather_code;
                const maxHoy = Math.round(res.daily.temperature_2m_max[0]);
                const minHoy = Math.round(res.daily.temperature_2m_min[0]);
                const descripcion = getDescripcionClima(codigo);
                const icono = getIconoClima(codigo);

                const ahora = new Date();
                const horasTimes = res.hourly.time;
                const horasTemps = res.hourly.temperature_2m;
                const horasCodigos = res.hourly.weather_code;
                let horasProximas = [];

                for (let i = 0; i < horasTimes.length && horasProximas.length < 8; i++) {
                    const fecha = new Date(horasTimes[i]);
                    if (fecha >= ahora) {
                        horasProximas.push({
                            hora: fecha.getHours(),
                            temp: Math.round(horasTemps[i]),
                            icono: getIconoClima(horasCodigos[i])
                        });
                    }
                }

                const diasSemana = ['Dom', 'Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb'];
                const diasDiario = res.daily.time.map((t, idx) => {
                    const d = new Date(t + 'T12:00:00');
                    return {
                        nombre: idx === 0 ? 'Hoy' : diasSemana[d.getDay()],
                        max: Math.round(res.daily.temperature_2m_max[idx]),
                        min: Math.round(res.daily.temperature_2m_min[idx]),
                        icono: getIconoClima(res.daily.weather_code[idx])
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
        const h12 = h % 12 || 12;
        return `${h12}:00 ${ampm}`;
    }

    function getDescripcionClima(codigo) {
        const codigos = {
            0: 'Despejado', 1: 'Mayormente despejado', 2: 'Parcialmente nublado', 3: 'Nublado',
            45: 'Niebla', 48: 'Niebla con escarcha', 51: 'Llovizna ligera', 53: 'Llovizna moderada',
            55: 'Llovizna intensa', 61: 'Lluvia ligera', 63: 'Lluvia moderada', 65: 'Lluvia intensa',
            80: 'Chubascos ligeros', 81: 'Chubascos moderados', 82: 'Chubascos fuertes', 95: 'Tormenta eléctrica'
        };
        return codigos[codigo] || 'Clima no disponible';
    }

    function getIconoClima(codigo) {
        if (codigo === 0) return '☀️';
        if (codigo === 1 || codigo === 2) return '🌤️';
        if (codigo === 3) return '☁️';
        if (codigo === 45 || codigo === 48) return '🌫️';
        if ([51, 53, 55, 61, 63, 65, 80, 81, 82].includes(codigo)) return '🌧️';
        if (codigo === 95) return '⛈️';
        return '🌡️';
    }

    // ===============================
    // ALERTA
    // ===============================
    function mostrarAlertaError(mensaje) {
        if (typeof Swal !== 'undefined') {
            Swal.fire({ icon: 'error', title: 'Error', text: mensaje });
        } else {
            console.error(mensaje);
        }
    }

    // ===============================
    // INGRESOS Y GASTOS
    // ===============================
    $.fn.cargarTransacciones = function (anio, mes, dia) {
        const fecha = anio + '-' + String(mes).padStart(2, '0') + '-' + String(dia).padStart(2, '0');

        $.ajax({
            url: '/agrosys/transactions/transaction?startDate=' + fecha + '&endDate=' + fecha,
            type: 'GET',
            success: function (response) {
                const data = response.data;
                const ingresos = data?.totalIngresos ?? 0;
                const egresos = data?.totalEgresos ?? 0;

                // Card Ingresos
                if (ingresos === 0) {
                    $('#cardIngresos').html(`
                    <h3>INGRESOS</h3>
                    <p style="color:#9ca3af;font-size:13px;margin-top:16px;align-self: center;">No hay ingresos para este día.</p>
                `);
                } else {
                    $('#cardIngresos').html(`
                    <h3>INGRESOS</h3>
                    <div style="display:flex;flex-direction:column;align-items:center;gap:10px;margin-top:8px;">
                        <div style="width:64px;height:64px;border-radius:50%;background:#dcfce7;display:flex;align-items:center;justify-content:center;">
                            <i class="ri-arrow-up-line" style="font-size:28px;color:#16a34a;"></i>
                        </div>
                        <span style="font-size:20px;font-weight:700;color:#16a34a;">+$${parseFloat(ingresos).toLocaleString('es-MX', { minimumFractionDigits: 2 })}</span>
                    </div>
                `);
                }

                // Card Gastos
                if (egresos === 0) {
                    $('#cardGastos').html(`
                    <h3>GASTOS</h3>
                    <p style="color:#9ca3af;font-size:13px;margin-top:16px; align-self: center;">No hay gastos para este día.</p>
                `);
                } else {
                    $('#cardGastos').html(`
                    <h3>GASTOS</h3>
                    <div style="display:flex;flex-direction:column;align-items:center;gap:10px;margin-top:8px;">
                        <div style="width:64px;height:64px;border-radius:50%;background:#fee2e2;display:flex;align-items:center;justify-content:center;">
                            <i class="ri-bank-card-line" style="font-size:28px;color:#dc2626;"></i>
                        </div>
                        <span style="font-size:20px;font-weight:700;color:#dc2626;">-$${parseFloat(egresos).toLocaleString('es-MX', { minimumFractionDigits: 2 })}</span>
                    </div>
                `);
                }
            },
            error: function () {
                $('#cardIngresos').html('<h3>INGRESOS</h3><p style="color:#d32f2f;font-size:13px;margin-top:16px;">Error al cargar.</p>');
                $('#cardGastos').html('<h3>GASTOS</h3><p style="color:#d32f2f;font-size:13px;margin-top:16px;">Error al cargar.</p>');
            }
        });
    };
    // ===============================
    // LÓGICA EXCLUSIVA DEL ADMIN
    // ===============================
    if (esAdmin) {

        $('#btnRegistrarNuevoLote').click(function () {
            $('#plantationIdLotInput').removeAttr('disabled');
        });

        // ===============================
        // RESUMEN SEMANAL DE SALARIOS
        // ===============================
        let modoEdicion = false;

        function recalcularTotales() {
            let grandTotal = 0;

            $('#modalSalariosBody tr').each(function () {
                let totalFila = 0;

                $(this).find('td[data-tipo="dia"]').each(function () {
                    const input = $(this).find('input');
                    const val = input.length ? parseFloat(input.val()) || 0 : parseFloat($(this).text().replace('$', '')) || 0;
                    totalFila += val;
                });

                $(this).find('td[data-tipo="extra"]').each(function () {
                    const input = $(this).find('input');
                    const val = input.length ? parseFloat(input.val()) || 0 : parseFloat($(this).find('span').text().replace('+$', '')) || 0;
                    totalFila += val;
                });

                $(this).find('td[data-tipo="total"]').text('$' + totalFila.toFixed(2));
                grandTotal += totalFila;
            });

            $('#modalSalariosGrandTotal').text('$' + grandTotal.toFixed(2));
        }

        function activarEdicion() {
            $('#modalSalariosBody tr').each(function () {
                $(this).find('td[data-tipo="dia"]').each(function () {
                    const td = $(this);
                    const texto = td.text().trim();
                    const val = texto === 'n/a' ? 0 : parseFloat(texto.replace('$', '')) || 0;
                    td.html('<input type="number" min="0" step="0.01" value="' + val.toFixed(2) + '" style="width:75px;border:1px solid #d1d5db;border-radius:6px;padding:2px 6px;font-size:12px;text-align:center;">');
                    td.find('input').on('input', function () { recalcularTotales(); });
                });

                $(this).find('td[data-tipo="extra"]').each(function () {
                    const td = $(this);
                    const span = td.find('span');
                    const val = span.length ? parseFloat(span.text().replace('+$', '').replace('—', '0')) || 0 : 0;
                    td.html('<input type="number" min="0" step="0.01" value="' + val.toFixed(2) + '" style="width:75px;border:1px solid #d1d5db;border-radius:6px;padding:2px 6px;font-size:12px;text-align:center;">');
                    td.find('input').on('input', function () { recalcularTotales(); });
                });
            });
        }

        function desactivarEdicion() {
            $('#modalSalariosBody tr').each(function () {
                $(this).find('td[data-tipo="dia"]').each(function () {
                    const td = $(this);
                    const val = parseFloat(td.find('input').val()) || 0;
                    td.html(val === 0 ? '<span style="color:#9ca3af;">n/a</span>' : '$' + val.toFixed(2));
                });

                $(this).find('td[data-tipo="extra"]').each(function () {
                    const td = $(this);
                    const val = parseFloat(td.find('input').val()) || 0;
                    td.html(val > 0
                        ? '<span style="background:#faeeda;color:#633806;font-size:11px;padding:2px 8px;border-radius:6px;">+$' + val.toFixed(2) + '</span>'
                        : '<span style="color:#d1d5db;">—</span>');
                });
            });
        }

        $(document).on('click', '#btnModificarSalarios', function () {
            modoEdicion = !modoEdicion;
            if (modoEdicion) {
                activarEdicion();
                $(this).text('Listo').removeClass('btn-lote').addClass('btn-asistencia');
            } else {
                desactivarEdicion();
                recalcularTotales();
                $(this).text('Modificar').removeClass('btn-asistencia').addClass('btn-lote');
            }
        });

        $(document).on('click', '#btnGenerarPago', function () {
            if (modoEdicion) {
                desactivarEdicion();
                recalcularTotales();
                modoEdicion = false;
                $('#btnModificarSalarios').text('Modificar').removeClass('btn-asistencia').addClass('btn-lote');
            }

            // ====================================================
            // FUNCION PARA GENERAR EL PDF 
            // ====================================================
            const { jsPDF } = window.jspdf;
            const doc = new jsPDF('l', 'mm', 'a4');

            const subtitulo = $('#modalSalariosSubtitle').text();
            const grandTotal = $('#modalSalariosGrandTotal').text();

            doc.setFontSize(14);
            doc.setFont('helvetica', 'bold');
            doc.text('Resumen semanal de salarios', 14, 16);
            doc.setFontSize(9);
            doc.setFont('helvetica', 'normal');
            doc.setTextColor(107, 114, 128);
            doc.text(subtitulo, 14, 22);
            doc.setTextColor(0, 0, 0);

            const headers = ['Nombre', 'Lunes', 'Martes', 'Miérc.', 'Jueves', 'Viernes', 'Extras', 'Total'];
            const rows = [];

            $('#modalSalariosBody tr').each(function () {
                const fila = [];
                $(this).find('td').each(function () {
                    fila.push($(this).text().trim());
                });
                rows.push(fila);
            });

            rows.push(['', '', '', '', '', '', 'Total general', grandTotal]);

            doc.autoTable({
                head: [headers],
                body: rows,
                startY: 28,
                styles: { fontSize: 10, cellPadding: 4 },
                headStyles: { fillColor: [249, 250, 251], textColor: [107, 114, 128], fontStyle: 'bold' },
                columnStyles: { 0: { fontStyle: 'bold' } },
                didParseCell: function (data) {
                    if (data.row.index === rows.length - 1) {
                        data.cell.styles.fontStyle = 'bold';
                    }
                }
            });

            doc.setFontSize(8);
            doc.setTextColor(150);
            doc.text('Generado el ' + new Date().toLocaleDateString('es-MX'), 14, doc.internal.pageSize.height - 8);

            doc.save('resumen-semanal-salarios.pdf');
        });

        $('#btnRegistrarNuevaTransaccion').click(function () {
            modoEdicion = false;
            $('#btnModificarSalarios').text('Modificar').removeClass('btn-asistencia').addClass('btn-lote');
            $('#modalSalariosBody').html('');
            $('#modalSalariosFoot').html('');
            $('#modalSalariosLoading').show();
            $('#modalSalariosContenido').hide();
            $('#modalSalariosTotal').hide();
            $('#modalSalariosGrandTotal').text('');
            $('#modalSalariosSubtitle').text('');

            $('#modalResumenSalarios').modal('show');

            $.ajax({
                url: '/agrosys/workers/attendance/weekly-salary',
                type: 'GET',
                success: function (response) {
                    if (!response.success || !response.data) {
                        $('#modalSalariosLoading').hide();
                        $('#modalSalariosBody').html('<tr><td colspan="8" class="text-center py-4" style="font-size:13px;color:#6b7280;">No se pudo cargar el reporte.</td></tr>');
                        $('#modalSalariosContenido').show();
                        return;
                    }

                    const data = response.data;
                    const dias = ['lunes', 'martes', 'miercoles', 'jueves', 'viernes'];
                    const workers = data.workers;

                    $('#modalSalariosSubtitle').text('Semana del ' + formatFecha(data.weekStart) + ' al ' + formatFecha(data.weekEnd));

                    let grandTotal = 0;
                    let rowsHtml = '';

                    workers.forEach(function (w) {
                        let fila = '<tr>';
                        fila += '<td style="padding:10px 16px;font-weight:500;color:#1f2937;">' + w.name + '</td>';

                        dias.forEach(function (dia) {
                            const val = w[dia];
                            if (val !== null && val !== undefined) {
                                fila += '<td data-tipo="dia" class="text-center" style="padding:10px 8px;color:#1f2937;">$' + parseFloat(val).toFixed(2) + '</td>';
                            } else {
                                fila += '<td data-tipo="dia" class="text-center" style="padding:10px 8px;color:#9ca3af;">n/a</td>';
                            }
                        });

                        const extras = parseFloat(w.extras || 0);
                        if (extras > 0) {
                            fila += '<td data-tipo="extra" class="text-center" style="padding:10px 8px;"><span style="background:#faeeda;color:#633806;font-size:11px;padding:2px 8px;border-radius:6px;">+$' + extras.toFixed(2) + '</span></td>';
                        } else {
                            fila += '<td data-tipo="extra" class="text-center" style="padding:10px 8px;"><span style="color:#d1d5db;">—</span></td>';
                        }

                        const total = parseFloat(w.total || 0);
                        grandTotal += total;
                        fila += '<td data-tipo="total" style="padding:10px 24px;font-weight:600;color:#1f2937;">$' + total.toFixed(2) + '</td>';
                        fila += '</tr>';
                        rowsHtml += fila;
                    });

                    $('#modalSalariosBody').html(rowsHtml);
                    $('#modalSalariosFoot').html('');
                    $('#modalSalariosGrandTotal').text('$' + grandTotal.toFixed(2));
                    $('#modalSalariosLoading').hide();
                    $('#modalSalariosContenido').show();
                    $('#modalSalariosTotal').css('display', 'flex');
                },
                error: function () {
                    $('#modalSalariosLoading').hide();
                    $('#modalSalariosBody').html('<tr><td colspan="8" class="text-center py-4" style="font-size:13px;color:#d32f2f;">Error al cargar el reporte semanal.</td></tr>');
                    $('#modalSalariosContenido').show();
                }
            });
        });

        function formatFecha(fechaStr) {
            const meses = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];
            const partes = fechaStr.split('-');
            return parseInt(partes[2]) + ' ' + meses[parseInt(partes[1]) - 1] + ' ' + partes[0];
        }

        $.fn.initFiltros = function () {
            const anioSelect = $('#filtroAnio');
            const mesSelect = $('#filtroMes');
            anioSelect.html('');
            mesSelect.html('');

            for (let anio = 2026; anio <= 2031; anio++) {
                anioSelect.append(`<option value="${anio}">${anio}</option>`);
            }

            const meses = [
                { valor: 1, nombre: 'Enero' }, { valor: 2, nombre: 'Febrero' },
                { valor: 3, nombre: 'Marzo' }, { valor: 4, nombre: 'Abril' },
                { valor: 5, nombre: 'Mayo' }, { valor: 6, nombre: 'Junio' },
                { valor: 7, nombre: 'Julio' }, { valor: 8, nombre: 'Agosto' },
                { valor: 9, nombre: 'Septiembre' }, { valor: 10, nombre: 'Octubre' },
                { valor: 11, nombre: 'Noviembre' }, { valor: 12, nombre: 'Diciembre' }
            ];

            meses.forEach(function (mes) {
                mesSelect.append(`<option value="${mes.valor}">${mes.nombre}</option>`);
            });

            //anioSelect.val('2026');
            mesSelect.val('5');
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
                diaSelect.append(`<option value="${dia}">${dia}</option>`);
            }
            diaSelect.val((diaSeleccionado && diaSeleccionado <= totalDias) ? diaSeleccionado : '1');
        };

        $.fn.getFiltrosSeleccionados = function () {
            return { anio: $('#filtroAnio').val(), mes: $('#filtroMes').val(), dia: $('#filtroDia').val() };
        };

        $.fn.initEventos = function () {
            $('#filtroAnio, #filtroMes').on('change', function () { $.fn.cargarDiasFiltro(); });
            $('#btnAplicarFiltros').on('click', function () {
                const filtros = $.fn.getFiltrosSeleccionados();
                const dia = filtros.dia != '1' ? filtros.dia : null;
                $.fn.cargarGraficoTareas(filtros.anio, filtros.mes, dia);
                $.fn.cargarGraficoTareasPorTrabajador(filtros.anio, filtros.mes, dia);
                $.fn.cargarTransacciones(filtros.anio, filtros.mes, filtros.dia);
            });
        };

        function construirParams(anio, mes, dia) {
            let params = 'anio=' + anio;
            if (mes) params += '&mes=' + mes;
            if (dia) params += '&dia=' + dia;
            return params;
        }

        function mostrarSinDatos(canvasId, msgId) {
            const canvas = document.getElementById(canvasId);
            if (canvas) canvas.style.display = 'none';
            let msg = document.getElementById(msgId);
            if (!msg) {
                msg = document.createElement('p');
                msg.id = msgId;
                msg.style.cssText = 'text-align:center; color:#888; font-size:13px; margin-top:20px;';
                canvas.parentNode.appendChild(msg);
            }
            msg.textContent = 'No hay datos para el periodo seleccionado.';
            msg.style.display = 'block';
        }

        function ocultarSinDatos(canvasId, msgId) {
            const msg = document.getElementById(msgId);
            if (msg) msg.style.display = 'none';
            const canvas = document.getElementById(canvasId);
            if (canvas) canvas.style.display = 'block';
        }

        let taskChartInstance = null;

        $.fn.cargarGraficoTareas = function (anio, mes, dia) {
            const params = construirParams(anio, mes, dia);
            $.ajax({
                url: '/agrosys/tasks/summary?' + params,
                type: 'GET',
                success: function (json) {
                    const data = json.data;
                    const ctx = document.getElementById('taskChart');
                    if (!ctx) return;
                    const valores = [data['Pendiente'] ?? 0, data['En Proceso'] ?? 0, data['Finalizada'] ?? 0];
                    const sinDatos = valores.every(v => v === 0);
                    if (taskChartInstance) { taskChartInstance.destroy(); taskChartInstance = null; }
                    if (sinDatos) { mostrarSinDatos('taskChart', 'taskChartMsg'); return; }
                    ocultarSinDatos('taskChart', 'taskChartMsg');
                    taskChartInstance = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: ['Pendiente', 'En Proceso', 'Finalizada'],
                            datasets: [{ label: 'Tareas', data: valores, backgroundColor: ['#FFB86A', '#8EC5FF', '#BBF451'], borderRadius: 6, borderWidth: 0 }]
                        },
                        options: {
                            indexAxis: 'y', responsive: true,
                            plugins: { legend: { display: false } },
                            scales: { x: { beginAtZero: true, ticks: { precision: 0 } } }
                        }
                    });
                },
                error: function () { console.error('Error al cargar el resumen de tareas'); }
            });
        };

        let workerChartInstance = null;

        $.fn.cargarGraficoTareasPorTrabajador = function (anio, mes, dia) {
            const params = construirParams(anio, mes, dia);
            $.ajax({
                url: '/agrosys/tasks/summary/by-worker?' + params,
                type: 'GET',
                success: function (json) {
                    const data = json.data;
                    const ctx = document.getElementById('workerTaskChart');
                    if (!ctx) return;
                    if (workerChartInstance) { workerChartInstance.destroy(); workerChartInstance = null; }
                    if (!data || data.length === 0) { mostrarSinDatos('workerTaskChart', 'workerChartMsg'); return; }
                    ocultarSinDatos('workerTaskChart', 'workerChartMsg');
                    workerChartInstance = new Chart(ctx, {
                        type: 'bar',
                        data: {
                            labels: data.map(d => d.workerName),
                            datasets: [
                                { label: 'Pendiente', data: data.map(d => d['Pendiente']), backgroundColor: '#FFB86A', borderRadius: 4, borderWidth: 0 },
                                { label: 'En Proceso', data: data.map(d => d['En Proceso']), backgroundColor: '#8EC5FF', borderRadius: 4, borderWidth: 0 },
                                { label: 'Finalizada', data: data.map(d => d['Finalizada']), backgroundColor: '#BBF451', borderRadius: 4, borderWidth: 0 }
                            ]
                        },
                        options: {
                            responsive: true,
                            plugins: { legend: { position: 'bottom', align: 'center', labels: { boxWidth: 12, padding: 10, font: { size: 11 } } } },
                            scales: { x: { stacked: true }, y: { stacked: true, beginAtZero: true, ticks: { precision: 0 } } }
                        }
                    });
                },
                error: function () { console.error('Error al cargar resumen de tareas por trabajador'); }
            });
        };

        $.fn.initFiltros();
        $.fn.initEventos();
        $.fn.getClima();

        const filtrosIniciales = $.fn.getFiltrosSeleccionados();
        $.fn.cargarGraficoTareas(filtrosIniciales.anio, filtrosIniciales.mes, null);
        $.fn.cargarGraficoTareasPorTrabajador(filtrosIniciales.anio, filtrosIniciales.mes, null);

        const hoy = new Date();
        $.fn.cargarTransacciones(hoy.getFullYear(), hoy.getMonth() + 1, hoy.getDate());
    }

    // ===============================
    // LÓGICA EXCLUSIVA DEL USUARIO
    // ===============================
    if (esUsuario) {

        $.fn.iniciarRelojUsuario = function () {
            const relojHoras = $('#relojHoras');
            const relojMinutos = $('#relojMinutos');
            const relojSegundos = $('#relojSegundos');
            const relojAmPm = $('#relojAmPm');
            if (!relojHoras.length) return;

            function actualizarReloj() {
                const ahora = new Date();
                let horas = ahora.getHours();
                const minutos = String(ahora.getMinutes()).padStart(2, '0');
                const segundos = String(ahora.getSeconds()).padStart(2, '0');
                const ampm = horas >= 12 ? 'PM' : 'AM';
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

        function initFecha() {
            const dias = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
            const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
            const hoy = new Date();
            $('#fechaDia').text(hoy.getDate());
            $('#fechaNombre').text(dias[hoy.getDay()] + ', ' + meses[hoy.getMonth()] + ' ' + hoy.getFullYear());
        }

        function renderSemana(semana) {
            const container = document.getElementById('semanaDotsContainer');
            if (!container) return;
            container.innerHTML = '';
            semana.forEach(({ dia, estado }) => {
                const col = document.createElement('div');
                col.className = 'semana-col';
                const dot = document.createElement('div');
                dot.className = 'semana-dot semana-dot--' + estado;
                if (estado === 'ok') dot.innerHTML = '<i class="ri-check-line"></i>';
                else if (estado === 'falta') dot.innerHTML = '<i class="ri-close-line"></i>';
                else if (estado === 'hoy') dot.textContent = 'Hoy';
                else dot.textContent = dia.charAt(0);
                const lbl = document.createElement('span');
                lbl.className = 'semana-dot-lbl';
                lbl.textContent = dia;
                if (estado === 'futuro') lbl.style.opacity = '0';
                col.appendChild(dot);
                col.appendChild(lbl);
                container.appendChild(col);
            });
        }

        $.fn.iniciarRelojUsuario();
        $.fn.getClimaExtendido();
        initFecha();
        renderSemana([
            { dia: 'Lun', estado: 'ok' },
            { dia: 'Mar', estado: 'ok' },
            { dia: 'Mié', estado: 'hoy' },
            { dia: 'Jue', estado: 'futuro' },
            { dia: 'Vie', estado: 'futuro' },
        ]);
    }

});