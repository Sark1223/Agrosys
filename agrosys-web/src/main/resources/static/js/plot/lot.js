$(document).ready(function () {

    // ================ CONFIGURACIONES ================

    $('#btn-submit-add-trade-mode').on('click', function () {
        const $f = $('#addTradeModeForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    $.fn.getAllConfigs();
                    $('#btn-close-add-trade-mode').trigger('click');

                }
            });
    });

    $('#btn-submit-edit-trade-mode').on('click', function () {
        if ($("#qtyLotTrade").val() > 0) {
            Swal.fire({
                icon: 'question',
                title: '¿Desea continuar?',
                html: `El Modo de comercio que intenta editar tiene <b>${$("#qtyLotTrade").val()}</b> lotes asociados.`,
                showCancelButton: true,
                confirmButtonColor: "#93c54b",
                cancelButtonColor: "#8e8c84",
                confirmButtonText: "Continuar",
                cancelButtonText: "No, cancelar"
            }).then((result) => {
                if (!result.isConfirmed) {
                    return;
                }
            });
        }

        const $f = $('#editTradeModeForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    $.fn.getAllConfigs();
                    $('#btn-close-edit-trade-mode').trigger('click');

                }
            });
    });

    $('#btn-submit-add-product-unit').on('click', function () {
        const $f = $('#addProductUnitForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    $.fn.getAllConfigs();
                    $('#btn-close-add-product-unit').trigger('click');

                }
            });
    });

    $('#btn-submit-edit-product-unit').on('click', function () {
        if ($("#qtyLotTrade").val() > 0) {
            Swal.fire({
                icon: 'question',
                title: '¿Desea continuar?',
                html: `La unidad de producto que intenta editar tiene <b>${$("#qtyLotTrade").val()}</b> lotes asociados.`,
                showCancelButton: true,
                confirmButtonColor: "#93c54b",
                cancelButtonColor: "#8e8c84",
                confirmButtonText: "Continuar",
                cancelButtonText: "No, cancelar"
            }).then((result) => {
                if (!result.isConfirmed) {
                    return;
                }
            });
        }
        const $f = $('#editProductUnitForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    $.fn.getAllConfigs();
                    $('#btn-close-edit-product-unit').trigger('click');

                }
            });
    });

    $('#btn-submit-add-product-type').on('click', function () {
        const $f = $('#addProductTypeForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    $.fn.getAllConfigs();
                    $('#btn-close-add-product-type').trigger('click');

                }
            });
    });

    $('#btn-submit-edit-product-type').on('click', function () {

        if ($("#qtyLotTrade").val() > 0) {
            Swal.fire({
                icon: 'question',
                title: '¿Desea continuar?',
                html: `El tipo de producto que intenta editar tiene <b>${$("#qtyLotTrade").val()}</b> lotes asociados.`,
                showCancelButton: true,
                confirmButtonColor: "#93c54b",
                cancelButtonColor: "#8e8c84",
                confirmButtonText: "Continuar",
                cancelButtonText: "No, cancelar"
            }).then((result) => {
                if (!result.isConfirmed) {
                    return;
                }
            });
        }

        const $f = $('#editProductTypeForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action'))
            .done(function (response) {
                if (response.success) {
                    $.fn.getAllConfigs();
                    $('#btn-close-edit-product-type').trigger('click');

                }
            });
    });

    // ================ LOTES ================
    $('#btn-submit-add-lot').on('click', function () {
        const $f = $('#addLotForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action')) //, '/agrosys/plots?tab=' + activeTab
            .done(function (response) {
                if (response.success) {
                    $.fn.getLotsByPlantation(parseInt($('#plantationIdInputLots').val()));
                    $('#btn-close-add-lot').trigger('click');
                }
            });
    });

    $('#btn-submit-edit-lot').on('click', function () {
        const $f = $('#editLotForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action')) //, '/agrosys/plots?tab=' + activeTab
            .done(function (response) {
                if (response.success) {
                    $.fn.getLotsByPlantation(parseInt($('#plantationIdInputLots').val()));
                    $('#btn-close-edit-lot').trigger('click');
                }
            });
    });

    $('#btn-submit-delete-lot').on('click', function () {
        const $f = $('#deleteLotForm');
        const activeTab = $.fn.getActiveTab();
        $.fn.postFormData($f, $f.attr('action')) //, '/agrosys/plots?tab=' + activeTab
            .done(function (response) {
                if (response.success) {
                    console.log("$('#plantationIdInputLots').val(): ", $('#plantationIdInputLots').val());
                    $.fn.getLotsByPlantation(parseInt($('#plantationIdInputLots').val()));
                    $('#btn-close-delete-lot').trigger('click');
                }
            });
    });


    $.fn.getAllConfigs = function () {
        $.ajax({
            url: '/agrosys/plots/config/get-all',
            type: 'GET',
            success: function (response) {
                const $containerTradeModes = $('#containerTradeModes');
                const $containerProductUnits = $('#containerProductUnits');
                const $containerProductTypes = $('#containerProductTypes');
                $containerTradeModes.html('');
                $containerProductUnits.html('');
                $containerProductTypes.html('');

                if (response.success) {

                    const data = response.data ? response.data : null;
                    const $message = $('<p class="fs-5 text-body-tertiary text-center w-100">Sin informacion registrada...</p>');
                    if (!data) {
                        $containerTradeModes.append($message);
                        $containerProductUnits.append($message);
                        $containerProductTypes.append($message);

                        return;
                    }

                    const tradeModes = data.filter(d => d.origin === "tm");
                    const productUnits = data.filter(d => d.origin === "pu");
                    const productTypes = data.filter(d => d.origin === "pt");

                    if (tradeModes.length === 0) $containerTradeModes.append($message);
                    if (productUnits.length === 0) $containerProductUnits.append($message);
                    if (productTypes.length === 0) $containerProductTypes.append($message);

                    $('#countTradeMode').text(tradeModes.length);
                    $('#countProductUnit').text(productUnits.length);
                    $('#countProductType').text(productTypes.length);

                    const $selectProductType = $('#productTypeLotInput, #productTypeLotInputEdit');
                    const $selectUnitType = $('#unitTypeLotInput, #unitTypeLotInputEdit');
                    const $selectTradeMode = $('#tradeModeLotInput, #tradeModeLotInputEdit');

                    $selectProductType.empty().append('<option value="" disabled selected>Seleccione un tipo</option>');
                    $selectUnitType.empty().append('<option value="" disabled selected>Seleccione una unidad</option>');
                    $selectTradeMode.empty().append('<option value="" disabled selected>Seleccione un modo de comercio</option>');

                    tradeModes.forEach(tm => {
                        const active = tm.active === 1 ? true : false;
                        if (active)
                            $selectTradeMode.append(`<option value="${tm.id}">${tm.name}</option>`)
                        const $card = $(`                                    
                                <div id="${tm.origin}-${tm.id}"
                                    class="bg-light card col-12 col-lg-3 col-md-4 col-sm-5 col-xxl-2 d-flex flex-row justify-content-between justify-content-md-center p-2 shadow-none">
                                    <div class="${active ? "col-9" : "col-8"}">
                                        <p class="m-0">${tm.name}</p>
                                        <p class="m-0" style="font-size: 11px;">#${tm.origin.toUpperCase()}${tm.id}</p>
                                    </div>
                                    <div class="align-items-start d-flex flex-row">
                                        <div class="badge ${active ? "bg-success" : "bg-secondary"} rounded-pill fw-light"
                                            style="font-size: 12px;">${active ? "activo" : "inactivo"}</div>
                                        <div class="dropdown">
                                            <i class="ri-more-2-fill" type="button" id="options-${tm.origin}-${tm.id}"
                                                data-bs-toggle="dropdown" aria-expanded="false">
                                            </i>
                                            <ul class="dropdown-menu" aria-labelledby="options-${tm.origin}-${tm.id}">
                                                <!-- <li><a class="dropdown-item details-plantation-stage"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#modalDetailsTradeMode" href="#"
                                                        data-bs-dismiss="modal"><i class="ri-eye-line pe-1"></i>Ver
                                                        detalles</a></li>  -->
                                                <li><a class="dropdown-item edit-${tm.origin}"
                                                        data-bs-toggle="modal" data-bs-target="#modalEditTradeMode"
                                                        href="#" data-bs-dismiss="modal"><i
                                                            class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>`);

                        $card.find(`.edit-${tm.origin}`).on('click', function (e) {

                            $('#qtyLotTrade').val(pu.qty);
                            $('#idTradeModeEdit').val(tm.id);
                            $('#nameTradeModeInputEdit').val(tm.name);
                            $('#cBxActiveTradeModeEdit').prop('checked', active);

                        });

                        $containerTradeModes.append($card);
                    });

                    productUnits.forEach(pu => {
                        const active = pu.active === 1 ? true : false;
                        if (active)
                            $selectUnitType.append(`<option value="${pu.id}">${pu.name}</option>`)

                        const $card = $(`                                    
                                <div id="${pu.origin}-${pu.id}"
                                    class="bg-light card col-12 col-lg-3 col-md-4 col-sm-5 col-xxl-2 d-flex flex-row justify-content-between justify-content-md-center p-2 shadow-none">
                                    <div class="${active ? "col-9" : "col-8"}">
                                        <p class="m-0">${pu.name}</p>
                                        <p class="m-0" style="font-size: 11px;">#${pu.origin.toUpperCase()}${pu.id}</p>
                                    </div>
                                    <div class="align-items-start d-flex flex-row">
                                        <div class="badge ${active ? "bg-success" : "bg-secondary"} rounded-pill fw-light"
                                            style="font-size: 12px;">${active ? "activo" : "inactivo"}</div>
                                        <div class="dropdown">
                                            <i class="ri-more-2-fill" type="button" id="options-${pu.origin}-${pu.id}"
                                                data-bs-toggle="dropdown" aria-expanded="false">
                                            </i>
                                            <ul class="dropdown-menu" aria-labelledby="options-${pu.origin}-${pu.id}">
                                                <!-- <li><a class="dropdown-item details-plantation-stage"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#modalDetailsTradeMode" href="#"
                                                        data-bs-dismiss="modal"><i class="ri-eye-line pe-1"></i>Ver
                                                        detalles</a></li>  -->
                                                <li><a class="dropdown-item edit-${pu.origin}"
                                                        data-bs-toggle="modal" data-bs-target="#modalEditProductUnit"
                                                        href="#" data-bs-dismiss="modal"><i
                                                            class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>`);

                        $card.find(`.edit-${pu.origin}`).on('click', function (e) {

                            $('#qtyLotTrade').val(pu.qty);
                            $('#idProductUnitEdit').val(pu.id);
                            $('#nameProductUnitInputEdit').val(pu.name);
                            $('#cBxActiveProductUnitEdit').prop('checked', active);

                        });

                        $containerProductUnits.append($card);
                    });

                    productTypes.forEach(pt => {
                        const active = pt.active === 1 ? true : false;
                        if (active)
                            $selectProductType.append(`<option value="${pt.id}">${pt.name}</option>`)
                        const $card = $(`                                    
                                <div id="${pt.origin}-${pt.id}"
                                    class="bg-light card col-12 col-lg-3 col-md-4 col-sm-5 col-xxl-2 d-flex flex-row justify-content-between justify-content-md-center p-2 shadow-none">
                                    <div class="${active ? "col-9" : "col-8"}">
                                        <p class="m-0">${pt.name}</p>
                                        <p class="m-0" style="font-size: 11px;">#${pt.origin.toUpperCase()}${pt.id}</p>
                                    </div>
                                    <div class="align-items-start d-flex flex-row">
                                        <div class="badge ${active ? "bg-success" : "bg-secondary"} rounded-pill fw-light"
                                            style="font-size: 12px;">${active ? "activo" : "inactivo"}</div>
                                        <div class="dropdown">
                                            <i class="ri-more-2-fill" type="button" id="options-${pt.origin}-${pt.id}"
                                                data-bs-toggle="dropdown" aria-expanded="false">
                                            </i>
                                            <ul class="dropdown-menu" aria-labelledby="options-${pt.origin}-${pt.id}">
                                                <!-- <li><a class="dropdown-item details-plantation-stage"
                                                        data-bs-toggle="modal"
                                                        data-bs-target="#modalDetailsTradeMode" href="#"
                                                        data-bs-dismiss="modal"><i class="ri-eye-line pe-1"></i>Ver
                                                        detalles</a></li>  -->
                                                <li><a class="dropdown-item edit-${pt.origin}"
                                                        data-bs-toggle="modal" data-bs-target="#modalEditProductType"
                                                        href="#" data-bs-dismiss="modal"><i
                                                            class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>`);

                        $card.find(`.edit-${pt.origin}`).on('click', function (e) {

                            $('#qtyLotTrade').val(pu.qty);
                            $('#idProductTypeEdit').val(pt.id);
                            $('#nameProductTypeInputEdit').val(pt.name);
                            $('#cBxActiveProductTypeEdit').prop('checked', active);

                        });

                        $containerProductTypes.append($card);
                    });


                } else {
                    const $message = $('<p class="fs-5 text-body-tertiary text-center w-100">Error en la solicitud, sin información...</p>');
                    $containerTradeModes.append($message);
                    $containerProductUnits.append($message);
                    $containerProductTypes.append($message);
                    $.fn.errorAlert(response.message || 'Ocurrió un error inesperado al obtener las parcelas');
                }
            },
            error: function (xhr, status, error) {
                const $message = $('<p class="fs-5 text-body-tertiary text-center w-100">Error en la solicitud, sin información...</p>');
                $containerTradeModes.append($message);
                $containerProductUnits.append($message);
                $containerProductTypes.append($message);
                $.fn.errorAlert('Ocurrió un error al comunicarse con el servidor');
            }

        });
    };



    // $('#btnAddPlantationLot').on('click', function () {
    // });

    $.fn.getPlantationsSelect();

    $.fn.getAllConfigs();


});