
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
