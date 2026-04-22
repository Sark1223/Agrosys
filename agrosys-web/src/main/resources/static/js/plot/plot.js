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

    function getActiveTab() {
        const activeTabLink = document.querySelector('.nav-link.active');
        if (activeTabLink) {
            const href = activeTabLink.getAttribute('href');
            return href.replace('#', '');
        }
        return 'plot';
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
                // const $containerStages = $('#containerEstados');

                // response.data.forEach( stage => {
                //     const $cardStage = $(`
                //         <div class="card mb-2" id="card-stage${stage.id}-p${plantationId}">
                //             <div class="card-body d-flex flex-row justify-content-between align-items-center gap-3 overflow-auto">
                //                 <div class="${disenioStages[stage.id].classBorder} btn px-1 py-0"
                //                     style="pointer-events: none; width: 125px;">
                //                     ${`<span class="${disenioStages[stage.id].classText}">${stage.name}</span>`}
                //                 </div>
                //                 <p class="card-text p-0 m-0">Fecha de inicio: ${new Date(stage.start_at).toLocaleDateString()}</p>
                //                 <p class="card-text p-0 m-0">Fecha de finalización: ${plantacion.end_at !== 'N/A' ? new Date(stage.end_at).toLocaleDateString() : plantacion.end_at}</p>
                //                 <div class="d-flex flex-row justify-content-end align-items-center gap-4">
                                    
                //                     <div class="dropdown position-static">
                //                         <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${stage.id}" data-bs-toggle="dropdown" aria-expanded="false">
                //                         </i>
                //                         <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${stage.id}">
                //                             <li><a class="dropdown-item edit-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalEditPlantationStage" href="#"><i
                //                                         class="ri-pencil-fill pe-1"></i>Editar</a></li>
                //                             <li><a class="dropdown-item delete-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalDeletePlantationStage" href="#"><i
                //                                         class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                //                         </ul>
                //                     </div>
                //                 </div>
                //             </div>
                //         </div>
                //         `);

                        
                // });
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
                        $('#plotIdInputPlantation').val(plot.plotId);
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
                                                    data-bs-target="#modalEditPlot">Editar Parcela</button>
                                            <button class="btn btn-outline-primary btn-sm py-0 px-3" data-bs-toggle="modal"
                                                data-bs-target="#modalAddPlantation">Agregar Plantación</button>
                                        </div>
                                    </div>
                                </div>
                            </div>`);
                        const plantaciones = plot.plantatios ? JSON.parse(plot.plantatios) : null;
                        console.log('Plantaciones:', plantaciones);

                        const statusDiv = accordionItem.find(`#status-${plot.plotId}`);
                        if (plantaciones[0] !== null) {
                            const collapseBody = accordionItem.find(`#collapseBody-${plot.plotId}`);
                            // plantaciones.sort((a, b) => b.plantacionId - a.plantacionId); // Ordenar por plantación más reciente

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
                                            <p class="card-title text-capitalize p-0 m-0">${plantacion.name.toLowerCase()}</p>
                                            <p class="card-text p-0 m-0">Fecha de inicio: ${new Date(plantacion.start_at).toLocaleDateString()}</p>
                                            <p class="card-text p-0 m-0">Fecha de finalización: ${plantacion.end_at !== 'N/A' ? new Date(plantacion.end_at).toLocaleDateString() : plantacion.end_at}</p>
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
                                                                    class="ri-eye-line pe-1"></i>Detalles</a></li>
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
                                        $('#fechaInicioPlantationInputEdit').val(plantacion.start_at);
                                        $('#fechaFinalizacionPlantationInputEdit').val(plantacion.end_at);
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
                                        $('#plantationDetailsStartAt').text(plantacion.start_at);
                                        $('#plantationDetailsEndAt').text(plantacion.start_at);
                                        $('#plantationDetailsNotes').text(plantacion.notas);
                                        console.log('ID de plantación para detalles:', plantacion.plantatioId);
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