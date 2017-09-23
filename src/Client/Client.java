package Client;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

import Util.Message;

//Classe principal da aplicacao do cliente
public class Client {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//cria a janela e a torna visivel
					ClientForm frame = new ClientForm();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
