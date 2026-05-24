let currentWeekId = null;

$(document).ready(function () {
    $.fn.loadWeeks();

    $('#btnLoadSummary').on('click', function () {
        const weekId = $('#weekSelect').val();
        if (!weekId) {
            Swal.fire({ icon: 'warning', title: 'Semana requerida', text: 'Selecciona una semana' });
            return;
        }
        currentWeekId = weekId;
        $.fn.loadSummary(weekId);
    });

    $('#btnGeneratePayments').on('click', function () {
        if (!currentWeekId) {
            Swal.fire({ icon: 'warning', title: 'Sin datos', text: 'Carga primero un resumen de semana' });
            return;
        }

        const rows = $('#tbodyPayments tr:not(.text-muted)');
        if (rows.length === 0) {
            Swal.fire({ icon: 'warning', title: 'Sin datos', text: 'No hay trabajadores para generar pago' });
            return;
        }

        Swal.fire({
            icon: 'question',
            title: '¿Generar pagos?',
            text: 'Se crearán transacciones EXPENSE para todos los trabajadores de esta semana',
            showCancelButton: true,
            confirmButtonText: 'Sí, generar',
            cancelButtonText: 'Cancelar'
        }).then(function (result) {
            if (result.isConfirmed) {
                $.fn.generatePayments();
            }
        });
    });

    $('#btnSaveEditPay').on('click', function () {
        const workerId = $('#editWorkerId').val();
        const weekId = $('#editWeekId').val();
        const amount = $('#editAmount').val();

        if (!workerId || !weekId || !amount) {
            Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'El monto es obligatorio' });
            return;
        }

        $.ajax({
            url: '/agrosys/transactions/weekly-pay/upsert',
            type: 'POST',
            data: { workerId: workerId, weekId: weekId, amount: amount },
            success: function (response) {
                if (response.success) {
                    Swal.fire({ icon: 'success', title: 'Éxito', text: response.message });
                    $('#modalEditPay').modal('hide');
                    $.fn.loadSummary(weekId);
                } else {
                    Swal.fire({ icon: 'error', title: 'Error', text: response.message });
                }
            },
            error: function () {
                Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo guardar el pago semanal' });
            }
        });
    });
});

$.fn.loadWeeks = function () {
    $.ajax({
        url: '/agrosys/transactions/weekly-pay/weeks',
        type: 'GET',
        success: function (response) {
            if (response.success && response.data) {
                const select = $('#weekSelect');
                select.html('<option value="">Seleccione una semana...</option>');
                response.data.forEach(function (week) {
                    select.append('<option value="' + week.weekId + '">' + escapeHtml(week.name) + ' (' + week.startDate + ' - ' + week.endDate + ')</option>');
                });
            }
        }
    });
};

$.fn.loadSummary = function (weekId) {
    $.ajax({
        url: '/agrosys/transactions/weekly-pay/summary?weekId=' + weekId,
        type: 'GET',
        success: function (response) {
            const tbody = $('#tbodyPayments');
            const tfoot = $('#tfootPayments');
            tbody.html('');

            if (response.success && response.data && response.data.length > 0) {
                let totalGeneral = 0;

                response.data.forEach(function (p, i) {
                    const weeklyPay = parseFloat(p.weeklyPay).toFixed(2);
                    const extras = parseFloat(p.extras).toFixed(2);
                    const total = parseFloat(p.total).toFixed(2);
                    totalGeneral += parseFloat(p.total);

                    tbody.append('<tr>' +
                        '<td>' + (i + 1) + '</td>' +
                        '<td>' + escapeHtml(p.workerName) + '</td>' +
                        '<td class="text-end">$ ' + weeklyPay +
                        ' <button class="btn btn-sm btn-outline-secondary ms-1 btn-edit-pay" data-workerid="' + p.workerId + '" data-workername="' + escapeHtml(p.workerName) + '" data-amount="' + weeklyPay + '"><i class="ri-pencil-fill"></i></button></td>' +
                        '<td class="text-end text-success">$ ' + extras + '</td>' +
                        '<td class="text-end fw-bold">$ ' + total + '</td>' +
                        '<td></td>' +
                        '</tr>');
                });

                tfoot.removeClass('d-none');
                $('#totalGeneral').text('$ ' + totalGeneral.toFixed(2));

                $('.btn-edit-pay').on('click', function () {
                    const btn = $(this);
                    $('#editWorkerId').val(btn.data('workerid'));
                    $('#editWorkerName').val(btn.data('workername'));
                    $('#editWeekId').val(weekId);
                    $('#editAmount').val(btn.data('amount'));
                    $('#modalEditPay').modal('show');
                });

            } else {
                tbody.html('<tr><td colspan="6" class="text-center text-muted">No se encontraron trabajadores para esta semana</td></tr>');
                tfoot.addClass('d-none');
            }
        },
        error: function () {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar el resumen' });
        }
    });
};

$.fn.generatePayments = function () {
    const weekId = currentWeekId;
    const rows = $('#tbodyPayments tr');
    const payments = [];
    let endDate = null;

    // Get endDate from the week dropdown selected option's text
    const selectedText = $('#weekSelect option:selected').text();
    const dateMatch = selectedText.match(/\((\d{4}-\d{2}-\d{2}) - (\d{4}-\d{2}-\d{2})\)/);
    if (dateMatch) {
        endDate = dateMatch[2]; // end_date of the week
    }

    rows.each(function () {
        const tds = $(this).find('td');
        if (tds.length >= 5) {
            const workerName = $(tds[1]).text();
            const weeklyPay = parseFloat($(tds[2]).text().replace('$ ', '').replace(/,/g, '')) || 0;
            const extras = parseFloat($(tds[3]).text().replace('$ ', '').replace(/,/g, '')) || 0;
            const total = parseFloat($(tds[4]).text().replace('$ ', '').replace(/,/g, '')) || 0;
            if (total > 0) {
                payments.push({
                    workerName: workerName.trim(),
                    weeklyPay: weeklyPay,
                    extras: extras,
                    total: total
                });
            }
        }
    });

    if (payments.length === 0) {
        Swal.fire({ icon: 'warning', title: 'Sin datos', text: 'No hay montos para generar' });
        return;
    }

    $.ajax({
        url: '/agrosys/transactions/weekly-pay/create',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            weekId: weekId,
            endDate: endDate,
            payments: payments
        }),
        success: function (response) {
            if (response.success) {
                Swal.fire({ icon: 'success', title: 'Éxito', text: response.message });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function () {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudieron generar los pagos' });
        }
    });
};

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}
