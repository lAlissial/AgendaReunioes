package repositorio;

/*
 * Projeto 2 de POO
 * Grupo: Adriana Albuquerque de Moura e Alíssia Deolinda Oliveira de Lima
 *
 */

import java.util.ArrayList;
import java.util.TreeMap;

import modelo.Participante;
import modelo.Reuniao;


public class Repositorio {

	private TreeMap<String,Participante> participantes = new TreeMap<>();
	private ArrayList<Reuniao> reunioes = new ArrayList<>();

	public void adicionar(Participante p){
		participantes.put(p.getNome(),p);
	}
	public void remover(Participante p){
		participantes.remove(p.getNome());
	}
	public Participante localizarParticipante(String nome){
		return participantes.get(nome.toLowerCase());
	}


	public void adicionar(Reuniao r){
		reunioes.add(r);
	}
	public void remover(Reuniao r){
		reunioes.remove(r);
	}
	public Reuniao localizarReuniao(int id){
		for(Reuniao reu : reunioes){
			if(reu.getId() == id){
				return reu;
			}
		}
		return null;
	}

	public ArrayList<Participante> getParticipantes() {
		return new ArrayList<Participante> (participantes.values());
	}

	public ArrayList<Reuniao> getReunioes() {
		return reunioes;
	}

	
	public int getTotalParticipantes(){
		return participantes.size();
	}
	public int getTotalReunioes(){
		return reunioes.size();
	}
	
	//...demais metodos
	public void apagarParticipantes() {
		participantes.clear();
	}
	public void apagarReunioes() {
		reunioes.clear();
	}

	/*public ArrayList<Reuniao> getReunioesVazias(){
		ArrayList<Reuniao> aux = new  ArrayList<Reuniao>();

		for(Reuniao reu : reunioes)
			if(reu.getTotalParticipantes()==0)
				aux.add(reu);

		return aux;
	}

	public ArrayList<Reuniao> getReunioesNParticipantes(int n){
		ArrayList<Reuniao> aux = new  ArrayList<Reuniao>();

		for(Reuniao reu : reunioes)
			if(reu.getTotalParticipantes()==n)
				aux.add(reu);

		return aux;
	}

	public  ArrayList<Participante>  getParticipantesSemReuniao(){
		ArrayList<Participante> aux = new ArrayList<Participante>();
		for(Participante p : participantes.values())
			if(p.getReunioes()==null)
				aux.add(p);

		return aux;
	}*/

}

