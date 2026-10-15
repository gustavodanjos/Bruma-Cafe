document.addEventListener('DOMContentLoaded', function() {
    const cards = document.querySelectorAll('.info-cafe-card[data-cafe-trigger]');
    const panels = document.querySelectorAll('.cafe-detail-panel');
    
    cards.forEach(card => {
        card.addEventListener('click', function(e) {
            const cafeId = this.getAttribute('data-cafe-trigger');
            
            panels.forEach(p => p.style.display = 'none');
            
            if (cafeId) {
                const targetPanel = document.getElementById('detail-' + cafeId);
                if (targetPanel) {
                    targetPanel.style.display = 'block';
                    
                    setTimeout(() => {
                        targetPanel.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                    }, 50);
                }
            }
        });
    });
});
