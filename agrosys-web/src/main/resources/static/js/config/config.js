// Configuración de usuarios
$('#btn-submit-add-user').on('click', function () {
    const $f = $('#addUserForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

$('#btn-submit-edit-user').on('click', function () {
    const $f = $('#editUserForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

$('#btn-submit-edit-password-user').on('click', function () {
    const $f = $('#editPasswordForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

$('#btn-submit-delete-user').on('click', function () {
    const $f = $('#deleteUserForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

//Configuración de roles
$('#btn-submit-add-role').on('click', function () {
    const $f = $('#addRoleForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

$('#btn-submit-edit-role').on('click', function () {
    const $f = $('#editRoleForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

$('#btn-submit-delete-role').on('click', function () {
    const $f = $('#deleteRoleForm');
    const activeTab = getActiveTab();
    $.fn.postFormData($f, $f.attr('action'), '/agrosys/config?tab=' + activeTab);
});

// Función auxiliar para obtener el tab activo actual
function getActiveTab() {
    const activeTabLink = document.querySelector('.nav-link.active');
    if (activeTabLink) {
        const href = activeTabLink.getAttribute('href');
        return href.replace('#', '');
    }
    return 'usuarios';
}

$.fn.getAllUsers = function () {

    $.ajax({
        url: '/agrosys/config/get-users',
        type: 'GET',
        success: function (response) {
            const cardsContainer = $('#cardsContainerUsers');
            cardsContainer.html(''); // Limpiar el contenedor antes de agregar nuevos usuarios

            if (response.success) { // Adaptar según tu objeto Response
                console.log('Usuarios obtenidos:', response.data);

                response.data.forEach(user => {
                    const userCard = $(`
                            <div class="card mb-3 me-2" style="width: 300px;">
                                <div class="card-body d-flex flex-row justify-content-between align-items-start">
                                    <div class="d-flex flex-column">
                                        <h5 class="card-title text-capitalize ">${user.firstName?.toLowerCase()} ${user.lastName?.toLowerCase()}</h5>
                                        <p class="card-text">${user.userName?.toLowerCase()}</p>
                                        <p class="card-text text-capitalize">${user.rolName?.toLowerCase()}</p>
                                    </div>
                                    <div class="dropdown">
                                        <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${user.userId}" data-bs-toggle="dropdown" aria-expanded="false">
                                        </i>
                                        <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${user.userId}">
                                            <li><a class="dropdown-item edit-password-user" data-bs-toggle="modal" data-bs-target="#modalEditPasswordUser" href="#">
                                                <i class="ri-key-fill pe-1"></i>Nueva contraseña</a></li>
                                            <li><a class="dropdown-item edit-user" data-bs-toggle="modal" data-bs-target="#modalEditUser" href="#"><i
                                                        class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                            <li><a class="dropdown-item delete-user" data-bs-toggle="modal" data-bs-target="#modalDeleteUser" href="#"><i
                                                        class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        `);

                    userCard.find('.edit-user').on('click', function () {

                        if (user) {
                            $('#userIdInputEdit').val(user.userId);
                            $('#nombreInputEdit').val(user.firstName);
                            $('#apellidoInputEdit').val(user.lastName);
                            $('#usernameInputEdit').val(user.userName);
                            $('#roleSelectEdit').val(user.rolId);
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'No se pudo cargar la información del usuario para editar'
                            });
                        }
                    });

                    userCard.find('.edit-password-user').on('click', function () {

                        if (user) {
                            $('#userIdInputEditPassword').val(user.userId);
                            $('#usernameInputEditPassword').val(user.userName);
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'No se pudo cargar la información del usuario para editar'
                            });
                        }
                    });

                    userCard.find('.delete-user').on('click', function () {

                        if (user) {
                            $('#textDelete').text(`¿Está seguro de que desea eliminar al usuario ${user.firstName} ${user.lastName}?`);
                            $('#userIdInputDelete').val(user.userId);
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'No se pudo cargar la información del usuario para editar'
                            });
                        }
                    });


                    cardsContainer.append(userCard);
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado al obtener los usuarios'
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

$.fn.getAllRoles = function () {

    $.ajax({
        url: '/agrosys/config/get-roles',
        type: 'GET',
        success: function (response) {
            const cardsContainer = $('#cardsContainerRoles');
            cardsContainer.html(''); // Limpiar el contenedor antes de agregar nuevos usuarios

            if (response.success) { // Adaptar según tu objeto Response
                console.log('Roles obtenidos:', response.data);

                const selects = $('#roleSelect, #roleSelectEdit');
                selects.empty();
                selects.append('<option value="" disabled selected>Seleccione un rol</option>');

                response.data.forEach(role => {
                    selects.append(`<option value="${role.rolId}">${role.name}</option>`);
                    const roleCard = $(`
                            <div class="card mb-3 me-2" style="width: 300px;">
                                <div class="card-body d-flex flex-row justify-content-between align-items-start">
                                    <div class="d-flex flex-column">
                                        <h5 class="card-title text-capitalize ">${role.name?.toLowerCase()}</h5>
                                        <p class="card-text">${role.description?.toLowerCase()}</p>
                                    </div>
                                    <div class="dropdown">
                                        <i class="ri-more-2-fill" type="button" id="dropdownMenuButton${role.id}" data-bs-toggle="dropdown" aria-expanded="false">
                                        </i>
                                        <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton${role.id}">
                                            <li><a class="dropdown-item edit-role" data-bs-toggle="modal" data-bs-target="#modalEditRole" href="#">
                                                <i class="ri-pencil-fill pe-1"></i>Editar</a></li>
                                            <li><a class="dropdown-item delete-role" data-bs-toggle="modal" data-bs-target="#modalDeleteRole" href="#"><i
                                                        class="ri-delete-bin-fill pe-1"></i>Eliminar</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        `);

                    roleCard.find('.edit-role').on('click', function () {

                        if (role) {
                            $('#roleIdInputEdit').val(role.rolId);
                            $('#nombreRolInputEdit').val(role.name);
                            $('#descripcionRolInputEdit').val(role.description);

                            const $modulos = $('#fieldSetModulosEdit');

                            $modulos.find('input[type="checkbox"]').prop('checked', false);

                            const modulosArray = role.modulos ? JSON.parse(role.modulos) : null ;
                            if (modulosArray) {
                                console.log('Módulos del rol:', modulosArray);

                                modulosArray.forEach(modulo => {
                                    console.log('Módulos del rol:', modulo);
                                    $modulos.find(`input[type="checkbox"][value="${modulo}"]`).prop('checked', true);
                                });
                            }
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'No se pudo cargar la información del rol para editar'
                            });
                        }
                    });

                    roleCard.find('.delete-role').on('click', function () {

                        if (role) {
                            $('#textDeleteRol').text(`¿Está seguro de que desea eliminar el rol ${role.name}?`);
                            $('#roleIdInputDelete').val(role.rolId);
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'No se pudo cargar la información del rol para eliminar'
                            });
                        }
                    });

                    cardsContainer.append(roleCard);
                });

            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: response.message || 'Ocurrió un error inesperado al obtener los roles'
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

$.fn.getAllUsers(); // Llamada inicial para obtener los usuarios al cargar la página
$.fn.getAllRoles(); // Llamada inicial para obtener los roles al cargar la página

