/*
 * Copyright (C) Luca Lorusso 2014, luca.lor17@gmail.com
 * 
 * Portions Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 */

package speech;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

/**
 * ???
 */

public class recognizer {

    // defining static variables, such as the text output area called "terminal"
    static int commandsNumber = 999;
    static String language = new String();
    static String configPath = new String();
    static String mapPath = new String();
    static String grammarPath = new String();
    static String exitSentence = new String();
    static String[] commandsArray = new String[commandsNumber]; // same size of outputArray
    static String[] outputArray = new String[commandsNumber]; // same size of commandArray
    static ConfigurationManager configManager;
    static Recognizer recognizer;
    static boolean listening = false;
    static boolean autoStart = false;
    static Microphone microphone;
    static Result result;
    static DatagramSocket udpSocket;
    static InetAddress ipReceiver;
    static JTextArea terminal = new JTextArea(18, 40);
    static JButton startButton = new JButton("Start");
    static JButton stopButton = new JButton("Stop");
    static JTextField ipText;
    static JTextField portText;
    
    public static void terminalOut(String s) { // homemade method to print out a string on terminal
        try {
            Document doc = terminal.getDocument();
            doc.insertString(doc.getLength(), s, null);
            /*SwingUtilities.invokeLater(new Runnable() { // UNCOMMENT to autoscroll UP
                public void run() {
                    terminal.scrollRectToVisible(terminal.getBounds());
                }
            });*/
        } catch(BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    public static void startListening() {
        try {
            udpSocket = new DatagramSocket(); // establish a new UDP socket connection
            ipReceiver = InetAddress.getByName(ipText.getText());
            byte[] buffer = "1".getBytes(); // sending START signal via UDP
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipReceiver, Integer.parseInt(portText.getText()));
			udpSocket.send(packet);
            listening = true;
            terminalOut("\nRecognition STARTED - SPEAK NOW!\n");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            ipText.setEnabled(false);
            portText.setEnabled(false);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void stopListening() {
        try { // sending STOP signal via UDP
        	byte[] buffer = "-1".getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipReceiver, Integer.parseInt(portText.getText()));
			udpSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
        listening = false;
        terminalOut("Recognition STOPPED.\n");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        ipText.setEnabled(true);
        portText.setEnabled(true);
    }
    
    public static String getTimeNow() {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return(sdf.format(cal.getTime()));
    }

    public static void main(final String[] args) throws IOException { // ]]] >>> MAIN <<< [[[
        
        // Let's start parsing input arguments...
    	switch (args.length) {
	        case 1: {
	        	language = args[0].toLowerCase(); // use first argument for language specification
	        	InetAddress localIpAddress = InetAddress.getLocalHost(); // getting machine IP address
	            ipText = new JTextField(localIpAddress.getHostAddress());
	            portText = new JTextField("5005");
	            }
	            break;
	        case 2: {
	        	language = args[0].toLowerCase();
	        	String[] ipParts = args[1].split(":"); // use second argument for ip:port specification, example "127.0.0.1:5005"
	        	ipText = new JTextField(ipParts[0]); // 127.0.0.1
	        	portText = new JTextField(ipParts[1]); // 5005
	            }
	            break;
	        case 3: {
	        	language = args[0].toLowerCase();
	        	String[] ipParts = args[1].split(":"); // example 127.0.0.1:5005
	        	ipText = new JTextField(ipParts[0]); // 127.0.0.1
	        	portText = new JTextField(ipParts[1]); // 5005
	        	if (args[2].equals("start")) autoStart = true; // if "start" is passed as third argument change boolean value
	            }
	            break;
	        default: {
	        	language = "eng"; // if there are NO arguments select ENG
	        	InetAddress localIpAddress = InetAddress.getLocalHost(); // getting machine IP address
	            ipText = new JTextField(localIpAddress.getHostAddress());
	            portText = new JTextField("5005");
	            }
	            break;
        }
        configPath = "/models/" + language + ".config.xml";
        mapPath = "/models/" + language + ".net.map";
        grammarPath = "/models/" + language + ".gram";
        
        // initializing main window
        JFrame mainWindow = new JFrame("Sphinx4 - Speech Recognition");
        mainWindow.setResizable(false);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(new Dimension(440, 300));
        mainWindow.getContentPane().setLayout(new BorderLayout());
        
        // defining Swing layout objects
        JPanel topPanel = new JPanel();
        JLabel ipLabel = new JLabel("IP : port");
        ipLabel.setHorizontalAlignment(SwingConstants.LEFT);
        topPanel.add(ipLabel);
        ipText.setHorizontalAlignment(SwingConstants.RIGHT);
        ipText.setColumns(10);
        topPanel.add(ipText);
        JLabel portLabel = new JLabel(":");
        portLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(portLabel);
        portText.setHorizontalAlignment(SwingConstants.RIGHT);
        portText.setColumns(5);
        topPanel.add(portText);
        mainWindow.getContentPane().add(topPanel, BorderLayout.NORTH);
        mainWindow.getContentPane().add(new JScrollPane(terminal), BorderLayout.CENTER);
        mainWindow.pack();
        terminal.setEditable(false);
        terminal.setLineWrap(true);
        terminal.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret) terminal.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JPanel bottomPanel = new JPanel();
        
        startButton.addActionListener(new ActionListener() { // actions for START button
            public void actionPerformed(ActionEvent e) {
                startListening();
            }
        });
        bottomPanel.add(startButton);
        
        stopButton.addActionListener(new ActionListener() { // actions for STOP button
            public void actionPerformed(ActionEvent e) {
                stopListening();
            }
        });
        stopButton.setEnabled(false);
        bottomPanel.add(stopButton);
        
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() { // actions for EXIT button
            public void actionPerformed(ActionEvent e) {
            	if (listening) stopListening();
                System.exit(0);
            }
        });
        bottomPanel.add(exitButton);
        
        mainWindow.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        mainWindow.setVisible(true);
        
        // SPHINX4 parameters
        configManager = new ConfigurationManager(recognizer.class.getResource(configPath));
        recognizer = (Recognizer) configManager.lookup("recognizer");
        recognizer.allocate();
        
        // parsing network command map
        InputStreamReader mapFile = new InputStreamReader(recognizer.class.getResourceAsStream(mapPath));
        BufferedReader mapBufferReader = new BufferedReader(mapFile);
        String mapLine = null;
        int lineCount = 0;
        try {
            while ((mapLine = mapBufferReader.readLine()) != null) {
                commandsArray[lineCount] = mapLine.replaceAll(".*\\<|\\>.*", ""); // returns only what is inside <>
                outputArray[lineCount] = mapLine.replaceAll(".*\\(|\\).*", ""); // returns only what is inside ()
                lineCount = lineCount + 1;
            }
            mapBufferReader.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        
        // parsing grammar rules to show on terminal
        terminal.setText("Grammar = [ " + language.toUpperCase() + " ]\n"); // showing grammar selected
        InputStreamReader gramFile = new InputStreamReader(recognizer.class.getResourceAsStream(grammarPath));
        BufferedReader gramBufferReader = new BufferedReader(gramFile);
        String gramLine = null;
        try { // parsing grammar rules to show on terminal
            while ((gramLine = gramBufferReader.readLine()) != null) {
                if (gramLine.contains("<speechSet>")
                    || gramLine.contains("<basicCommands>")
                    || gramLine.contains("<control>")
                    || gramLine.contains("<objects>")
                    || gramLine.contains("<exit>")) {
                    terminalOut(gramLine + "\n");
                }
                if (gramLine.contains("<exit>") && !gramLine.contains("<speechSet>")) { // definind exit sentence
                    exitSentence = gramLine.replaceAll(".*\\(|\\).*", "");
                }
            }
            gramBufferReader.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        
        // start the microphone or exit the programm if this is not possible
        microphone = (Microphone) configManager.lookup("microphone");
        if (!microphone.startRecording()) {
            terminalOut("\nCannot start your microphone... :'(\n");
            recognizer.deallocate();
            System.exit(1);
        }
        else if (autoStart) startListening(); // AUTOSTART if "start" is passed as third argument
        
        // loop the recognition until the programm exits.
        while (true) {
            String outputUDP = new String();
            result = recognizer.recognize();
            if (listening) { // contidion to make START/STOP button useful
                if (result != null) { // actions to do when a word is recognized
                    String resultText = result.getBestFinalResultNoFiller();
                    if (Arrays.asList(commandsArray).contains(resultText) && (resultText != "")) {
                        terminalOut(" " + getTimeNow() + " -- " + resultText + '\n'); // show timestamp
                        outputUDP = outputArray[Arrays.asList(commandsArray).indexOf(resultText)];
                        byte[] buffer = outputUDP.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipReceiver, Integer.parseInt(portText.getText()));
                        udpSocket.send(packet);
                    }
                    if (resultText.contains(exitSentence.toLowerCase())) stopListening(); // if exit sentence is pronounced, stop listening
                } else {
                    terminalOut("I can't hear what you said... Speak louder!\n");
                }
            }
        }
    }
}