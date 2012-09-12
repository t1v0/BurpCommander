package burp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import burp.IHttpRequestResponse;

public class BurpCommanderUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BurpCommanderUI (BurpCommander bc) {
		commander = bc;
		
		setSize(800, 600);
        setTitle("Burp Commander");
        
        JTabbedPane tabs = new JTabbedPane();
        
        // Target Tab
        JLabel hostLabel = new JLabel("Host:");
        host = new JTextField();
        
        JLabel portLabel = new JLabel("Port:");
        port = new JTextField();
        
        JLabel sslLabel = new JLabel("use SSL");
        ssl = new JCheckBox();
        
        JPanel targetTab = new JPanel();
        	
        GroupLayout targetLayout = new GroupLayout(targetTab);
        targetTab.setLayout(targetLayout);
        targetLayout.setAutoCreateGaps(true);
        targetLayout.setAutoCreateContainerGaps(true);
        
        GroupLayout.SequentialGroup targetVert = targetLayout.createSequentialGroup();
        targetVert.addGroup(targetLayout.createParallelGroup().addComponent(hostLabel).addComponent(host, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
        targetVert.addGroup(targetLayout.createParallelGroup().addComponent(portLabel).addComponent(port, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE));
        targetVert.addGroup(targetLayout.createParallelGroup().addComponent(ssl).addComponent(sslLabel));
        targetLayout.setVerticalGroup(targetVert);
        
        GroupLayout.ParallelGroup targetHoriz = targetLayout.createParallelGroup();
        targetHoriz.addGroup(targetLayout.createSequentialGroup().addComponent(hostLabel).addComponent(host, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE));
        targetHoriz.addGroup(targetLayout.createSequentialGroup().addComponent(portLabel).addComponent(port, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE));
        targetHoriz.addGroup(targetLayout.createSequentialGroup().addComponent(ssl).addComponent(sslLabel));
        targetLayout.setHorizontalGroup(targetHoriz);
        
        tabs.add("Target", targetTab);
        
        
        // Positions Tab
        positions = new JTextPane();
        JScrollPane scrollpane = new JScrollPane(positions);
       
        JButton addMark = new JButton("Add ¤");
        addMark.addActionListener(this);
        
        JButton removeMark = new JButton("Remove ¤");
        removeMark.addActionListener(this);
        
        JButton clear = new JButton("Clear");
        clear.addActionListener(this);
        
        JLabel payloadLabel = new JLabel("Select the payload to inject");
        
        JPanel positionsTab = new JPanel();
        
        GroupLayout positionsLayout = new GroupLayout(positionsTab);
        positionsTab.setLayout(positionsLayout);
        positionsLayout.setAutoCreateGaps(true);
        positionsLayout.setAutoCreateContainerGaps(true);
        
        GroupLayout.SequentialGroup positionsVert = positionsLayout.createSequentialGroup();
        positionsVert.addComponent(payloadLabel);
        positionsVert.addGroup(positionsLayout.createParallelGroup().addComponent(scrollpane).addGroup(positionsLayout.createSequentialGroup().addComponent(addMark).addComponent(removeMark).addComponent(clear)));
        positionsLayout.setVerticalGroup(positionsVert);
        
        GroupLayout.ParallelGroup positionsHoriz = positionsLayout.createParallelGroup();
        positionsHoriz.addComponent(payloadLabel);
        positionsHoriz.addGroup(positionsLayout.createSequentialGroup().addComponent(scrollpane).addGroup(positionsLayout.createParallelGroup().addComponent(addMark).addComponent(removeMark).addComponent(clear)));
        positionsLayout.setHorizontalGroup(positionsHoriz);
        
        tabs.add("Positions", positionsTab);
        
        // Payloads Tab
        response = new JTextPane();
        JScrollPane responseScrollPane = new JScrollPane(response);
        
        JButton go = new JButton("Go");
        go.addActionListener(this);
        
        payload = new JTextField();
        payload.addActionListener(this);
        
        JLabel commandLabel = new JLabel("Type a Command");
        JLabel responseLabel = new JLabel("Response:");
        
        JPanel payloadTab = new JPanel();
        
        GroupLayout payloadLayout = new GroupLayout(payloadTab);
        payloadTab.setLayout(payloadLayout);
        payloadLayout.setAutoCreateGaps(true);
        payloadLayout.setAutoCreateContainerGaps(true);
        
        GroupLayout.SequentialGroup payloadVert = payloadLayout.createSequentialGroup();
        payloadVert.addComponent(commandLabel);
        payloadVert.addGroup(payloadLayout.createParallelGroup().addComponent(payload, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(go));
        payloadVert.addComponent(responseLabel);
        payloadVert.addComponent(responseScrollPane);
        payloadLayout.setVerticalGroup(payloadVert);
        
        GroupLayout.ParallelGroup payloadHoriz = payloadLayout.createParallelGroup();
        payloadHoriz.addComponent(commandLabel);
        payloadHoriz.addGroup(payloadLayout.createSequentialGroup().addComponent(payload, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(go));
        payloadHoriz.addComponent(responseLabel);
        payloadHoriz.addComponent(responseScrollPane);
        payloadLayout.setHorizontalGroup(payloadHoriz);
        
        tabs.add("Payload", payloadTab);
        
        getContentPane().add(tabs, "Center");
        

        
	}
	
	public void newRequest(IHttpRequestResponse req) {
		try {
			// sending from Intruder add these weird chars we need to strip out
			String temp = new String(req.getRequest());
			temp = temp.replaceAll("§", "");
			
			positions.setText(temp.toString());
			
			host.setText(req.getHost());
			port.setText(String.valueOf(req.getPort()));
			ssl.setSelected(req.getProtocol().equalsIgnoreCase("https"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setVisible(true);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setForeground(sas, Color.BLACK);
		
		if (arg0.getActionCommand().equals("Add ¤")) {
			// add mark action
			if (positions.getSelectionStart() >= 0) {
				int start = positions.getSelectionStart();
				int end = positions.getSelectionEnd();
				
				try {
					positions.getDocument().insertString(start, SPECIALCHAR, sas);
					
					if (start != end)
						positions.getDocument().insertString(end+1, SPECIALCHAR, sas); // +1 to account for the inserted char
					
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				updateHighlights();
			} 
		} else if (arg0.getActionCommand().equals("Remove ¤")) {
			if (positions.getSelectionStart() >= 0) {
				int start = positions.getSelectionStart();
				int end = positions.getSelectionEnd();
				
				//look at selection, and both forward and back one
				try {
					if (positions.getDocument().getText(start, 1).equals(SPECIALCHAR) ) {
						positions.getDocument().remove(start, 1);
						
					} else if (positions.getDocument().getEndPosition().getOffset() < start+1 && positions.getDocument().getText(start+1, 1).equals(SPECIALCHAR)) {
						positions.getDocument().remove(start+1, 1);
						
					} else if (positions.getDocument().getText(start-1, 1).equals(SPECIALCHAR)) {
						positions.getDocument().remove(start-1, 1);
						
					}
										
					if (start != end ) {
						//look at selection, and both forward and back one
						if (positions.getDocument().getText(end, 1).equals(SPECIALCHAR)) {
							positions.getDocument().remove(end, 1);
						} else if (positions.getDocument().getEndPosition().getOffset() < start+1 && positions.getDocument().getText(end+1, 1).equals(SPECIALCHAR)) {
							positions.getDocument().remove(end+1, 1);
						} else if (positions.getDocument().getText(end-1, 1).equals(SPECIALCHAR)) {
							positions.getDocument().remove(end-1, 1);
						} else if (positions.getDocument().getText(end-2, 1).equals(SPECIALCHAR)) {
							positions.getDocument().remove(end-2, 1);
						}
					}
					
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				updateHighlights();
			}
		} else if (arg0.getActionCommand().equals("Clear")) {
			
			for (int i = positions.getDocument().getStartPosition().getOffset(); i < positions.getDocument().getEndPosition().getOffset(); i++ ) {
				try {
					if ( positions.getDocument().getText(i, 1).equals(SPECIALCHAR) ) {
						positions.getDocument().remove(i, 1);
						i--;
					}
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			updateHighlights();
		} else if (arg0.getActionCommand().equals("Go") || arg0.getSource().getClass().equals(JTextField.class)) {
			StringBuffer request = new StringBuffer(positions.getText());
			
			
			int start = request.indexOf(SPECIALCHAR, 0);
			int end = request.indexOf(SPECIALCHAR, start+1);
			while (start >= 0 && end > start) {
				request.replace(start, end+1, payload.getText());
				
				start = request.indexOf(SPECIALCHAR, 0);
				end = request.indexOf(SPECIALCHAR, start+1);
			}
			
			response.setText(commander.sendRequest(host.getText(), Integer.parseInt(port.getText()), ssl.isSelected(), request.toString()));
			
		}
		
		
		
	}
	
	private void updateHighlights() {
		StyledDocument doc = positions.getStyledDocument();
		
		// set everything back to black
		SimpleAttributeSet sasBlack = new SimpleAttributeSet();
		StyleConstants.setForeground(sasBlack, Color.BLACK);
		doc.setCharacterAttributes(doc.getStartPosition().getOffset(), doc.getLength(), sasBlack, true);
		
		SimpleAttributeSet sasRed = new SimpleAttributeSet();
		StyleConstants.setForeground(sasRed, Color.RED);
		
		
		boolean toggle = false;
		for ( int i = doc.getStartPosition().getOffset(); i < doc.getEndPosition().getOffset(); i++ ) {
			try {
				if (doc.getText(i, 1).equals(SPECIALCHAR)) {
					
					if (!toggle) {
						doc.setCharacterAttributes(i, doc.getEndPosition().getOffset()-i, sasRed, true);
						toggle = !toggle;
					} else {
						doc.setCharacterAttributes(i+1, doc.getEndPosition().getOffset()-(i+1), sasBlack, true);
						toggle = !toggle;
					}
				}
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	//private StringBuffer request;
	private BurpCommander commander;
	private JTextPane positions;
	private JTextPane response;
	private JTextField payload;
	private JTextField  host;
	private JTextField  port;
	private JCheckBox ssl;
	private static final String SPECIALCHAR = "¤";


}
