package fachada;

/*
 * Projeto 2 de POO
 * Grupo: Adriana Albuquerque de Moura e Alíssia Deolinda Oliveira de Lima
 *
 */

import java.time.Duration;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import modelo.Participante;
import modelo.Reuniao;
import repositorio.Repositorio;



public class Fachada {
	private static int idreuniao;             //Vai receber guardando id que guardará o ultimo id

	private static int guardandoid;           //P/ auxiliar o idreuniao vai pegar os ids da inicializacao e guardara o ultimo


	private static Repositorio repositorio = new Repositorio();	//existe somente um repositorio


	public static ArrayList<Participante> listarParticipantes() {
		return repositorio.getParticipantes();
	}


	public static ArrayList<Reuniao> listarReunioes() {
		return repositorio.getReunioes();
	}


	public static Participante criarParticipante(String nome, String email) throws  Exception{
		nome = nome.trim();
		email = email.trim();
		Participante p = repositorio.localizarParticipante(nome);
		if (p!=null){
			throw new Exception("criar participante - participante já incluso:" + nome);
		} else {
			p = new Participante(nome,email);
			repositorio.adicionar(p);
			return p;
		}
	}


	public static Reuniao criarReuniao (String datahora, String assunto, ArrayList<String> nomes) throws  Exception{
		assunto = assunto.trim();

		String pegandoexc1="";
		int seraqchegou1=0;
		String pegandoexc2="";
		int seraqchegou2=0;
		String pegandoexc3="";
		int seraqchegou3=0;

		int contador1 = 0; //conta participantes invalidos

		if (nomes.size()<2) {
			throw new Exception("criar reuniao - reuniao nao pode ser criada: participantes insuficientes");
		} else {
			idreuniao++;
			Reuniao r = new Reuniao(idreuniao, datahora, assunto);
			repositorio.adicionar(r);

			for(String nomezito: nomes) {
				try {
					adicionarParticipanteReuniao(nomezito, r.getId());
				} catch (Exception e) {
					if (e.getMessage().contains(" adicionar - participante")){
						contador1++;
						seraqchegou1 = 1;
						pegandoexc1 = e.getMessage();
					}

					if (e.getMessage().contains("adicionar novamente - participante")){
						contador1++;
						seraqchegou2 = 1;
						pegandoexc2 = e.getMessage();

					}
					if (e.getMessage().contains("inserido em uma reuniao nesse horario")) {
						contador1++;
						seraqchegou3 = 1;
						pegandoexc3 = e.getMessage();
					}
				}
			}


			if ((nomes.size()-contador1)<2){
				cancelarReuniao(r.getId());
				throw new Exception("Reuniao sera cancelada: participantes insuficientes");
			}


			if(seraqchegou1 == 1){
				throw new Exception(pegandoexc1);
			}
			if (seraqchegou2 == 1) {
				throw new Exception(pegandoexc2);
			}
			if (seraqchegou3 == 1) {
				throw new Exception(pegandoexc3);
			}

			return r;
		}

	}


