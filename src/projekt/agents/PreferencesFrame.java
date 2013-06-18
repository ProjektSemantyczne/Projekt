package projekt.agents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
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




public class PreferencesFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	public PreferencesFrame() {
		
	}
	public String[] Create(Set<OWLNamedIndividual> individuals) throws IOException {
		final String[] ret = new String[individuals.size()];
		this.setSize(800, 540); 
		this.setResizable(false);
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
		this.add(background, BorderLayout.CENTER);
		background.setLayout(new GridLayout(0,1));
		
		this.setModal(true);
		//adding other component
		JButton b = new JButton("Send");
		final JPanel panel = new JPanel( ); 
		final JPanel panel2 = new JPanel();
		final JPanel panel3 = new JPanel();
		final JPanel panelPusty = new JPanel();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		background.setBackground(Color.blue);
		panel.setLayout(new GridLayout(0,7));
		panel.setOpaque(false);
		panel2.setLayout(new GridLayout(0,7));
		panel2.setOpaque(false);
		panel3.setLayout(new GridLayout(0,7));
		panel3.setOpaque(false);
		panelPusty.setOpaque(false);
		buttonPanel.setOpaque(false);
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		Integer i = 0;
		String[] days = new String[3];
		while(it.hasNext()){
			String[] item2 = it.next().toString().split("#");
			String item3 = item2[1].split(">")[0];
			JCheckBox check = new JCheckBox(item3);
			check.setBorderPainted(false);
			check.setOpaque(false);
			//check.setOpaque(false);
			if(item3.equals("Train") || item3.equals("Plane") || item3.equals("Bus")) {
				panel3.add(check);
			}
			else if(item3.matches(".*[^0-9].*")) {
				panel2.add(check);
			}
			else {
				panel.add(check);
			}
			//panel.setBounds(300,40 ,200,100);
		}
		b.setBounds(318, 143, 98, 27);
		background.add(new JLabel("Choose preferences: "));
		background.add(panel2);
		background.add(new JLabel("Choose prefered stay in days: "));
		background.add(panel);
		background.add(new JLabel("Choose prefered transport: "));
		background.add(panel3);
		background.add(panelPusty);
		buttonPanel.add(b);
		background.add(buttonPanel);
		b.addActionListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent ae) {
				Component[] components = panel.getComponents();
				Component[] components2 = panel2.getComponents();
				Component[] components3 = panel3.getComponents();
				int x = 0;
				for (int i = 0; i < components.length; i++) {
					JCheckBox cb = (JCheckBox)components[i];
					if (cb.isSelected()) {
						ret[x++] = cb.getText();
					}
				}
				for (int i = 0; i < components2.length; i++) {
					JCheckBox cb = (JCheckBox)components2[i];
					if (cb.isSelected()) {
						ret[x++] = cb.getText();
					}
				}
				for (int i = 0; i < components3.length; i++) {
					JCheckBox cb = (JCheckBox)components3[i];
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