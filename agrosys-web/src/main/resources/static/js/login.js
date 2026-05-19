document.addEventListener('DOMContentLoaded', function () {
    const toggleBtn = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const eyeIcon = document.getElementById('eyeIcon');

    if (!toggleBtn || !passwordInput || !eyeIcon) return;

    toggleBtn.addEventListener('click', function () {
        const isHidden = passwordInput.type === 'password';

        passwordInput.type = isHidden ? 'text' : 'password';

        eyeIcon.classList.toggle('ri-eye-line', !isHidden);
        eyeIcon.classList.toggle('ri-eye-off-line', isHidden);

        toggleBtn.setAttribute('aria-label', isHidden ? 'Ocultar contraseña' : 'Mostrar contraseña');
    });
});