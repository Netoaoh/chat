package Util;
import java.io.Serializable;
import java.util.HashSet;

public class Message implements Serializable {
	
	private String from;	//Nome do usuario que enviou a mensagem
	private String text;	//Texto da mensagem
	private String to;		//Nome do destinatario da mensagem
	private Action action;	//acao que deve ser executada
	private HashSet<String> onlines = new HashSet<String>();	//lista de usuarios online
	
	//Enum que define as possiveis acoes
	public enum Action{
		CONNECT,
		DISCONNECT,
		SEND_TO,
		SEND_ALL,
		REFRESH_ONLINE
	}
	
	//retorna o emissor
	public String getFrom() {
		return from;
	}
	//define o emissor
	public void setFrom(String from) {
		this.from = from;
	}
	//retorna o texto
	public String getText() {
		return text;
	}
	//define o destinatario
	public void setText(String text) {
		this.text = text;
	}
	//retorna o destinatario
	public String getTo() {
		return to;
	}
	//define o destinatario
	public void setTo(String to) {
		this.to = to;
	}
	//retorna a acao desejada
	public Action getAction() {
		return action;
	}
	//define a acao desejada
	public void setAction(Action action) {
		this.action = action;
	}
	//retorna a lista de usuarios online
	public HashSet<String> getOnlines() {
		return onlines;
	}
	//define a lista de usuarios online
	public void setOnlines(HashSet<String> onlines) {
		this.onlines = onlines;
	}

}