	public static void 	adicionarParticipanteReuniao(String nome, int id) throws Exception {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		Duration duracao;
		long horas;

		nome = nome.trim();

		Participante p = repositorio.localizarParticipante(nome);

		Reuniao r = repositorio.localizarReuniao(id);


		for (Participante partnome: r.getParticipantes()){
			if (nome.equalsIgnoreCase(partnome.getNome())){
				throw new Exception(String.format("Nao pode adicionar novamente - participante %s ja incluso",nome));
			}
		}

		if(p==null){
			throw new Exception(String.format("Nao pode adicionar - participante %s inexistente",nome));
		}

		if(r==null){
			throw new Exception(String.format("Nao pode adicionar - reuniao %d inexistente",id));
		}


		if (p.getReunioes().isEmpty()){
			r.adicionar(p);
			p.adicionar(r);
		} else {
			for (Reuniao reuni : p.getReunioes()) {
				duracao = Duration.between(r.getDatahora(), reuni.getDatahora());
				horas = duracao.toHours();

				if (Math.abs(horas) < 2) {
					throw new Exception("adicionar pessoa a reuniao- Participante " + p.getNome() + " nao sera inserido na reuniao " + r.getId() + " pois esta inserido em uma reuniao nesse horario");
				}
			}
			r.adicionar(p);
			p.adicionar(r);
		}

		enviarEmail(p.getEmail(), "Adicionado a reuniao", String.format("Voce foi adicionado(a) a reuniao %d que ocorrera em %s",r.getId(),r.getDatahora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).toString()));

	}


	public static void 	removerParticipanteReuniao(String nome, int id) throws Exception {
		nome = nome.trim();

		Participante p = repositorio.localizarParticipante(nome);

		if(p==null){
			throw new Exception("Nao pode remover - participante inexistente");
		}

		Reuniao r = repositorio.localizarReuniao(id);
		if(r==null){
			throw new Exception("Nao pode remover - reuniao inexistente");
		}

		int controle=0;
		for (Participante partnome: r.getParticipantes()){
			if (nome.equalsIgnoreCase(partnome.getNome())) {
				r.remover(partnome);
				p.remover(r);
				controle++;
				break;
			}
		}

		enviarEmail(p.getEmail(), "Removido de reuniao", String.format("Voce foi removido(a) da reuniao %d que iria ocorrer em %s",r.getId(),r.getDatahora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).toString()));

		if (controle==1){
			if(r.getParticipantes().size()<2){
				cancelarReuniao(id);
				throw new Exception("Reuniao será cancelada: nao ha participantes suficientes");
			}
		}

		if (controle<1){
			throw new Exception("Nao se pode remover o que nao existe - participante nao esta nem incluso");
		}

	}


	public static void	cancelarReuniao(int id) throws Exception{
		Reuniao r = repositorio.localizarReuniao(id);
		if(r==null){
			throw new Exception("Nao pode cancelar - reuniao inexistente");
		}

		for(Participante p: r.getParticipantes()){
			enviarEmail(p.getEmail(), "Reuniao cancelada", String.format("Voce foi removido da reuniao %d que iria ocorrer em %s pois ela foi cancelada",r.getId(),r.getDatahora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).toString()));
			p.remover(r);
		}

		repositorio.remover(r);

	}


	public static void	inicializar() throws Exception {
		Scanner arquivo1=null;
		Scanner arquivo2=null;
		try{
			arquivo1 = new Scanner( new File("participantes.txt"));
		}catch(FileNotFoundException e){
			throw new Exception("arquivo de participantes inexistente:");
		}
		try{
			arquivo2 = new Scanner( new File("reunioes.txt"));
		}catch(FileNotFoundException e){
			throw new Exception("arquivo de reunioes inexistente:");
		}

		String linha;
		String[] partes;
		String nome, email;
		while(arquivo1.hasNextLine()) {
			linha = arquivo1.nextLine().trim();
			partes = linha.split(";");
			nome = partes[0];
			email = partes[1];
			Participante p = new Participante(nome,email);
			repositorio.adicionar(p);
		}
		arquivo1.close();


		String id, datahora, assunto;
		String[] nomes;
		while(arquivo2.hasNextLine()) {
			linha = arquivo2.nextLine().trim();
			partes = linha.split(";");
			id = partes[0];
			datahora = partes[1];
			assunto = partes[2];
			nomes = partes[3].split(",");
			Reuniao r = new Reuniao(Integer.parseInt(id), datahora, assunto);
			for(String n : nomes){
				Participante p = repositorio.localizarParticipante(n);
				r.adicionar(p);
				p.adicionar(r);
				enviarEmail(p.getEmail(), "Adicionado a reuniao", String.format("Voce foi adicionado(a) a reuniao %d que ocorrera em %s",r.getId(),r.getDatahora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).toString()));

			}
			repositorio.adicionar(r);
			guardandoid = Integer.parseInt(id);
		}
		idreuniao = guardandoid;

		arquivo2.close();
	}


	public static void	finalizar() throws Exception{
		FileWriter arquivo1=null;
		FileWriter arquivo2=null;
		try{
			arquivo1 = new FileWriter( new File("participantes.csv") ); //talevz tntar colocar p escreve aqui dentro do try
		}catch(IOException e){
			throw new Exception("problema na criação do arquivo de participantes");
		}
		try{
			arquivo2 = new FileWriter( new File("reunioes.csv") );
		}catch(IOException e){
			throw new Exception("problema na criação do arquivo de reunioes");
		}

		for(Participante p : repositorio.getParticipantes()) {
			arquivo1.write(p.getNome() +";" + p.getEmail() +"\n");
		}
		arquivo1.close();

		ArrayList<String> lista;
		String nomes;
		for(Reuniao r : repositorio.getReunioes()) {
			lista = new ArrayList<>();
			for(Participante p : r.getParticipantes()) {
				lista.add(p.getNome());
			}
			nomes = String.join(",", lista);
			arquivo2.write(r.getId()+";"+r.getDatahora()+";"+r.getAssunto()+";"+nomes+"\n");
		}
		arquivo2.close();

	}
	

	/**************************************************************
	 * 
	 * MÉTODO PARA ENVIAR EMAIL, USANDO UMA CONTA (SMTP) DO GMAIL
	 * ELE ABRE UMA JANELA PARA PEDIR A SENHA DO EMAIL DO EMITENTE
	 * ELE USA A BIBLIOTECA JAVAMAIL 1.6.2
	 * Lembrar de: 
	 * 1. desligar antivirus e de 
	 * 2. ativar opcao "Acesso a App menos seguro" na conta do gmail
	 * 
	 **************************************************************/
	public static void enviarEmail(String emaildestino, String assunto, String mensagem){
		try {
			//configurar emails
			String emailorigem = "seuemail@gmail.com";
			String senhaorigem = pegarSenha();

			//Gmail
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");

			Session session;
			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(emailorigem, senhaorigem);
				}
			});

			MimeMessage message = new MimeMessage(session);
			message.setSubject(assunto);		
			message.setFrom(new InternetAddress(emailorigem));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emaildestino));
			message.setText(mensagem);   // usar "\n" para quebrar linhas
			Transport.send(message);

			//System.out.println("enviado com sucesso");

		} catch (MessagingException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * JANELA PARA DIGITAR A SENHA DO EMAIL
	 */
	public static String pegarSenha(){
		JPasswordField field = new JPasswordField(10);
		field.setEchoChar('*'); 
		JPanel painel = new JPanel();
		painel.add(new JLabel("Entre com a senha do seu email:"));
		painel.add(field);
		JOptionPane.showMessageDialog(null, painel, "Senha", JOptionPane.PLAIN_MESSAGE);
		String texto = new String(field.getPassword());
		return texto.trim();
	}

}
