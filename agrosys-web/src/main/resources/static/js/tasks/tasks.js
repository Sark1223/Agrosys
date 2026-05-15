let currentTaskId = null;
let currentSpecialTaskId = null;
let tasksData = [];
let specialTasksData = [];
let stages = [];
let plotsData = [];
let plantationsData = {};
let allPlantationsData = [];
let plantationInfoMap = {};
let workersData = [];

function buildPlantationInfoMap(callback) {
    $.ajax({
        url: '/agrosys/tasks/plots/get-all',
        type: 'GET',
        success: function(plotResponse) {
            if (plotResponse.success && Array.isArray(plotResponse.data)) {
                plotsData = plotResponse.data;
                let completedRequests = 0;
                const totalRequests = plotResponse.data.length;

                if (totalRequests === 0) {
                    callback();
                    return;
                }

                plotResponse.data.forEach(function(plot) {
                    $.ajax({
                        url: '/agrosys/tasks/plantation/get-all-by-plot-id/' + plot.plotId,
                        type: 'GET',
                        success: function(plantResponse) {
                            if (plantResponse.success && Array.isArray(plantResponse.data)) {
                                plantationsData[plot.plotId] = plantResponse.data;
                                plantResponse.data.forEach(function(plantation) {
                                    allPlantationsData.push(plantation);
                                    plantationInfoMap[plantation.plantatioId] = {
                                        plotId: plot.plotId,
                                        plotName: plot.name,
                                        plantationName: plantation.name
                                    };
                                });
                            }
                            completedRequests++;
                            if (completedRequests === totalRequests && callback) {
                                callback();
                            }
                        },
                        error: function() {
                            completedRequests++;
                            if (completedRequests === totalRequests && callback) {
                                callback();
                            }
                        }
                    });
                });
            } else {
                callback();
            }
        },
        error: function() {
            callback();
        }
    });
}

function getActiveTab() {
    const activeTabLink = document.querySelector('.nav-link.active');
    if (activeTabLink) {
        const href = activeTabLink.getAttribute('href');
        return href.replace('#', '');
    }
    return 'generales';
}

// Stage styles similar to plots
const stageStyles = {
    1: { textClass: 'text-warning', borderClass: 'border-warning' },
    2: { textClass: 'text-info', borderClass: 'border-info' },
    3: { textClass: 'text-success', borderClass: 'border-success' }
};

function getStageName(stageId) {
    switch(stageId) {
        case 1: return 'Pendiente';
        case 2: return 'En Proceso';
        case 3: return 'Finalizada';
        default: return 'Desconocido';
    }
}

$.fn.formatDate = function (dateString) {
    if (!dateString) return 'N/A';
    const [year, month, day] = dateString.split("-");
    const localDate = new Date(year, month -1, day);
    const monthName = localDate.toLocaleString('es-ES', { month: 'short' });
    return `${day}/${monthName}/${year}`;
}

$('#btn-submit-add-task').on('click', function () {
    const $f = $('#addTaskForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/tasks?tab=' + activeTab);
});

$('#btn-submit-edit-task').on('click', function () {
    const $f = $('#editTaskForm');
    const activeTab = getActiveTab();
    const taskId = $('#taskIdInputEdit').val();

    let isValid = true;
    $f.find('.is-invalid').removeClass('is-invalid');
    $f.find('.invalid-feedback').hide();

    $f.find('input, select, textarea').each(function () {
        const $field = $(this);
        const isRequired = $field.prop('required');
        const value = $field.val();

        if (isRequired && (!value || value.toString().trim() === "")) {
            isValid = false;
            $field.addClass('is-invalid');
            let $feedback = $field.siblings('.invalid-feedback');
            if ($feedback.length) {
                $feedback.text('Este campo es requerido').show();
            } else {
                $field.after('<div class="invalid-feedback" style="display: block;">Este campo es requerido</div>');
            }
        }
    });

    if (!isValid) {
        $f.find('.is-invalid').first().focus();
        return;
    }

    const formData = new FormData($f[0]);
    $.ajax({
        url: $f.attr('action') + '/' + taskId,
        type: 'PUT',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: response.message || 'Tarea actualizada correctamente'
                }).then(() => {
                    $('#modalEditTask').modal('hide');
                    $.fn.getAllTasks();
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado'
                });
            }
        },
        error: function (xhr) {
            let errorMsg = "Error en el servidor";
            if (xhr.status === 403) errorMsg = "No tienes permisos para esta acción";
            else if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMsg = xhr.responseJSON.message;
            }
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: errorMsg
            });
        }
    });
});

