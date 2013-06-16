package projekt.agents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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




public class MyFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	public MyFrame() {
		
	}
	public String[] Create(Set<OWLNamedIndividual> individuals) throws IOException {
		final String[] ret = new String[individuals.size()];
		this.setSize(600, 450);
		this.setTitle("Travel");
		URL url = this.getClass().getResource("/images/travel1.png");
		try {
			BufferedImage bf = ImageIO.read(url);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setLayout(new BorderLayout());
		JLabel background=new JLabel(new ImageIcon(getClass().getResource("/images/travel1.png")));
		this.add(background);
		background.setLayout(new FlowLayout());
		this.setModal(true);
		//adding other component
		JButton b = new JButton("Send");
		final JPanel panel = new JPanel( ); 
		panel.setBackground(new Color(0,0,0,0));
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		Integer i = 0;
		while(it.hasNext()){
			String[] item2 = it.next().toString().split("#");
			String item3 = item2[1].split(">")[0];
			JCheckBox check = new JCheckBox(item3);
			check.setBorderPainted(false);
			//check.setOpaque(false);
			panel.add(check);
			//panel.setBounds(300,40 ,200,100);
		}
		b.setBounds(318, 143, 98, 27);
		background.add(b);
		background.add(panel);
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