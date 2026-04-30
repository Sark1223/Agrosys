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
                    text: response.message || 'Trabajador actualizado exitosamente'
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
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al actualizar el trabajador';
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
                    text: response.message || 'Trabajador eliminado exitosamente'
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
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al eliminar el trabajador';
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
                $.fn.fillWorkerFilter(response.data);

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

    // Inicializar la pestaña de asistencia cuando se haga clic en ella para no cargarla de golpe
    $('a[data-bs-toggle="tab"][href="#asistencia"]').on('shown.bs.tab', function (e) {
        // Poner la fecha de hoy por defecto (Criterio de tu HU)
        var hoy = new Date().toISOString().split('T')[0];
        $('#attendanceDate').val(hoy);
        
        // Traer la lista de trabajadores
        $.fn.getWorkersForAttendance();
    });

    // Inicializar el historial al cambiar de pestaña
    $('a[data-bs-toggle="tab"][href="#historial"]').on('shown.bs.tab', function (e) {
        const hoy = new Date().toISOString().split('T')[0];
    
    // Solo ponemos fechas por defecto si los campos están vacíos
    if (!$('#filterStartDate').val()) $('#filterStartDate').val(hoy);
    if (!$('#filterEndDate').val()) $('#filterEndDate').val(hoy);
        $.fn.getAttendanceHistory();
    });
});


// =========================================================================
// ================= LOGICA DE ASISTENCIA (ATTENDANCE) =====================
// =========================================================================

