CATÁLOGO DE GAMES


Projeto desenvolvido em Java + Spring Boot que consome a API pública da RAWG.io para listar, buscar, favoritar e recomendar jogos eletrônicos.



TECNOLOGIAS UTILIZADAS:
Java 21+
Spring Boot
API RAWG.io
RestTemplate
Maven
Servidor disponível em:
http://localhost:8000/games




EXEMPLOS DE REQUISIÇÕES:



Buscar jogos por nome:
GET/http://localhost:8000/games/buscar?nome=zelda

Listar todos os jogos 
GET/ http://localhost:8000/games/todos

Listar os jogos favoritos
GET http://localhost:8000/games/favoritos

Adicionar um jogo aos favoritos
POST  http://localhost:8000/games/favoritar

Listar os melhores jogos por nota
GET/ http://localhost:8000/games/top/10


Recomendação por gênero e nota
GET/http://localhost:8080/games/recomendados?genero=role-playing-games-rpg&notaMinima=80&paginas=3


Adicionar (?page="número que o usúario escolher") ao final da requisição para escolher uma página específica da API
Adicionar (totalPages="número que o usuário escolher") ao final da requisição que busca o jogo pelo nome,para determinar uma quantidade de páginas a retornar



ESTRUTURA DO PROJETO:



VIDEOGAMEMODEL.JAVA

Classe que representa os dados de um jogo.
Contém os atributos principais obtidos da RAWG API, como:
    
name: nome do jogo    
released: data de lançamento   
metacritic: nota no Metacritic    
background_image: imagem de fundo
genres: lista de gêneros
Essa classe é usada tanto para exibir dados quanto para popular listas de jogos.

VIDEOGAMESERVICE.JAVA
Classe de serviço responsável por toda a lógica de negócio e requisições à API externa RAWG.
Principais responsabilidades:

Buscar jogos por página ou nome,

Recomendação de jogos com base em critérios como gênero e nota,

Listagem dos jogos com maior nota,

Gerenciamento da lista de jogos favoritados,

Comunicação com a RAWG API via RestTemplate,



VIDEOGAMECONTROLLER.JAVA

Classe que expõe a API REST da aplicação.
É a camada que interage com o usuário (via HTTP), fornecendo os seguintes endpoints:

Listar jogos, buscar por nome,

Recomendação e ranking,

Favoritar e listar favoritos,

Retornar dados sobre o projeto,

Utiliza os serviços da VideogameService para processar as requisições.



FAVORITODTO.JAVA
Objeto de transferência de dados (DTO) usado para receber o nome do jogo a ser adicionado aos favoritos.
Usado no corpo do POST /games/favoritar.



GAME.JAVA
Classe que representa um jogo individual retornado pela RAWG API.
Usada como parte da resposta da API externa. Contém informações detalhadas como:

name: nome do jogo
released: data de lançamento
metacritic: nota no Metacritic
background_image: imagem principal
genres: lista de gêneros do jogo
platforms: lista de plataformas (envolvendo a classe PlatformWrapper)

Essa classe é usada internamente para interpretar a resposta da API e repassar os dados ao VideogameModel.



GAMERESPONSE.JAVA
Classe que modela a resposta completa da RAWG API ao buscar por jogos.
Contém:

results: lista de objetos Game

É essencial para deserializar corretamente a resposta JSON da RAWG API

PLATAFORM.JAVA
Classe que representa uma plataforma (como PC, PlayStation, Xbox, etc.).
Contém apenas:

name: nome da plataforma

Faz parte da hierarquia usada para deserializar corretamente as plataformas dentro de cada jogo.



PLATFORMWRAPPER.JAVA
Classe auxiliar usada para encapsular o objeto Platform dentro da estrutura da resposta da API.
A RAWG API retorna plataformas em um formato aninhado como:

"platforms": [
  {
    "platform": {
      "name": "PC"
    }
  }
]
Portanto, o PlatformWrapper serve para mapear esse nível intermediário.



