document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('brumaContactForm');
    if (!form) return;

    const successBox = document.querySelector('.cmp-contactform__success');
    const errorBox = document.querySelector('.cmp-contactform__message-box--error');
    const submitBtn = document.getElementById('contactSubmitBtn');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        if (errorBox) {
            errorBox.style.display = 'none';
            errorBox.textContent = '';
        }
        submitBtn.disabled = true;
        submitBtn.textContent = 'Enviando...';

        const formData = new FormData(form);
        const actionUrl = form.getAttribute('action');

        try {
            
            let csrfToken = '';
            try {
                const tokenResponse = await fetch('/libs/granite/csrf/token.json');
                if (tokenResponse.ok) {
                    const tokenJson = await tokenResponse.json();
                    csrfToken = tokenJson.token;
                }
            } catch (err) {
                console.warn('Não foi possível obter o CSRF token. Ignorando no modo Publish.', err);
            }

            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded'
            };
            if (csrfToken) {
                headers['CSRF-Token'] = csrfToken;
            }

            const response = await fetch(actionUrl, {
                method: 'POST',
                body: new URLSearchParams(formData),
                headers: headers
            });

            
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.indexOf("application/json") === -1) {
                const htmlError = await response.text();
                console.error('Servidor retornou um erro HTML (Status: ' + response.status + '):', htmlError);
                throw new Error('Erro do servidor. Status: ' + response.status);
            }

            const result = await response.json();

            if (response.ok && result.success) {
                form.style.display = 'none';
                if (successBox) {
                    successBox.style.display = 'block';
                }
            } else {
                if (errorBox) {
                    errorBox.textContent = result.error || 'Ocorreu um erro ao enviar a mensagem.';
                    errorBox.style.display = 'block';
                }
                submitBtn.disabled = false;
                submitBtn.textContent = 'Enviar mensagem';
            }
        } catch (error) {
            console.error('Erro na submissão do formulário:', error);
            if (errorBox) {
                errorBox.textContent = 'Erro de comunicação com o servidor. Verifique o console.';
                errorBox.style.display = 'block';
            }
            submitBtn.disabled = false;
            submitBtn.textContent = 'Enviar mensagem';
        }
    });
});