// Función para obtener y dibujar los trabajadores en la pestaña de Asistencia
$.fn.getWorkersForAttendance = function() {
    $.ajax({
        url: '/agrosys/workers/get-all', 
        type: 'GET',
        success: function(response) {
            var container = $('#cardsContainerAsistencia');
            container.html(''); 

            if (response.success && Array.isArray(response.data)) {
                if (response.data.length === 0) {
                    container.html('<div class="w-100 text-center p-4 text-muted fst-italic">No hay trabajadores activos.</div>');
                    return;
                }

                // Generamos una fila por cada trabajador
                response.data.forEach(function(worker) {
                    var workerId = worker.workerId || worker.id;
                    var nombre = worker.name || worker.nombre || 'Sin nombre';
                    var photo = worker.photo || null;

                    var photoHtml = photo 
                        ? '<img src="' + escapeHtml(photo) + '" class="rounded-circle me-3" style="width: 40px; height: 40px; object-fit: cover;">' 
                        : '<div class="rounded-circle bg-secondary d-flex align-items-center justify-content-center me-3" style="width: 40px; height: 40px;"><i class="ri-user-3-fill text-white"></i></div>';

                    // HTML Limpio del renglón
                    var rowHtml = $(`
                        <div class="d-flex align-items-center justify-content-between border-bottom py-2 attendance-row" data-worker-id="${workerId}">
                            
                            <div class="d-flex align-items-center gap-3">
                                ${photoHtml}
                                <span class="fw-bold text-capitalize" style="font-size: 1.1rem;">${escapeHtml(nombre.toLowerCase())}</span>
                            </div>

                            <div class="d-flex align-items-center gap-4">
                                
                                <div class="form-check mb-0 d-flex align-items-center">
                                    <input class="form-check-input attendance-checkbox me-2" type="checkbox" checked id="chk_${workerId}" style="width: 1.3rem; height: 1.3rem; cursor:pointer;">
                                    <label class="form-check-label text-success attendance-label mt-1" for="chk_${workerId}" style="width: 50px;">Asistió</label>
                                </div>

                                <div class="d-flex align-items-center gap-2">
                                    <label class="mb-0 small text-muted">Hrs:</label>
                                    <select class="form-select form-select-sm hours-select" style="width: 70px; cursor:pointer;">
                                        <option value="8" selected>8</option>
                                        <option value="7">7</option>
                                        <option value="6">6</option>
                                        <option value="5">5</option>
                                        <option value="4">4</option>
                                        <option value="3">3</option>
                                        <option value="2">2</option>
                                        <option value="1">1</option>
                                        <option value="0">0</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    `);

                    // ÚNICA Lógica del Checkbox clásico
                    rowHtml.find('.attendance-checkbox').on('change', function() {
                        var isChecked = $(this).is(':checked');
                        var row = $(this).closest('.attendance-row');
                        var label = row.find('.attendance-label');
                        var select = row.find('.hours-select');

                        if (isChecked) {
                            label.text('Asistió').removeClass('text-danger').addClass('text-success');
                            select.prop('disabled', false).val('8');
                        } else {
                            label.text('Faltó').removeClass('text-success').addClass('text-danger');
                            select.prop('disabled', true).val('0');
                        }
                    });

                    container.append(rowHtml);
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudieron cargar los trabajadores para asistencia' });
            }
        },
        error: function() {
            Swal.fire({ icon: 'error', title: 'Error', text: 'Error de red al cargar trabajadores' });
        }
    });
};

// Configurar el botón de guardar Asistencia
$('#btnSaveAttendance').on('click', function(e) {
    e.preventDefault();
    
    var dateVal = $('#attendanceDate').val();
    if (!dateVal) {
        Swal.fire({ icon: 'warning', title: 'Atención', text: 'Debes seleccionar una fecha' });
        return;
    }

    var registros = [];
    var $rows = $('#cardsContainerAsistencia .attendance-row');

    if ($rows.length === 0) {
        Swal.fire({ icon: 'warning', title: 'Atención', text: 'No hay trabajadores para registrar' });
        return;
    }

    // Recorremos cada fila para armar el paquete de datos
    $rows.each(function() {
        var workerId = $(this).data('worker-id');
        // AQUI ESTABA EL ERROR DEL SWITCH: Lo cambiamos a buscar el checkbox
        var asistio = $(this).find('.attendance-checkbox').is(':checked');
        var horas = parseInt($(this).find('.hours-select').val());

        registros.push({
            workerId: workerId,
            date: dateVal,
            attended: asistio,
            hoursWorked: horas
        });
    });

    // Enviamos al backend (A LA URL CORRECTA)
    $.ajax({
        url: '/agrosys/workers/attendance/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(registros),
        success: function(response) {
            if (response.success) {
                Swal.fire({ icon: 'success', title: '¡Guardado!', text: 'La asistencia se registró correctamente' });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message || 'No se pudo guardar la asistencia' });
            }
        },
        error: function(xhr) {
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Ocurrió un error al guardar';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

// Función para cargar el historial
$.fn.getAttendanceHistory = function() {
    const workerId = $('#filterWorker').val();
    const startDate = $('#filterStartDate').val();
    const endDate = $('#filterEndDate').val();

    if (!startDate || !endDate) {
        Swal.fire({
            icon: 'warning',
            title: 'Fechas requeridas',
            text: 'Por favor selecciona un rango de fechas para consultar.'
        });
        return;
    }

    $.ajax({
        url: '/agrosys/workers/attendance/history', // Ruta de nuestro nuevo puente
        type: 'GET',
        data: {
            workerId: workerId,
            startDate: startDate,
            endDate: endDate
        },
        success: function(response) {
            const tbody = $('#tbodyHistorial');
            tbody.html(''); // Limpiar tabla

            if (response.success && response.data.length > 0) {
                const diasSemana = ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];
                response.data.forEach(reg => {
                    const fechaObj = new Date(reg.date + 'T00:00:00');
                    const nombreDia = diasSemana[fechaObj.getDay()];
                    const statusClass = reg.attended ? 'badge bg-success' : 'badge bg-danger';
                    const statusText = reg.attended ? 'Asistió' : 'Faltó';
                    
                    tbody.append(`
                        <tr>
                            <td>
                                <div class="fw-bold">${reg.date}</div>
                                <small class="text-muted">${nombreDia}</small>
                            </td>
                            <td class="text-capitalize align-content-center">${reg.workerName.toLowerCase()}</td>
                            <td class="align-content-center"><span class="${statusClass}">${statusText}</span></td>
                            <td class="align-content-center">${reg.hoursWorked} hrs</td>
                        </tr>
                    `);
                });
            } else {
                tbody.html('<tr><td colspan="4" class="text-center text-muted">No se encontraron registros.</td></tr>');
            }
        },
        error: function() {
            Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo cargar el historial' });
        }
    });
};

// Evento para el botón de filtrar
$('#btnFiltrarHistorial').on('click', function() {
    $.fn.getAttendanceHistory();
});

// Función para llenar el select de trabajadores en el historial
$.fn.fillWorkerFilter = function(workers) {
    const select = $('#filterWorker');
    if (select.length) { // Solo si el elemento existe en el HTML
        select.html('<option value="">Todos los trabajadores</option>');
        workers.forEach(w => {
            const id = w.workerId || w.id;
            const name = w.name || w.nombre;
            select.append(`<option value="${id}">${name}</option>`);
        });
    }
};