// scripts.js
document.addEventListener('DOMContentLoaded', () => {
    console.log('scripts.js carregado ✅');

    const btn = document.getElementById('toggle-theme');
    const root = document.documentElement; // <html>

    if (!btn) {
        console.warn('Botão #toggle-theme não encontrado no DOM.');
        return;
    }

    btn.setAttribute('type', 'button');

    // aplica preferência salva
    if (localStorage.getItem('theme') === 'dark') {
        root.classList.add('dark-theme');
    }

    const updateIcon = () => {
        if (root.classList.contains('dark-theme')) {
            btn.innerHTML = '<i class="fa-solid fa-sun"></i>';
            btn.title = 'Tema claro';
        } else {
            btn.innerHTML = '<i class="fa-solid fa-moon"></i>';
            btn.title = 'Tema escuro';
        }
    };
    updateIcon();

    btn.addEventListener('click', () => {
        root.classList.toggle('dark-theme');
        const dark = root.classList.contains('dark-theme');
        localStorage.setItem('theme', dark ? 'dark' : 'light');
        updateIcon();
    });
});
