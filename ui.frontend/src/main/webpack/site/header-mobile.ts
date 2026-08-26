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
        this.setupResponsiveLayout();
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

    private setupResponsiveLayout(): void {
        if (!this.header || !this.container || !this.navGroup) {
            return;
        }

        const buttonElement = this.header.querySelector<HTMLElement>('.cmp-button, .button');
        if (!buttonElement) {
            return;
        }

        // Identify the wrapper to move (the button component itself or its grid column wrapper)
        let buttonWrapper: HTMLElement = buttonElement;
        if (buttonElement.parentElement && 
            buttonElement.parentElement !== this.container && 
            buttonElement.parentElement.classList.contains('aem-GridColumn')) {
            buttonWrapper = buttonElement.parentElement;
        }

        let navWrapper: HTMLElement | null = this.header.querySelector<HTMLElement>('.cmp-navigation, .navigation');
        if (navWrapper && 
            navWrapper.parentElement && 
            navWrapper.parentElement !== this.container && 
            navWrapper.parentElement.classList.contains('aem-GridColumn')) {
            navWrapper = navWrapper.parentElement;
        }

        // Create a semantic <li> container for the mobile menu
        let mobileLiWrapper = this.navGroup.querySelector<HTMLElement>('.cmp-navigation__item--button-wrapper');
        if (!mobileLiWrapper) {
            mobileLiWrapper = document.createElement('li');
            mobileLiWrapper.className = 'cmp-navigation__item cmp-navigation__item--button-wrapper';
            mobileLiWrapper.style.width = '100%';
            mobileLiWrapper.style.listStyle = 'none';
        }

        const handleResponsive = (): void => {
            const isMobile = window.innerWidth <= 768;
            if (isMobile) {
                if (buttonWrapper.parentElement !== mobileLiWrapper) {
                    mobileLiWrapper.appendChild(buttonWrapper);
                }
                if (mobileLiWrapper.parentElement !== this.navGroup) {
                    this.navGroup?.appendChild(mobileLiWrapper);
                }
            } else {
                if (buttonWrapper.parentElement !== this.container) {
                    const nextSibling = navWrapper?.nextSibling;
                    const isNextSiblingChild = nextSibling && this.container?.contains(nextSibling) && nextSibling.parentNode === this.container;
                    if (isNextSiblingChild) {
                        this.container?.insertBefore(buttonWrapper, nextSibling);
                    } else {
                        this.container?.appendChild(buttonWrapper);
                    }
                }
                if (mobileLiWrapper.parentElement) {
                    mobileLiWrapper.remove();
                }
            }
        };

        window.addEventListener('resize', handleResponsive);
        handleResponsive();
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
