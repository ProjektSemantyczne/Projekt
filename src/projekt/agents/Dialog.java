package projekt.agents;

import java.util.Iterator;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Dialog extends JDialog{
	JComboBox faceCombo = new JComboBox();
	private static final long serialVersionUID = -2116540009796309329L;
	public Dialog() {
		
	}
	public String Create(Set<OWLNamedIndividual> individuals, String tytul) {
		Object[] items = new Object[individuals.size()];
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		Integer i = 0;
		while(it.hasNext()){
			String[] item2 = it.next().toString().split("#");
			String item3 = item2[1].split(">")[0];
			items[i++] = item3;
		}
		String s = (String)JOptionPane.showInputDialog(
                new JFrame(),
                "Wybierz " + tytul,
                "Wybierz " + tytul,
                JOptionPane.QUESTION_MESSAGE,
                null,
                items,
                null);
		return s;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
