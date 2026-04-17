// ── Agregar Worker ──────────────────────────────────────────
$('#btn-submit-add-user').on('click', function () {
    const nombre = $('#nombreInput').val().trim();
    const notas = $('#apellidoInput').val().trim();

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

$.fn.getAllWorkers = function () {
    $.ajax({
        url: '/agrosys/workers/get-all',          // ← ruta del endpoint 
        type: 'GET',
        contentType: 'application/json',
        success: function (response) {
            if (response.success && Array.isArray(response.data)) {
                renderWorkers(response.data);
            } else {
                console.error('Respuesta inválida:', response);
                Swal.fire('Error', 'No se pudieron cargar los trabajadores', 'error');
            }
        },
        error: function (xhr) {
            console.error('Error en getAllWorkers:', xhr);
            let msg = 'Error al cargar trabajadores';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                msg = xhr.responseJSON.message;
            }
            Swal.fire('Error', msg, 'error');
        }
    });
};

function renderWorkers(workers) {
    const container = $('#cardsContainerUsers');
    if (!container.length) {
        console.warn('No se encontró el contenedor #cardsContainerUsers');
        return;
    }

    if (!workers || workers.length === 0) {
        container.html('<div class="alert alert-info text-center">No hay trabajadores registrados.</div>');
        return;
    }

    let html = '';
    workers.forEach(worker => {
        
        const nombre = worker.name || worker.nombre || 'Sin nombre';
        const notas = worker.notas || worker.apellido || '';
        const id = worker.id;

        html += `
            <div class="card m-2 shadow-sm" style="width: 18rem;">
                <div class="card-body">
                    <h5 class="card-title">${escapeHtml(nombre)}</h5>
                    <p class="card-text text-muted">${escapeHtml(notas)}</p>
                    <button class="btn btn-sm btn-warning edit-worker" data-id="${id}">Editar</button>
                    <button class="btn btn-sm btn-danger delete-worker" data-id="${id}">Eliminar</button>
                </div>
            </div>
        `;
    });
    container.html(html);

    // Asignar eventos a los botones
    $('.edit-worker').on('click', function () {
        const workerId = $(this).data('id');
        
        console.log('Editar worker con id:', workerId);
        
    });

    $('.delete-worker').on('click', function () {
        const workerId = $(this).data('id');
        console.log('Eliminar worker con id:', workerId);
        
    });
}


function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function (m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

// Carga inicial al estar listo el DOM

$(document).ready(function () {
    $.fn.getAllWorkers();
});