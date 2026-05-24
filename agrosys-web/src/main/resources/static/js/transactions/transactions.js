let editingId = null;
let deleteId = null;

$(document).ready(function () {
    const today = new Date().toISOString().split('T')[0];
    const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0];
    $('#filterStartDate').val(firstDay);
    $('#filterEndDate').val(today);

    $('#btnFilter').on('click', function () {
        $.fn.getTransactions();
    });

    $('#btnSaveTransaction').on('click', function () {
        const type = $('#formType').val();
        const date = $('#formDate').val();
        const amount = $('#formAmount').val();
        const description = $('#formDescription').val();
        const plotId = $('#formPlot').val();

        if (!type || !date || !amount) {
            Swal.fire({ icon: 'warning', title: 'Campos requeridos', text: 'Tipo, fecha y monto son obligatorios' });
            return;
        }

        const id = $('#transactionIdInput').val();
        const url = id ? '/agrosys/transactions/update/' + id : '/agrosys/transactions/register';
        const data = {
            transactionType: type,
            createAt: date,
            amount: amount
        };
        if (description) data.description = description;
        if (plotId) data.plotId = plotId;

        $.ajax({
            url: url,
            type: id ? 'PUT' : 'POST',
            data: data,
            success: function (response) {
                if (response.success) {
                    Swal.fire({ icon: 'success', title: 'Éxito', text: response.message });
                    $('#modalAddTransaction').modal('hide');
                    $('#transactionForm')[0].reset();
                    $('#transactionIdInput').val('');
                    editingId = null;
                    $.fn.getTransactions();
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

    $('#btnConfirmDelete').on('click', function () {
        if (!deleteId) return;
        $.ajax({
            url: '/agrosys/transactions/delete/' + deleteId,
            type: 'DELETE',
            success: function (response) {
                if (response.success) {
                    Swal.fire({ icon: 'success', title: 'Éxito', text: response.message });
                    $('#modalDeleteTransaction').modal('hide');
                    deleteId = null;
                    $.fn.getTransactions();
                } else {
                    Swal.fire({ icon: 'error', title: 'Error', text: response.message });
                }
            },
            error: function (xhr) {
                const msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al eliminar';
                Swal.fire({ icon: 'error', title: 'Error', text: msg });
            }
        });
    });

    $('#modalAddTransaction').on('hidden.bs.modal', function () {
        $('#transactionForm')[0].reset();
        $('#transactionIdInput').val('');
        editingId = null;
        $('#modalTitle').text('Nueva Transacción');
    });

    $('#modalAddTransaction').on('show.bs.modal', function () {
        if (!editingId && !$('#formDate').val()) {
            $('#formDate').val(new Date().toISOString().split('T')[0]);
        }
    });

    $.fn.loadPlots();
    $.fn.getTransactions();
});

$.fn.loadPlots = function () {
    $.ajax({
        url: '/agrosys/plots/get-all',
        type: 'GET',
        success: function (response) {
            if (response.success && response.data) {
                const filterSelect = $('#filterPlot');
                const formSelect = $('#formPlot');
                const options = '<option value="">Todas</option>';
                const formOptions = '<option value="">Sin parcela</option>';
                filterSelect.html(options);
                formSelect.html(formOptions);
                response.data.forEach(function (plot) {
                    filterSelect.append('<option value="' + plot.plotId + '">' + escapeHtml(plot.name) + '</option>');
                    formSelect.append('<option value="' + plot.plotId + '">' + escapeHtml(plot.name) + '</option>');
                });
            }
        }
    });
};

$.fn.getTransactions = function () {
    const startDate = $('#filterStartDate').val();
    const endDate = $('#filterEndDate').val();
    const type = $('#filterType').val();
    const plotId = $('#filterPlot').val();

    if (!startDate || !endDate) {
        Swal.fire({ icon: 'warning', title: 'Fechas requeridas', text: 'Selecciona un rango de fechas' });
        return;
    }
    if (startDate > endDate) {
        Swal.fire({ icon: 'warning', title: 'Fechas inválidas', text: 'La fecha inicio no puede ser mayor a la fecha fin' });
        return;
    }

    let url = '/agrosys/transactions/history?startDate=' + startDate + '&endDate=' + endDate;
    if (type) url += '&type=' + type;
    if (plotId) url += '&plotId=' + plotId;

    $.ajax({
        url: url,
        type: 'GET',
        success: function (response) {
            const tbody = $('#tbodyTransactions');
            tbody.html('');

            if (response.success && response.data && response.data.length > 0) {
                const typeLabels = { INCOME: 'Ingreso', EXPENSE: 'Egreso', ADJUSTMENT: 'Ajuste' };
                const typeColors = { INCOME: 'text-success', EXPENSE: 'text-danger', ADJUSTMENT: 'text-warning' };

                response.data.forEach(function (tx) {
                    const amount = parseFloat(tx.amount).toFixed(2);
                    const amountClass = parseFloat(tx.amount) < 0 ? 'text-danger' : 'text-success';

                    tbody.append('<tr>' +
                        '<td>' + tx.transactionId + '</td>' +
                        '<td>' + tx.createAt + '</td>' +
                        '<td><span class="badge ' + (typeColors[tx.transactionType] || '') + '">' + (typeLabels[tx.transactionType] || tx.transactionType) + '</span></td>' +
                        '<td>' + escapeHtml(tx.description) + '</td>' +
                        '<td>' + (tx.plotName ? escapeHtml(tx.plotName) : '<span class="text-muted">-</span>') + '</td>' +
                        '<td class="fw-bold ' + amountClass + '">$ ' + amount + '</td>' +
                        '<td>' +
                            '<button class="btn btn-sm btn-outline-info me-1 btn-edit" data-id="' + tx.transactionId + '"><i class="ri-pencil-fill"></i></button>' +
                            '<button class="btn btn-sm btn-outline-danger btn-delete" data-id="' + tx.transactionId + '"><i class="ri-delete-bin-fill"></i></button>' +
                        '</td>' +
                        '</tr>');
                });

                $('.btn-edit').on('click', function () {
                    const id = $(this).data('id');
                    $.ajax({
                        url: '/agrosys/transactions/' + id,
                        type: 'GET',
                        success: function (r) {
                            if (r.success && r.data) {
                                const tx = r.data;
                                editingId = tx.transactionId;
                                $('#transactionIdInput').val(tx.transactionId);
                                $('#formType').val(tx.transactionType);
                                $('#formDate').val(tx.createAt);
                                $('#formAmount').val(tx.amount);
                                $('#formPlot').val(tx.plotId || '');
                                $('#formDescription').val(tx.description);
                                $('#modalTitle').text('Editar Transacción');
                                $('#modalAddTransaction').modal('show');
                            }
                        }
                    });
                });

                $('.btn-delete').on('click', function () {
                    deleteId = $(this).data('id');
                    $('#deleteText').text('¿Está seguro de eliminar la transacción #' + deleteId + '?');
                    $('#modalDeleteTransaction').modal('show');
                });

            } else {
                tbody.html('<tr><td colspan="7" class="text-center text-muted">No se encontraron transacciones en el rango seleccionado</td></tr>');
            }
        },
        error: function () {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar el historial' });
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
