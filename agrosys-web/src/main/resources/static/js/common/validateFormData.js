$.fn.postFormData = function(form, rutaPost, redirectUrl) {
    // 1. Validación básica de parámetros
    if (!form.length) return;

    let isValid = true;
    const formData = new FormData(); // Usamos FormData para manejar archivos si es necesario

    // 2. Limpiar estados de validación previos
    form.find('.is-invalid').removeClass('is-invalid');

    // 3. Recolectar y validar datos (Usamos el contexto 'this' de cada elemento)
    form.find('input, select, textarea').each(function() {
        const $field = $(this); // El campo actual
        const name = $field.attr('name');
        const value = $field.val();
        const isRequired = $field.prop('required');

        if (name) { // Solo procesar si tiene atributo name
            if (isRequired && (!value || value.trim() === "")) {
                isValid = false;
                $field.addClass('is-invalid');
            } else {
                formData.append(name, value);
            }
        }
    });

    if (!isValid) {
        console.warn('Formulario inválido');
        return; 
    }

    console.log('Datos del formulario:', formData);

    // 4. Ejecución del AJAX
    $.ajax({
        url: rutaPost,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if(response.success || response.status === "OK") { // Adaptar según tu objeto Response
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: response.message || 'Operación realizada correctamente',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    window.location.href = redirectUrl;
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado'
                });
            }
        },
        error: function(xhr) {
            let errorMsg = "Error en el servidor";
            if (xhr.status === 403) errorMsg = "No tienes permisos para esta acción";
            
            Swal.fire({ icon: 'error', title: 'Error', text: errorMsg });
        }
    });
};