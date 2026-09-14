# KeroPro — Front-end (React)

Interface web/mobile do KeroPro, consumindo a API Java em `../backend`.

## Como rodar

```bash
npm install
npm start
```

A aplicação abre em `http://localhost:3000` e espera a API em `http://localhost:8080/api`
(configurável via variável de ambiente `REACT_APP_API_URL`).

## Estrutura

```
src/
├── api/api.js          # todas as chamadas fetch para a API Java
├── styles/              # CSS puro, separado por camada
│   ├── variables.css    # tokens de cor/tipografia
│   ├── base.css         # reset e layout do "frame" do app
│   ├── components.css   # estilos de componentes reutilizáveis
│   └── pages.css        # estilos específicos de cada tela
├── components/           # peças reutilizáveis (Gauge, Stepper, TopBar, MapMock, ProCard)
├── pages/                 # uma tela por arquivo
└── App.jsx                # orquestra a navegação entre telas
```
