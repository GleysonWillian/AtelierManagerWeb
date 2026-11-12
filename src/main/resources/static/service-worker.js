const CACHE_NAME = "ateliermanager-v2";

// Arquivos estáticos que serão armazenados em cache
const ARQUIVOS_CACHE = [
    "/",
    "/index.html",
    "/manifest.json",
    "/icons/icon-192.png",
    "/icons/icon-512.png",
    "/css/style.css",
    "/js/app.js"
];

// Instalação do Service Worker
self.addEventListener("install", (event) => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then((cache) => cache.addAll(ARQUIVOS_CACHE))
            .then(() => self.skipWaiting())
    );
});

// Ativação do Service Worker
self.addEventListener("activate", (event) => {
    event.waitUntil(
        caches.keys().then((nomesCaches) => {
            return Promise.all(
                nomesCaches
                    .filter((nome) => nome !== CACHE_NAME)
                    .map((nome) => caches.delete(nome))
            );
        }).then(() => self.clients.claim())
    );
});

// Interceptação das requisições
self.addEventListener("fetch", (event) => {

    const requisicao = event.request;
    const url = new URL(requisicao.url);

    // Ignora requisições externas
    if (url.origin !== self.location.origin) {
        return;
    }

    // Não armazena chamadas da API
    if (url.pathname.startsWith("/api/")) {
        return;
    }

    // Intercepta somente requisições GET
    if (requisicao.method !== "GET") {
        return;
    }

    event.respondWith(
        caches.match(requisicao)
            .then((respostaCache) => {
                return respostaCache || fetch(requisicao);
            })
    );
});
