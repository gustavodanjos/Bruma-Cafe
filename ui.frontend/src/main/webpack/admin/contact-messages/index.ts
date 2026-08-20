import "./_contact-messages.scss";

var allMessages = [];
var currentFilter = 'UNREAD';

function loadMessages() {
    var container = document.getElementById('messagesList');
    if (!container) return;
    container.innerHTML = '<div class="loading-state"><coral-wait size="M" centered></coral-wait> Carregando mensagens...</div>';
    
    fetch('/bin/brumacafe/contact/list', {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
    .then(function(response) {
        if (!response.ok) {
            throw new Error('Falha ao buscar mensagens');
        }
        return response.json();
    })
    .then(function(data) {
        allMessages = data || [];
        renderList();
    })
    .catch(function(error) {
        console.error(error);
        var container = document.getElementById('messagesList');
        if (container) {
            container.innerHTML = '<div class="empty-state" style="color:var(--bruma-danger);">Erro ao carregar mensagens. Verifique sua conexão ou permissões.</div>';
        }
    });
}

function renderList() {
    var container = document.getElementById('messagesList');
    if (!container) return;

    var filtered = allMessages.filter(function(msg) {
        return msg.status === currentFilter;
    });

    if (filtered.length === 0) {
        container.innerHTML = '<div class="empty-state">Nenhuma mensagem encontrada nesta caixa.</div>';
        return;
    }
    
    container.innerHTML = '';
    filtered.forEach(function(msg) {
        var id = escapeHtml(msg.id);
        var isUnread = msg.status === 'UNREAD';
        
        var safeMessage = escapeHtml(msg.message).replace(/\n/g, '<br>');
        var rawMessageText = escapeHtml(msg.message).replace(/\n/g, ' ');
        var snippet = rawMessageText.length > 80 ? rawMessageText.substring(0, 80) + '...' : rawMessageText;
        
        var actionsHtml = getActionsHtml(msg);
        var quickActionsHtml = getQuickActionsHtml(msg);
        
        var msgHtml = 
            '<div class="bruma-msg-item ' + (isUnread ? 'bruma-msg-unread' : '') + '">' +
                '<div class="bruma-msg-header" onclick="toggleMessage(\'' + id + '\')">' +
                    '<div class="bruma-msg-sender">' + escapeHtml(msg.name) + '</div>' +
                    '<div class="bruma-msg-subject-snippet">' +
                        '<span class="bruma-msg-subject">' + escapeHtml(msg.subject) + '</span>' +
                        '<span class="bruma-msg-snippet"> - ' + snippet + '</span>' +
                    '</div>' +
                    '<div class="bruma-msg-date-actions">' +
                        '<div class="bruma-msg-date">' + escapeHtml(msg.date) + '</div>' +
                        '<div class="bruma-msg-quick-actions">' + quickActionsHtml + '</div>' +
                    '</div>' +
                '</div>' +
                '<div class="bruma-msg-body" id="msg-body-' + id + '" style="display: none;">' +
                    '<div class="bruma-msg-content-details">' +
                        '<p><strong>De:</strong> ' + escapeHtml(msg.name) + ' &lt;<a href="mailto:' + escapeHtml(msg.email) + '">' + escapeHtml(msg.email) + '</a>&gt;</p>' +
                        '<p><strong>Data:</strong> ' + escapeHtml(msg.date) + '</p>' +
                        '<p><strong>Assunto:</strong> ' + escapeHtml(msg.subject) + '</p>' +
                        '<hr class="bruma-msg-divider">' +
                        '<p class="bruma-msg-text">' + safeMessage + '</p>' +
                    '</div>' +
                    '<div class="bruma-msg-actions">' + actionsHtml + '</div>' +
                '</div>' +
            '</div>';
            
        container.insertAdjacentHTML('beforeend', msgHtml);
    });
}

function toggleMessage(id) {
    var body = document.getElementById('msg-body-' + id);
    if (body) {
        if (body.style.display === 'none' || body.style.display === '') {
            body.style.display = 'block';
        } else {
            body.style.display = 'none';
        }
    }
}

function getActionsHtml(msg) {
    var id = escapeHtml(msg.id);
    if (msg.status === 'TRASHED') {
        return '<button class="action-btn" onclick="performAction(event, \'' + id + '\', \'RECOVER\')">Recuperar</button>' +
               '<button class="action-btn action-btn--danger" onclick="performAction(event, \'' + id + '\', \'HARD_DELETE\')">Excluir Permanente</button>';
    } else {
        var readAction = msg.status === 'UNREAD' 
            ? '<button class="action-btn" onclick="performAction(event, \'' + id + '\', \'MARK_READ\')">Marcar Lida</button>'
            : '<button class="action-btn" onclick="performAction(event, \'' + id + '\', \'MARK_UNREAD\')">Marcar Ñ Lida</button>';
            
        return readAction + 
               '<button class="action-btn action-btn--danger" onclick="performAction(event, \'' + id + '\', \'TRASH\')">Lixeira</button>';
    }
}

function getQuickActionsHtml(msg) {
    var id = escapeHtml(msg.id);
    
    // SVGs Inline
    var iconTrash = '<svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>';
    var iconRead = '<svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M21.2 8.4c.5.3.8.8.8 1.4v10.2a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9.8c0-.6.3-1.1.8-1.4l8-5.3c.7-.5 1.7-.5 2.4 0l8 5.3z"></path><polyline points="22,10 12,16.7 2,10"></polyline></svg>';
    var iconUnread = '<svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>';
    var iconRecover = '<svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><path d="M3 7v6h6"></path><path d="M21 17a9 9 0 0 0-9-9 9 9 0 0 0-6 2.3L3 13"></path></svg>';
    var iconDeletePerm = '<svg viewBox="0 0 24 24" width="18" height="18" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>';

    if (msg.status === 'TRASHED') {
        return '<button class="quick-action-btn" title="Recuperar mensagem" onclick="performAction(event, \'' + id + '\', \'RECOVER\')">' + iconRecover + '</button>' +
               '<button class="quick-action-btn quick-action-btn--danger" title="Excluir Permanentemente" onclick="performAction(event, \'' + id + '\', \'HARD_DELETE\')">' + iconDeletePerm + '</button>';
    } else {
        var readAction = msg.status === 'UNREAD' 
            ? '<button class="quick-action-btn" title="Marcar como Lida" onclick="performAction(event, \'' + id + '\', \'MARK_READ\')">' + iconRead + '</button>'
            : '<button class="quick-action-btn" title="Marcar como Não Lida" onclick="performAction(event, \'' + id + '\', \'MARK_UNREAD\')">' + iconUnread + '</button>';
            
        return readAction + 
               '<button class="quick-action-btn" title="Mandar para lixeira" onclick="performAction(event, \'' + id + '\', \'TRASH\')">' + iconTrash + '</button>';
    }
}

function performAction(event, id, action) {
    if (event) {
        event.stopPropagation();
    }
    
    fetch('/libs/granite/csrf/token.json')
    .then(function(res) {
        if (res.ok) return res.json();
        return { token: '' };
    })
    .then(function(data) {
        var headers = {
            'Content-Type': 'application/x-www-form-urlencoded'
        };
        if (data.token) {
            headers['CSRF-Token'] = data.token;
        }

        return fetch('/bin/brumacafe/contact/action', {
            method: 'POST',
            headers: headers,
            body: 'id=' + encodeURIComponent(id) + '&action=' + encodeURIComponent(action)
        });
    })
    .then(function(response) {
        if (!response.ok) throw new Error('Ação falhou');
        loadMessages();
    })
    .catch(function(error) {
        console.error(error);
        alert('Ocorreu um erro ao executar a ação. Verifique as permissões.');
    });
}

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}

document.addEventListener('DOMContentLoaded', function() {
    loadMessages();
    
    var tabList = document.getElementById('brumaTabList');
    if (tabList) {
        tabList.addEventListener('coral-tablist:change', function(e) {
            var selectedTab = (e as any).detail.selection;
            if (selectedTab) {
                currentFilter = selectedTab.getAttribute('data-filter') || 'UNREAD';
                renderList();
            }
        });
    }
});


// Expose functions to global scope for inline HTML handlers
(window as any).toggleMessage = toggleMessage;
(window as any).performAction = performAction;
(window as any).loadMessages = loadMessages;
