export function initArticleListFilters() {
  const hubs = document.querySelectorAll('.bruma-article-hub');

  hubs.forEach((hub) => {
    const filterButtons = hub.querySelectorAll('.bruma-filter-btn');
    const cards = hub.querySelectorAll('.bruma-article-card');
    const searchInput = hub.querySelector('.bruma-article-search__input');
    const countNumberEl = hub.querySelector('.bruma-article-count__number');
    const emptyFeedback = hub.querySelector('.bruma-article-empty-feedback');

    if (!cards.length) return;

    let currentCategory = 'all';
    let currentSearch = '';

    const applyFilters = () => {
      let visibleCount = 0;

      cards.forEach((card) => {
        const category = (card.getAttribute('data-category') || '').trim().toLowerCase();
        const title = (card.querySelector('.bruma-article-card__title')?.textContent || '').toLowerCase();
        const desc = (card.querySelector('.bruma-article-card__description')?.textContent || '').toLowerCase();

        const matchesCat = currentCategory === 'all' || category === currentCategory;
        const matchesSearch = !currentSearch || title.includes(currentSearch) || desc.includes(currentSearch);

        if (matchesCat && matchesSearch) {
          card.classList.remove('is-hidden');
          visibleCount++;
        } else {
          card.classList.add('is-hidden');
        }
      });

      if (countNumberEl) {
        countNumberEl.textContent = visibleCount;
      }

      if (emptyFeedback) {
        emptyFeedback.style.display = visibleCount === 0 ? 'block' : 'none';
      }
    };

    filterButtons.forEach((button) => {
      button.addEventListener('click', () => {
        filterButtons.forEach((btn) => {
          btn.classList.remove('is-active');
          btn.setAttribute('aria-selected', 'false');
        });

        button.classList.add('is-active');
        button.setAttribute('aria-selected', 'true');

        currentCategory = (button.getAttribute('data-filter') || 'all').trim().toLowerCase();
        applyFilters();
      });
    });

    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        currentSearch = e.target.value.trim().toLowerCase();
        applyFilters();
      });
    }

    applyFilters();
  });
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initArticleListFilters);
} else {
  initArticleListFilters();
}