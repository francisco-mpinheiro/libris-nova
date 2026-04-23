
let userProfile = "";
let userId = null;


document.addEventListener("DOMContentLoaded", async () => {
    try {

        const response = await fetch("/auth/me");

        if (response.ok) {
            const user = await response.json();

            if (user.perfil) {
                userProfile = String(user.perfil).toUpperCase();
            }
            userId = user.id;

            console.log("Logado como:", user.nome, "| ID:", userId, "| Perfil:", userProfile);

            ajustarBotoesTopo();
            loadBooks();

        } else {

            console.warn("Usuário não autenticado no servidor.");
        }
    } catch (error) {
        console.error("Erro ao carregar dados do usuário:", error);
    }
});

function ajustarBotoesTopo() {
    const btnNovo = document.getElementById("btn-novo-livro");
    const linkAdminUsuarios = document.getElementById("link-admin-usuarios");

    if (userProfile === "ADMIN") {
        if (btnNovo) {
            btnNovo.classList.remove("hidden");
            btnNovo.style.display = "flex";
        }
        if (linkAdminUsuarios) {
            linkAdminUsuarios.classList.remove("hidden");
            linkAdminUsuarios.style.display = "inline-block";
        }
    }


}


async function loadBooks() {
    try {

        const response = await fetch("/livros", {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.status === 403) {
            console.error("Acesso negado aos livros. Verifique se você está logado ou tentando acessar via URL.");
            return;
        }

        if (!response.ok) {
            throw new Error("Erro ao carregar livros");
        }

        const books = await response.json();
        const container = document.getElementById("books-container");
        container.innerHTML = "";

        if (books.length === 0) {
            container.innerHTML = '<p style="grid-column: 1/-1; text-align: center; color: #666;">Nenhum livro encontrado no acervo.</p>';
            return;
        }

        const isAdmin = (userProfile === "ADMIN");
        const isStaff = (userProfile === "ADMIN" || userProfile === "APOIO");

        books.forEach(book => {
            const capaUrl = book.capa && book.capa.trim() !== ""
                ? book.capa
                : "https://via.placeholder.com/200x300/cccccc/666666?text=Sem+Capa";

            let badgeStyle = "background: #eee; color: #333;";
            if (book.status === "DISPONIVEL") badgeStyle = "background: #d4edda; color: #155724;";
            else if (book.status === "SOLICITADO") badgeStyle = "background: #fff3cd; color: #856404;";
            else badgeStyle = "background: #f8d7da; color: #721c24;";

            const badgeHtml = `<span class="status-badge" style="${badgeStyle}">${book.status}</span>`;


            let mainButton = "";

            if (book.status === "DISPONIVEL") {

                mainButton = `<button onclick="solicitarLivro(${book.id})" class="btn-borrow-card">Solicitar</button>`;
            } else if (book.status === "SOLICITADO") {

                mainButton = `<button disabled class="btn-borrow-card" style="background-color: #fff3cd; color: #856404; cursor: not-allowed;">Aguardando Aprovação</button>`;
            } else {

                mainButton = `<button disabled class="btn-borrow-card" style="background-color: #ccc; cursor: not-allowed;">Indisponível</button>`;
            }

        
            let actionButtons = "";
            const capaSafe = book.capa ? book.capa.replace(/'/g, "\\'") : "";

            if (isAdmin) {
               
                actionButtons = `
                    <button onclick="abrirModalEditar(${book.id}, '${book.titulo}', '${book.autor}', '${book.isbn}', ${book.anoPublicacao}, '${capaSafe}')"
                        class="btn-action-small" title="Editar">
                        <span class="material-symbols-outlined" style="font-size: 18px;">edit</span>
                    </button>
                    <button onclick="deleteBook(${book.id})" 
                        class="btn-action-small btn-delete-small" title="Excluir">
                        <span class="material-symbols-outlined" style="font-size: 18px;">delete</span>
                    </button>
                `;
            } else {
                actionButtons = `
                    <button onclick="abrirModalDetalhes('${book.titulo}', '${book.autor}', '${book.isbn}', ${book.anoPublicacao}, '${capaSafe}')"
                        class="btn-action-small" title="Ver Detalhes">
                        <span class="material-symbols-outlined" style="font-size: 18px;">visibility</span>
                    </button>
                `;
            }

            const card = document.createElement("div");
            card.className = "book-card";
            card.innerHTML = `
                <div class="card-image-area">
                    <img src="${capaUrl}" alt="${book.titulo}" onerror="this.src='https://via.placeholder.com/200x300?text=Erro+Imagem'">
                    ${badgeHtml}
                    <div class="card-actions-overlay">
                        ${mainButton}
                        <div style="display: flex; gap: 10px; margin-top: 10px;">
                            ${actionButtons}
                        </div>
                    </div>
                </div>
                <div class="card-info">
                    <h3 title="${book.titulo}">${book.titulo}</h3>
                    <p>${book.autor}</p>
                </div>
            `;
            container.appendChild(card);
        });

    } catch (error) {
        console.error("Erro ao carregar livros:", error);
    }
}

function filtrarLivros() {
    const input = document.getElementById("inputBusca");
    const filtro = input.value.toUpperCase();
    const cards = document.getElementsByClassName("book-card");

    for (let i = 0; i < cards.length; i++) {
        const card = cards[i];
        const titulo = card.querySelector("h3").innerText;
        const autor = card.querySelector("p").innerText;

        if (titulo.toUpperCase().indexOf(filtro) > -1 ||
            autor.toUpperCase().indexOf(filtro) > -1) {
            card.style.display = "";
        } else {
            card.style.display = "none";
        }
    }
}


async function solicitarLivro(livroId) {
    if (!confirm("Deseja solicitar este livro?")) return;


    try {
        const url = `/emprestimos?usuarioId=${userId}&livroId=${livroId}`;

        const response = await fetch(url, {
            method: "POST"
        });

        if (response.ok) {
            alert("Solicitação enviada! Verifique na página de gestão.");
            loadBooks();
        } else {
            const erro = await response.text();
            alert("Erro: " + erro);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
    }
}

async function borrowBook(livroId, idPreDefinido = null) {
    let targetUserId = userId;
    if (userProfile === "ADMIN" || userProfile === "APOIO") {
        if (idPreDefinido) {
            targetUserId = idPreDefinido;
        } else {
            const inputId = prompt("Digite o ID do Usuário para este empréstimo:");
            if (!inputId) return;
            targetUserId = inputId;
        }
    }

    if (!confirm(`Confirmar empréstimo?`)) return;

    try {
        const response = await fetch(`/emprestimos?usuarioId=${targetUserId}&livroId=${livroId}`, { method: "POST" });
        if (response.ok) { alert("Empréstimo realizado! 📖"); loadBooks(); }
        else { alert("Erro ao processar empréstimo."); }
    } catch (error) { alert("Erro de conexão."); }
}


function abrirModal() {
    const modal = document.getElementById("modal-livro");
    modal.classList.remove("hidden");
    document.getElementById("form-livro").reset();
    document.getElementById("livro-id").value = "";
    const headerTitle = modal.querySelector("h3");
    if (headerTitle) headerTitle.innerText = "Novo Livro";
}

function abrirModalEditar(id, titulo, autor, isbn, ano, capa) {
    const modal = document.getElementById("modal-livro");
    modal.classList.remove("hidden");
    document.getElementById("livro-id").value = id;
    document.getElementById("titulo").value = titulo;
    document.getElementById("autor").value = autor;
    document.getElementById("isbn").value = isbn;
    document.getElementById("ano").value = ano;
    document.getElementById("capa").value = capa || "";
    const headerTitle = modal.querySelector("h3");
    if (headerTitle) headerTitle.innerText = "Editar Livro";
}

function fecharModal() {
    document.getElementById("modal-livro").classList.add("hidden");
}

function abrirModalDetalhes(titulo, autor, isbn, ano, capa) {
    const modal = document.getElementById("modal-detalhes");
    modal.classList.remove("hidden");

    document.getElementById("detalhe-titulo").innerText = titulo;
    document.getElementById("detalhe-autor").innerText = autor;
    document.getElementById("detalhe-isbn").innerText = isbn;
    document.getElementById("detalhe-ano").innerText = ano;
    
    const imgCapa = document.getElementById("detalhe-capa");
    imgCapa.src = capa && capa.trim() !== "" ? capa : "https://via.placeholder.com/200x300/cccccc/666666?text=Sem+Capa";
}

function fecharModalDetalhes() {
    document.getElementById("modal-detalhes").classList.add("hidden");
}



const formLivro = document.getElementById("form-livro");
if (formLivro) {
    formLivro.addEventListener("submit", async (e) => {
        e.preventDefault();
        const id = document.getElementById("livro-id").value;
        const livroData = {
            titulo: document.getElementById("titulo").value,
            autor: document.getElementById("autor").value,
            isbn: document.getElementById("isbn").value,
            anoPublicacao: parseInt(document.getElementById("ano").value),
            capa: document.getElementById("capa").value
        };

        let url = "/livros";
        let method = "POST";
        if (id) {
            url = `/livros/${id}`;
            method = "PUT";
        }

        try {
            const response = await fetch(url, {
                method: method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(livroData)
            });

            if (response.ok) {
                alert(id ? "Livro atualizado!" : "Livro cadastrado!");
                fecharModal();
                loadBooks();
            } else {
                alert("Erro ao salvar.");
            }
        } catch (error) { alert("Erro de conexão."); }
    });
}


async function deleteBook(id) {
    if (!confirm("Tem certeza que deseja excluir este livro?")) return;
    const response = await fetch(`/livros/${id}`, { method: "DELETE" });
    if (response.ok) { alert("Excluído com sucesso!"); loadBooks(); }
    else { alert("Erro ao excluir."); }
}


async function carregarMeusEmprestimos() {
    const tbody = document.getElementById("meus-emprestimos-body");
    tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>Carregando...</td></tr>";

    try {
        const response = await fetch("/emprestimos");
        if (response.ok) {
            const data = await response.json();
            tbody.innerHTML = "";
            
            if (data.length === 0) {
                tbody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>Nenhum registro.</td></tr>";
                return;
            }

            data.forEach(emp => {
                let statusTexto = "";
                let btn = "";

                
                switch (emp.status) {
                    case "PENDENTE":
                        statusTexto = "Pendente";
                     
                        btn = `<button class="btn-action-small" style="background: #ccc; color: white; border: none; padding: 15px 40px; border-radius: 8px; font-weight: bold; cursor: not-allowed; font-size: 13px;" disabled>Aguardando</button>`;
                        break;

                    case "ATIVO":
                        statusTexto = "Ativo";
                        
                        btn = `<button class="btn-action-small" style="background: #ff9800; color: white; border: none; padding: 15px 30px; border-radius: 8px; font-weight: bold; cursor: pointer; font-size: 13px;" onclick="devolverLivro(${emp.id})">Devolver</button>`;
                        break;

                    case "AGUARDANDO_CONFIRMACAO":
                        statusTexto = "Aguardando Devolução";
                    
                        btn = `<button class="btn-action-small" style="background: #5dade2; color: white; border: none; padding: 15px 30px; border-radius: 8px; font-weight: bold; cursor: not-allowed; font-size: 13px;" disabled>Em Análise</button>`;
                        break;

                    case "FINALIZADO":
                        statusTexto = "Finalizado";
                        
                        btn = `<span style="color: green; font-weight: bold;">Devolvido</span>`;
                        break;
                }

                tbody.innerHTML += `
                    <tr>
                        <td>${emp.livro.titulo}</td>
                        <td>${new Date(emp.dataEmprestimo).toLocaleDateString()}</td>
                        <td>${statusTexto}</td>
                        <td>${btn}</td>
                    </tr>`;
            });
        }
    } catch (e) { 
        console.error(e); 
        tbody.innerHTML = "<tr><td colspan='4' style='text-align:center; color:red;'>Erro ao carregar dados.</td></tr>";
    }
}

async function devolverLivro(id) {
    if (!confirm("Confirmar devolução?")) return;
    const res = await fetch(`/emprestimos/${id}/devolucao`, { method: "POST" });
    if (res.ok) { alert("Devolvido!"); carregarMeusEmprestimos(); loadBooks(); }
    else alert("Erro na devolução.");
}




window.onclick = function (e) {
    const modalLivro = document.getElementById("modal-livro");
    const modalDetalhes = document.getElementById("modal-detalhes");
    const modalMeusEmprestimos = document.getElementById("modal-meus-emprestimos");


    if (e.target === modalLivro) fecharModal();
    if (e.target === modalDetalhes) fecharModalDetalhes();
    if (e.target === modalMeusEmprestimos) fecharModalMeusEmprestimos();
}










