package foreverse.ksynthesis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;


public class ThresholdValueDialog {

	private double threshold;
	private JDialog dialog;
	private JButton ok;
	private JButton cancel;
	private JTextField input;
	
	public ThresholdValueDialog() {
		threshold = -1;
		
		// Create the dialog
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel message = new JLabel("Enter a clustering threshold between 0 and 1");
		panel.add(message);
		input = new JTextField();
		input.getDocument().addDocumentListener(new ThresholdChecker());
		panel.add(input);
		ok = new JButton("Ok");
		ok.setEnabled(false);
		cancel = new JButton("Cancel");
		
		JOptionPane pane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.NO_OPTION, null, new JButton[]{ok, cancel}, ok);
		dialog = pane.createDialog("Clustering threshold");
		
		// Set the behaviour of the components
		ok.addActionListener(new ButtonListener());
		cancel.addActionListener(new ButtonListener());
		
		// Display the dialog
		dialog.setVisible(true);
	}

	/**
	 * Returns the new threshold value 
	 * or -1 if the user did not entered one.
	 * @return
	 */
	public double getValue() {
		return threshold;
	}

	private class ButtonListener implements ActionListener {
		

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource().equals(ok)) {
				threshold = Double.parseDouble(input.getText());
			}
			
			dialog.setVisible(false);
		}
		
	}
	
	private class ThresholdChecker implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			checkThreshold(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			checkThreshold(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			
		}
		
		private void checkThreshold(DocumentEvent event) {
			Document doc = event.getDocument();
			try {
				String thresholdText = doc.getText(0, doc.getLength());
				double thresholdValue = Double.parseDouble(thresholdText);
				if (thresholdValue >= 0 && thresholdValue <=1) {
					ok.setEnabled(true);	
				} else {
					ok.setEnabled(false);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				ok.setEnabled(false);
			}
		}
	}
}
