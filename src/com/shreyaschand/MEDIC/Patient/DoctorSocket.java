package com.shreyaschand.MEDIC.Patient;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DoctorSocket {
	public Socket sock;
	public PrintWriter writer;
	public BufferedReader reader;

	public DoctorSocket(Socket sock, PrintWriter writer, BufferedReader reader) {
		this.sock = sock;
		this.writer = writer;
		this.reader = reader;
	}
}