package projekt.agents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Frame extends JFrame implements ActionListener {
	JComboBox faceCombo = new JComboBox();
	String ret = new String();
    public Frame(Set<OWLNamedIndividual> individuals) {
    	setTitle("Wybór Pañstwa");
		JLabel emptyLabel = new JLabel("Wybierz pañstwo:");
		JButton button = new JButton("Wybierz");
		JPanel comboPanel = new JPanel();
		comboPanel.setLayout(new GridLayout(3,1));
		//JComboBox faceCombo = new JComboBox();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		comboPanel.add(emptyLabel);
		pack();
		setVisible(true);
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		while(it.hasNext()){
			String[] item2 = it.next().toString().split("#");
			String item3 = item2[1].split(">")[0];
			faceCombo.addItem(item3);
		}
		comboPanel.add(faceCombo);
		comboPanel.add(button);
		add(comboPanel);
		button.addActionListener(this);
    }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		ret = faceCombo.getSelectedItem().toString();
	}
}
