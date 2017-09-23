package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashSet;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import Util.Message;
import Util.Message.Action;

public class ClientSocketListener implements Runnable {
	private ObjectInputStream input; //objeto de transmissao de dados
	private Socket socket; //socket do cliente
	ClientForm form; //interface do sistema
	
	public ClientSocketListener(Socket socket, ClientForm form){
		try {
			//inicializa os sockets
			this.socket = socket;
			//referencia o formulario aberto
			this.form = form;
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
			//loop que e executado enquanto sao recebidos dados do servidor
			while((message = (Message) input.readObject()) != null){
				//recebe a acao solicitada pela mensagem
				Action action = message.getAction();
				
				//verifica o tipo da acao e executa o metodo correspondente
				switch(action){
					case CONNECT:
						//chamada do metodo que exibe a confimacao de conexao
						connected(message);
						break;
					case DISCONNECT:
						//chamada do metodo que exibe a confimacao de desconexao
						disconneted();
						//fecha o socket
						socket.close();
						break;
					case SEND_TO:
						//chamada do metodo que exibe a mensagem recebida na area de mensagens
						receive(message);
						break;
					case REFRESH_ONLINE:
						//chamada do metodo que exibe a lista de usuarios online recebida do servidor
						refresh(message);
						break;
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void connected(Message message){
		//verifica se o texto da mensagem confirma a conexao
		if(!message.getText().equals("Conectado.")){
			//exibe uma caixa de dialogo com a mensagem
			JOptionPane.showMessageDialog(null, message.getText());
			return;
		}
		//altera o status dos componentes no formulario
		form.connectedForm();
		//exibe uma caixa de dialogo com a mensagem
		JOptionPane.showMessageDialog(null, message.getText());
	}
	
	public void disconneted(){
		//altera o status dos componentes no formulario
		form.disconnectedForm();
		//exibe uma caixa de dialogo com a mensagem
		JOptionPane.showMessageDialog(null, "Voce foi desconectado");
	}
	
	private void receive(Message message){
		//verifica se o texto da mensagem nao confirma uma desconexao
		if(!message.getText().equals("desconectou-se.")){
			//imprime a mensagem na area de conversa
			form.txtAreaChat.append(message.getFrom() + " disse: " + message.getText() + "\n");
		}else{
			//imprime a mensagem na area de conversa
			form.txtAreaChat.append(message.getFrom() + " " + message.getText() + "\n");
		}
	}
	
	private void refresh(Message message){
		//armazena os nomes enviados pelo servidor
		HashSet<String> names = message.getOnlines();
		//remove o nome do usuario atual
		names.remove(message.getFrom());
		
		//converte a lista para um formato compativel com o componente JList
		String[] array = (String[])names.toArray(new String[names.size()]);
		
		//adiciona os nomes ao JList
		form.list.setListData(array);
		//define o modo de selecao do JList
		form.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//define a forma com que o jList exibe os nomes
		form.list.setLayoutOrientation(JList.VERTICAL);
	}
}
