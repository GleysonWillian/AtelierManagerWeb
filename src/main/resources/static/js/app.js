
document.addEventListener("DOMContentLoaded", () => {

    // =====================================
    // NAVEGAÇÃO
    // =====================================

    const menuLinks = document.querySelectorAll(".menu a");
    const pageLinks = document.querySelectorAll("[data-page]");
    const paginas = document.querySelectorAll(".page-content");

    pageLinks.forEach(link => {

        link.addEventListener("click", event => {

            event.preventDefault();

            const paginaSelecionada = link.dataset.page;
            const pagina = document.getElementById(`${paginaSelecionada}-page`);

            if (!pagina) return;

            paginas.forEach(item => item.hidden = true);
            pagina.hidden = false;

            menuLinks.forEach(item => item.classList.remove("active"));

            const linkMenu = document.querySelector(
                `.menu a[data-page="${paginaSelecionada}"]`
            );

            if (linkMenu) linkMenu.classList.add("active");

            if (paginaSelecionada === "clientes") listarClientes();

           if (paginaSelecionada === "ordens") {
               listarOrdens();
               carregarClientesSelect();
           }

           if (paginaSelecionada === "estoque") {
               listarInsumos();
           }

           if (paginaSelecionada === "movimentacoes") {
               listarMovimentacoes();
               carregarInsumosMovimentacao();
           }

        });

    });


    // =====================================
    // ELEMENTOS DOS CLIENTES
    // =====================================

    const btnNovoCliente = document.getElementById("btn-novo-cliente");
    const btnCancelarCliente = document.getElementById("btn-cancelar-cliente");
    const formularioCliente = document.getElementById("cadastro-cliente");
    const areaFormularioCliente = document.getElementById("form-cliente");
    const tabelaClientes = document.getElementById("lista-clientes");

    let clienteEmEdicao = null;


    // =====================================
    // CLIENTES - ABRIR FORMULÁRIO
    // =====================================

    btnNovoCliente.addEventListener("click", () => {

        clienteEmEdicao = null;
        formularioCliente.reset();

        document.querySelector("#form-cliente h2").textContent =
            "Novo Cliente";

        formularioCliente.querySelector("button[type='submit']").textContent =
            "Salvar Cliente";

        areaFormularioCliente.hidden = false;

    });


    // =====================================
    // CLIENTES - CANCELAR
    // =====================================

    btnCancelarCliente.addEventListener("click", () => {

        clienteEmEdicao = null;
        formularioCliente.reset();
        areaFormularioCliente.hidden = true;

    });


    // =====================================
    // CLIENTES - LISTAR
    // =====================================

    async function listarClientes() {

        tabelaClientes.innerHTML = `
            <tr>
                <td colspan="4">Carregando clientes...</td>
            </tr>
        `;

        try {

            const resposta = await fetch("/api/clientes");

            if (!resposta.ok) {
                throw new Error("Erro ao buscar clientes.");
            }

            const clientes = await resposta.json();

            tabelaClientes.innerHTML = "";

            if (clientes.length === 0) {
                tabelaClientes.innerHTML = `
                    <tr>
                        <td colspan="4">Nenhum cliente cadastrado.</td>
                    </tr>
                `;
                return;
            }

            clientes.forEach(cliente => {

                const linha = document.createElement("tr");

                const id = document.createElement("td");
                id.textContent = cliente.idCliente;

                const nome = document.createElement("td");
                nome.textContent = cliente.nome;

                const telefone = document.createElement("td");
                telefone.textContent = cliente.telefone;

                const acoes = document.createElement("td");

                const btnEditar = document.createElement("button");
                btnEditar.textContent = "Editar";
                btnEditar.classList.add("btn-secondary");

                btnEditar.addEventListener("click", () => {
                    editarCliente(cliente);
                });

                const btnExcluir = document.createElement("button");
                btnExcluir.textContent = "Excluir";
                btnExcluir.classList.add("btn-danger");

                btnExcluir.addEventListener("click", () => {
                    excluirCliente(cliente);
                });

                acoes.append(btnEditar, btnExcluir);
                linha.append(id, nome, telefone, acoes);

                tabelaClientes.appendChild(linha);

            });

        } catch (erro) {

            console.error("Erro:", erro);

            tabelaClientes.innerHTML = `
                <tr>
                    <td colspan="4">Erro ao carregar os clientes.</td>
                </tr>
            `;

        }

    }


    // =====================================
    // CLIENTES - EDITAR
    // =====================================

    function editarCliente(cliente) {

        clienteEmEdicao = cliente.idCliente;

        document.getElementById("nome-cliente").value = cliente.nome;
        document.getElementById("telefone-cliente").value = cliente.telefone;

        document.querySelector("#form-cliente h2").textContent =
            "Editar Cliente";

        formularioCliente.querySelector("button[type='submit']").textContent =
            "Salvar Alterações";

        areaFormularioCliente.hidden = false;

        window.scrollTo({ top: 0, behavior: "smooth" });

    }


    // =====================================
    // CLIENTES - EXCLUIR
    // =====================================

    async function excluirCliente(cliente) {

        const confirmar = confirm(
            `Deseja realmente excluir o cliente ${cliente.nome}?`
        );

        if (!confirmar) return;

        try {

            const resposta = await fetch(
                `/api/clientes/${cliente.idCliente}`,
                { method: "DELETE" }
            );

            if (!resposta.ok) {
                throw new Error("Não foi possível excluir o cliente.");
            }

            alert("Cliente excluído com sucesso!");

            await listarClientes();

        } catch (erro) {

            console.error("Erro:", erro);

            alert(
                "Erro ao excluir o cliente. Verifique se existem ordens de serviço vinculadas a ele."
            );

        }

    }


    // =====================================
    // CLIENTES - CADASTRAR E ATUALIZAR
    // =====================================

    formularioCliente.addEventListener("submit", async event => {

        event.preventDefault();

        const cliente = {
            nome: document.getElementById("nome-cliente").value.trim(),
            telefone: document.getElementById("telefone-cliente").value.trim()
        };

        if (!cliente.nome || !cliente.telefone) {
            alert("Preencha todos os campos.");
            return;
        }

        const editando = clienteEmEdicao !== null;

        const url = editando
            ? `/api/clientes/${clienteEmEdicao}`
            : "/api/clientes";

        try {

            const resposta = await fetch(url, {
                method: editando ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(cliente)
            });

            if (!resposta.ok) {
                throw new Error("Não foi possível salvar o cliente.");
            }

            alert(
                editando
                    ? "Cliente atualizado com sucesso!"
                    : "Cliente cadastrado com sucesso!"
            );

            clienteEmEdicao = null;
            formularioCliente.reset();
            areaFormularioCliente.hidden = true;

            await listarClientes();

        } catch (erro) {

            console.error("Erro:", erro);
            alert("Erro ao salvar o cliente. Verifique os dados e tente novamente.");

        }

    });


    // =====================================
    // ELEMENTOS DAS ORDENS DE SERVIÇO
    // =====================================

    const btnNovaOrdem = document.getElementById("btn-nova-ordem");
    const btnCancelarOrdem = document.getElementById("btn-cancelar-ordem");
    const formularioOrdem = document.getElementById("cadastro-ordem");
    const areaFormularioOrdem = document.getElementById("form-ordem");
    const tabelaOrdens = document.getElementById("lista-ordens");
    const selectCliente = document.getElementById("cliente-ordem");
    const pesquisaOrdem = document.getElementById("pesquisa-ordem");

    let ordemEmEdicao = null;
    let ordensCarregadas = [];

    // =====================================
    // ELEMENTOS DAS MOVIMENTAÇÕES
    // =====================================


    // =====================================
    // ORDENS - ABRIR FORMULÁRIO
    // =====================================

    btnNovaOrdem.addEventListener("click", async () => {

        ordemEmEdicao = null;
        formularioOrdem.reset();

        document.getElementById("titulo-form-ordem").textContent =
            "Nova Ordem de Serviço";

        formularioOrdem.querySelector("button[type='submit']").textContent =
            "Salvar Ordem";

        document.getElementById("data-entrada").value =
            new Date().toLocaleDateString("en-CA");

        document.getElementById("status-ordem").value = "Pendente";

        await carregarClientesSelect();

        areaFormularioOrdem.hidden = false;

        window.scrollTo({ top: 0, behavior: "smooth" });

    });


    // =====================================
    // ORDENS - CANCELAR
    // =====================================

    btnCancelarOrdem.addEventListener("click", () => {

        ordemEmEdicao = null;
        formularioOrdem.reset();
        areaFormularioOrdem.hidden = true;

    });


    // =====================================
    // ORDENS - CARREGAR CLIENTES NO SELECT
    // =====================================

    async function carregarClientesSelect() {

        try {

            const resposta = await fetch("/api/clientes");

            if (!resposta.ok) {
                throw new Error("Erro ao carregar clientes.");
            }

            const clientes = await resposta.json();

            selectCliente.innerHTML = `
                <option value="">Selecione um cliente</option>
            `;

            clientes.forEach(cliente => {

                const opcao = document.createElement("option");

                opcao.value = cliente.idCliente;
                opcao.textContent = cliente.nome;

                selectCliente.appendChild(opcao);

            });

        } catch (erro) {

            console.error("Erro:", erro);
            alert("Não foi possível carregar os clientes.");

        }

    }


    // =====================================
    // ORDENS - LISTAR
    // =====================================

    async function listarOrdens() {

        tabelaOrdens.innerHTML = `
            <tr>
                <td colspan="7">Carregando ordens...</td>
            </tr>
        `;

        try {

            const resposta = await fetch("/api/ordens");

            if (!resposta.ok) {
                throw new Error("Erro ao buscar ordens.");
            }

            ordensCarregadas = await resposta.json();

            renderizarOrdens(ordensCarregadas);

        } catch (erro) {

            console.error("Erro:", erro);

            tabelaOrdens.innerHTML = `
                <tr>
                    <td colspan="7">Erro ao carregar as ordens de serviço.</td>
                </tr>
            `;

        }

    }


    // =====================================
    // ORDENS - EXIBIR NA TABELA
    // =====================================

    function renderizarOrdens(ordens) {

        tabelaOrdens.innerHTML = "";

        if (ordens.length === 0) {

            tabelaOrdens.innerHTML = `
                <tr>
                    <td colspan="7">Nenhuma ordem cadastrada.</td>
                </tr>
            `;

            return;
        }

        ordens.forEach(ordem => {

            const linha = document.createElement("tr");

            const id = document.createElement("td");
            id.textContent = ordem.idOrdem;

            const cliente = document.createElement("td");
            cliente.textContent = ordem.nomeCliente;

            const entrada = document.createElement("td");
            entrada.textContent = formatarData(ordem.dataEntrada);

            const entrega = document.createElement("td");
            entrega.textContent = formatarData(ordem.dataPrevistaEntrega);

            const valor = document.createElement("td");
            valor.textContent = Number(ordem.valorTotal).toLocaleString(
                "pt-BR",
                { style: "currency", currency: "BRL" }
            );

            const status = document.createElement("td");
            status.textContent = ordem.status;

            const acoes = document.createElement("td");

            const btnEditar = document.createElement("button");
            btnEditar.textContent = "Editar";
            btnEditar.classList.add("btn-secondary");

            btnEditar.addEventListener("click", () => {
                editarOrdem(ordem);
            });

            const btnExcluir = document.createElement("button");
            btnExcluir.textContent = "Excluir";
            btnExcluir.classList.add("btn-danger");

            btnExcluir.addEventListener("click", () => {
                excluirOrdem(ordem);
            });

            acoes.append(btnEditar, btnExcluir);
            linha.append(id, cliente, entrada, entrega, valor, status, acoes);

            tabelaOrdens.appendChild(linha);

        });

    }


    // =====================================
    // ORDENS - FORMATAR DATA
    // =====================================

    function formatarData(data) {

        if (!data) return "";

        const [ano, mes, dia] = data.split("-");

        return `${dia}/${mes}/${ano}`;

    }


    // =====================================
    // ORDENS - EDITAR
    // =====================================

    async function editarOrdem(ordem) {

        ordemEmEdicao = ordem.idOrdem;

        await carregarClientesSelect();

        document.getElementById("cliente-ordem").value =
            ordem.idCliente;

        document.getElementById("data-entrada").value =
            ordem.dataEntrada;

        document.getElementById("data-entrega").value =
            ordem.dataPrevistaEntrega;

        document.getElementById("valor-total").value =
            ordem.valorTotal;

        document.getElementById("status-ordem").value =
            ordem.status;

        document.getElementById("observacoes-ordem").value =
            ordem.observacoes || "";

        document.getElementById("titulo-form-ordem").textContent =
            "Editar Ordem de Serviço";

        formularioOrdem.querySelector("button[type='submit']").textContent =
            "Salvar Alterações";

        areaFormularioOrdem.hidden = false;

        window.scrollTo({ top: 0, behavior: "smooth" });

    }


    // =====================================
    // ORDENS - CADASTRAR E ATUALIZAR
    // =====================================

    formularioOrdem.addEventListener("submit", async event => {

        event.preventDefault();

        const ordem = {
            idCliente: Number(selectCliente.value),
            dataEntrada: document.getElementById("data-entrada").value,
            dataPrevistaEntrega: document.getElementById("data-entrega").value,
            valorTotal: Number(document.getElementById("valor-total").value),
            status: document.getElementById("status-ordem").value,
            observacoes: document.getElementById("observacoes-ordem").value.trim()
        };

        if (!ordem.idCliente || !ordem.dataEntrada ||
            !ordem.dataPrevistaEntrega || !ordem.valorTotal) {

            alert("Preencha todos os campos obrigatórios.");
            return;

        }

        if (ordem.dataPrevistaEntrega < ordem.dataEntrada) {
            alert("A data de entrega não pode ser anterior à data de entrada.");
            return;
        }

        if (ordem.valorTotal < 0) {
            alert("O valor não pode ser negativo.");
            return;
        }

        const editando = ordemEmEdicao !== null;

        const url = editando
            ? `/api/ordens/${ordemEmEdicao}`
            : "/api/ordens";

        try {

            const resposta = await fetch(url, {
                method: editando ? "PUT" : "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(ordem)
            });

            if (!resposta.ok) {

                const mensagem = await resposta.text();

                throw new Error(mensagem || "Erro ao salvar ordem.");

            }

            alert(
                editando
                    ? "Ordem atualizada com sucesso!"
                    : "Ordem cadastrada com sucesso!"
            );

            ordemEmEdicao = null;
            formularioOrdem.reset();
            areaFormularioOrdem.hidden = true;

            await listarOrdens();

        } catch (erro) {

            console.error("Erro:", erro);
            alert("Erro ao salvar ordem: " + erro.message);

        }

    });


    // =====================================
    // ORDENS - EXCLUIR
    // =====================================

    async function excluirOrdem(ordem) {

        const confirmar = confirm(
            `Deseja realmente excluir a ordem ${ordem.idOrdem} do cliente ${ordem.nomeCliente}?`
        );

        if (!confirmar) return;

        try {

            const resposta = await fetch(
                `/api/ordens/${ordem.idOrdem}`,
                { method: "DELETE" }
            );

            if (resposta.status === 409) {
                alert("Não é possível excluir esta ordem, pois existem itens associados.");
                return;
            }

            if (resposta.status === 404) {
                alert("Ordem de serviço não encontrada.");
                return;
            }

            if (!resposta.ok) {
                throw new Error("Não foi possível excluir a ordem.");
            }

            alert("Ordem excluída com sucesso!");

            await listarOrdens();

        } catch (erro) {

            console.error("Erro:", erro);
            alert("Erro ao excluir ordem de serviço.");

        }

    }


    // =====================================
    // ORDENS - PESQUISAR
    // =====================================

    pesquisaOrdem.addEventListener("input", () => {

        const termo = pesquisaOrdem.value.toLowerCase().trim();

        const resultado = ordensCarregadas.filter(ordem => {

            return (
                ordem.nomeCliente.toLowerCase().includes(termo) ||
                ordem.status.toLowerCase().includes(termo) ||
                String(ordem.idOrdem).includes(termo)
            );

        });

        renderizarOrdens(resultado);

    });


});


