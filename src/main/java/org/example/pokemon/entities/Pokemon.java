package org.example.pokemon.entities;

import lombok.Getter;
import lombok.Setter;

// @Getter gera os getts
// @Setter gera setts

@Getter
@Setter
public class Pokemon {

    private Long id;
    private String nome;
    private String tipo;
    private Integer altura;
    private Integer peso;

    // Construtor vazio: o Spring precisa dele para transformar o JSON em objeto.
    public Pokemon() {
    }

    public Pokemon(Long id, String nome, String tipo, Integer altura, Integer peso) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.altura = altura;
        this.peso = peso;
    }
}