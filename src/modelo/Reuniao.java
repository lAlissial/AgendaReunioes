package modelo;

/*
 * Projeto 2 de POO
 * Grupo: Adriana Albuquerque de Moura e Alíssia Deolinda Oliveira de Lima
 *
 */

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Reuniao {
    private int id;
    private LocalDateTime datahora;
    private String assunto;
    private ArrayList<Participante> participantes = new ArrayList<>();
    private static DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Reuniao(int idz,String datahora, String assunto) {
        this.id = idz;
        this.datahora = LocalDateTime.parse(datahora,formatador);
        this.assunto = assunto;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int idw) {
        this.id = idw;
    }

    public LocalDateTime getDatahora() {
        return this.datahora;
    }

    public void setDatahora(LocalDateTime datahoraz) {
        this.datahora = datahoraz;
    }

    public String getAssunto() {
        return this.assunto;
    }

    public void setAssunto(String assuntoz) {
        this.assunto = assuntoz;
    }

    public void adicionar(Participante p){
        participantes.add(p);
    }
    public void remover(Participante p){
        participantes.remove(p);
    }
    public Participante localizarParticipante(String nome){
        for(Participante p : participantes){
            if(p.getNome().equals(nome.toLowerCase()))
                return p;
        }
        return null;
    }

    public ArrayList<Participante> getParticipantes() {
        return participantes;
    }

    public int getTotalParticipantes(){
        return participantes.size();
    }

    @Override
    public String toString() {
        String outputz = "Reuniao {" + "id = " + this.id + "| datahora = " + this.getDatahora().format(formatador) + "| assunto = '" + this.assunto + '\'' +  "| participantes = ";
        if (!this.participantes.isEmpty()) {
            for (Participante p: this.participantes) {
                outputz += p.getNome() + "  ";
            }
            outputz += "}";
        } else {
            outputz += "Nao tem";
        }

        return outputz;
    }

}
