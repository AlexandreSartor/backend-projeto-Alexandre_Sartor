package Games.Games.Service;

import Games.Games.Model.VideogameModel;
import Games.Games.util.Game;
import Games.Games.util.GameResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideogameService {

    private final String API_KEY = "be091db890da48d8bb03eabfc134546b";
    private final String API_URL = "https://api.rawg.io/api/games?key=" + API_KEY;

    private final List<VideogameModel> storedGames = new ArrayList<>();
    private final List<String> favoriteGames = new ArrayList<>();

    public List<VideogameModel> getGameData(int page) {
        RestTemplate restTemplate = new RestTemplate();
        List<VideogameModel> videogames = new ArrayList<>();
        int pageSize = 40;
        String url = API_URL + "&page=" + page + "&page_size=" + pageSize;

        GameResponse response = restTemplate.getForObject(url, GameResponse.class);

        if (response != null && response.getResults() != null) {
            for (Game game : response.getResults()) {
                VideogameModel model = new VideogameModel();
                model.setName(game.getName());
                model.setReleased(game.getReleased());
                model.setBackground_image(game.getBackground_image());
                model.setMetacritic(game.getMetacritic() != null ? game.getMetacritic().toString() : "N/A");

                if (game.getPlatforms() != null && !game.getPlatforms().isEmpty()) {
                    model.setPlatform(game.getPlatforms().get(0).getPlatform().getName());
                } else {
                    model.setPlatform("Desconhecido");
                }

                if (game.getGenres() != null) {
                    List<String> generos = game.getGenres().stream()
                            .map(g -> g.getName())
                            .collect(Collectors.toList());
                    model.setGenres(generos);
                }

                videogames.add(model);
            }
        }

        return videogames;
    }

    public List<VideogameModel> searchGamesByName(String searchTerm, int totalPages) {
        RestTemplate restTemplate = new RestTemplate();
        List<VideogameModel> resultGames = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            String url = API_URL + "&search=" + UriUtils.encode(searchTerm, StandardCharsets.UTF_8)
                    + "&page=" + page + "&page_size=20";

            GameResponse response = restTemplate.getForObject(url, GameResponse.class);

            if (response != null && response.getResults() != null) {
                for (Game game : response.getResults()) {
                    VideogameModel model = new VideogameModel();
                    model.setName(game.getName());
                    model.setReleased(game.getReleased());
                    model.setBackground_image(game.getBackground_image());
                    model.setMetacritic(game.getMetacritic() != null ? game.getMetacritic().toString() : "N/A");

                    if (game.getPlatforms() != null && !game.getPlatforms().isEmpty()) {
                        model.setPlatform(game.getPlatforms().get(0).getPlatform().getName());
                    } else {
                        model.setPlatform("Desconhecido");
                    }

                    if (game.getGenres() != null) {
                        List<String> generos = game.getGenres().stream()
                                .map(g -> g.getName())
                                .collect(Collectors.toList());
                        model.setGenres(generos);
                    }

                    resultGames.add(model);
                }
            }
        }

        return resultGames;
    }

    public List<VideogameModel> getAllGamesCombined(int page) {
        return getGameData(page);
    }

    public List<VideogameModel> getTopRatedGames(int quantidade, int page) {
        return getAllGamesCombined(page).stream()
                .filter(g -> {
                    try {
                        Integer.parseInt(g.getMetacritic());
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .sorted((g1, g2) -> Integer.compare(Integer.parseInt(g2.getMetacritic()), Integer.parseInt(g1.getMetacritic())))
                .limit(quantidade)
                .toList();
    }

    public void addGame(VideogameModel gameData) {
        storedGames.add(gameData);
    }

    public void addFavoriteGame(String gameName) {
        favoriteGames.add(gameName);
    }

    public List<VideogameModel> getFavoriteGames() {
        List<VideogameModel> favoritosEncontrados = new ArrayList<>();
        int page = 1;
        Set<String> favoritosRestantes = new HashSet<>(favoriteGames);

        while (!favoritosRestantes.isEmpty()) {
            List<VideogameModel> paginaJogos = getAllGamesCombined(page);
            if (paginaJogos.isEmpty()) break;

            for (VideogameModel jogo : paginaJogos) {
                if (favoritosRestantes.contains(jogo.getName())) {
                    favoritosEncontrados.add(jogo);
                    favoritosRestantes.remove(jogo.getName());
                }
            }
            page++;
        }

        return favoritosEncontrados;
    }

    public List<VideogameModel> recomendarJogosPorGeneroENota(String genero, int notaMinima, int paginas) {
        RestTemplate restTemplate = new RestTemplate();
        List<VideogameModel> recomendados = new ArrayList<>();
        Set<String> nomesAdicionados = new HashSet<>();

        for (int page = 1; page <= paginas; page++) {
            String url = "https://api.rawg.io/api/games?key=" + API_KEY + "&genres=" + genero +
                    "&page=" + page + "&page_size=20&metacritic=" + notaMinima;

            GameResponse response = restTemplate.getForObject(url, GameResponse.class);

            if (response != null && response.getResults() != null) {
                for (Game game : response.getResults()) {
                    if (game.getMetacritic() != null && game.getMetacritic() >= notaMinima &&
                            !nomesAdicionados.contains(game.getName())) {

                        VideogameModel model = new VideogameModel();
                        model.setName(game.getName());
                        model.setReleased(game.getReleased());
                        model.setBackground_image(game.getBackground_image());
                        model.setMetacritic(String.valueOf(game.getMetacritic()));

                        if (game.getPlatforms() != null && !game.getPlatforms().isEmpty()) {
                            model.setPlatform(game.getPlatforms().get(0).getPlatform().getName());
                        } else {
                            model.setPlatform("Desconhecido");
                        }

                        if (game.getGenres() != null) {
                            List<String> generos = game.getGenres().stream()
                                    .map(g -> g.getName())
                                    .collect(Collectors.toList());
                            model.setGenres(generos);
                        }

                        recomendados.add(model);
                        nomesAdicionados.add(game.getName());
                    }
                }
            }
        }

        return recomendados;
    }

    public void RetornarMembros() {
        System.out.println("integrantes:Alexandre Sartor Teixeira");
        System.out.println("nome_projeto:Catálogo de Games");

    }

}
