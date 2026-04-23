document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const nome = document.getElementById('nome').value;
    const email = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    
    // PERFIL FIXO (Segurança: Usuário não escolhe mais)
    const perfil = 'USUARIO'; 

    try {
        const response = await fetch('/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, senha, perfil })
        });

        if (response.ok) {
            alert('Cadastro realizado com sucesso! 🎉');
            window.location.href = '/login';
        } else {
            const errorDiv = document.getElementById('error-msg');
            errorDiv.classList.remove('hidden');
            errorDiv.innerText = "Erro: Email já cadastrado ou dados inválidos.";
        }
    } catch (error) {
        console.error('Erro:', error);
        const errorDiv = document.getElementById('error-msg');
        errorDiv.classList.remove('hidden');
        errorDiv.innerText = "Erro de conexão com o servidor.";
    }
});