package Client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JList;

import Util.Message;
import Util.Message.Action;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.SwingConstants;

public class ClientForm extends JFrame implements ActionListener {
	
	private JPanel contentPane;		//panel principal
	
	public Socket socket;	//socket que sera usado para comunicacao
	private Message message;	//mensagem que sera enviada ao servidor
	private ClientService service;	//servico que efetua a comunicacao com o servidor
	
	private JTextArea txtAreaMessage = new JTextArea();	//text area para digitacao da mensagem
	public JTextArea txtAreaChat = new JTextArea();	//text area para exibicao da conversa
	
	private JPanel pnlChat = new JPanel();	//panel que armazena o text area que exibe a conversa
	private JPanel pnlMessage = new JPanel();	//panel que armazena o text area para digitacao da mensagem
	private JPanel pnlList = new JPanel();	//panel que armazena a lista que exibe os usuarios online
	
	private JMenuBar menuBar = new JMenuBar();	//menu superior
	private JMenu mnArquivo = new JMenu("Arquivo"); //item do menu superior
	public final JMenuItem mntmDesconectar = new JMenuItem("Desconectar"); //sub item do menu superior usado para desconexao
	public final JMenuItem mntmConectar = new JMenuItem("Conectar");	//sub item do menu superior usado para conexao
	
	private JButton btnEnviar = new JButton("Enviar");	//botao que envia a mensagem
	private JButton btnLimpar = new JButton("Limpar");	//botao que limpa a mensagem digitada
	
	public JList list = new JList();	//lista que exibe os usuarios conectados
	
	private ClientSocketListener listener;	//classe utilizada para receber e interpretar as mensagens recebidas do servidor
	private final JPanel panel_1 = new JPanel(); //panel de ajuste dos componentes
	private final JPanel panel_2 = new JPanel(); //panel de ajuste dos componentes
	private final JPanel panel_3 = new JPanel(); //panel de ajuste dos componentes
	
	public ClientForm() {
		initComponents();
	}
	
	//Metodo que inicia os componente graficos da janela
	private void initComponents(){
		//define a funcao de fechamento e as dimensoes da janela
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 630, 409);
		//cria uma menu bar
		setJMenuBar(menuBar);
		//adiciona itens a menu bar
		menuBar.add(mnArquivo);
		mnArquivo.add(mntmConectar);
		mnArquivo.add(mntmDesconectar);
		
