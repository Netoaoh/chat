package Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Util.Message;

public class ClientService {
	
	private Socket socket;	//socket do cliente
	private ObjectOutputStream output;	//objeto que serializa os dados para a transmissao
	
	public Socket connect(){
		try {
			//inicializacao do socket pasando o ip e a porta do servidor
			this.socket = new Socket("localhost", 5555);
			//inicializacao do objeto de transmissao de dados
			this.output = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return socket;
	}
	
	//envia uma mensagem para o servidor
	public void send(Message message){
		try {
			//envia a mensagem para o servidor
			output.writeObject(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
