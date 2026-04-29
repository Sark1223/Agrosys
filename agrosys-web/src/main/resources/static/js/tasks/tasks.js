let currentTaskId = null;
let tasksData = [];
let specialTasksData = [];
let stages = [];

$('#btn-submit-add-task').on('click', function () {
    const $f = $('#addTaskForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/tasks?tab=' + activeTab);
});

$('#btn-submit-edit-task').on('click', function () {
    const $f = $('#editTaskForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action') + '/' + $('#taskIdInputEdit').val(), '/agrosys/tasks?tab=' + activeTab);
});

$('#btn-submit-add-special-task').on('click', function () {
    const $f = $('#addSpecialTaskForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/tasks?tab=' + activeTab);
});

function getActiveTab() {
    const activeTabLink = document.querySelector('.nav-link.active');
    if (activeTabLink) {
        const href = activeTabLink.getAttribute('href');
        return href.replace('#', '');
    }
    return 'generales';
}

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
        success: function(response) {
            var cardsContainer = $('#cardsContainerTasks');
            cardsContainer.html('');

            if (response.success && Array.isArray(response.data)) {
                tasksData = response.data;

                if (response.data.length === 0) {
                    cardsContainer.html('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-4 fst-italic w-100" style="height: 100px;">No hay tareas registradas.</div>');
                    return;
                }

                response.data.forEach(function(task) {
                    var taskId = task.taskId;
                    var name = task.name || 'Sin nombre';
                    var description = task.description || '';
                    var createAt = task.createAt || '';
                    var endAt = task.endAt || '';
                    var taskStageId = task.taskStageId || 1;

                    var stageBadge = getStageBadge(taskStageId);

                    var taskCard = $(
                        '<div class="card mb-3 me-2 task-card" style="width: 300px; cursor: pointer;" data-id="' + taskId + '">' +
                            '<div class="card-body d-flex flex-row justify-content-between align-items-start">' +
                                '<div class="d-flex align-items-center gap-3">' +
                                    '<div class="rounded-circle bg-primary d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">' +
                                        '<i class="ri-task-fill text-white"></i>' +
                                    '</div>' +
                                    '<div class="d-flex flex-column">' +
                                        '<h5 class="card-title text-capitalize mb-0">' + escapeHtml(name.toLowerCase()) + '</h5>' +
                                        '<small class="text-muted">' + escapeHtml(description.substring(0, 30)) + '</small>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="dropdown">' +
                                    '<i class="ri-more-2-fill" type="button" id="dropdownMenuButton' + taskId + '" data-bs-toggle="dropdown" aria-expanded="false"></i>' +
                                    '<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton' + taskId + '">' +
                                        '<li><a class="dropdown-item view-task" data-id="' + taskId + '" href="#"><i class="ri-eye-fill pe-1"></i>Ver Detalle</a></li>' +
                                        '<li><a class="dropdown-item edit-task" data-id="' + taskId + '" href="#"><i class="ri-pencil-fill pe-1"></i>Editar</a></li>' +
                                        '<li><a class="dropdown-item delete-task" data-id="' + taskId + '" href="#"><i class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>' +
                                    '</ul>' +
                                '</div>' +
                            '</div>' +
                            '<div class="card-footer text-muted">' + stageBadge + '</div>' +
                        '</div>'
                    );

                    taskCard.find('.view-task').on('click', function(e) {
                        e.stopPropagation();
                        var task = null;
                        for (var i = 0; i < tasksData.length; i++) {
                            if (tasksData[i].taskId === taskId) {
                                task = tasksData[i];
                                break;
                            }
                        }
                        $('#viewTaskName').text(task.name);
                        $('#viewTaskId').text(task.taskId);
                        $('#viewTaskDescription').text(task.description || 'Sin descripción');
                        $('#viewTaskCreateAt').text(task.createAt);
                        $('#viewTaskEndAt').text(task.endAt || ' Sin fecha');
                        $('#viewTaskStage').text(getStageName(task.taskStageId));
                        $('#modalViewTask').modal('show');
                    });

                    taskCard.find('.edit-task').on('click', function(e) {
                        e.stopPropagation();
                        var task = null;
                        for (var i = 0; i < tasksData.length; i++) {
                            if (tasksData[i].taskId === taskId) {
                                task = tasksData[i];
                                break;
                            }
                        }
                        $('#taskIdInputEdit').val(taskId);
                        $('#nameInputEdit').val(task.name);
                        $('#descriptionInputEdit').val(task.description || '');
                        $('#createAtInputEdit').val(task.createAt);
                        $('#endAtInputEdit').val(task.endAt || '');
                        $('#taskStageSelectEdit').val(task.taskStageId || 1);
                        $('#modalEditTask').modal('show');
                    });

                    taskCard.find('.delete-task').on('click', function(e) {
                        e.stopPropagation();
                        currentTaskId = taskId;
                        $('#textDeleteTask').text('¿Está seguro de que desea eliminar la tarea ' + name + '?');
                        $('#modalDeleteTask').modal('show');
                    });

                    cardsContainer.append(taskCard);
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado al obtener las tareas'
                });
            }
        },
        error: function(xhr) {
            var msg = 'Error al cargar las tareas';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                msg = xhr.responseJSON.message;
            }
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
};

$.fn.getAllSpecialTasks = function() {
    $.ajax({
        url: '/agrosys/tasks/special/get-all',
        type: 'GET',
        success: function(response) {
            var cardsContainer = $('#cardsContainerSpecialTasks');
            cardsContainer.html('');

            if (response.success && Array.isArray(response.data)) {
                specialTasksData = response.data;

                if (response.data.length === 0) {
                    cardsContainer.html('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-4 fst-italic w-100" style="height: 100px;">No hay tareas especiales registradas.</div>');
                    return;
                }

                response.data.forEach(function(task) {
                    var taskId = task.taskId;
                    var workerId = task.workerId;
                    var paymentAmount = task.paymentAmount || 0;

                    var taskCard = $(
                        '<div class="card mb-3 me-2 special-task-card" style="width: 300px; cursor: pointer;" data-id="' + taskId + '">' +
                            '<div class="card-body d-flex flex-row justify-content-between align-items-start">' +
                                '<div class="d-flex align-items-center gap-3">' +
                                    '<div class="rounded-circle bg-warning d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;">' +
                                        '<i class="ri-user-star-fill text-white"></i>' +
                                    '</div>' +
                                    '<div class="d-flex flex-column">' +
                                        '<h5 class="card-title mb-0">Worker #' + workerId + '</h5>' +
                                        '<small class="text-success fw-bold">$' + parseFloat(paymentAmount).toFixed(2) + '</small>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>'
                    );

                    cardsContainer.append(taskCard);
                });
            }
        },
        error: function(xhr) {
            var msg = 'Error al cargar las tareas especiales';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                msg = xhr.responseJSON.message;
            }
            Swal.fire({ icon: 'error', title: 'Error', text: msg });
        }
    });
};

function getStageBadge(stageId) {
    switch(stageId) {
        case 1: return '<span class="badge bg-warning text-dark">Pendiente</span>';
        case 2: return '<span class="badge bg-primary">En Proceso</span>';
        case 3: return '<span class="badge bg-success">Finalizada</span>';
        default: return '<span class="badge bg-secondary">Desconocido</span>';
    }
}

function getStageName(stageId) {
    switch(stageId) {
        case 1: return 'Pendiente';
        case 2: return 'En Proceso';
        case 3: return 'Finalizada';
        default: return 'Desconocido';
    }
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

$(document).ready(function() {
    $.fn.getAllTasks();
    $.fn.getAllSpecialTasks();
});