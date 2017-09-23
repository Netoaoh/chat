package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import Util.Message;
import Util.Message.Action;

public class ServerService {
	
	private ServerSocket serverSocket;	//socket do servidor
	private ServerSocketListener listener;	//classe utilizada para receber e interpretar as mensagens recebidas do cliente
	private Socket socket; //socket
	public HashMap<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();	//lista de usuarios conectados
	
	public ServerService(){
		try {
			//inicia o socket do servidor definindo a porta de conexao
			serverSocket = new ServerSocket(5555);
			//escreve uma mensagem no console indicando que o servidor esta em execucao
			System.out.println("Server on.");
			//loop infinito que mantem o server um execucao
			while(true){
				//aguarda a conexao de algum cliente
				socket = serverSocket.accept();
				//instancia a classe utilizada para receber e interpretar as mensagens recebidas do cliente
				listener = new ServerSocketListener(socket, this);
				//inicia uma nova thread
				new Thread(listener).start();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	//metodo ustilizado para efetuar a conexao
	public boolean connect(Message message, ObjectOutputStream output){
		//verifica se nao existem usuarios conectados
		if(mapOnlines.size() == 0){
			//define o texto da mensagem e a envia para o cliente que solicitou a conexao
			message.setText("Conectado.");
			send(message, output);
			return true;
		}
		
		//verifica se o nome digitado pelo usuario ja existe na lista de usuarios online
		if(mapOnlines.containsKey(message.getFrom())){
			//define a mensagem de erro que sera retornada ao usuario
			message.setText("Nao foi possivel realizar a conexao com esse nick.");
			//envia a mensagem para o cliente
			send(message, output);
			return false;
		}
		else{
			//define a mensagem que sera retornada ao usuario
			message.setText("Conectado.");
			//envia a mensagem para o cliente
			send(message, output);
			return true;
		}
	}
	
	//metodo usado para realizar a desconexao
	public void disconect(Message message, ObjectOutputStream output){
		//remove o usuario da lista de usuarios online
		mapOnlines.remove(message.getFrom());
		//cria uma noma mensagem que sera enviada para o cliente
		Message msg = new Message();
		//configura a mensagem que sera enviada para notificar todos os usuarios
		msg.setFrom(message.getFrom());
		msg.setText("desconectou-se.");
		msg.setAction(Action.SEND_TO);
		//envia para todos os usuarios
		sendAll(msg);
	}
	
	//metodo que envia uma mensagem a um cliente que ainda nao foi adicionado a lista de usuarios
	public void send(Message message, ObjectOutputStream output){
		try {
			//envia uma mensagem para o cliente
			output.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Metodo que envia uma mensagem para um cliente
	public void sendTo(Message message){
		//loop que percorre todos os clientes conectados
		for(Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()){
			//verifica se o cliente e o destinatario
			if(kv.getKey().equals(message.getTo())){
				try {
					//envia a mensagem para o cliente
					kv.getValue().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//metodo que envia uma mensagem para todos os clientes
	public void sendAll(Message message){
		//loop que percorre todos os clientes conectados
		for(Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()){
			//verifica se o cliente nao e o cliente que enviou a mensagem
			if(!kv.getKey().equals(message.getFrom())){
				try {
					//configura a acao da mensagem e a envia
					message.setAction(Action.SEND_TO);
					kv.getValue().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//Metodo que retorna a lista de usuarios onlien
	public void sendOnlines(){
		//lista que armazenara o nome dos clientes conectados
		HashSet<String> setNames = new HashSet<String>();
		//loop que percorre todos os clientes conectados
		for(Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()){
			//adiciona o nome a lista
			setNames.add(kv.getKey());
		}
		//configura uma nova mensagem que sera enviada aos clientes
		Message message = new Message();
		message.setAction(Action.REFRESH_ONLINE);
		message.setOnlines(setNames);
		//loop que percorre todos os clientes conectados
		for(Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()){
			//verifica se o cliente nao e o cliente que enviou a mensagem
			if(!kv.getKey().equals(message.getFrom())){
				try {
					//configura a mensagem e a envia
					message.setFrom(kv.getKey());
					kv.getValue().writeObject(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
