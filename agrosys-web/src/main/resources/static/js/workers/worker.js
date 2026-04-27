let currentWorkerId = null;
let workersData = [];

$('#btn-submit-edit-worker').on('click', function () {
    const $f = $('#editWorkerForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/workers?tab=' + activeTab);
});

function getActiveTab() {
    const activeTabLink = document.querySelector('.nav-link.active');
    if (activeTabLink) {
        const href = activeTabLink.getAttribute('href');
        return href.replace('#', '');
    }
    return 'workers'; // Valor por defecto si no se encuentra el enlace activo
}


function fileToBase64(file) {
    return new Promise(function(resolve, reject) {
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function() {
            resolve(reader.result);
        };
        reader.onerror = function(error) {
            reject(error);
        };
    });
}

$('#btn-submit-add-worker').on('click', function () {
    const $f = $('#addWorkerForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/workers?tab=' + activeTab);
});

function submitEditWorker(workerId, nombre, notas, salary, photo) {
    var data = {
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
        success: function(response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Exito',
                    text: 'Worker actualizado exitosamente'
                }).then(function() {
                    $('#modalEditWorker').modal('hide');
                    $('#editWorkerForm')[0].reset();
                    $.fn.getAllWorkers();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function(xhr) {
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al actualizar el worker';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
}

$('#btn-submit-delete-worker').on('click', function() {
    var id = currentWorkerId;

    if (!id) {
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo obtener el ID del trabajador' });
        return;
    }

    $.ajax({
        url: '/agrosys/workers/delete',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify({ workerId: parseInt(id) }),
        success: function(response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Exito',
                    text: 'Worker eliminado exitosamente'
                }).then(function() {
                    $('#modalDeleteWorker').modal('hide');
                    currentWorkerId = null;
                    $.fn.getAllWorkers();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function(xhr) {
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al eliminar el worker';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

$.fn.getAllWorkers = function() {
    $.ajax({
        url: '/agrosys/workers/get-all',
        type: 'GET',
        success: function(response) {
            var cardsContainer = $('#cardsContainerWorkers');
            cardsContainer.html('');

            if (response.success && Array.isArray(response.data)) {
                workersData = response.data;

                if (response.data.length === 0) {
                    cardsContainer.html('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-4 fst-italic w-100" style="height: 100px;">No hay trabajadores registrados.</div>');
                    return;
                }

                response.data.forEach(function(worker) {
                    var workerId = worker.workerId || worker.id;
                    var nombre = worker.name || worker.nombre || 'Sin nombre';
                    var notas = worker.notas || '';
                    var salary = worker.salary || 0;
                    var photo = worker.photo || null;

                    var photoHtml;
                    if (photo) {
                        photoHtml = '<img src="' + escapeHtml(photo) + '" alt="Foto de ' + escapeHtml(nombre) + '" class="rounded-circle" style="width: 50px; height: 50px; object-fit: cover;">';
                    } else {
                        photoHtml = '<div class="rounded-circle bg-secondary d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;"><i class="ri-user-3-fill text-white"></i></div>';
                    }

                    var workerCard = $(
                        '<div class="card mb-3 me-2 worker-card" style="width: 300px; cursor: pointer;" data-id="' + workerId + '">' +
                            '<div class="card-body d-flex flex-row justify-content-between align-items-start">' +
                                '<div class="d-flex align-items-center gap-3">' +
                                    photoHtml +
                                    '<div class="d-flex flex-column">' +
                                        '<h5 class="card-title text-capitalize mb-0">' + escapeHtml(nombre.toLowerCase()) + '</h5>' +
                                        '<small class="text-success fw-bold">$' + parseFloat(salary).toFixed(2) + '</small>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="dropdown">' +
                                    '<i class="ri-more-2-fill" type="button" id="dropdownMenuButton' + workerId + '" data-bs-toggle="dropdown" aria-expanded="false"></i>' +
                                    '<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton' + workerId + '">' +
                                        '<li><a class="dropdown-item view-worker" data-id="' + workerId + '" href="#"><i class="ri-eye-fill pe-1"></i>Ver Detalle</a></li>' +
                                        '<li><a class="dropdown-item edit-worker" data-id="' + workerId + '" href="#"><i class="ri-pencil-fill pe-1"></i>Editar</a></li>' +
                                        '<li><a class="dropdown-item delete-worker" data-id="' + workerId + '" href="#"><i class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>' +
                                    '</ul>' +
                                '</div>' +
                            '</div>' +
                        '</div>'
                    );

                    workerCard.find('.view-worker').on('click', function(e) {
                        e.stopPropagation();
                        var w = null;
                        for (var i = 0; i < workersData.length; i++) {
                            if (workersData[i].workerId === workerId) {
                                w = workersData[i];
                                break;
                            }
                        }
                        var wPhotoHtml;
                        if (w.photo) {
                            wPhotoHtml = '<img src="' + escapeHtml(w.photo) + '" alt="Foto de ' + escapeHtml(w.name) + '" class="rounded-circle" style="width: 120px; height: 120px; object-fit: cover;">';
                        } else {
                            wPhotoHtml = '<div class="rounded-circle bg-secondary d-flex align-items-center justify-content-center" style="width: 120px; height: 120px; margin: auto;"><i class="ri-user-3-fill text-white" style="font-size: 50px;"></i></div>';
                        }

                        $('#viewPhotoContainer').html(wPhotoHtml);
                        $('#viewWorkerName').text(w.name);
                        $('#viewWorkerId').text(w.workerId);
                        $('#viewWorkerNotas').text(w.notas || 'Sin notas');
                        $('#viewWorkerSalary').text(parseFloat(w.salary || 0).toFixed(2));

                        $('#modalViewWorker').modal('show');
                    });

                    workerCard.find('.edit-worker').on('click', function(e) {
                        e.stopPropagation();
                        var w = null;
                        for (var i = 0; i < workersData.length; i++) {
                            if (workersData[i].workerId === workerId) {
                                w = workersData[i];
                                break;
                            }
                        }
                        $('#workerIdInputEdit').val(workerId);
                        $('#nombreInputEdit').val(w.name);
                        $('#notasInputEdit').val(w.notas || '');
                        $('#salaryInputEdit').val(w.salary);

                        if (w.photo) {
                            $('#currentPhotoPreview').html('<img src="' + escapeHtml(w.photo) + '" alt="Foto actual" class="rounded-circle" style="width: 80px; height: 80px; object-fit: cover;">');
                        } else {
                            $('#currentPhotoPreview').html('');
                        }

                        $('#modalEditWorker').modal('show');
                    });

                    workerCard.find('.delete-worker').on('click', function(e) {
                        e.stopPropagation();
                        currentWorkerId = workerId;
                        $('#textDelete').text('Esta seguro de que desea eliminar al trabajador ' + nombre + '?');
                        $('#modalDeleteWorker').modal('show');
                    });

                    cardsContainer.append(workerCard);
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrio un error inesperado al obtener los trabajadores'
                });
            }
        },
        error: function(xhr) {
            var msg = 'Error al cargar los trabajadores';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                msg = xhr.responseJSON.message;
            }
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
};

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

$(document).ready(function() {
    $.fn.getAllWorkers();
});