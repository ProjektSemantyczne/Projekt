package projekt.agents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.semanticweb.owlapi.model.OWLNamedIndividual;




class backImage extends JComponent {


	private static final long serialVersionUID = 1L;
	Image i;

	public backImage(Image i) {
		this.i = i;
	}
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(i, 0, 0, null);  // Drawing image using drawImage method
	}
}
public class MyFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	public MyFrame() {
		
	}
	public String[] Create(Set<OWLNamedIndividual> individuals) throws IOException {
		final String[] ret = new String[individuals.size()];
		this.setSize(600, 450);
		this.setTitle("Travel");
		URL url = this.getClass().getResource("/images/travel1.png");
		BufferedImage bf = ImageIO.read(url);
		this.setContentPane(new backImage(bf));
		this.setModal(true);
		//adding other component
		JButton b = new JButton("Send");
		final JPanel panel = new JPanel( ); 
		panel.setBackground(new Color(0, 0, 0,0));
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		Integer i = 0;
		while(it.hasNext()){
			String[] item2 = it.next().toString().split("#");
			String item3 = item2[1].split(">")[0];
			JCheckBox check = new JCheckBox(item3);
			//check.setOpaque(false);
			panel.add(check);
			panel.setBounds(300,40 ,200,100);
		}
		b.setBounds(318, 143, 98, 27);
		this.add(b);
		this.add(panel);
		b.addActionListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent ae) {
				Component[] components = panel.getComponents();
				int x = 0;
				for (int i = 0; i < components.length; i++) {
					JCheckBox cb = (JCheckBox)components[i];
					if (cb.isSelected()) {
						ret[x++] = cb.getText();
					}
				}
				dispose();
			}
		});
		return ret;
	}
}