// ==========================================
// CONTROLE DE ESTOQUE
// ==========================================

let insumos = [];
let insumoEditando = null;

const API_INSUMOS = "/api/insumos";

const formInsumo = document.getElementById("form-insumo");
const cadastroInsumo = document.getElementById("cadastro-insumo");
const listaInsumos = document.getElementById("lista-insumos");

// Listar materiais
async function listarInsumos() {
    try {
        const resposta = await fetch(API_INSUMOS);

        if (!resposta.ok) {
            throw new Error("Erro ao carregar os materiais.");
        }

        insumos = await resposta.json();
        renderizarInsumos(insumos);

    } catch (erro) {
        console.error(erro);
        alert("Não foi possível carregar o estoque.");
    }
}

// Renderizar tabela
function renderizarInsumos(dados) {
    listaInsumos.innerHTML = "";

    if (dados.length === 0) {
        listaInsumos.innerHTML = `
            <tr>
                <td colspan="7">Nenhum material cadastrado.</td>
            </tr>
        `;
        return;
    }

    dados.forEach(insumo => {
        const estoqueBaixo =
            Number(insumo.quantidadeAtual) <= Number(insumo.estoqueMinimo);

        const linha = document.createElement("tr");

        linha.innerHTML = `
            <td>${insumo.idInsumo}</td>
            <td>${insumo.nome}</td>
            <td>${Number(insumo.quantidadeAtual).toFixed(2)}</td>
            <td>${insumo.unidadeMedida}</td>
            <td>${Number(insumo.estoqueMinimo).toFixed(2)}</td>
            <td>
                <span class="${estoqueBaixo ? "status-baixo" : "status-normal"}">
                    ${estoqueBaixo ? "Estoque baixo" : "Normal"}
                </span>
            </td>
            <td>
                <button class="btn-secondary"
                    onclick="editarInsumo(${insumo.idInsumo})">
                    Editar
                </button>

                <button class="btn-secondary"
                    onclick="excluirInsumo(${insumo.idInsumo})">
                    Excluir
                </button>
            </td>
        `;

        listaInsumos.appendChild(linha);
    });
}

