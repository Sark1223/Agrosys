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

    function getActiveTab() {
        const activeTabLink = document.querySelector('.nav-link.active');
        if (activeTabLink) {
            const href = activeTabLink.getAttribute('href');
            return href.replace('#', '');
        }
        return 'plot';
    }

    $.fn.getAllPlots = function () {

        $.ajax({
            url: '/agrosys/plots/get-all',
            type: 'GET',
            success: function (response) {
                console.log(response);

                const accordion = $('#accordionContainerPlots');
                accordion.html(''); // Limpiar el contenedor antes de agregar nuevos usuarios
                $("#message-plot").attr("style", "display:none !important;");
                if (response.success) { // Adaptar según tu objeto Response
                    console.log('Parcelas obtenidas:', response.data);

                    response.data.forEach(plot => {
                        // const plotCard = $(`
                        //         <div class="card mb-3 me-2" style="width: 300px;">
                        //             <div class="card-body d-flex flex-row justify-content-between align-items-start">
                        //                 <div class="d-flex flex-column">
                        //                     <h5 class="card-title text-capitalize ">${plot.name.toLowerCase()}</h5>
                        //                     <p class="card-text">${plot.plotName?.toLowerCase()}</p>
                        //                     <p class="card-text text-capitalize">${plot.rolName?.toLowerCase()}</p>
                        //                 </div>
                        //                 <div class="dropdown">
                        //                     <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${plot.plotId}" data-bs-toggle="dropdown" aria-expanded="false">
                        //                     </i>
                        //                     <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${plot.plotId}">
                        //                         <li><a class="dropdown-item edit-plot" data-bs-toggle="modal" data-bs-target="#modalEditPlot" href="#"><i
                        //                                     class="ri-pencil-fill pe-1"></i>Editar</a></li>
                        //                         <li><a class="dropdown-item delete-plot" data-bs-toggle="modal" data-bs-target="#modalDeleteUser" href="#"><i
                        //                                     class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                        //                     </ul>
                        //                 </div>
                        //             </div>
                        //         </div>
                        //     `);


                        const accordionItem = $(`
                            <div class="accordion-item">
                                <h2 class="accordion-header" id="heading-${plot.plotId}">
                                    <div class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse-${plot.plotId}" aria-expanded="true" aria-controls="collapse-${plot.plotId}">
                                        ${plot.name.toLowerCase()}
                                    </div>
                                </h2>
                                <div id="collapse-${plot.plotId}" class="accordion-collapse collapse"
                                    aria-labelledby="heading-${plot.plotId}" data-bs-parent="#accordionContainerPlots" style="">
                                    <div class="accordion-body" id="collapseBody-${plot.plotId}">
                                        ${plot.description?.toLowerCase()} <br>
                                        
                                    </div>
                                </div>
                            </div>`);
                        const plantaciones = plot.plantatios ? JSON.parse(plot.plantatios) : null;
                        console.log('Plantaciones:', plantaciones);

                        if (plantaciones[0] !== null) {
                            const collapseBody = accordionItem.find(`#collapseBody-${plot.plotId}`);
                            plantaciones.forEach(plantacion => {
                                console.log('Plantación:', plantacion);
                                const cardPlantacion = $(`
                                    <div class="card mb-2">
                                        <div class="card-body d-flex flex-row justify-content-between align-items-start">
                                            <p class="card-title text-capitalize">${plantacion.name.toLowerCase()}</p>
                                            <p class="card-text">Fecha de inicio: ${new Date(plantacion.start_at).toLocaleDateString()}</p>
                                            <p class="card-text">Fecha de finalización: ${plantacion.end_at !== 'N/A' ? new Date(plantacion.end_at).toLocaleDateString() : plantacion.end_at}</p>
                                        </div>
                                    </div>
                                `);
                                collapseBody.append(cardPlantacion);
                            });
                        }


                        accordion.append(accordionItem);

                        // plotCard.find('.edit-plot').on('click', function () {

                        //     if (plot) {
                        //         $('#plotIdInputEdit').val(plot.plotId);
                        //         $('#nombrePlotInputEdit').val(plot.name);
                        //         $('#descripcionPlotInputEdit').val(plot.description);
                        //     } else {
                        //         Swal.fire({
                        //             icon: 'error',
                        //             title: 'Error',
                        //             text: 'No se pudo cargar la información de la parcela para editar'
                        //         });
                        //     }
                        // });

                        // plotCard.find('.delete-plot').on('click', function () {

                        //     if (plot) {
                        //         $('#textDelete').text(`¿Está seguro de que desea eliminar la parcela ${plot.firstName} ${plot.lastName}?`);
                        //         $('#plotIdInputDelete').val(plot.plotId);
                        //     } else {
                        //         Swal.fire({
                        //             icon: 'error',
                        //             title: 'Error',
                        //             text: 'No se pudo cargar la información de la parcela para editar'
                        //         });
                        //     }
                        // });

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