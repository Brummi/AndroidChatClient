package com.brummid.chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

/*
*
* Created by Felix Wimbauer 12/10/2015
*
 */

public class Client
{
	Socket client;
	SocketAddress address;
	InputStream inputStream;
	OutputStream outputStream;
	public boolean connected;
	private PrintWriter writer;
	private BufferedReader reader;

	
	public Client(String ip, int port)
	{
		Log.i("Connecting", "Connecting to " + ip + " at port " + port);
		
		address = new InetSocketAddress(ip, port);
		client = new Socket();
		
		try {
			client.connect(address, 2000);
		} catch (IOException e1) {
			connected = false;
			Log.e("IOException", e1.getMessage());
			return;
		}
		
		try
		{
			handleConnection();
		}
		catch (IOException e) 
		{
			Log.e("IOException", e.getMessage());
		}
		
		connected = true;
	}
	
	public void handleConnection() throws IOException
	{
		inputStream = client.getInputStream();
		outputStream = client.getOutputStream();
		writer = new PrintWriter(outputStream);
		reader = new BufferedReader(new InputStreamReader(inputStream));
	}
	
	public void send(String msg)
	{
		writer.println(msg);
		writer.flush();
	}	
	
	public PrintWriter getWriter()
	{
		return writer;
	}

	public BufferedReader getReader() 
	{
		return reader;
	}

	public void close()
	{
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public Socket getSocket()
	{
		return client;
	}
}