// Abrir formulário para novo material
document.getElementById("btn-novo-insumo")
    .addEventListener("click", () => {

        insumoEditando = null;
        cadastroInsumo.reset();

        document.getElementById("titulo-form-insumo").textContent =
            "Novo Material";

        formInsumo.hidden = false;
    });

// Cancelar formulário
document.getElementById("btn-cancelar-insumo")
    .addEventListener("click", () => {

        formInsumo.hidden = true;
        cadastroInsumo.reset();
        insumoEditando = null;
    });

// Salvar ou atualizar material
cadastroInsumo.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    const dados = {
        nome: document.getElementById("nome-insumo").value.trim(),
        quantidadeAtual: Number(
            document.getElementById("quantidade-insumo").value
        ),
        unidadeMedida: document.getElementById("unidade-insumo").value.trim(),
        estoqueMinimo: Number(
            document.getElementById("minimo-insumo").value
        )
    };

    const editando = insumoEditando !== null;

    const url = editando
        ? `${API_INSUMOS}/${insumoEditando}`
        : API_INSUMOS;

    try {
        const resposta = await fetch(url, {
            method: editando ? "PUT" : "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(dados)
        });

        if (!resposta.ok) {
            throw new Error("Não foi possível salvar o material.");
        }

        alert(editando
            ? "Material atualizado com sucesso!"
            : "Material cadastrado com sucesso!"
        );

        cadastroInsumo.reset();
        formInsumo.hidden = true;
        insumoEditando = null;

        await listarInsumos();

    } catch (erro) {
        console.error(erro);
        alert("Erro ao salvar o material.");
    }
});

