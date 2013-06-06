package csheets.ext.rtc;

import java.net.InetAddress;

public class ServerInformation {

    private String nome;
    private int pessoas;
    private InetAddress ip;

    public ServerInformation(String nome, int pessoas, InetAddress ip) {
	this.nome = nome;
	this.pessoas = pessoas;
	this.ip = ip;
    }

    public String toString() {
	return (nome + " - " + getIp() + " - " + pessoas + " pessoa(s)");
    }

    public String getNome() {
	return nome;
    }

    public String getIp() {
	String address = ip.toString();
	while (address.startsWith("/")) {
	    address = address.substring(1);
	}
	return address;
    }

    public int getPessoas() {
	return pessoas;
    }
}
