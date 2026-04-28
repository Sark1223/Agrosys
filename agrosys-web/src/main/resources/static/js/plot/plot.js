$(document).ready(function () {
    // Configuración de plots
    $('#btn-submit-add-plot').on('click', function () {
        const $f = $('#addPlotForm');
        const activeTab = getActiveTab();
        $.fn.postFormData($f, $f.attr('action'), '/agrosys/plots?tab=' + activeTab);
    });

    $('#btn-submit-edit-plot').on('click', function () {
        const $f = $('#editPlotForm');
        const activeTab = getActiveTab();
        $.fn.postFormData($f, $f.attr('action'), '/agrosys/plots?tab=' + activeTab);
    });

    // Configuración de plantaciones
    $('#btn-submit-add-plantation').on('click', function () {
        const $f = $('#addPlantationForm');
        const activeTab = getActiveTab();
        $.fn.postFormData($f, $f.attr('action'), '/agrosys/plots?tab=' + activeTab);
    });

    $('#btn-submit-edit-plantation').on('click', function () {
        const $f = $('#editPlantationForm');
        const activeTab = getActiveTab();
        $.fn.postFormData($f, $f.attr('action'), '/agrosys/plots?tab=' + activeTab);
    });

    $('#btn-submit-delete-plantation').on('click', function () {
        const $f = $('#deletePlantationForm');
        const activeTab = getActiveTab();
        $.fn.postFormData($f, $f.attr('action'), '/agrosys/plots?tab=' + activeTab);
    });

    $('#btn-submit-add-state').on('click', function () {
        const $f = $('#addStateForm');
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    //Cerrar modal y recargar información de details
                    $.fn.getStagesByPlantation($('#plantationIdInputState').val());
                    $.fn.getAllPlots();

                    $('#btn-close-add-state').trigger('click');
                }
            });


    });

    $('#btn-submit-edit-state').on('click', function () {
        const $f = $('#editStateForm');
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    //Cerrar modal y recargar información de details
                    $.fn.getStagesByPlantation($('#plantationIdInputState').val());
                    $.fn.getAllPlots();

                    $('#btn-close-edit-state').trigger('click');
                }
            });


    });

    function getActiveTab() {
        const activeTabLink = document.querySelector('.nav-link.active');
        if (activeTabLink) {
            const href = activeTabLink.getAttribute('href');
            return href.replace('#', '');
        }
        return 'plot';
    }

    $.fn.formatDate = function (dateString) {
        const [year, month, day] = dateString.split("-");
        const localDate = new Date(year, month - 1, day); // mes es base 0
        const monthName = localDate.toLocaleString('es-ES', { month: 'short' });

        return `${day}/${monthName}/${year}`;
    }

    const disenioStages = {
        0: { classText: 'text-secondary', classBorder: 'border-secondary' },
        1: { classText: 'text-warning', classBorder: 'border-warning' },
        2: { classText: 'text-info', classBorder: 'border-info' },
        3: { classText: 'text-success', classBorder: 'border-success' }
    };

    $.fn.getStagesByPlantation = function (plantationId) {
        $.ajax({
            url: `/agrosys/plots/plantation/${plantationId}/stages`,
            type: 'GET',
            success: function (response) {
                console.log('Respuesta de etapas:', response);

                if (response.success) {
                    const stages = response.data;
                    const stagesSize = stages.length;
                    
                    if (stagesSize > 0) {
                        $('#plantationDetailsCurrentState').text(stages[stagesSize - 1].name).removeAttr('class').addClass(disenioStages[stagesSize].classText + ' btn px-3 py-0 ms-4 ' + disenioStages[stagesSize].classBorder).css('pointer-events', 'none');
                        const $containerStages = $('#containerEstados');
                        $containerStages.html('');

                        stages.forEach(stage => {
                            let display = stage.id === stagesSize ? 'block' : 'none';
                            const $cardStage = $(`
                                <div class="card mb-2" id="card-stage${stage.id}-p${plantationId}">
                                    <div class="card-body d-flex flex-row justify-content-between align-items-center gap-3 overflow-auto">
                                        <div class="${disenioStages[stage.id].classBorder} btn px-1 py-0"
                                            style="pointer-events: none; width: 125px;">
                                            ${`<span class="${disenioStages[stage.id].classText}">${stage.name}</span>`}
                                        </div>
                                        <p class="card-text p-0 m-0">Fecha de inicio: <span class="text-capitalize">${$.fn.formatDate(stage.start_at)}</span></p>
                                        <p class="card-text p-0 m-0">Fecha de finalización: <span class="text-capitalize">${stage.end_at !== 'Sin fecha' ? $.fn.formatDate(stage.end_at) : stage.end_at}</span></p>
                                        <div class="d-flex flex-row justify-content-end align-items-center gap-4">
                                            
                                            <div class="dropdown position-static">
                                                <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${stage.id}" data-bs-toggle="dropdown" aria-expanded="false">
                                                </i>
                                                <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${stage.id}">
                                                <li><a class="dropdown-item details-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalDetailsPlantationStage" href="#"><i
                                                                class="ri-eye-line pe-1"></i>Ver detalles</a></li>
                                                    <li style="display: ${display};"><a class="dropdown-item edit-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalEditPlantationStage" href="#" data-bs-dismiss="modal"><i
                                                                class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                                    <li style="display: ${display};"><a class="dropdown-item delete-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalDeletePlantationStage" href="#" data-bs-dismiss="modal"><i
                                                                class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            `);

                            $cardStage.find('.edit-plantation-stage').on('click', function () {
                                const stageId = stage.id;

                                $('#plantationIdInputStateEdit').val(plantationId);
                                $('#nombreStateInputEdit').val(stageId);
                                $('#fechaInicioStateInputEdit').val(stage.start_at);
                                $('#fechaFinalizacionStateInputEdit').val(stage.end_at);
                                $('#notasStateInputEdit').val(stage.notes);
                            });

                            $cardStage.find('.delete-plantation-stage').on('click', function () {
                                const stageId = stage.id;
                                const plantationId = plot.id;

                                $('#textDeletePlantationStage').text(`¿Está seguro de que desea eliminar la etapa ${stage.name} de la plantación ${plot.name}?`);
                                $('#plantationIdInputStateDelete').val(plantationId);
                                $('#stageIdInputStateDelete').val(stageId);
                            });


                            $containerStages.append($cardStage);
                        });
                    } else {
                        $('#containerEstados').html('<div class="alert border-primary text-body-tertiary text-center" role="alert" style="background: #e9e9e9a6;">No hay etapas registradas para esta plantación.</div>');
                    }
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: response.message || 'Ocurrió un error inesperado al obtener las etapas de la plantación'
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
    }

    $.fn.getAllPlots = function () {

        $.ajax({
            url: '/agrosys/plots/get-all',
            type: 'GET',
            success: function (response) {
                console.log(response);

                const accordion = $('#accordionContainerPlots');
                accordion.html('');

                $("#message-plot").attr("style", "display:none !important;");
                if (response.success) {
                    console.log('Parcelas obtenidas:', response.data);

                    response.data.forEach(plot => {
                        const accordionItem = $(`
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="heading-${plot.plotId}">
                                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse-${plot.plotId}" aria-expanded="true" aria-controls="collapse-${plot.plotId}">
                                        <div class="d-flex flex-row justify-content-between align-items-center w-100">
                                            <div class="d-flex flex-row align-items-center gap-2">
                                                <i class="ri-plant-fill fs-5 btn btn-link p-0 text-decoration-none" 
                                                    style="filter: brightness(0.85);"></i>    
                                                <span>${plot.name}</span>
                                            </div>
                                            <div class="" id="status-${plot.plotId}"></div>
                                        </div>
                                    </button>
                                </h2>
                                <div id="collapse-${plot.plotId}" class="accordion-collapse collapse"
                                    aria-labelledby="heading-${plot.plotId}" data-bs-parent="#accordionContainerPlots" style="">
                                    <div class="accordion-body" id="collapseBody-${plot.plotId}">
                                        <p class="mb-0">${plot.description}</p>
                                        <div class="d-flex flex-row justify-content-end align-items-center gap-4 mt-2 mb-3">
                                            <button class="edit-plot btn btn-outline-info btn-sm py-0 px-3" data-bs-toggle="modal"
                                                    data-bs-target="#modalEditPlot"><i class="ri-edit-2-fill pe-1"></i>Editar Parcela</button>
                                            <button class="btn btn-outline-primary btn-sm py-0 px-3 btn-agregar-plantacion-parcela" data-bs-toggle="modal"
                                                data-bs-target="#modalAddPlantation"> <i class="ri-add-line pe-1"></i>Agregar Plantación</button>
                                        </div>
                                    </div>
                                </div>
                            </div>`);
                        const plantaciones = plot.plantatios ? JSON.parse(plot.plantatios) : null;
                        console.log('Plantaciones:', plantaciones);

                        const statusDiv = accordionItem.find(`#status-${plot.plotId}`);
                        if (plantaciones[0] !== null) {
                            const collapseBody = accordionItem.find(`#collapseBody-${plot.plotId}`);

                            const stagePlotText = plantaciones[0].stageId === null ? 'Sin etapa' : plantaciones[0].stage;
                            statusDiv.html('').append(`<span class="${plantaciones[0].stageId === null ? disenioStages[0].classText : disenioStages[plantaciones[0].stageId].classText}">${stagePlotText}</span>`);
                            statusDiv.removeAttr('class').addClass((plantaciones[0].stageId === null ? disenioStages[0].classBorder : disenioStages[plantaciones[0].stageId].classBorder) + ' btn px-1 py-0 me-5');
                            statusDiv.css({
                                'pointer-events': 'none',
                                'width': '125px'
                            });
                            plantaciones.forEach(plantacion => {
                                console.log('Plantación:', plantacion);
                                const cardPlantacion = $(`
                                    <div class="card mb-2">
                                        <div class="card-body d-flex flex-row justify-content-between align-items-center gap-3 overflow-auto">
                                            <p class="card-title text-capitalize p-0 m-0 w-25">${plantacion.name.toLowerCase()}</p>
                                            <p class="card-text p-0 m-0">Fecha de inicio: <span class="text-capitalize">${$.fn.formatDate(plantacion.start_at)}</span></p>
                                            <p class="card-text p-0 m-0">Fecha de finalización: <span class="text-capitalize">${plantacion.end_at !== 'N/A' ? $.fn.formatDate(plantacion.end_at) : plantacion.end_at}</span></p>
                                            <div class="d-flex flex-row justify-content-end align-items-center gap-4">
                                                <div class="${plantacion.stageId === null ? disenioStages[0].classBorder : disenioStages[plantacion.stageId].classBorder} btn px-1 py-0" id="status-plantacion-${plantacion.plantacioId}"
                                                    style="pointer-events: none; width: 125px;">
                                                    ${plantacion.stageId === null ? `<span class="${disenioStages[0].classText}">Sin etapa</span>` : `<span class="${disenioStages[plantacion.stageId].classText}">${plantacion.stage}</span>`}
                                                </div>
                                                <div class="dropdown position-static">
                                                    <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${plot.plotId}" data-bs-toggle="dropdown" aria-expanded="false">
                                                    </i>
                                                    <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${plot.plotId}">
                                                        <li><a class="dropdown-item details-plantation" data-bs-toggle="modal" data-bs-target="#modalDetailsPlantation" href="#"><i
                                                                    class="ri-eye-line pe-1"></i>Ver detalles</a></li>
                                                        <li><a class="dropdown-item edit-plantation" data-bs-toggle="modal" data-bs-target="#modalEditPlantation" href="#"><i
                                                                    class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                                        <li><a class="dropdown-item delete-plantation" data-bs-toggle="modal" data-bs-target="#modalDeletePlantation" href="#"><i
                                                                    class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                `);

                                cardPlantacion.find('.edit-plantation').on('click', function (e) {
                                    if (plot) {
                                        $('#plotIdInputPlantationEdit').val(plot.plotId);
                                        $('#plantationIdInputEdit').val(plantacion.plantatioId);
                                        $('#nombrePlantationInputEdit').val(plantacion.name);
                                        // $('#fechaInicioPlantationInputEdit').val(plantacion.start_at);
                                        // $('#fechaFinalizacionPlantationInputEdit').val(plantacion.end_at);
                                        $('#notasPlantationInputEdit').val(plantacion.notas);
                                    } else {
                                        Swal.fire({
                                            icon: 'error',
                                            title: 'Error',
                                            text: 'No se pudo cargar la información de la parcela para editar'
                                        });
                                    }
                                });

                                cardPlantacion.find('.delete-plantation').on('click', function () {

                                    if (plantacion) {
                                        $('#textDeletePlantation').text(`¿Está seguro de que desea eliminar la plantacion ${plantacion.name} ${plantacion.startAt}?`);
                                        $('#plantationIdInputDelete').val(plantacion.plantatioId);
                                    } else {
                                        Swal.fire({
                                            icon: 'error',
                                            title: 'Error',
                                            text: 'No se pudo cargar la información de la plantación para editar'
                                        });
                                    }
                                });

                                cardPlantacion.find('.details-plantation').on('click', function () {
                                    if (plantacion) {
                                        $('#plantationDetailsName').text(`${plantacion.name}`);
                                        $('#plantationDetailsOriginPlot').text(`${plot.name}`);
                                        $('#plantationDetailsStartAt').text($.fn.formatDate(plantacion.start_at));
                                        $('#plantationDetailsEndAt').text(plantacion.end_at !== 'N/A' ? $.fn.formatDate(plantacion.end_at) : plantacion.end_at);
                                        $('#plantationDetailsNotes').text(plantacion.notas);
                                        $('#plantationIdInputState').val(plantacion.plantatioId);
                                        $('#plantationIdInputStateEdit').val(plantacion.plantatioId);
                                        $('#plantationIdInputStateDelete').val(plantacion.plantatioId);
                                        $.fn.getStagesByPlantation(parseInt(plantacion.plantatioId));
                                    } else {
                                        Swal.fire({
                                            icon: 'error',
                                            title: 'Error',
                                            text: 'No se pudo cargar la información de la plantación para editar'
                                        });
                                    }
                                });
                                collapseBody.append(cardPlantacion);
                            });
                        }
                        else {
                            statusDiv.html('').append('<span class="text-body-tertiary">Sin plantaciones</span>');
                            statusDiv.removeAttr('class').addClass('btn border-secondary px-2 py-0 me-5');
                        }



                        accordionItem.find('.edit-plot').on('click', function (e) {
                            if (plot) {
                                $('#plotIdInputEdit').val(plot.plotId);
                                $('#nombrePlotInputEdit').val(plot.name);
                                $('#descripcionPlotInputEdit').val(plot.description);
                            } else {
                                Swal.fire({
                                    icon: 'error',
                                    title: 'Error',
                                    text: 'No se pudo cargar la información de la parcela para editar'
                                });
                            }
                        });

                        accordionItem.find('.btn-agregar-plantacion-parcela').on('click', function (e) {
                            $('#plotIdInputPlantation').val(plot.plotId);
                        });


                        accordion.append(accordionItem);

                    });

                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: response.message || 'Ocurrió un error inesperado al obtener las parcelas'
                    });
                }
                //
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

    $.fn.getAllPlots(); // Llamada inicial para obtener los usuarios al cargar la página
});