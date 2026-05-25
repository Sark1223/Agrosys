let selectedWeekId = null;
let summaryData = [];

$(document).ready(function () {
    $.fn.loadWeeks();

    $('#btnLoadSummary').on('click', function () {
        selectedWeekId = $('#weekSelect').val();
        if (!selectedWeekId) {
            Swal.fire({ icon: 'warning', title: 'Semana requerida', text: 'Selecciona una semana' });
            return;
        }
        $.fn.loadSummary(selectedWeekId);
    });

    $('#btnGeneratePayments').on('click', function () {
        if (!selectedWeekId) {
            Swal.fire({ icon: 'warning', title: 'Semana requerida', text: 'Selecciona una semana primero' });
            return;
        }

        const payments = [];
        $('#tbodySummary tr').each(function () {
            const workerId = $(this).data('worker-id');
            const totalAmount = $(this).data('total');
            if (workerId && totalAmount && parseFloat(totalAmount) > 0) {
                payments.push({ workerId: workerId, amount: totalAmount });
            }
        });

        if (payments.length === 0) {
            Swal.fire({ icon: 'warning', title: 'Sin pagos', text: 'No hay pagos válidos para generar' });
            return;
        }

        Swal.fire({
            title: 'Generar Pagos',
            text: 'Se generarán ' + payments.length + ' pago(s) como transacciones de tipo Egreso. ¿Continuar?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sí, generar',
            cancelButtonText: 'Cancelar'
        }).then(function (result) {
            if (result.isConfirmed) {
                $.ajax({
                    url: '/agrosys/transactions/weekly-pay/create',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ weekId: parseInt(selectedWeekId), payments: payments }),
                    success: function (response) {
                        if (response.success) {
                            Swal.fire({ icon: 'success', title: 'Éxito', text: response.message });
                        } else {
                            Swal.fire({ icon: 'error', title: 'Error', text: response.message });
                        }
                    },
                    error: function (xhr) {
                        const msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al generar pagos';
                        Swal.fire({ icon: 'error', title: 'Error', text: msg });
                    }
                });
            }
        });
    });

    $(document).on('click', '.btn-edit-pay', function () {
        const workerId = $(this).data('worker-id');
        const workerName = $(this).data('worker-name');
        const weekId = $(this).data('week-id');
        const amount = $(this).data('amount');

        $('#editWorkerId').val(workerId);
        $('#editWorkerName').val(workerName);
        $('#editWeekId').val(weekId || selectedWeekId);
        $('#editAmount').val(amount || '');
        $('#modalEditPay').modal('show');
    });

    $('#btnSavePay').on('click', function () {
        const workerId = $('#editWorkerId').val();
        const weekId = $('#editWeekId').val();
        const amount = $('#editAmount').val();

        if (!amount || parseFloat(amount) < 0) {
            Swal.fire({ icon: 'warning', title: 'Monto inválido', text: 'Ingresa un monto válido' });
            return;
        }

        $.ajax({
            url: '/agrosys/transactions/weekly-pay/upsert',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ workerId: parseInt(workerId), weekId: parseInt(weekId), amount: parseFloat(amount) }),
            success: function (response) {
                if (response.success) {
                    Swal.fire({ icon: 'success', title: 'Éxito', text: response.message });
                    $('#modalEditPay').modal('hide');
                    $.fn.loadSummary(selectedWeekId);
                } else {
                    Swal.fire({ icon: 'error', title: 'Error', text: response.message });
                }
            },
            error: function (xhr) {
                const msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al guardar';
                Swal.fire({ icon: 'error', title: 'Error', text: msg });
            }
        });
    });

    $('#modalEditPay').on('hidden.bs.modal', function () {
        $('#editPayForm')[0].reset();
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
                    select.append('<option value="' + week.weekId + '">' +
                        escapeHtml(week.name) + ' (' + week.startDate + ' - ' + week.endDate + ')</option>');
                });
            }
        },
        error: function () {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudieron cargar las semanas' });
        }
    });
};

$.fn.loadSummary = function (weekId) {
    $.ajax({
        url: '/agrosys/transactions/weekly-pay/summary?weekId=' + weekId,
        type: 'GET',
        success: function (response) {
            const tbody = $('#tbodySummary');
            tbody.html('');

            if (response.success && response.data && response.data.length > 0) {
                summaryData = response.data;
                response.data.forEach(function (item) {
                    const defaultAmount = item.amount ? parseFloat(item.amount) : (parseFloat(item.salary) / 52);
                    const bonus = item.bonus ? parseFloat(item.bonus) : 0;
                    const total = defaultAmount + bonus;

                    const row = $('<tr data-worker-id="' + item.workerId +
                        '" data-amount="' + defaultAmount.toFixed(2) +
                        '" data-total="' + total.toFixed(2) + '">');
                    row.append('<td>' + escapeHtml(item.workerName) + '</td>');
                    row.append('<td>$ ' + parseFloat(item.salary).toFixed(2) + '</td>');
                    row.append('<td class="fw-bold">$ ' + defaultAmount.toFixed(2) + '</td>');
                    row.append('<td class="text-success fw-bold">$ ' + bonus.toFixed(2) + '</td>');
                    row.append('<td class="fw-bold">$ ' + total.toFixed(2) + '</td>');
                    row.append('<td>' +
                        '<button class="btn btn-sm btn-outline-info btn-edit-pay" ' +
                        'data-worker-id="' + item.workerId + '" ' +
                        'data-worker-name="' + escapeHtml(item.workerName) + '" ' +
                        'data-week-id="' + weekId + '" ' +
                        'data-amount="' + defaultAmount.toFixed(2) + '">' +
                        '<i class="ri-pencil-fill"></i></button>' +
                        '</td>');
                    tbody.append(row);
                });
            } else {
                tbody.html('<tr><td colspan="6" class="text-center text-muted">No hay trabajadores registrados</td></tr>');
            }
        },
        error: function () {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar el resumen' });
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
