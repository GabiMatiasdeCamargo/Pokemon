package org.example.pokemon.service;

import org.example.pokemon.entities.Pokemon;
import org.example.pokemon.repositories.PokemonRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PokemonService {

    //Na atividade - 50 pokemons
    private static final int LIMITE = 50;

    private final PokemonRepository repository;

    // RestClient é a ferramenta do Spring para CHAMAR outras APIs.
    // Aqui já deixamos configurado o endereço base da PokeAPI.
    private final RestClient restClient = RestClient.create("https://pokeapi.co/api/v2");

    public PokemonService(PokemonRepository repository) {
        this.repository = repository;
    }

    //Listartodos
    public List<Pokemon> listarTodos() {
        return repository.findAll().stream()
                .limit(LIMITE)
                .toList();
    }

    public Optional<Pokemon> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // PUT: altera um Pokémon que já está na sua coleção.
    public Optional<Pokemon> atualizar(Long id, Pokemon pokemon) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        pokemon.setId(id);
        return Optional.of(repository.save(pokemon));
    }

    public boolean deletar(Long id) {
        return repository.deleteById(id);
    }


    // POST: busca o Pokémon na PokeAPI pelo nome e salva na sua coleção.
    // Devolve vazio se o nome não existir na PokeAPI.
    public Optional<Pokemon> criarPelaPokeApi(String nome) {
        return buscarNaPokeApi(nome).map(repository::save);
    }

    // Chama GET https://pokeapi.co/api/v2/pokemon/{nome} e monta um Pokemon com a resposta.
    @SuppressWarnings("unchecked")
    private Optional<Pokemon> buscarNaPokeApi(String nome) {
        try {
            // O JSON da PokeAPI é lido como um Map (chave -> valor).
            Map<String, Object> json = restClient.get()
                    .uri("/pokemon/{nome}", nome.toLowerCase().trim())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            // "types" é uma lista; cada item tem um "type" com o "name".
            // Ex.: [{"type":{"name":"grass"}}, {"type":{"name":"poison"}}] -> "grass/poison"
            List<Map<String, Object>> tipos = (List<Map<String, Object>>) json.get("types");
            String tipo = tipos.stream()
                    .map(t -> (String) ((Map<String, Object>) t.get("type")).get("name"))
                    .collect(Collectors.joining("/"));

            Pokemon pokemon = new Pokemon();
            pokemon.setNome((String) json.get("name"));
            pokemon.setTipo(tipo);
            pokemon.setAltura((Integer) json.get("height"));
            pokemon.setPeso((Integer) json.get("weight"));
            return Optional.of(pokemon);

        } catch (HttpClientErrorException.NotFound e) {
            // A PokeAPI respondeu 404: esse nome não existe.
            return Optional.empty();
        }
    }

    // lista nomes direto da PokeAPI, no máximo 50 por página.
    // Chama GET https://pokeapi.co/api/v2/pokemon?limit=50&offset={offset}
    @SuppressWarnings("unchecked")
    public List<String> listarDaPokeApi(int offset) {
        Map<String, Object> json = restClient.get()
                .uri("/pokemon?limit={limite}&offset={offset}", LIMITE, offset)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> resultados = (List<Map<String, Object>>) json.get("results");
        return resultados.stream()
                .map(r -> (String) r.get("name"))
                .toList();
    }
}