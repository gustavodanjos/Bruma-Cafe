/**
 * Header Experience Fragment — Interactive behavior
 * Handles mobile navigation toggle and scroll shadow indicator.
 */

document.addEventListener('DOMContentLoaded', () => {
    const header = document.querySelector<HTMLElement>('header.experiencefragment');
    if (!header) {
        return;
    }

    const container = header.querySelector<HTMLElement>('.cmp-container');
    const navGroup = header.querySelector<HTMLElement>('.cmp-navigation__group');

    if (!container || !navGroup) {
        return;
    }

    // Injeta botão hamburger mobile se ainda não existir
    if (!header.querySelector('.cmp-navigation__toggle')) {
        const toggleBtn = document.createElement('button');
        toggleBtn.className = 'cmp-navigation__toggle';
        toggleBtn.type = 'button';
        toggleBtn.setAttribute('aria-label', 'Alternar menu de navegação');
        toggleBtn.setAttribute('aria-expanded', 'false');
        toggleBtn.innerHTML = `
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
                 stroke="currentColor" stroke-width="2" stroke-linecap="round">
                <line x1="3" y1="7" x2="21" y2="7"/>
                <line x1="3" y1="12" x2="21" y2="12"/>
                <line x1="3" y1="17" x2="21" y2="17"/>
            </svg>
        `;

        let isOpen = false;
        toggleBtn.addEventListener('click', () => {
            isOpen = !isOpen;
            navGroup.classList.toggle('is-open', isOpen);
            toggleBtn.setAttribute('aria-expanded', String(isOpen));
        });

        container.appendChild(toggleBtn);
    }

    // Scroll shadow effect
    const handleScroll = () => {
        if (window.scrollY > 10) {
            header.classList.add('is-scrolled');
        } else {
            header.classList.remove('is-scrolled');
        }
    };

    window.addEventListener('scroll', handleScroll, { passive: true });
    handleScroll();
});
