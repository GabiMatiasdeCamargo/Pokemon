package org.example.pokemon.repositories;

import org.example.pokemon.entities.Pokemon;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PokemonRepository {

    private final Map<Long, Pokemon> banco = new ConcurrentHashMap<>();

    // Gera ids automáticos 1,2,3
    private final AtomicLong contador = new AtomicLong();

    // Devolve todos os Pokemons
    public List<Pokemon> findAll() {
        return banco.values().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
    }

    // Busca por id.
    // Optional = "pode ou não ter resultado".
    public Optional<Pokemon> findById(Long id) {
        return Optional.ofNullable(banco.get(id));
    }

    // Salva um Pokémon (serve para criar e para atualizar).
    public Pokemon save(Pokemon p) {
        // Sem id = Pokémon novo, gera o próximo id. Com id = atualização.
        if (p.getId() == null) {
            p.setId(contador.incrementAndGet());
        }
        banco.put(p.getId(), p);
        return p;
    }

    // Verifica se existe um Pokémon com esse id.
    public boolean existsById(Long id) {
        return banco.containsKey(id);
    }

    // Remove pelo id. true = removeu, false = o id não existia.
    public boolean deleteById(Long id) {
        return banco.remove(id) != null;
    }
}