let currentWorkerId = null;
let workersData = [];

function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}

$('#btn-submit-add-user').on('click', async function () {
    const nombre = $('#nombreInput').val().trim();
    const notas = $('#apellidoInput').val().trim();
    const salary = $('#salaryInput').val().trim();
    const photoFile = $('#photoInput')[0].files[0];

    if (!nombre || !salary) {
        $('#addUserForm')[0].reportValidity();
        return;
    }

    let photo = null;
    if (photoFile) {
        try {
            photo = await fileToBase64(photoFile);
        } catch (e) {
            Swal.fire({ icon: 'error', title: 'Error', text: 'Error al procesar la imagen' });
            return;
        }
    }

    const data = {
        name: nombre,
        notas: notas || null,
        salary: parseFloat(salary),
        photo: photo
    };

    $.ajax({
        url: '/agrosys/workers/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
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

$('#btn-submit-edit-user').on('click', async function () {
    const workerId = $('#workerIdInputEdit').val();
    const nombre = $('#nombreInputEdit').val().trim();
    const notas = $('#notasInputEdit').val().trim();
    const salary = $('#salaryInputEdit').val().trim();
    const photoFile = $('#photoInputEdit')[0].files[0];

    if (!workerId || !nombre || !salary) {
        $('#editUserForm')[0].reportValidity();
        return;
    }

    let photo = null;
    if (photoFile) {
        try {
            photo = await fileToBase64(photoFile);
        } catch (e) {
            Swal.fire({ icon: 'error', title: 'Error', text: 'Error al procesar la imagen' });
            return;
        }
    } else {
        const currentWorker = workersData.find(w => w.workerId === parseInt(workerId));
        photo = currentWorker ? currentWorker.photo : null;
    }

    const data = {
        workerId: parseInt(workerId),
        name: nombre,
        notas: notas || null,
        salary: parseFloat(salary),
        photo: photo
    };

    $.ajax({
        url: '/agrosys/workers/update',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: 'Worker actualizado exitosamente'
                }).then(() => {
                    $('#modalEditUser').modal('hide');
                    $('#editUserForm')[0].reset();
                    $.fn.getAllWorkers();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function (xhr) {
            const msg = xhr.responseJSON?.message || 'Error al actualizar el worker';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

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
                    currentWorkerId = null;   
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

$.fn.getAllWorkers = function () {
    $.ajax({
        url: '/agrosys/workers/get-all',
        type: 'GET',
        success: function (response) {
            const cardsContainer = $('#cardsContainerUsers');
            cardsContainer.html(''); 

            if (response.success && Array.isArray(response.data)) {
                console.log('Workers obtenidos:', response.data);
                workersData = response.data;

                if (response.data.length === 0) {
                    cardsContainer.html('<div class="alert alert-info text-center">No hay trabajadores registrados.</div>');
                    return;
                }

                response.data.forEach(worker => {
                    const workerId = worker.workerId || worker.id;
                    const nombre = worker.name || worker.nombre || 'Sin nombre';
                    const notas = worker.notas || '';
                    const salary = worker.salary || 0;
                    const photo = worker.photo || null;

                    const photoHtml = photo 
                        ? `<img src="${escapeHtml(photo)}" alt="Foto de ${escapeHtml(nombre)}" class="rounded-circle" style="width: 50px; height: 50px; object-fit: cover;">`
                        : `<div class="rounded-circle bg-secondary d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;"><i class="ri-user-3-fill text-white"></i></div>`;

                    const workerCard = $(`
                        <div class="card mb-3 me-2 worker-card" style="width: 300px; cursor: pointer;" data-id="${workerId}">
                            <div class="card-body d-flex flex-row justify-content-between align-items-start">
                                <div class="d-flex align-items-center gap-3">
                                    ${photoHtml}
                                    <div class="d-flex flex-column">
                                        <h5 class="card-title text-capitalize mb-0">${escapeHtml(nombre.toLowerCase())}</h5>
                                        <small class="text-success fw-bold">$${parseFloat(salary).toFixed(2)}</small>
                                    </div>
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
                        mostrarDetallesWorker(workerId, nombre, notas, salary, photo);
                    });


                    workerCard.find('.edit-worker').on('click', function (e) {
                        e.stopPropagation();
                        const w = workersData.find(w => w.workerId === workerId);
                        $('#workerIdInputEdit').val(workerId);
                        $('#nombreInputEdit').val(w.name);
                        $('#notasInputEdit').val(w.notas || '');
                        $('#salaryInputEdit').val(w.salary);
                        
                        if (w.photo) {
                            $('#currentPhotoPreview').html(`<img src="${escapeHtml(w.photo)}" alt="Foto actual" class="rounded-circle" style="width: 80px; height: 80px; object-fit: cover;">`);
                        } else {
                            $('#currentPhotoPreview').html('');
                        }
                        
                        $('#modalEditUser').modal('show');
                    });

                    workerCard.find('.delete-worker').on('click', function (e) {
                        e.stopPropagation();
                        currentWorkerId = workerId;
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

function mostrarDetallesWorker(id, nombre, notas, salary, photo) {
    const photoHtml = photo 
        ? `<img src="${escapeHtml(photo)}" alt="Foto" class="rounded-circle mb-3" style="width: 100px; height: 100px; object-fit: cover;">`
        : `<div class="rounded-circle bg-secondary d-flex align-items-center justify-content-center mb-3" style="width: 100px; height: 100px;"><i class="ri-user-3-fill text-white" style="font-size: 40px;"></i></div>`;
    
    Swal.fire({
        title: `Detalles de ${nombre}`,
        html: `
            ${photoHtml}<br>
            <strong>ID:</strong> ${id}<br>
            <strong>Nombre:</strong> ${escapeHtml(nombre)}<br>
            <strong>Notas:</strong> ${escapeHtml(notas) || 'Sin notas'}<br>
            <strong>Salario:</strong> $${parseFloat(salary).toFixed(2)}
        `,
        icon: 'info',
        confirmButtonText: 'Cerrar'
    });
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

$(document).ready(function () {
    $.fn.getAllWorkers();
});