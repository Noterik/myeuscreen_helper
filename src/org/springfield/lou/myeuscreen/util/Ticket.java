package org.springfield.lou.myeuscreen.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springfield.fs.Fs;
import org.springfield.fs.FsNode;

public class Ticket {
	private String ticket;
	private String userUrl; 
	private String ticketPropertyName;
	
	public String getTicketPropertyName() {
		return ticketPropertyName;
	}

	public void setTicketPropertyName(String ticketPropertyName) {
		this.ticketPropertyName = ticketPropertyName;
	}

	public String getTicket() {
		return ticket;
	}

	private void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	public Ticket() {
		
	}
	
	public Ticket(String userUrl, String ticketPropertyName) {
//		String userUrl = "/domain/euscreenxl/user/" + name + "/account/default";
		setUserUrl(userUrl);
		setTicketPropertyName(ticketPropertyName);
		setTicket(Ticket.generateTicketNumber());
		
		Fs.setProperty(userUrl, ticketPropertyName, getTicket());
	}
	
	public boolean checkTicketExist() {
		FsNode user = Fs.getNode(getUserUrl());
		String ticket = user.getProperty(getTicketPropertyName());

		if(ticket != null && ticket != "") {
			return true;
		}
		
		return false;
	}
	
	public static boolean checkTicketExist(String userUrl, String ticketPropertyName) {
		FsNode user = Fs.getNode(userUrl);
		String ticket = user.getProperty(ticketPropertyName);

		if(ticket != null && ticket != "") {

			return true;
		}

		return false;
	}
	
	public static boolean checkTicketExist(String userUrl, String ticketPropertyName, String ticketNumber) {
		FsNode user = Fs.getNode(userUrl);
		String ticket = user.getProperty(ticketPropertyName);

		if(ticket != null && ticket != "" && ticket == ticketNumber) {

			return true;
		}

		return false;
	}

	public static String generateTicketNumber() {
		Random random = new Random();
		DateFormat date = new SimpleDateFormat("yyyyMMdd");
		String ticketNumber = date.format(new Date()) + String.valueOf(random.nextInt(999999999));
		return ticketNumber;
	}
}