		//cria o painel principal e define suas propriedades
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		//cria um painel e define suas propriedades
		pnlList.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlList.setLayout(new BorderLayout(0, 0));
		//cria um painel e define suas propriedades
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlList.add(panel, BorderLayout.NORTH);
		//cria uma label para a lista de usuarios online e a adiciona ao panel
		JLabel lblUsuariosOnline = new JLabel("Usuarios Online");
		panel.add(lblUsuariosOnline);
		pnlList.add(list, BorderLayout.CENTER);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(pnlList, BorderLayout.WEST);
		//cria um painel e define suas propriedades
		contentPane.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		panel_3.add(pnlMessage, BorderLayout.SOUTH);
		pnlMessage.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlMessage.setLayout(new BorderLayout(0, 0));
		panel_1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		//cria um painel e define suas propriedades
		pnlMessage.add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new BorderLayout(0, 0));
		//adiciona os botoes enviar e limpar ao panel
		panel_1.add(btnEnviar);
		panel_1.add(btnLimpar, BorderLayout.SOUTH);
		
		//cria um painel e define suas propriedades
		panel_2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlMessage.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		//adiciona a area de digitacao da mensagem ao panel
		panel_2.add(txtAreaMessage, BorderLayout.CENTER);
		panel_3.add(pnlChat, BorderLayout.CENTER);
		pnlChat.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlChat.setLayout(new BorderLayout(0, 0));
		//adiciona a area de exibibicao da conversa ao panel
		pnlChat.add(txtAreaChat, BorderLayout.CENTER);
		
		//adiciona os action listener aos botoes
		btnLimpar.addActionListener(this);
		btnEnviar.addActionListener(this);
		mntmConectar.addActionListener(this);
		mntmDesconectar.addActionListener(this);
		
		//desabilita a edicao na area de exibicao da conversa
		txtAreaChat.setEnabled(false);
		txtAreaChat.setEditable(false);
		
		//metodo que seta o estado padrao dos componentes
		disconnectedForm();
	}
	
	//Metodo que desabilita os componentes quando o usuario se desconecta
	public void disconnectedForm(){
		this.mntmDesconectar.setEnabled(false);
		this.mntmConectar.setEnabled(true);
		btnLimpar.setEnabled(false);
		btnEnviar.setEnabled(false);
		txtAreaMessage.setEnabled(false);
		list.setEnabled(false);
		String[] clear = { "" };
		list.setListData(clear);
	}
	
	//Metodo que habilita os componentes quando o usuario se conecta
	public void connectedForm(){
		this.mntmDesconectar.setEnabled(true);
		this.mntmConectar.setEnabled(false);
		btnLimpar.setEnabled(true);
		btnEnviar.setEnabled(true);
		txtAreaMessage.setEnabled(true);
		list.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Verifica se o botao conectar foi pressionado
		if(e.getSource() == mntmConectar){
			//cria uma nova mensagem que sera enviada via socket
			this.message = new Message();
			String name = null;
			//exibe uma janela para que o usuario digite seu nome
			name = JOptionPane.showInputDialog("Digite o seu nick.");
			
			//verifica se o nome foi digitado
			if(!name.isEmpty()){
				//cria uma nova instancia de comunicacao com o servidor
				this.service = new ClientService();
				//chamada do metodo que se conecta ao servidor
				this.socket = service.connect();
				//cria uma nova instancia que recebe os sockets do servidor
				listener = new ClientSocketListener(socket, this);
				//inicia a thread
				new Thread(listener).start();
				
				//define as informacoes da mensagem para se conectar ao servidor
				this.message.setAction(Action.CONNECT);
				this.message.setFrom(name);
				//envia a mensagem ao servidor
				this.service.send(message);
			}
		}
		//Verifica se o botao desconectar foi pressionado
		if(e.getSource() == mntmDesconectar){
			//armazena o nome do cliente que solicitou a desconexao
			String name = this.message.getFrom();
			//cria uma nova mensagem e define os parametros enviados para o servidor
			this.message = new Message();
			this.message.setFrom(name);
			this.message.setAction(Action.DISCONNECT);
			//envia a mensagem para o servidor
			this.service.send(this.message);
			//limpa os campos de texto da interface
			this.txtAreaChat.setText("");
			this.txtAreaMessage.setText("");
			//exibe mensagem de desconexao para o cliente
			listener.disconneted();
		}
		
		//Verifica se o botao enviar foi pressionado
		if(e.getSource() == btnEnviar){
			//armazena o texto digitado e o nome do cliente
			String text = this.txtAreaMessage.getText();
			String name = this.message.getFrom();
			//cria uma nova mensagem
			this.message = new Message();
			//verifica se algum usuario da lista de usuarios online esta selecionado
			if(this.list.getSelectedIndex() > -1 ){
				//configura a mensagem para o usuario selecionado
				this.message.setTo(this.list.getSelectedValue().toString());
				this.message.setAction(Action.SEND_TO);
				this.list.clearSelection();
			} else{
				//configura a mensagem para todos caso nenhum usuario tenha sido selecionado
				this.message.setAction(Action.SEND_ALL);
			}
			
			//verifica se algo foi digitado
			if(!text.isEmpty()){
				//configura o texto da mensagem
				this.message.setFrom(name);
				this.message.setText(text);
				this.txtAreaChat.append("Eu: " + text + "\n");
				//envia a mensagem para o servidor
				this.service.send(message);
			}
			//limpa o campo de texto
			this.txtAreaMessage.setText("");
		}
		//Verifica se o botao limpar foi pressionado
		if(e.getSource() == btnLimpar){
			//limpa o campo de texto
			this.txtAreaMessage.setText("");
		}
	}
}
