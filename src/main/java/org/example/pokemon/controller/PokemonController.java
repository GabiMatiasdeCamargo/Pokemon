package org.example.pokemon.controller;

import org.example.pokemon.entities.Pokemon;
import org.example.pokemon.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pokemons")
public class PokemonController {

    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    // GET /pokemons - lista os Pokémons que você salvou (até 50)
    @GetMapping
    public List<Pokemon> listarTodos() {
        return service.listarTodos();
    }

    // GET /pokemons/pokeapi?offset=0 - lista nomes direto da PokeAPI (50 por página)
    @GetMapping("/pokeapi")
    public List<String> listarDaPokeApi(@RequestParam(defaultValue = "0") int offset) {
        return service.listarDaPokeApi(offset);
    }

    // GET /pokemons/1 - busca um Pokémon salvo
    @GetMapping("/{id}")
    public ResponseEntity<Pokemon> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // A API DE POKEMON N DEIXA ALTERAR OS POKEMONS MAS ESCREVI O CODIGO PRA SABER COMO FAZER COM APIS QUE PODE
    // POST /pokemons com {"nome":"pikachu"}
    // Só o nome é usado: o resto vem da PokeAPI.
    @PostMapping
    public ResponseEntity<Pokemon> criar(@RequestBody Pokemon pokemon) {
        if (pokemon.getNome() == null || pokemon.getNome().isBlank()) {
            return ResponseEntity.badRequest().build(); // 400: faltou o nome
        }
        return service.criarPelaPokeApi(pokemon.getNome())
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p)) // 201
                .orElse(ResponseEntity.notFound().build());                  // 404: nome não existe na PokeAPI
    }

    // PUT /pokemons/1 - altera um Pokémon salvo (nome, tipo, altura, peso)
    @PutMapping("/{id}")
    public ResponseEntity<Pokemon> atualizar(@PathVariable Long id, @RequestBody Pokemon pokemon) {
        return service.atualizar(id, pokemon)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /pokemons/1 - remove um Pokémon salvo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.deletar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}