// Editar material
function editarInsumo(id) {
    const insumo = insumos.find(item => item.idInsumo === id);

    if (!insumo) return;

    insumoEditando = id;

    document.getElementById("titulo-form-insumo").textContent =
        "Editar Material";

    document.getElementById("nome-insumo").value = insumo.nome;
    document.getElementById("quantidade-insumo").value =
        insumo.quantidadeAtual;
    document.getElementById("unidade-insumo").value =
        insumo.unidadeMedida;
    document.getElementById("minimo-insumo").value =
        insumo.estoqueMinimo;

    formInsumo.hidden = false;
}

// Excluir material
async function excluirInsumo(id) {
    const confirmar = confirm(
        "Deseja realmente excluir este material?"
    );

    if (!confirmar) return;

    try {
        const resposta = await fetch(`${API_INSUMOS}/${id}`, {
            method: "DELETE"
        });

        if (!resposta.ok) {
            throw new Error("Não foi possível excluir o material.");
        }

        alert("Material excluído com sucesso!");

        await listarInsumos();

    } catch (erro) {
        console.error(erro);
        alert("Erro ao excluir. Verifique se existem movimentações vinculadas.");
    }
}

// Pesquisa de materiais
document.getElementById("pesquisa-insumo")
    .addEventListener("input", (evento) => {

        const pesquisa = evento.target.value.toLowerCase();

        const filtrados = insumos.filter(insumo =>
            insumo.nome.toLowerCase().includes(pesquisa)
        );

        renderizarInsumos(filtrados);
    });

