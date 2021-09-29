package modelo;

/*
 * Projeto 2 de POO
 * Grupo: Adriana Albuquerque de Moura e Alíssia Deolinda Oliveira de Lima
 *
 */

import java.util.ArrayList;
import java.util.Objects;

public class Participante {
    private String nome;
    private String email;
    private ArrayList<Reuniao> reunioes = new ArrayList<>();

    public Participante(String nome, String email) {
        this.nome = nome.toLowerCase();
        this.email = email;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nomez) {
        this.nome = nomez;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String emailz) {
        this.email = emailz;
    }

    public ArrayList<Reuniao> getReunioes() {
        return this.reunioes;
    }

    public void adicionar(Reuniao reuniaoz) {
        this.reunioes.add(reuniaoz);
    }

    public void remover(Reuniao reuniaoz) {
        this.reunioes.remove(reuniaoz);
    }

    @Override
    public String toString() {
        String outputzito= "Participante { " + "Nome = '" + nome + '\'' + "| Email = '" + email + '\'' + "| Reunioes = ";
        if (!reunioes.isEmpty()){
            for (Reuniao r: reunioes) {
                outputzito += r.getId() + "  ";
            }
            outputzito += "}";
        } else {
            outputzito += "Nao tem";
        }

        return outputzito;
    }
}
