
const disenioStages = {
    0: { classText: 'text-secondary', classBorder: 'border-secondary' },
    1: { classText: 'text-warning', classBorder: 'border-warning' },
    2: { classText: 'text-info', classBorder: 'border-info' },
    3: { classText: 'text-success', classBorder: 'border-success' },
    4: { classText: 'text-dark', classBorder: 'border-dark' }
};

$.fn.formatDate = function (dateString) {
    if (!dateString || dateString === 'Sin fecha' || dateString === 'N/A') {
        return dateString;
    }
    const [year, month, day] = dateString.split("-");
    const localDate = new Date(year, month - 1, day);
    const monthName = localDate.toLocaleString('es-ES', { month: 'short' });

    return `${day}/${monthName}/${year}`;
}

$.fn.getActiveTab = function () {
    const activeTabLink = document.querySelector('.nav-link.active');
    if (activeTabLink) {
        const href = activeTabLink.getAttribute('href');
        return href.replace('#', '');
    }
    return 'plot';
}

$.fn.getStagesByPlantation = function (plantationId) {
    $.ajax({
        url: `/agrosys/plots/plantation/${plantationId}/stages`,
        type: 'GET',
        success: function (response) {

            if (response.success) {
                $('#btnAddStateToPlantation').removeAttr('disabled').removeAttr('title');
                const stages = response.data;
                const stagesSize = stages.length;
                $('#nombreStateInput').val(stagesSize + 1);
                $('#nombreStateInput').prop('disabled', true);

                if ((stagesSize + 1) === 4) {
                    $('label[for="fechaInicioStateInput"]').text('Fecha de terminación');
                    $('#fechaFinalizacionStateInput').parent().hide();
                }
                else {
                    $('label[for="fechaInicioStateInput"]').text('Fecha de inicio');
                    $('#fechaFinalizacionStateInput').parent().show();
                }
                if (stagesSize > 0) {
                    if (stagesSize === 4) {
                        $('#btnAddStateToPlantation').attr('disabled', 'disabled').attr('title', 'No se pueden agregar más etapas a esta plantación');
                        // $('#btnAddStateToPlantation').off('click'); Desvincula cualquier evento click previamente asociado al botón
                        $('#plantationDetailsEndAt').text($.fn.formatDate(stages[stagesSize - 1].end_at));
                    }

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
                                        ${stage.id !== 4
                                ?
                                `<p class="card-text p-0 m-0">Fecha de inicio: <span class="text-capitalize">${$.fn.formatDate(stage.start_at)}</span></p>
                                                                <p class="card-text p-0 m-0">Fecha de finalización: <span class="text-capitalize">${stage.end_at !== 'Sin fecha' ? $.fn.formatDate(stage.end_at) : stage.end_at}</span></p>`
                                :
                                `<p class="card-text p-0 m-0">Fecha de terminación: <span class="text-capitalize">${$.fn.formatDate(stage.start_at)}</span></p>`
                            }
                                        <div class="d-flex flex-row justify-content-end align-items-center gap-4">
                                            
                                            <div class="dropdown position-static">
                                                <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${stage.id}" data-bs-toggle="dropdown" aria-expanded="false">
                                                </i>
                                                <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${stage.id}">
                                                <li><a class="dropdown-item details-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalDetailsPlantationStage" href="#" data-bs-dismiss="modal"><i
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

                        $cardStage.find('.details-plantation-stage').on('click', function () {
                            $('#stageDetailsPlantationName').text($('#plantationDetailsName').text());
                            $('#stageNameDetails').text(stage.name);
                            $('#stageStartDateDetails').text($.fn.formatDate(stage.start_at));
                            $('#stageEndDateDetails').text($.fn.formatDate(stage.end_at));
                            $('#stageNotesDetails').text(stage.notes ? stage.notes : 'Sin notas');

                            if (stage.id !== 4) {
                                $('#stageStartDateDetails').parent().show();
                            }
                            else {
                                $('#stageStartDateDetails').parent().hide();
                            }
                        });

                        $cardStage.find('.edit-plantation-stage').on('click', function () {
                            const stageId = stage.id;

                            $('#plantationIdInputStateEdit').val(plantationId);
                            $('#nombreStateInputEdit').val(stageId);
                            $('#fechaInicioStateInputEdit').val(stage.start_at);
                            if (stage.id === 4) {
                                $('label[for="fechaInicioStateInputEdit"]').text('Fecha de terminación');
                                $('#fechaFinalizacionStateInputEdit').parent().hide();
                                $('#notasStateInputEdit').parent().hide();
                                $('#fechaFinalizacionStateInputEdit').val(stage.start_at);
                            }
                            else {
                                $('label[for="fechaInicioStateInputEdit"]').text('Fecha de inicio');
                                $('#fechaFinalizacionStateInputEdit').parent().show();
                                $('#notasStateInputEdit').parent().show();
                                $('#fechaFinalizacionStateInputEdit').val(stage.end_at);
                            }
                            $('#notasStateInputEdit').val(stage.notes);
                        });

                        $cardStage.find('.delete-plantation-stage').on('click', function () {
                            const stageId = stage.id;
                            $('#textDeletePlantationStage').text(`¿Está seguro de que desea eliminar la etapa ${stage.name.toUpperCase()} de la plantación "${$(`#plantationDetailsName`).text()}"?`);
                            $('#plantationIdInputStateDelete').val(plantationId);
                            $('#stageIdInputStateDelete').val(stageId);
                        });


                        $containerStages.append($cardStage);
                    });
                } else {
                    $('#plantationDetailsCurrentState').text('Sin etapa').removeAttr('class').addClass(disenioStages[0].classText + ' btn px-3 py-0 ms-4 ' + disenioStages[0].classBorder).css('pointer-events', 'none');
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

};

$.fn.getLotsByPlantation = function (plantationId) {
    $.ajax({
        url: `/agrosys/plots/lots/by-plantation/${plantationId}`,
        type: 'GET',
        success: function (response) {

            console.log(response);
            if (response.success) {

                // $('#btnAddStateToPlantation').removeAttr('disabled').removeAttr('title');
                // const stages = response.data;
                // const stagesSize = stages.length;
                // $('#nombreStateInput').val(stagesSize + 1);
                // $('#nombreStateInput').prop('disabled', true);

                // if ((stagesSize + 1) === 4) {
                //     $('label[for="fechaInicioStateInput"]').text('Fecha de terminación');
                //     $('#fechaFinalizacionStateInput').parent().hide();
                // }
                // else {
                //     $('label[for="fechaInicioStateInput"]').text('Fecha de inicio');
                //     $('#fechaFinalizacionStateInput').parent().show();
                // }
                // if (stagesSize > 0) {
                //     if (stagesSize === 4) {
                //         $('#btnAddStateToPlantation').attr('disabled', 'disabled').attr('title', 'No se pueden agregar más etapas a esta plantación');
                //         // $('#btnAddStateToPlantation').off('click'); Desvincula cualquier evento click previamente asociado al botón
                //         $('#plantationDetailsEndAt').text($.fn.formatDate(stages[stagesSize - 1].end_at));
                //     }

                //     $('#plantationDetailsCurrentState').text(stages[stagesSize - 1].name).removeAttr('class').addClass(disenioStages[stagesSize].classText + ' btn px-3 py-0 ms-4 ' + disenioStages[stagesSize].classBorder).css('pointer-events', 'none');

                //     const $containerStages = $('#containerEstados');
                //     $containerStages.html('');

                //     stages.forEach(stage => {
                //         let display = stage.id === stagesSize ? 'block' : 'none';
                //         const $cardStage = $(`
                //                 <div class="card mb-2" id="card-stage${stage.id}-p${plantationId}">
                //                     <div class="card-body d-flex flex-row justify-content-between align-items-center gap-3 overflow-auto">
                //                         <div class="${disenioStages[stage.id].classBorder} btn px-1 py-0"
                //                             style="pointer-events: none; width: 125px;">
                //                             ${`<span class="${disenioStages[stage.id].classText}">${stage.name}</span>`}
                //                         </div>
                //                         ${stage.id !== 4
                //                 ?
                //                 `<p class="card-text p-0 m-0">Fecha de inicio: <span class="text-capitalize">${$.fn.formatDate(stage.start_at)}</span></p>
                //                                                 <p class="card-text p-0 m-0">Fecha de finalización: <span class="text-capitalize">${stage.end_at !== 'Sin fecha' ? $.fn.formatDate(stage.end_at) : stage.end_at}</span></p>`
                //                 :
                //                 `<p class="card-text p-0 m-0">Fecha de terminación: <span class="text-capitalize">${$.fn.formatDate(stage.start_at)}</span></p>`
                //             }
                //                         <div class="d-flex flex-row justify-content-end align-items-center gap-4">

                //                             <div class="dropdown position-static">
                //                                 <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${stage.id}" data-bs-toggle="dropdown" aria-expanded="false">
                //                                 </i>
                //                                 <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${stage.id}">
                //                                 <li><a class="dropdown-item details-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalDetailsPlantationStage" href="#" data-bs-dismiss="modal"><i
                //                                                 class="ri-eye-line pe-1"></i>Ver detalles</a></li>
                //                                     <li style="display: ${display};"><a class="dropdown-item edit-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalEditPlantationStage" href="#" data-bs-dismiss="modal"><i
                //                                                 class="ri-pencil-fill pe-1"></i>Editar</a></li>
                //                                     <li style="display: ${display};"><a class="dropdown-item delete-plantation-stage" data-bs-toggle="modal" data-bs-target="#modalDeletePlantationStage" href="#" data-bs-dismiss="modal"><i
                //                                                 class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                //                                 </ul>
                //                             </div>
                //                         </div>
                //                     </div>
                //                 </div>
                //             `);

                //         $cardStage.find('.details-plantation-stage').on('click', function () {
                //             $('#stageDetailsPlantationName').text($('#plantationDetailsName').text());
                //             $('#stageNameDetails').text(stage.name);
                //             $('#stageStartDateDetails').text($.fn.formatDate(stage.start_at));
                //             $('#stageEndDateDetails').text($.fn.formatDate(stage.end_at));
                //             $('#stageNotesDetails').text(stage.notes ? stage.notes : 'Sin notas');

                //             if (stage.id !== 4) {
                //                 $('#stageStartDateDetails').parent().show();
                //             }
                //             else {
                //                 $('#stageStartDateDetails').parent().hide();
                //             }
                //         });

                //         $cardStage.find('.edit-plantation-stage').on('click', function () {
                //             const stageId = stage.id;

                //             $('#plantationIdInputStateEdit').val(plantationId);
                //             $('#nombreStateInputEdit').val(stageId);
                //             $('#fechaInicioStateInputEdit').val(stage.start_at);
                //             if (stage.id === 4) {
                //                 $('label[for="fechaInicioStateInputEdit"]').text('Fecha de terminación');
                //                 $('#fechaFinalizacionStateInputEdit').parent().hide();
                //                 $('#notasStateInputEdit').parent().hide();
                //                 $('#fechaFinalizacionStateInputEdit').val(stage.start_at);
                //             }
                //             else {
                //                 $('label[for="fechaInicioStateInputEdit"]').text('Fecha de inicio');
                //                 $('#fechaFinalizacionStateInputEdit').parent().show();
                //                 $('#notasStateInputEdit').parent().show();
                //                 $('#fechaFinalizacionStateInputEdit').val(stage.end_at);
                //             }
                //             $('#notasStateInputEdit').val(stage.notes);
                //         });

                //         $cardStage.find('.delete-plantation-stage').on('click', function () {
                //             const stageId = stage.id;
                //             $('#textDeletePlantationStage').text(`¿Está seguro de que desea eliminar la etapa ${stage.name.toUpperCase()} de la plantación "${$(`#plantationDetailsName`).text()}"?`);
                //             $('#plantationIdInputStateDelete').val(plantationId);
                //             $('#stageIdInputStateDelete').val(stageId);
                //         });


                //         $containerStages.append($cardStage);
                //     });
                // } else {
                //     $('#plantationDetailsCurrentState').text('Sin etapa').removeAttr('class').addClass(disenioStages[0].classText + ' btn px-3 py-0 ms-4 ' + disenioStages[0].classBorder).css('pointer-events', 'none');
                //     $('#containerEstados').html('<div class="alert border-primary text-body-tertiary text-center" role="alert" style="background: #e9e9e9a6;">No hay etapas registradas para esta plantación.</div>');
                // }
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

};

let today = new Date();
let endDate = today.toISOString().split('T')[0];
$('#filterPlantationStartDate').val(new Date(today.getFullYear(), today.getMonth(), 1).toISOString().split('T')[0]);
$('#filterPlantationEndDate').val(endDate);

$.fn.getAllPlantations = function () {
    $container = $('#containerPlantations');
    $container.html('').append('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-5 text" style="height: 100px;">Cargando información...</div>')
    $.ajax({
        url: `/agrosys/plots/plantations/get-all?initDate=${$('#filterPlantationStartDate').val()}&endDate=${$('#filterPlantationEndDate').val()}`,
        type: 'GET',
        success: function (response) {

            console.log(response);
            if (response.success) {

                const plantations = response.data;
                if (!plantations) {
                    $container.append('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-5 text" style="height: 100px;">Sin información...</div>')
                    return;
                }

                $container.html('');

                plantations.forEach(plantation => {
                    const $card = $(`
                                <div class="card mb-2">
                                    <div class="card-body d-flex flex-row justify-content-between align-items-center gap-3 overflow-auto">
                                        <p class="card-title text-capitalize p-0 m-0 w-25">${plantation.name.toLowerCase()}</p>
                                        <p class="card-text p-0 m-0">Fecha de inicio: <span class="text-capitalize">${$.fn.formatDate(plantation.startAt)}</span></p>
                                        <p class="card-text p-0 m-0">Fecha de finalización: <span class="text-capitalize">${plantation.endAt !== 'N/A' ? $.fn.formatDate(plantation.endAt) : plantation.endAt}</span></p>
                                        <div class="d-flex flex-row justify-content-end align-items-center gap-4">
                                            <div class="${plantation.stageId === null ? disenioStages[0].classBorder : disenioStages[plantation.stageId].classBorder} btn px-1 py-0" id="status-plantation-${plantation.plantacioId}"
                                                style="pointer-events: none; width: 125px;">
                                                ${plantation.stageId === null ? `<span class="${disenioStages[0].classText}">Sin etapa</span>` : `<span class="${disenioStages[plantation.stageId].classText}">${plantation.stage}</span>`}
                                            </div>
                                            <div class="dropdown position-static">
                                                <i class="ri-more-2-fill" type="button" id="dpBtn-plantation-${plantation.plotId}" data-bs-toggle="dropdown" aria-expanded="false">
                                                </i>
                                                <ul class="dropdown-menu" aria-labelledby="dpBtn-plantation-${plantation.plotId}">
                                                    <li><a class="dropdown-item details-plantation" data-bs-toggle="modal" data-bs-target="#modalDetailsPlantation" href="#"><i
                                                                class="ri-eye-line pe-1"></i>Ver detalles</a></li>
                                                    <li><a class="dropdown-item lots-plantation" data-bs-toggle="modal" data-bs-target="#modalLotsPlantation" href="#"><i
                                                                class="ri-mist-line pe-1"></i>Ver lotes</a></li>
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

                    $card.find('.edit-plantation').on('click', function (e) {
                        if (plot) {
                            $('#plotIdInputPlantationEdit').val(plantation.plotId);
                            $('#plantationIdInputEdit').val(plantation.plantatioId);
                            $('#nombrePlantationInputEdit').val(plantation.name);
                            // $('#fechaInicioPlantationInputEdit').val(plantacion.start_at);
                            // $('#fechaFinalizacionPlantationInputEdit').val(plantacion.end_at);
                            $('#notasPlantationInputEdit').val(plantation.notas);
                        } else {
                            $.fn.errorAlert('No se pudo cargar la información de la plantación para editar');
                        }
                    });

                    $card.find('.delete-plantation').on('click', function () {
                        if (plantation) {
                            $('#textDeletePlantation').text(`¿Está seguro de que desea eliminar la plantacion ${plantation.name} ${plantation.startAt}?`);
                            $('#plantationIdInputDelete').val(plantation.plantatioId);
                        } else {
                            $.fn.errorAlert('No se pudo cargar la información de la plantación para eliminar');
                        }
                    });

                    $card.find('.details-plantation').on('click', function () {
                        if (plantation) {
                            $('#plantationIdInputDetails').val(plantation.plantatioId);
                            $('#plantationDetailsName').text(`${plantation.name}`);
                            // $('#plantationDetailsOriginPlot').text(`${plot.name}`); FALTA NOMBRE DEL PLOT
                            $('#plantationDetailsStartAt').text($.fn.formatDate(plantation.startAt));
                            $('#plantationDetailsEndAt').text(plantation.endAt !== 'N/A' ? $.fn.formatDate(plantation.endAt) : plantation.endAt);
                            $('#plantationDetailsNotes').text(plantation.notas);
                            $('#plantationIdInputState').val(plantation.plantatioId);
                            $('#plantationIdInputStateEdit').val(plantation.plantatioId);
                            $('#plantationIdInputStateDelete').val(plantation.plantatioId);
                            $.fn.getStagesByPlantation(parseInt(plantation.plantatioId));
                        } else {
                            $.fn.errorAlert('No se pudo cargar la información de la plantación para ver detalles');
                        }
                    });

                    $card.find('.lots-plantation').on('click', function () {
                        if (plantation) {
                            // $('#plantationIdInputDetails').val(plantacion.plantatioId);
                            $('#plantationLotName').text(`${plantation.name}`);
                            // $('#plantationLotOriginPlot').text(`${plot.name}`); 
                            $('#plantationLotsNotes').text(plantation.notas);
                            $('#plantationIdInputState').val(plantation.plantatioId);
                            $.fn.getLotsByPlantation(parseInt(plantation.plantatioId));
                        }
                    });

                    $container.append($card);
                });

            } else {
                $container.append('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-5 text" style="height: 100px;">Sin información...</div>')
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado al obtener las etapas de la plantación'
                });
            }
        },
        error: function (xhr, status, error) {
            $container.append('<div class="d-flex justify-content-center align-items-center text-body-tertiary fs-5 text" style="height: 100px;">Error...</div>')

            console.error('Error en la solicitud AJAX:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Ocurrió un error al comunicarse con el servidor'
            });
        }
    });
};

$('#tabPlantations').on('click', function (e) {
    // aquí ya se activó el tab "Plantaciones"
    $.fn.getAllPlantations();
});

$('#btnFilterPlantations').on('click', function () {
    $.fn.getAllPlantations();
});