// ==========================================
// CONTROLE DE MOVIMENTAÇÕES
// ==========================================

const formularioMovimentacao = document.getElementById("cadastro-movimentacao");
const listaMovimentacoes = document.getElementById("lista-movimentacoes");
const selectInsumoMovimentacao = document.getElementById("insumo-movimentacao");

// Carregar materiais no select de movimentação
async function carregarInsumosMovimentacao() {

    try {
        const resposta = await fetch("/api/insumos");

        if (!resposta.ok) {
            throw new Error("Erro ao carregar os materiais.");
        }

        const materiais = await resposta.json();

        selectInsumoMovimentacao.innerHTML =
            '<option value="">Selecione um material</option>';

        materiais.forEach(insumo => {

            const opcao = document.createElement("option");

            opcao.value = insumo.idInsumo;
            opcao.textContent = insumo.nome;

            selectInsumoMovimentacao.appendChild(opcao);

        });

    } catch (erro) {
        console.error("Erro ao carregar materiais:", erro);
    }
}

// ==========================================
// LISTAR HISTÓRICO DE MOVIMENTAÇÕES
// ==========================================

async function listarMovimentacoes() {

    listaMovimentacoes.innerHTML = `
        <tr>
            <td colspan="5">Carregando movimentações...</td>
        </tr>
    `;

    try {
        const resposta = await fetch("/api/movimentacoes");

        if (!resposta.ok) {
            throw new Error("Erro ao carregar movimentações.");
        }

        const movimentacoes = await resposta.json();

        listaMovimentacoes.innerHTML = "";

        if (movimentacoes.length === 0) {
            listaMovimentacoes.innerHTML = `
                <tr>
                    <td colspan="5">Nenhuma movimentação registrada.</td>
                </tr>
            `;
            return;
        }

        movimentacoes.forEach(movimentacao => {

            const linha = document.createElement("tr");

            linha.innerHTML = `
                <td>${movimentacao.idMovimentacao}</td>
                <td>${movimentacao.nomeInsumo}</td>
                <td>${movimentacao.tipo}</td>
                <td>${Number(movimentacao.quantidade).toFixed(2)}</td>
                <td>
                    ${movimentacao.dataMovimentacao
                        ? new Date(movimentacao.dataMovimentacao).toLocaleString("pt-BR")
                        : ""}
                </td>
            `;

            listaMovimentacoes.appendChild(linha);

        });

    } catch (erro) {
        console.error("Erro ao listar movimentações:", erro);

        listaMovimentacoes.innerHTML = `
            <tr>
                <td colspan="5">Erro ao carregar o histórico.</td>
            </tr>
        `;
    }
}

