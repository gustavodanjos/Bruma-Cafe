/**
 * Header Experience Fragment — Interactive Controller
 * Handles mobile navigation toggle and scroll shadow indicator for Bruma Café.
 */

class HeaderController {
    private readonly header: HTMLElement | null;
    private readonly container: HTMLElement | null;
    private readonly navGroup: HTMLElement | null;

    constructor() {
        this.header = document.querySelector<HTMLElement>(
            'header.experiencefragment, .experiencefragment, .cmp-experiencefragment--header, header'
        );

        if (!this.header) {
            this.container = null;
            this.navGroup = null;
            return;
        }

        this.container = this.header.querySelector<HTMLElement>('.cmp-container');
        this.navGroup = this.header.querySelector<HTMLElement>('.cmp-navigation__group');

        this.init();
    }

    private init(): void {
        this.setupMobileMenuToggle();
        this.setupScrollShadow();
    }

    private setupMobileMenuToggle(): void {
        if (!this.header || !this.container || !this.navGroup) {
            return;
        }

        // Evita inserção duplicada do botão hambúrguer
        if (this.header.querySelector('.cmp-navigation__toggle')) {
            return;
        }

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
            this.navGroup?.classList.toggle('is-open', isOpen);
            toggleBtn.setAttribute('aria-expanded', String(isOpen));
        });

        this.container.appendChild(toggleBtn);
    }

    private setupScrollShadow(): void {
        if (!this.header) {
            return;
        }

        const handleScroll = (): void => {
            if (window.scrollY > 10) {
                this.header?.classList.add('is-scrolled');
            } else {
                this.header?.classList.remove('is-scrolled');
            }
        };

        window.addEventListener('scroll', handleScroll, { passive: true });
        handleScroll();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new HeaderController();
});
