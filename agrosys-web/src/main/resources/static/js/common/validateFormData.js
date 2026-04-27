$.fn.postFormData = function (form, rutaPost, redirectUrl) {
    // 1. Validación básica de parámetros
    if (!form.length) return;

    let isValid = true;
    const formData = new FormData();

    // 2. Limpiar estados de validación previos
    form.find('.is-invalid').removeClass('is-invalid');
    form.find('.invalid-feedback').hide(); // Ocultar mensajes previos

    // 3. Recolectar y validar datos con todas las reglas HTML5
    form.find('input, select, textarea').each(function () {
        const $field = $(this);
        const name = $field.attr('name');
        const value = $field.val();
        const isRequired = $field.prop('required');

        // Obtener reglas de validación
        const minLength = $field.attr('minlength');
        const maxLength = $field.attr('maxlength');
        const pattern = $field.attr('pattern');
        const type = $field.attr('type');
        const min = $field.attr('min');
        const max = $field.attr('max');

        let fieldValid = true;
        let errorMessage = '';

        if (name) {
            // Validación de requerido
            if (isRequired && (!value || value.toString().trim() === "")) {
                fieldValid = false;
                errorMessage = $field.data('required-message') || 'Este campo es requerido';
            }

            // Validación de minlength (solo si el campo tiene valor)
            else if (minLength && value && value.toString().length < parseInt(minLength)) {
                fieldValid = false;
                errorMessage = $field.data('minlength-message') || `Debe tener al menos ${minLength} caracteres`;
            }

            // Validación de maxlength
            else if (maxLength && value && value.toString().length > parseInt(maxLength)) {
                fieldValid = false;
                errorMessage = $field.data('maxlength-message') || `No puede exceder ${maxLength} caracteres`;
            }

            // Validación de patrón (regex)
            else if (pattern && value) {
                const regex = new RegExp(pattern);
                if (!regex.test(value)) {
                    fieldValid = false;
                    errorMessage = $field.data('pattern-message') || 'El formato no es válido';
                }
            }

            // Validación específica por tipo de input
            else if (type === 'email' && value && value.trim() !== "") {
                const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailRegex.test(value)) {
                    fieldValid = false;
                    errorMessage = $field.data('email-message') || 'Ingrese un correo electrónico válido';
                }
            }

            else if (type === 'number' && value) {
                const numValue = parseFloat(value);
                if (isNaN(numValue)) {
                    fieldValid = false;
                    errorMessage = 'Debe ser un número válido';
                } else if (min && numValue < parseFloat(min)) {
                    fieldValid = false;
                    errorMessage = `El valor mínimo es ${min}`;
                } else if (max && numValue > parseFloat(max)) {
                    fieldValid = false;
                    errorMessage = `El valor máximo es ${max}`;
                }
            }

            // Si el campo es válido, agregarlo al FormData
            if (fieldValid) {
                // Para inputs tipo checkbox múltiples o radio buttons
                if ($field.is(':checkbox')) {
                    if ($field.is(':checked')) {
                        formData.append(name, value);
                        console.log(`Checkbox agregado: ${name} = ${value}`);
                    }
                    console.log(`No checkeado: ${name} = ${value}`);

                }
                else if ($field.is(':radio')) {
                    if ($field.is(':checked')) {
                        formData.append(name, value);
                    }
                }
                else if ($field.attr("type") === "file") {
                    if ($field[0].files.length > 0) {
                        formData.append(name, $field[0].files[0]);
                        console.log(`Archivo agregado: ${name} = ${$field[0].files[0].name}`);
                    }
                } else {
                    formData.append(name, value);
                }
            } else {
                console.warn(`Campo inválido: ${name} - ${errorMessage}`);
                isValid = false;
                $field.addClass('is-invalid');

                // Mostrar mensaje de error personalizado
                let $feedback = $field.siblings('.invalid-feedback');
                if ($feedback.length) {
                    $feedback.text(errorMessage).show();
                } else {
                    // Crear el elemento si no existe
                    $field.after(`<div class="invalid-feedback" style="display: block;">${errorMessage}</div>`);
                }
            }
        }
    });


    if (!isValid) {
        // Enfocar el primer campo con error
        form.find('.is-invalid').first().focus();
        console.warn('Formulario inválido');
        return;
    }

    console.log('Datos del formulario:', Object.fromEntries(formData));

    // 4. Ejecución del AJAX
    $.ajax({
        url: rutaPost,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            if (response.success || response.status === "OK") {
                Swal.fire({
                    icon: 'success',
                    title: 'Éxito',
                    text: response.message || 'Operación realizada correctamente',
                    // timer: 2000,
                    showConfirmButton: true
                }).then(() => {
                    if (redirectUrl) {
                        window.location.href = redirectUrl;
                    }
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
            else if (xhr.status === 422) {
                // Manejar errores de validación del servidor (Laravel por ejemplo)
                const response = xhr.responseJSON;
                if (response && response.errors) {
                    errorMsg = Object.values(response.errors).flat().join('\n');
                }
            }

            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: errorMsg,
                toast: true,
                position: 'top-end'
            });
        }
    });
};