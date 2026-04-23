const API_URL = '/admin/usuarios';

document.addEventListener('DOMContentLoaded', () => {
   
    carregarUsuarios();
    
    const form = document.getElementById('form-usuario');
    if (form) {
        form.addEventListener('submit', salvarUsuario);
    }
});


window.abrirModalNovo = abrirModalNovo;
window.fecharModal = fecharModal;
window.prepararEdicao = prepararEdicao;
window.deletarUsuario = deletarUsuario;


async function carregarUsuarios() {
    try {
    
        const response = await fetch(API_URL);

        if (response.status === 403) {
            console.error("Acesso Negado: Perfil insuficiente.");
            return;
        }

        const usuarios = await response.json();
        const tbody = document.getElementById('tabela-usuarios-body');
        tbody.innerHTML = '';

        usuarios.forEach(user => {
            const iniciais = user.nome.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
            
            const classeBadge = 
                user.perfil === 'ADMIN' ? 'badge-admin' : 
                user.perfil === 'APOIO' ? 'badge-support' : 'badge-student';

            tbody.innerHTML += `
                <tr>
                    <td style="font-weight: 600; color: var(--text-muted);">#${user.id}</td>
                    <td>
                        <div class="user-info">
                            <div class="avatar">${iniciais}</div>
                            <span style="font-weight: 500;">${user.nome}</span>
                        </div>
                    </td>
                    <td>${user.email}</td>
                    <td><span class="badge ${classeBadge}">${user.perfil}</span></td>
                    <td>
                        <div class="actions">
                            <button class="action-btn" onclick="prepararEdicao(${user.id})" title="Editar">
                                <span class="material-symbols-outlined">edit</span>
                            </button>
                            <button class="action-btn delete" onclick="deletarUsuario(${user.id})" title="Excluir">
                                <span class="material-symbols-outlined">delete</span>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });
    } catch (error) {
        console.error("Erro ao carregar usuários:", error);
    }
}


function abrirModalNovo() {
    console.log("Tentando abrir o modal...");
    const modal = document.getElementById('user-modal');
    
    if (modal) {
        document.getElementById('modal-title').innerText = "Novo Usuário";
        document.getElementById('form-usuario').reset();
        document.getElementById('user-id').value = '';
        document.getElementById('user-senha').required = true;
        document.getElementById('senha-help').style.display = 'none';
        
        modal.classList.remove('hidden');
        console.log("Classe 'hidden' removida");
    } else {
        console.error("Elemento 'user-modal' não encontrado no HTML");
    }
}

function fecharModal() {
    const modal = document.getElementById('user-modal');
    if (modal) modal.classList.add('hidden');
}


async function prepararEdicao(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (!response.ok) throw new Error("Usuário não encontrado");

        const user = await response.json();

        document.getElementById('modal-title').innerText = "Editar Usuário";
        document.getElementById('user-id').value = user.id;
        document.getElementById('user-nome').value = user.nome;
        document.getElementById('user-email').value = user.email;
        document.getElementById('user-perfil').value = user.perfil;
        
        document.getElementById('user-senha').value = '';
        document.getElementById('user-senha').required = false;
        document.getElementById('senha-help').style.display = 'block';
        
        const modal = document.getElementById('user-modal');
        if (modal) modal.classList.remove('hidden');
    } catch (error) {
        console.error(error);
        alert("Erro ao carregar dados do usuário.");
    }
}


async function salvarUsuario(e) {
    e.preventDefault();
    const id = document.getElementById('user-id').value;
    
    const dados = {
        nome: document.getElementById('user-nome').value,
        email: document.getElementById('user-email').value,
        perfil: document.getElementById('user-perfil').value
    };

    const senha = document.getElementById('user-senha').value;
    if (senha) {
        dados.senha = senha;
    }

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/${id}` : API_URL;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        if (response.ok) {
            fecharModal();
            document.getElementById('form-usuario').reset();
            carregarUsuarios();
            alert(id ? "Usuário atualizado!" : "Usuário criado!");
        } else {
            const erro = await response.text();
            alert("Erro ao salvar: " + erro);
        }
    } catch (error) {
        alert("Erro de conexão.");
    }
}


async function deletarUsuario(id) {
    if (confirm(`Excluir usuário #${id}?`)) {
        try {
            const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
            if (response.ok) carregarUsuarios();
        } catch (error) {
            alert("Erro de conexão.");
        }
    }


    window.abrirModalNovo = abrirModalNovo;
}