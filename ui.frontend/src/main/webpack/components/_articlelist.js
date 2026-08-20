export function initArticleListFilters() {
  const hubs = document.querySelectorAll('.bruma-article-hub');

  hubs.forEach((hub) => {
    const filterButtons = hub.querySelectorAll('.bruma-filter-btn');
    const cards = hub.querySelectorAll('.bruma-article-card');
    const countNumberEl = hub.querySelector('.bruma-article-count__number');
    const emptyFeedback = hub.querySelector('.bruma-article-empty-feedback');

    if (!filterButtons.length || !cards.length) {
      return;
    }

    const updateCount = (count) => {
      if (countNumberEl) {
        countNumberEl.textContent = count;
      }
    };

    updateCount(cards.length);

    filterButtons.forEach((button) => {
      button.addEventListener('click', () => {
        const selectedFilter = (button.getAttribute('data-filter') || 'all').trim().toLowerCase();

        filterButtons.forEach((btn) => {
          btn.classList.remove('is-active');
          btn.setAttribute('aria-selected', 'false');
        });

        button.classList.add('is-active');
        button.setAttribute('aria-selected', 'true');

        let visibleCount = 0;

        cards.forEach((card) => {
          const cardCategory = (card.getAttribute('data-category') || '').trim().toLowerCase();

          if (selectedFilter === 'all' || cardCategory === selectedFilter) {
            card.classList.remove('is-hidden');
            visibleCount++;
          } else {
            card.classList.add('is-hidden');
          }
        });

        updateCount(visibleCount);

        if (emptyFeedback) {
          emptyFeedback.style.display = visibleCount === 0 ? 'block' : 'none';
        }
      });
    });
  });
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initArticleListFilters);
} else {
  initArticleListFilters();
}