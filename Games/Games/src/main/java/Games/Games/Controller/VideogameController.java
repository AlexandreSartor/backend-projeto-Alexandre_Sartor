package Games.Games.Controller;

import Games.Games.Model.VideogameModel;
import Games.Games.Service.VideogameService;
import Games.Games.util.FavoritoDTO;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*")
public class VideogameController {

    private final VideogameService videogameService;

    public VideogameController(VideogameService videogameService) {
        this.videogameService = videogameService;
    }

    @GetMapping
    public List<VideogameModel> getGames(@RequestParam(defaultValue = "1") int page) {
        return videogameService.getGameData(page);
    }

    @GetMapping("/todos")
    public List<VideogameModel> getAllGames(@RequestParam(defaultValue = "1") int page) {
        return videogameService.getAllGamesCombined(page);
    }

    @GetMapping("/top/{quantidade}")
    public List<VideogameModel> getTopGames(@PathVariable int quantidade,
                                            @RequestParam(defaultValue = "1") int page) {
        return videogameService.getTopRatedGames(quantidade, page);
    }

    @GetMapping("/favoritos")
    public List<VideogameModel> listarFavoritos() {
        return videogameService.getFavoriteGames();
    }

    @GetMapping("/buscar")
    public List<VideogameModel> searchGames(@RequestParam String nome,
                                            @RequestParam(defaultValue = "5") int totalPages) {
        return videogameService.searchGamesByName(nome, totalPages);
    }

    @GetMapping("/recomendados")
    public List<VideogameModel> recomendarJogosPorGenero(@RequestParam String genero,
                                                         @RequestParam(defaultValue = "80") int notaMinima,
                                                         @RequestParam(defaultValue = "2") int paginas) {
        return videogameService.recomendarJogosPorGeneroENota(genero, notaMinima, paginas);
    }

    @GetMapping("/sobre")
    public Map<String, String> retornarMembros() {
        Map<String, String> projeto = new HashMap<>();
        projeto.put("integrantes", "Alexandre Sartor Teixeira");
        projeto.put("nome_projeto", "Catálogo de Games");
        return projeto;
    }



    @PostMapping("/favoritar")
    public String favoritarJogo(@RequestBody FavoritoDTO favorito) {
        videogameService.addFavoriteGame(favorito.getName());
        return "Jogo '" + favorito.getName() + "' adicionado aos favoritos!";
    }
}