// ==========================================
// CADASTRAR MOVIMENTAÇÃO
// ==========================================

formularioMovimentacao.addEventListener("submit", async (evento) => {

    evento.preventDefault();

    const movimentacao = {
        idInsumo: Number(selectInsumoMovimentacao.value),
        tipo: document.getElementById("tipo-movimentacao").value,
        quantidade: Number(
            document.getElementById("quantidade-movimentacao").value
        )
    };

    // Validar campos
    if (
        !movimentacao.idInsumo ||
        !movimentacao.tipo ||
        !Number.isFinite(movimentacao.quantidade) ||
        movimentacao.quantidade <= 0
    ) {
        alert("Preencha todos os campos corretamente.");
        return;
    }

    try {

        const resposta = await fetch("/api/movimentacoes", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(movimentacao)
        });

        if (!resposta.ok) {
            const mensagem = await resposta.text();
            throw new Error(mensagem || "Erro ao registrar movimentação.");
        }

        alert("Movimentação registrada com sucesso!");

        formularioMovimentacao.reset();

        // Atualizar histórico e estoque
        await listarMovimentacoes();
        await carregarInsumosMovimentacao();
        await listarInsumos();

    } catch (erro) {

        console.error("Erro ao registrar movimentação:", erro);
        alert("Erro ao registrar movimentação: " + erro.message);

    }

});