let currentWorkerId = null;
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

// Eliminar Worker
$('#btn-submit-delete-user').on('click', function () {
    const id = currentWorkerId;  
    console.log('ID a eliminar (desde variable global):', id);

    if (!id) {
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo obtener el ID del trabajador' });
        return;
    }

    $.ajax({
        url: '/agrosys/workers/delete',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify({ userId: parseInt(id) }),
        success: function (response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: 'Worker eliminado exitosamente'
                }).then(() => {
                    $('#modalDeleteUser').modal('hide');
                    currentWorkerId = null;   // limpiar variable
                    $.fn.getAllWorkers();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function (xhr) {
            const msg = xhr.responseJSON?.message || 'Error al eliminar el worker';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

//Obtener y mostrar todos los workers 
$.fn.getAllWorkers = function () {
    $.ajax({
        url: '/agrosys/workers/get-all',
        type: 'GET',
        success: function (response) {
            const cardsContainer = $('#cardsContainerUsers');
            cardsContainer.html(''); 

            if (response.success && Array.isArray(response.data)) {
                console.log('Workers obtenidos:', response.data);

                if (response.data.length === 0) {
                    cardsContainer.html('<div class="alert alert-info text-center">No hay trabajadores registrados.</div>');
                    return;
                }

                response.data.forEach(worker => {
                    const workerId = worker.workerId || worker.id;  // asegurar el campo ID
                    const nombre = worker.name || worker.nombre || 'Sin nombre';
                    const notas = worker.notas || worker.apellido || '';

                    const workerCard = $(`
                        <div class="card mb-3 me-2 worker-card" style="width: 300px; cursor: pointer;" data-id="${workerId}">
                            <div class="card-body d-flex flex-row justify-content-between align-items-start">
                                <div class="d-flex flex-column">
                                    <h5 class="card-title text-capitalize">${escapeHtml(nombre.toLowerCase())}</h5>
                                    <p class="card-text text-muted">${escapeHtml(notas)}</p>
                                </div>
                                <div class="dropdown">
                                    <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${workerId}" data-bs-toggle="dropdown" aria-expanded="false"></i>
                                    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${workerId}">
                                        <li><a class="dropdown-item edit-worker" data-id="${workerId}" href="#"><i class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                        <li><a class="dropdown-item delete-worker" data-id="${workerId}" href="#"><i class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    `);

                    workerCard.on('click', function (e) {
                        if ($(e.target).closest('.dropdown').length) return;
                        mostrarDetallesWorker(workerId, nombre, notas);
                    });


                    workerCard.find('.edit-worker').on('click', function (e) {
                        e.stopPropagation();
                        $('#workerIdInputEdit').val(workerId);
                        $('#nombreInputEdit').val(nombre);
                        $('#notasInputEdit').val(notas);
                        $('#modalEditUser').modal('show');
                    });

                    workerCard.find('.delete-worker').on('click', function (e) {
                        e.stopPropagation();
                        currentWorkerId = workerId;   // almacena en variable global
                        $('#textDelete').text(`¿Está seguro de que desea eliminar al trabajador ${nombre}?`);
                        console.log('ID guardado en variable global:', currentWorkerId);
                        $('#modalDeleteUser').modal('show');
                    });

                    cardsContainer.append(workerCard);
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado al obtener los trabajadores'
                });
            }
        },
        error: function (xhr) {
            console.error('Error en getAllWorkers:', xhr);
            let msg = 'Error al cargar los trabajadores';
            if (xhr.responseJSON && xhr.responseJSON.message) msg = xhr.responseJSON.message;
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
};

function mostrarDetallesWorker(id, nombre, notas) {
    Swal.fire({
        title: `Detalles de ${nombre}`,
        html: `<strong>ID:</strong> ${id}<br><strong>Nombre:</strong> ${nombre}<br><strong>Notas:</strong> ${notas}`,
        icon: 'info',
        confirmButtonText: 'Cerrar'
    });
}

// Función auxiliar para evitar XSS 
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

// Carga inicial 
$(document).ready(function () {
    $.fn.getAllWorkers();
});