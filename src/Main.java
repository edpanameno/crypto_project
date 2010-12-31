import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Main {
	
	// The default Width and Height for the
	// Internal JPanels that will be used for
	// the send, receive and modify functions
	private static final int WIDTH = 550;
	private static final int HEIGHT = 400;

	public static void main(String[] args) {
		 
		MainWindow mainWindow = new MainWindow();
	    mainWindow.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	static class MainWindow extends JFrame {
		
		private JMenuBar menuBar;
		private JMenu fileMenu;
		
		private JMenuItem sendFile;
		private JMenuItem receiveFile;
		private JMenuItem modifyFile;
		private JMenuItem exit;
		
		public MainWindow() {
			
			this.setTitle("Cryptography Project");
			this.setSize(Main.WIDTH, Main.HEIGHT);
			
			this.setLayout(new BorderLayout(5, 10));
			
			this.menuBar = new JMenuBar();
			this.setJMenuBar(this.menuBar);
			
			this.fileMenu = new JMenu("File");
			this.menuBar.add(this.fileMenu);
			
			this.sendFile = new JMenuItem("Send File");
			this.fileMenu.add(this.sendFile);
			
			this.receiveFile = new JMenuItem("Receive File");
			this.fileMenu.add(this.receiveFile);
			
			this.modifyFile = new JMenuItem("Modify File");
			this.fileMenu.add(this.modifyFile);
			
			this.fileMenu.add(new JSeparator());
			
			this.exit = new JMenuItem("Exit");
			this.fileMenu.add(this.exit);
			
			// Action Listeners for FileMenuItems
			this.sendFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setSendPanel();
				}
				
			});
			
			this.receiveFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setReceivePanel();
				}
			});
			
			this.modifyFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setModifyPanel();
				}
			});
			
			this.exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(1);
				}
			});
		}
		
		private void setSendPanel() {
			this.getContentPane().removeAll();
			this.getContentPane().add(new SendFilePanel());
			this.pack();
		}
		
		private void setReceivePanel() {
			this.getContentPane().removeAll();
			this.getContentPane().add(new ReceiveFilePanel());
			this.pack();
		}
		
		private void setModifyPanel() {
			this.getContentPane().removeAll();
			this.getContentPane().add(new ModifyFilePanel());
			this.pack();
		}
	}
	
	@SuppressWarnings("serial")
	static class SendFilePanel extends JPanel {
		
		private JPanel topButtonPanel;
		
		private JTextField txtFilePath;
		private JTextField txtPrivateKeyPath;
		
		private JButton btnSelectFile;
		private JButton btnSelectPrivateKey;
		private JButton btnSignFile;
		private JTextArea txtSignedText;
		
		private String selectedFileFullPath = "";
		private String simpleFileName = "";
		private String selectedPrivateKeyFullPath = "";
		private String simplePrivateKeyName = "";
		
		private JFileChooser fileChooser = new JFileChooser();
		
		public SendFilePanel() {

			this.setPreferredSize(new Dimension(Main.WIDTH, Main.HEIGHT));
		
			this.topButtonPanel = new JPanel(new GridLayout(2, 2));
			this.btnSelectFile = new JButton("Select File to Send");
			this.btnSelectFile.setSize(new Dimension(10, 20));
			
			this.btnSelectPrivateKey = new JButton("Select Private Key");
			this.btnSelectPrivateKey.setSize(new Dimension(10, 200));
			
			this.txtFilePath = new JTextField();
			this.txtFilePath.setPreferredSize(new Dimension(100, 20));
			
			this.txtPrivateKeyPath = new JTextField();
			this.txtPrivateKeyPath.setPreferredSize(new Dimension(100, 20));
			
			this.btnSignFile = new JButton("Sign File");			
			this.txtSignedText = new JTextArea();
			
			this.setLayout(new BorderLayout());			
			
			this.topButtonPanel.add(this.btnSelectFile);
			this.topButtonPanel.add(this.txtFilePath);
			this.topButtonPanel.add(this.btnSelectPrivateKey);
			this.topButtonPanel.add(this.txtPrivateKeyPath);
			this.add(this.topButtonPanel, BorderLayout.NORTH);

			this.add(this.txtSignedText, BorderLayout.CENTER);
			this.add(this.btnSignFile, BorderLayout.SOUTH);
			
			this.btnSelectFile.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int returnValue = fileChooser.showOpenDialog(null);
					
					if(returnValue == JFileChooser.APPROVE_OPTION) {
						
						selectedFileFullPath = fileChooser.getSelectedFile().getAbsolutePath();
						simpleFileName = fileChooser.getSelectedFile().getName();
						txtFilePath.setText(simpleFileName);
					}
				}
			});
			
			this.btnSelectPrivateKey.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					int returnValue = fileChooser.showOpenDialog(null);
					
					if(returnValue == JFileChooser.APPROVE_OPTION) {
						
						selectedPrivateKeyFullPath = fileChooser.getSelectedFile().getAbsolutePath();
						simplePrivateKeyName = fileChooser.getSelectedFile().getName();
						txtPrivateKeyPath.setText(simplePrivateKeyName);
					}
					
				}
			});
			
			this.btnSignFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(selectedFileFullPath.isEmpty() || selectedPrivateKeyFullPath.isEmpty()) {
						JOptionPane.showMessageDialog(null, "You must select a File and a Private Key");
					}
					else {
						try {
							
							DigitalSignature.signAndSendFile(selectedFileFullPath, selectedPrivateKeyFullPath);
							File signedFile = new File(selectedFileFullPath + ".signed");
							FileReader fileReader = new FileReader(signedFile);
							
							// Show the content of the signed file in the 
							// text area provided.
							txtSignedText.setLineWrap(true);
							txtSignedText.read(fileReader, signedFile.toString());
							fileReader.close();
							
						} catch (NoSuchAlgorithmException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	@SuppressWarnings("serial")
	static class ReceiveFilePanel extends JPanel {
		
		private JPanel topButtonPanel;
		
		private JButton btnSelectFile;
		private JTextField txtSelectedFilePath;
		
		private JButton btnSelectPublicKey;
		private JTextField txtPublicKeyPath;
		
		private JButton btnVerifyFile;
		private JTextArea txtMessage;
		
		private String selectedFileFullPath = "";
		private String simpleFileName = "";
		private String selectedPublicKeyFullPath = "";
		private String simplePublicKeyName = "";
		
		private JFileChooser fileChooser = new JFileChooser();
		
		public ReceiveFilePanel() {
			
			this.setPreferredSize(new Dimension(Main.WIDTH, Main.HEIGHT));			
			this.topButtonPanel = new JPanel(new GridLayout(2, 2));
			
			this.btnSelectFile = new JButton("Select File to Receive");
			this.txtSelectedFilePath = new JTextField();
			
			this.btnSelectPublicKey = new JButton("Public Key");
			this.txtPublicKeyPath = new JTextField();
			
			this.topButtonPanel.add(this.btnSelectFile, BorderLayout.NORTH);
			this.topButtonPanel.add(this.txtSelectedFilePath, BorderLayout.NORTH);
			this.topButtonPanel.add(this.btnSelectPublicKey, BorderLayout.NORTH);
			this.topButtonPanel.add(this.txtPublicKeyPath, BorderLayout.NORTH);
			
			this.setLayout(new BorderLayout());
			
			this.txtMessage = new JTextArea();
			this.add(this.txtMessage, BorderLayout.CENTER);
			
			this.add(this.topButtonPanel, BorderLayout.NORTH);			
			this.btnVerifyFile = new JButton("Verify File");
			this.add(this.btnVerifyFile, BorderLayout.SOUTH);
			
			this.btnSelectFile.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					int returnValue = fileChooser.showOpenDialog(null);
						
					if(returnValue == JFileChooser.APPROVE_OPTION) {
							
						selectedFileFullPath = fileChooser.getSelectedFile().getAbsolutePath();
						simpleFileName = fileChooser.getSelectedFile().getName();
						txtSelectedFilePath.setText(simpleFileName);
					}
				}
			});
			
			this.btnSelectPublicKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					int returnValue = fileChooser.showOpenDialog(null);
					
					if(returnValue == JFileChooser.APPROVE_OPTION) {
						
						selectedPublicKeyFullPath = fileChooser.getSelectedFile().getAbsolutePath();
						simplePublicKeyName = fileChooser.getSelectedFile().getName();
						txtPublicKeyPath.setText(simplePublicKeyName);
					}
				}
				
			});
			
			this.btnVerifyFile.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(selectedFileFullPath.isEmpty() || selectedPublicKeyFullPath.isEmpty()) {
						JOptionPane.showMessageDialog(null, "You must select a File and Public Key");
					}
					else {
						
						try {
							
							if(DigitalSignature.hasFileBeenModified(selectedFileFullPath, selectedPublicKeyFullPath)) {
							
								JOptionPane.showMessageDialog(null, "The file '" + simpleFileName + "' has been modified", "File Modified", JOptionPane.WARNING_MESSAGE);
								txtMessage.setText("The file '" + simpleFileName + "' has been modified.");
							}
							else {
								
								// The integrity of the file has been verified
								// Show a message box stating so, then show the content
								// of the file.
								JOptionPane.showMessageDialog(null, "The integrity of the file '" + simpleFileName + "' has been verified!");
								
								ObjectInputStream sentFile = new ObjectInputStream(new FileInputStream(selectedFileFullPath));
								sentFile.readObject();
								byte[] fileContentByteArray = new byte[sentFile.available()];
								sentFile.read(fileContentByteArray);
								sentFile.close();
								
								// Show the content of the message (without the signature)
								// to the user
								txtMessage.setText(new String(fileContentByteArray));
							}
							
						} catch (NoSuchAlgorithmException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
				
			});
		}
	}
	
	@SuppressWarnings("serial")
	static class ModifyFilePanel extends JPanel {
		
		private JPanel topButtonPanel;
		
		private JButton btnFileToModify;
		private JTextField txtFileToModify;
		private JLabel lblIndexToModify;
		private JTextField txtIndexToModify;
		private JTextArea txtFileModificationMessage;
		private JButton btnModifyFile;
		
		private String fullFilePathName = "";
		private String simpleFileName = "";
		int indexToModify;
		
		private JFileChooser fileChooser = new JFileChooser();
		
		public ModifyFilePanel() {
			
			this.setPreferredSize(new Dimension(Main.WIDTH, Main.HEIGHT));			
			this.topButtonPanel = new JPanel(new GridLayout(2, 2));
							
			this.btnFileToModify = new JButton("File to Modify");
			this.txtFileToModify = new JTextField();
			this.lblIndexToModify = new JLabel("Index to Modify");
			
			this.txtIndexToModify = new JTextField();
			this.txtFileModificationMessage = new JTextArea();
			this.btnModifyFile = new JButton("Modify File!");
			
			this.setLayout(new BorderLayout());
			
			this.topButtonPanel.add(this.btnFileToModify, BorderLayout.NORTH);
			this.topButtonPanel.add(this.txtFileToModify, BorderLayout.NORTH);
			this.topButtonPanel.add(this.lblIndexToModify, BorderLayout.NORTH);
			this.topButtonPanel.add(this.txtIndexToModify, BorderLayout.NORTH);
			
			this.add(this.topButtonPanel, BorderLayout.NORTH);
			this.add(this.txtFileModificationMessage, BorderLayout.CENTER);
			this.add(this.btnModifyFile, BorderLayout.SOUTH);
			
			// Button Event Handlers
			this.btnFileToModify.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					int returnValue = fileChooser.showOpenDialog(null);
					
					if(returnValue == JFileChooser.APPROVE_OPTION) {
						
						fullFilePathName = fileChooser.getSelectedFile().getAbsolutePath();
						simpleFileName = fileChooser.getSelectedFile().getName();
						txtFileToModify.setText(simpleFileName);
					}
				}	
			});
			
			this.btnModifyFile.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(fullFilePathName.isEmpty() || txtIndexToModify.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "Please Select a File to modify and index to modify");
					}
					else {
						try {
							
							ChangeByte.modifyFile(fullFilePathName, new Integer(txtIndexToModify.getText()));
							
							// Show message inside of text area showing the user that the file
							// has been modified successfully.
							txtFileModificationMessage.setText("The file '" + simpleFileName + "' has been modified at the specified index.");
						} catch (NumberFormatException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}
	}
}
