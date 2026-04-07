$.fn.getAllUsers = function () {

    $.ajax({
        url: '/agrosys/plots/get-all',
        type: 'GET',
        success: function (response) {
            console.log(response);
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

$.fn.getAllUsers(); // Llamada inicial para obtener los usuarios al cargar la página