$('#btn-submit-add-special-task').on('click', function () {
    var $f = $('#addSpecialTaskForm');
    var activeTab = getActiveTab();

    if (!$f[0].checkValidity()) {
        $f.addClass('was-validated');
        $f.find(':invalid').first().focus();
        return;
    }

    var workerIds = workerAddAutocomplete ? workerAddAutocomplete.getSelected() : [];
    var origAction = $f.attr('action');
    var formData = new FormData($f[0]);
    formData.set('workerIds', workerIds.join(','));

    $.ajax({
        url: origAction,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.success || response.status === "OK") {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: response.message || 'Tarea especial creada exitosamente',
                    showConfirmButton: true
                }).then(function() {
                    window.location.href = '/agrosys/tasks?tab=' + activeTab;
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message || 'Ocurrió un error inesperado' });
            }
        },
        error: function(xhr) {
            var errorMsg = "Error en el servidor";
            if (xhr.status === 403) errorMsg = "No tienes permisos para esta acción";
            else if (xhr.responseJSON && xhr.responseJSON.message) errorMsg = xhr.responseJSON.message;
            Swal.fire({ icon: 'error', title: 'Error', text: errorMsg });
        }
    });
});

$('#btn-submit-edit-special-task').on('click', function() {
    var taskId = $('#specialTaskIdInputEdit').val();
    if (!taskId) {
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo obtener el ID de la tarea' });
        return;
    }

    var $f = $('#editSpecialTaskForm');
    if (!$f[0].checkValidity()) {
        $f.addClass('was-validated');
        $f.find(':invalid').first().focus();
        return;
    }

    var formData = new FormData($f[0]);
    var workerIds = workerEditAutocomplete ? workerEditAutocomplete.getSelected() : [];
    formData.set('workerIds', workerIds.join(','));

    $.ajax({
        url: '/agrosys/tasks/special/edit/' + taskId,
        type: 'PUT',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: response.message || 'Tarea especial actualizada correctamente'
                }).then(function() {
                    $('#modalEditSpecialTask').modal('hide');
                    $.fn.getAllSpecialTasks();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message || 'Ocurrió un error inesperado' });
            }
        },
        error: function(xhr) {
            var errorMsg = "Error en el servidor";
            if (xhr.status === 403) errorMsg = "No tienes permisos para esta acción";
            else if (xhr.responseJSON && xhr.responseJSON.message) errorMsg = xhr.responseJSON.message;
            Swal.fire({ icon: 'error', title: 'Error', text: errorMsg });
        }
    });
});

