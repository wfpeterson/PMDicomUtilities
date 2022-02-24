package gov.va.med.imaging.dicom.utilities.view.scrubber;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import gov.va.med.imaging.dicom.utilities.model.IScrubControl;

public class LogFrame extends JFrame {

	private static final long serialVersionUID = -929545800423167120L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JButton btnNewButton;
	private JTextArea txtrTextArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LogFrame frame = new LogFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LogFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 605, 297);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane);

		txtrTextArea = new JTextArea();
		txtrTextArea.setEditable(false);
		//txtrTextArea.setText("Text Area");
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(IScrubControl.SCRUBFAILEDLOG));
			txtrTextArea.read(buffer, "jTextArea");
			buffer.close();
		} 
		catch (FileNotFoundException fnfX) {
			fnfX.printStackTrace();
		} 
		catch (IOException ioX) {
			ioX.printStackTrace();
		}

		scrollPane.setViewportView(txtrTextArea);
		
		btnNewButton = new JButton("Close");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		contentPane.add(btnNewButton);
		
		setVisible(true);
	}
	
	/*
	private void printToTextArea(){
    	SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
    		
			@Override
			protected Void doInBackground() throws Exception {
				
				//String textLine;
				FileReader reader = new FileReader(ScrubControl.SCRUBFAILEDLOG);
				BufferedReader buffer = new BufferedReader(reader);
				//while((textLine = buffer.readLine()) != null){
				
					txtrTextArea.read(buffer, "jTextArea");
					
				//}
				
				return null;
			}    		
    	};
    	
    	worker.execute();
    }
    */

}
