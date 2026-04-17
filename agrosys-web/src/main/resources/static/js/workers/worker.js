// ── Agregar Worker ──────────────────────────────────────────
$('#btn-submit-add-user').on('click', function () {
    const nombre = $('#nombreInput').val().trim();
    const notas  = $('#apellidoInput').val().trim();

    if (!nombre || !notas) {
        $('#addUserForm')[0].reportValidity();
        return;
    }

    $.ajax({
        url: '/agrosys/workers/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ name: nombre, notas: notas }),
        success: function (response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: 'Worker registrado exitosamente'
                }).then(() => {
                    $('#modalAddUser').modal('hide');
                    $('#addUserForm')[0].reset();
                    $.fn.getAllWorkers();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function (xhr) {
            const msg = xhr.responseJSON?.message || 'Error al registrar el worker';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

// ── Carga inicial ───────────────────────────────────────────
$.fn.getAllWorkers();