package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;

import Util.Message;
import Util.Message.Action;

public class ServerSocketListener implements Runnable {
	//objeto de transmissao de dados (utilizado para enviar dados para o cliente)
	private ObjectOutputStream output;
	//objeto de transmissao de dados (utilizado para receber dados do cliente)
	private ObjectInputStream input;
	//referencia da classe serverService que armazena os sockets do servidor
	private ServerService serverService;
	
	public ServerSocketListener(Socket socket, ServerService serverService){
		try {
			//referencia a classe serverService
			this.serverService = serverService;
			//inicializa o objeto de transmissao
			this.output = new ObjectOutputStream(socket.getOutputStream());
			//inicializa o objeto de transmissao
			this.input = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		Message message = null;
		
		try {
			//loop que e executado enquanto sao recebidos dados do cliente
			while((message = (Message) input.readObject()) != null){
				//recebe a acao solicitada pela mensagem
				Action action = message.getAction();
				
				//verifica o tipo da acao e executa o metodo correspondente
				switch(action){
					case CONNECT:
						//chamada do metodo que realiza a conexao
						boolean isConnected = serverService.connect(message, output);
						//verifica se a conexao foi realizada com sucesso
						if(isConnected){
							//adiciona o usuario a lista de usuarios online
							serverService.mapOnlines.put(message.getFrom(), output);
							//atualiza a lista de todos os usuario
							serverService.sendOnlines();
						}
						break;
					case DISCONNECT:
						//chamada do metodo que realiza a desconexao
						serverService.disconect(message, output);
						//atualiza a lista de todos os usuario
						serverService.sendOnlines();
						return;
					case SEND_TO:
						//chamada do metodo que redireciona uma mensagem para um usuario especifico
						serverService.sendTo(message);
						break;
					case SEND_ALL:
						//chamada do metodo que redireciona uma mensagem para todos os usuarios
						serverService.sendAll(message);
						break;
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			//Realiza a desconexao e atualiza a lista de usuarios onlines caso ocorra alguma excecao
			serverService.disconect(message, output);
			serverService.sendOnlines();
		}
	}
}