$('#btn-submit-delete-special-task').on('click', function() {
    var id = currentSpecialTaskId;
    if (!id) {
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo obtener el ID de la tarea' });
        return;
    }

    $.ajax({
        url: '/agrosys/tasks/special/delete/' + id,
        type: 'DELETE',
        contentType: 'application/json',
        success: function(response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: 'Tarea especial eliminada exitosamente'
                }).then(function() {
                    $('#modalDeleteSpecialTask').modal('hide');
                    currentSpecialTaskId = null;
                    $.fn.getAllSpecialTasks();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function(xhr) {
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al eliminar la tarea especial';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

$('#btn-submit-delete-task').on('click', function() {
    var id = currentTaskId;

    if (!id) {
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo obtener el ID de la tarea' });
        return;
    }

    $.ajax({
        url: '/agrosys/tasks/delete/' + id,
        type: 'DELETE',
        contentType: 'application/json',
        success: function(response) {
            if (response.success) {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: 'Tarea eliminada exitosamente'
                }).then(function() {
                    $('#modalDeleteTask').modal('hide');
                    currentTaskId = null;
                    $.fn.getAllTasks();
                });
            } else {
                Swal.fire({ icon: 'error', title: 'Error', text: response.message });
            }
        },
        error: function(xhr) {
            var msg = xhr.responseJSON ? xhr.responseJSON.message : 'Error al eliminar la tarea';
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
});

$.fn.getAllTasks = function() {
    $.ajax({
        url: '/agrosys/tasks/get-all',
        type: 'GET',
        success: function (response) {
            const accordion = $('#accordionContainerTasks');
            accordion.html('');

            $("#message-tasks").attr("style", "display:none !important; height: 100px;");

            if (response.success) {
                tasksData = response.data;

                if (response.data.length === 0) {
                    $('#message-tasks').html('Sin tareas registradas. Cree una nueva tarea para comenzar.').show();
                    return;
                }

                response.data.forEach(task => {
                    const taskId = task.taskId;
                    const name = task.name || 'Sin nombre';
                    const description = task.description || 'Sin descripción';
                    const createAt = task.createAt || '';
                    const endAt = task.endAt || '';
                    const taskStageId = task.taskStageId || 1;

                    const plantationInfo = plantationInfoMap[task.plantationId] || {};
                    const plantationName = plantationInfo.plantationName || 'N/A';

                    const accordionItem = $(
                        '<div class="accordion-item">' +
                            '<h2 class="accordion-header" id="heading-' + taskId + '">' +
                                '<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" ' +
                                    'data-bs-target="#collapse-' + taskId + '" aria-expanded="true" aria-controls="collapse-' + taskId + '">' +
                                    '<div class="d-flex flex-row justify-content-between align-items-center w-100">' +
                                        '<div class="d-flex flex-row align-items-center gap-2">' +
                                            '<i class="ri-task-fill fs-5 btn btn-link p-0 text-decoration-none" ' +
                                                'style="filter: brightness(0.85);"></i>' +
                                            '<span>' + escapeHtml(name) + '</span>' +
                                        '</div>' +
                                        '<div class="" id="status-' + taskId + '"></div>' +
                                    '</div>' +
                                '</button>' +
                            '</h2>' +
                            '<div id="collapse-' + taskId + '" class="accordion-collapse collapse" ' +
                                'aria-labelledby="heading-' + taskId + '" data-bs-parent="#accordionContainerTasks" style="">' +
                                '<div class="accordion-body" id="collapseBody-' + taskId + '">' +
                                    '<p class="mb-1"><strong>Plantío:</strong> ' + escapeHtml(plantationName) + '</p>' +
                                    '<div class="d-flex flex-row justify-content-between align-items-center gap-4 mt-2 mb-3">' +
                                        '<p class="card-text p-0 m-0">Fecha inicio: <span class="text-capitalize">' + (createAt ? $.fn.formatDate(createAt) : 'N/A') + '</span></p>' +
                                        '<p class="card-text p-0 m-0">Fecha estimada: <span class="text-capitalize">' + (endAt ? $.fn.formatDate(endAt) : 'Sin fecha') + '</span></p>' +
                                        '<div class="d-flex flex-row justify-content-end align-items-center gap-4">' +
                                            '<button class="edit-task btn btn-outline-info btn-sm py-0 px-3" data-bs-toggle="modal" ' +
                                                'data-bs-target="#modalEditTask"><i class="ri-edit-2-fill pe-1"></i>Editar</button>' +
                                            '<button class="btn btn-outline-primary btn-sm py-0 px-3 btn-view-task" data-bs-toggle="modal" ' +
                                                'data-bs-target="#modalViewTask"> <i class="ri-eye-fill pe-1"></i>Ver Detalles</button>' +
                                            '<button class="btn btn-outline-danger btn-sm py-0 px-3 delete-task"><i class="ri-delete-bin-fill pe-1"></i>Eliminar</button>' +
                                        '</div>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>'
                    );

                    const statusDiv = accordionItem.find(`#status-${taskId}`);
                    statusDiv.html('').append(`<span class="${stageStyles[taskStageId].textClass}">${getStageName(taskStageId)}</span>`);
                    statusDiv.removeAttr('class').addClass(stageStyles[taskStageId].borderClass + ' btn px-1 py-0 me-5');
                    statusDiv.css({
                        'pointer-events': 'none',
                        'width': '125px'
                    });

                    accordionItem.find('.edit-task').on('click', function (e) {
                        $('#taskIdInputEdit').val(taskId);
                        $('#nameInputEdit').val(name);
                        $('#descriptionInputEdit').val(task.description || '');
                        $('#createAtInputEdit').val(createAt);
                        $('#endAtInputEdit').val(endAt || '');
                        $('#taskStageSelectEdit').val(taskStageId);

                        $.fn.loadPlotsForEdit(task.plantationId);
                    });

                    accordionItem.find('.btn-view-task').on('click', function (e) {
                        $('#viewTaskName').text(name);
                        $('#viewTaskId').text(taskId);
                        $('#viewTaskDescription').text(description);
                        $('#viewTaskCreateAt').text(createAt ? $.fn.formatDate(createAt) : 'N/A');
                        $('#viewTaskEndAt').text(endAt ? $.fn.formatDate(endAt) : 'Sin fecha');
                        $('#viewTaskStage').text(getStageName(taskStageId));
                        $('#modalViewTask').modal('show');
                    });

                    accordionItem.find('.delete-task').on('click', function () {
                        currentTaskId = taskId;
                        $('#textDeleteTask').text(`¿Está seguro de que desea eliminar la tarea ${name}?`);
                        $('#modalDeleteTask').modal('show');
                    });

                    accordion.append(accordionItem);
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado al obtener las tareas'
                });
            }
        },
        error: function (xhr, status, error) {
            console.error('Error en la solicitud AJAX:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Ocurrió un error al comunicarse con el servidor'
            });
        }
    });
};

function getWorkerName(workerId) {
    var worker = workersData.find(function(w) { return w.workerId === workerId; });
    return worker ? worker.name : 'ID ' + workerId;
}

function getWorkerNames(workerIds) {
    if (!workerIds || !Array.isArray(workerIds) || workerIds.length === 0) return 'N/A';
    return workerIds.map(getWorkerName).join(', ');
}

$.fn.loadWorkers = function(callback) {
    $.ajax({
        url: '/agrosys/tasks/workers/get-all',
        type: 'GET',
        success: function(response) {
            if (response.success && Array.isArray(response.data)) {
                workersData = response.data;
            }
            if (callback) callback();
        },
        error: function() {
            if (callback) callback();
        }
    });
};

var workerAddAutocomplete, workerEditAutocomplete;
var workerAutocompleteInitDone = false;

function initWorkerAutocomplete(searchId, dropdownId, containerId, autocompleteRef) {
    var $search = $('#' + searchId);
    var $dropdown = $('#' + dropdownId);
    var $container = $('#' + containerId);
    var selectedWorkers = [];

    if (!$search.length || !$dropdown.length || !$container.length) return;

    function renderTags() {
        $container.html('');
        selectedWorkers.forEach(function(w) {
            var tag = $(
                '<span class="badge bg-primary d-inline-flex align-items-center gap-1 me-1" style="font-size: 0.85rem;">' +
                    escapeHtml(w.name) +
                    '<i class="ri-close-line" style="cursor:pointer;" data-id="' + w.workerId + '"></i>' +
                '</span>'
            );
            tag.find('.ri-close-line').on('click', function() {
                var id = parseInt($(this).data('id'));
                selectedWorkers = selectedWorkers.filter(function(s) { return s.workerId !== id; });
                renderTags();
            });
            $container.append(tag);
        });
    }

    function renderDropdown(query) {
        $dropdown.html('');
        if (!query) {
            $dropdown.addClass('d-none');
            return;
        }
        var matching = workersData.filter(function(w) {
            return w.name.toLowerCase().indexOf(query.toLowerCase()) !== -1;
        });
        if (matching.length === 0) {
            $dropdown.html('<small class="p-2 text-muted d-block">Sin resultados</small>').removeClass('d-none');
            return;
        }
        matching.forEach(function(w) {
            var alreadySelected = selectedWorkers.some(function(s) { return s.workerId === w.workerId; });
            var item = $(
                '<div class="dropdown-item py-1 px-2" style="cursor:pointer;" data-id="' + w.workerId + '">' +
                    escapeHtml(w.name) +
                    (alreadySelected ? ' <i class="ri-check-line text-success"></i>' : '') +
                '</div>'
            );
            item.on('click', function() {
                if (!alreadySelected) {
                    selectedWorkers.push({ workerId: w.workerId, name: w.name });
                    renderTags();
                }
                $search.val('').focus();
                $dropdown.addClass('d-none');
            });
            $dropdown.append(item);
        });
        $dropdown.removeClass('d-none');
    }

    $search.on('input', function() {
        renderDropdown($(this).val());
    });

    $search.on('focus', function() {
        if ($(this).val()) {
            renderDropdown($(this).val());
        }
    });

    autocompleteRef.setSelected = function(ids) {
        selectedWorkers = [];
        if (ids && Array.isArray(ids)) {
            workersData.forEach(function(w) {
                if (ids.indexOf(w.workerId) !== -1) {
                    selectedWorkers.push({ workerId: w.workerId, name: w.name });
                }
            });
        }
        renderTags();
    };

    autocompleteRef.getSelected = function() {
        return selectedWorkers.map(function(w) { return w.workerId; });
    };
}

$.fn.initWorkerComponents = function() {
    if (workerAutocompleteInitDone) return;
    workerAutocompleteInitDone = true;

    $(document).on('click', function(e) {
        if (!$(e.target).closest('.worker-selector').length) {
            $('.worker-dropdown').addClass('d-none');
        }
    });

    workerAddAutocomplete = {};
    workerEditAutocomplete = {};
    initWorkerAutocomplete('workerSearchAdd', 'workerDropdownAdd', 'selectedWorkersAdd', workerAddAutocomplete);
    initWorkerAutocomplete('workerSearchEdit', 'workerDropdownEdit', 'selectedWorkersEdit', workerEditAutocomplete);
};

function renderSpecialTaskAccordion(tasks) {
    var accordion = $('#accordionContainerSpecialTasks');
    accordion.html('');

    if (!tasks || tasks.length === 0) {
        $('#message-special-tasks').html('No hay tareas especiales registradas.').show();
        return;
    }
    $('#message-special-tasks').hide();

    tasks.forEach(function(task) {
        var specialTaskId = task.specialTaskId;
        var name = task.name || 'Sin nombre';
        var paymentAmount = task.paymentAmount || 0;
        var workersStr = getWorkerNames(task.workerIds);
        var stageStyle = stageStyles[task.taskStageId] || stageStyles[1];

        var accordionItem = $(
            '<div class="accordion-item">' +
                '<h2 class="accordion-header" id="special-heading-' + specialTaskId + '">' +
                    '<button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" ' +
                        'data-bs-target="#special-collapse-' + specialTaskId + '" aria-expanded="true" aria-controls="special-collapse-' + specialTaskId + '">' +
                        '<div class="d-flex flex-row justify-content-between align-items-center w-100">' +
                            '<div class="d-flex flex-row align-items-center gap-2">' +
                                '<i class="ri-task-fill fs-5 btn btn-link p-0 text-decoration-none" ' +
                                    'style="filter: brightness(0.85);"></i>' +
                                '<span>' + escapeHtml(name) + '</span>' +
                            '</div>' +
                            '<div class="" id="special-status-' + specialTaskId + '"></div>' +
                        '</div>' +
                    '</button>' +
                '</h2>' +
                '<div id="special-collapse-' + specialTaskId + '" class="accordion-collapse collapse" ' +
                    'aria-labelledby="special-heading-' + specialTaskId + '" data-bs-parent="#accordionContainerSpecialTasks">' +
                    '<div class="accordion-body">' +
                        '<p class="mb-1"><strong>Pago:</strong> <span class="text-success">$' + parseFloat(paymentAmount).toFixed(2) + '</span></p>' +
                        '<p class="mb-1"><strong>Workers:</strong> ' + workersStr + '</p>' +
                        '<div class="d-flex flex-row justify-content-between align-items-center gap-4 mt-2 mb-3">' +
                            '<p class="card-text p-0 m-0">Inicio: <span class="text-capitalize">' + (task.createAt ? $.fn.formatDate(task.createAt) : 'N/A') + '</span></p>' +
                            '<p class="card-text p-0 m-0">Fin: <span class="text-capitalize">' + (task.endAt ? $.fn.formatDate(task.endAt) : 'Sin fecha') + '</span></p>' +
                            '<div class="d-flex flex-row justify-content-end align-items-center gap-2">' +
                                '<button class="edit-special-task btn btn-outline-info btn-sm py-0 px-3"><i class="ri-edit-2-fill pe-1"></i>Editar</button>' +
                                '<button class="btn btn-outline-primary btn-sm py-0 px-3 view-special-task"><i class="ri-eye-fill pe-1"></i>Ver</button>' +
                                '<button class="btn btn-outline-danger btn-sm py-0 px-3 delete-special-task"><i class="ri-delete-bin-fill pe-1"></i>Eliminar</button>' +
                            '</div>' +
                        '</div>' +
                    '</div>' +
                '</div>' +
            '</div>'
        );

        var statusDiv = accordionItem.find('#special-status-' + specialTaskId);
        statusDiv.html('<span class="' + stageStyle.textClass + '">' + getStageName(task.taskStageId || 1) + '</span>');
        statusDiv.addClass(stageStyle.borderClass + ' btn px-1 py-0 me-5');
        statusDiv.css({ 'pointer-events': 'none', 'width': '125px' });

        accordionItem.find('.edit-special-task').on('click', function() {
            $('#specialTaskIdInputEdit').val(specialTaskId);
            $('#nameInputSpecialEdit').val(name);
            $('#paymentAmountInputSpecialEdit').val(paymentAmount);
            $('#createAtInputSpecialEdit').val(task.createAt || '');
            $('#taskStageIdInputSpecialEdit').val(task.taskStageId || 1);
            $('#endAtInputSpecialEdit').val(task.endAt || '');
            $('#descriptionInputSpecialEdit').val(task.description || '');
            if (workerEditAutocomplete) {
                workerEditAutocomplete.setSelected(task.workerIds || []);
            }
            $('#modalEditSpecialTask').modal('show');
        });

        accordionItem.find('.view-special-task').on('click', function() {
            $('#viewSpecialTaskName').text(name);
            $('#viewSpecialTaskId').text(specialTaskId);
            $('#viewSpecialTaskDescription').text(task.description || 'Sin descripción');
            $('#viewSpecialTaskCreateAt').text(task.createAt ? $.fn.formatDate(task.createAt) : 'N/A');
            $('#viewSpecialTaskEndAt').text(task.endAt ? $.fn.formatDate(task.endAt) : 'Sin fecha');
            $('#viewSpecialTaskPayment').text(parseFloat(paymentAmount).toFixed(2));
            $('#viewSpecialTaskStage').text(getStageName(task.taskStageId || 1));
            $('#viewSpecialTaskWorkers').text(workersStr);
            $('#modalViewSpecialTask').modal('show');
        });

        accordionItem.find('.delete-special-task').on('click', function() {
            currentSpecialTaskId = specialTaskId;
            $('#textDeleteSpecialTask').text('¿Está seguro de que desea eliminar la tarea especial "' + escapeHtml(name) + '"?');
            $('#modalDeleteSpecialTask').modal('show');
        });

        accordion.append(accordionItem);
    });
}

$.fn.getAllSpecialTasks = function() {
    $.ajax({
        url: '/agrosys/tasks/special/get-all',
        type: 'GET',
        success: function(response) {
            if (response.success && Array.isArray(response.data)) {
                specialTasksData = response.data;
                renderSpecialTaskAccordion(specialTasksData);
            } else {
                $('#message-special-tasks').html('No hay tareas especiales registradas.').show();
            }
        },
        error: function(xhr) {
            $('#message-special-tasks').hide();
            var msg = 'Error al cargar las tareas especiales';
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

$.fn.loadPlots = function() {
    $.ajax({
        url: '/agrosys/tasks/plots/get-all',
        type: 'GET',
        success: function(response) {
            if (response.success && Array.isArray(response.data)) {
                plotsData = response.data;
                var select = $('#plotIdInput');
                select.html('<option value="">Seleccione una parcela</option>');
                response.data.forEach(function(plot) {
                    select.append('<option value="' + plot.plotId + '">' + escapeHtml(plot.name) + '</option>');
                });
            }
        },
        error: function(xhr) {
            console.error('Error al cargar parcelas:', xhr);
        }
    });
};

$.fn.loadPlotsForEdit = function(selectedPlantationId) {
    plantationsData = {};
    allPlantationsData = [];

    $.ajax({
        url: '/agrosys/tasks/plots/get-all',
        type: 'GET',
        success: function(response) {
            if (response.success && Array.isArray(response.data)) {
                plotsData = response.data;
                var select = $('#plotIdInputEdit');
                select.html('<option value="">Seleccione una parcela</option>');
                response.data.forEach(function(plot) {
                    select.append('<option value="' + plot.plotId + '">' + escapeHtml(plot.name) + '</option>');
                });

                if (selectedPlantationId) {
                    response.data.forEach(function(plot) {
                        $.ajax({
                            url: '/agrosys/tasks/plantation/get-all-by-plot-id/' + plot.plotId,
                            type: 'GET',
                            success: function(plantResponse) {
                                if (plantResponse.success && Array.isArray(plantResponse.data)) {
                                    plantationsData[plot.plotId] = plantResponse.data;
                                    plantResponse.data.forEach(function(plantation) {
                                        allPlantationsData.push(plantation);
                                    });

                                    for (var i = 0; i < plantResponse.data.length; i++) {
                                        if (plantResponse.data[i].plantatioId === selectedPlantationId) {
                                            $('#plotIdInputEdit').val(plot.plotId);
                                            $.fn.loadPlantationsForEdit(plot.plotId, selectedPlantationId);
                                            return;
                                        }
                                    }
                                }
                            },
                            error: function(xhr) {
                                console.error('Error al cargar plantíos para parcela ' + plot.plotId, xhr);
                            }
                        });
                    });
                }
            }
        },
        error: function(xhr) {
            console.error('Error al cargar parcelas:', xhr);
        }
    });
};

$.fn.loadPlantations = function(plotId) {
    if (!plotId) {
        $('#plantationIdInput').html('<option value="">Seleccione un plantío</option>');
        return;
    }
    $.ajax({
        url: '/agrosys/tasks/plantation/get-all-by-plot-id/' + plotId,
        type: 'GET',
        success: function(response) {
            var select = $('#plantationIdInput');
            select.html('<option value="">Seleccione un plantío</option>');
            if (response.success && Array.isArray(response.data) && response.data.length > 0) {
                plantationsData[plotId] = response.data;
                response.data.forEach(function(plantation) {
                    allPlantationsData.push(plantation);
                    select.append('<option value="' + plantation.plantatioId + '">' + escapeHtml(plantation.name) + '</option>');
                });
            }
        },
        error: function(xhr) {
            console.error('Error al cargar plantíos:', xhr);
            $('#plantationIdInput').html('<option value="">Error al cargar</option>');
        }
    });
};

$.fn.loadPlantationsForEdit = function(plotId, selectedPlantationId) {
    if (!plotId) {
        $('#plantationIdInputEdit').html('<option value="">Seleccione un plantío</option>');
        return;
    }
    $.ajax({
        url: '/agrosys/tasks/plantation/get-all-by-plot-id/' + plotId,
        type: 'GET',
        success: function(response) {
            var select = $('#plantationIdInputEdit');
            select.html('<option value="">Seleccione un plantío</option>');
            if (response.success && Array.isArray(response.data) && response.data.length > 0) {
                plantationsData[plotId] = response.data;
                response.data.forEach(function(plantation) {
                    allPlantationsData.push(plantation);
                    select.append('<option value="' + plantation.plantatioId + '">' + escapeHtml(plantation.name) + '</option>');
                });

                if (selectedPlantationId) {
                    $('#plantationIdInputEdit').val(selectedPlantationId);
                }
            }
        },
        error: function(xhr) {
            console.error('Error al cargar plantíos:', xhr);
            $('#plantationIdInputEdit').html('<option value="">Error al cargar</option>');
        }
    });
};

$(document).on('change', '#plotIdInput', function() {
    var plotId = $(this).val();
    $.fn.loadPlantations(plotId);
});

$(document).on('change', '#plotIdInputEdit', function() {
    var plotId = $(this).val();
    $.fn.loadPlantationsForEdit(plotId, null);
});

$(document).ready(function() {
    var workersLoaded = false;

    function onWorkersDone() {
        if (workersLoaded) return;
        workersLoaded = true;
        $.fn.getAllSpecialTasks();
        $.fn.initWorkerComponents();
    }

    $.fn.loadWorkers(onWorkersDone);
    setTimeout(onWorkersDone, 3000);

    $.fn.loadPlots();
    buildPlantationInfoMap(function() {
        $.fn.getAllTasks();
    });
});