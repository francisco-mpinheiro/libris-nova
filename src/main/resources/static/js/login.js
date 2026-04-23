  document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const senha = document.getElementById('senha').value;

            try {
                const response = await fetch('/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, senha })
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('token', data.token); // Salva o token!
                    window.location.href = '/'; // Vai para a Home
                } else {
                    document.getElementById('error-msg').style.display = 'block';
                }
            } catch (error) {
                console.error('Erro:', error);
                document.getElementById('error-msg').style.display = 'block';
            }
        });