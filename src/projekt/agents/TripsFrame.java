package projekt.agents;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TripsFrame extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2988259647989202553L;
	private boolean DEBUG = false;
	JDialog frame;
	Set<OWLNamedIndividual> individuals;
	JTable table;
	ProcessRequests pr = new ProcessRequests();
	public TripsFrame (Set<OWLNamedIndividual> individuals) {
		this.individuals = individuals;
	}
	class Thanks extends JPanel {
		
		private static final long serialVersionUID = -8647868881395842670L;
		public Thanks(Object valueAt) {
			super();
			setLayout(new BorderLayout());
			JLabel background=new JLabel(new ImageIcon(getClass().getResource("/images/travel1.png")));
			add(background);
			background.setLayout(new FlowLayout());
			JLabel l = new JLabel("Dziêkujemy za wybranie wycieczki i ¿yczymy mi³ych wakacji w " + valueAt.toString() +" :)");
			background.add(l);
		}
		
		
	}
	class Frame extends JPanel {

		private static final long serialVersionUID = 1127786648621514430L;

		public Frame() {
			super();
			setLayout(new BorderLayout());
			JLabel background=new JLabel(new ImageIcon(getClass().getResource("/images/travel1.png")));
			add(background);
			background.setLayout(new FlowLayout());
			JButton b = new JButton("Send");
			b.setBounds(318, 143, 98, 27);
			
			background.add(b);
			String[] columnNames = {"Country",
					"City",
					"Hotel",
					"Date",
					"Duration",
					"Price",
			"Yes/No"};

			Object[][] data = {};

			table = new JTable();
			DefaultTableModel model = new DefaultTableModel(data, columnNames);
			table.setModel(model);
			table.setPreferredScrollableViewportSize(new Dimension(600, 300));
			table.setFillsViewportHeight(true);

			if (DEBUG) {
				table.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						printDebugData(table);
					}
				});
			}
			Set<OWLNamedIndividual> dateInd = pr.getIndividualsFromClass("Date");
			Iterator<OWLNamedIndividual> iterator = individuals.iterator();
			while(iterator.hasNext()) {
				String hotelName = null;
				String tripDate = null;
				String duration = null;
				String country = null;
				String city = null;
				String temphotelName = null;
				String tempduration = null;
				String cena = null;
				String tripData = null;
				OWLNamedIndividual ind = iterator.next();
				Map<OWLObjectPropertyExpression, Set<OWLIndividual>> indiv = pr.getObjectProperties(ind);
				Iterator<OWLObjectPropertyExpression> cityKeyIterator = indiv.keySet().iterator();
				while(cityKeyIterator.hasNext()) {
					OWLObjectPropertyExpression key = cityKeyIterator.next();
					if(pr.stripFromIRI(key.toString()).equals("hasCountry")) {
						country = pr.stripFromIRI(indiv.get(key).iterator().next().toString());
					}
				}
				Iterator<OWLNamedIndividual> dateIterator = dateInd.iterator();
				while(dateIterator.hasNext()) {
					OWLNamedIndividual ind2 = dateIterator.next();
					Map<OWLObjectPropertyExpression, Set<OWLIndividual>> prop = pr.getObjectProperties(ind2);
					Iterator<OWLObjectPropertyExpression> indKeys = prop.keySet().iterator();
					//System.out.println(ind2.toString());
					while(indKeys.hasNext()) {
						OWLObjectPropertyExpression key = indKeys.next();
						if(pr.stripFromIRI(key.toString()).equals("hasDuration")) {
							tempduration = pr.stripFromIRI(prop.get(key).iterator().next().toString());
						}
						if(pr.stripFromIRI(key.toString()).equals("hasHotelName")) {
							temphotelName = pr.stripFromIRI(prop.get(key).iterator().next().toString());
						}
						if(pr.stripFromIRI(key.toString()).equals("hasCity")) {
							if(prop.get(key).iterator().next().equals(ind)) {
								tripDate = pr.stripFromIRI(ind2.toString());
								duration = tempduration;
								hotelName = temphotelName;
								Map<OWLDataPropertyExpression, Set<OWLLiteral>> s = pr.getDataValues(ind2);
								Iterator<OWLDataPropertyExpression> dataIterator = s.keySet().iterator();
								while(dataIterator.hasNext()) {
									OWLDataPropertyExpression dataKey = dataIterator.next();
									if(pr.stripFromIRI(dataKey.toString()).equals("hasPrice")) {
										cena = pr.getPrice(s.get(dataKey).iterator().next().toString());
									}
								}
								tripData = pr.stripFromIRI(ind2.toString());
								Object[] data1 = {country, pr.stripFromIRI(ind.toString()), hotelName, tripData, duration, cena};
								model.addRow(data1);
							}
							
						}
					}
				}
			}
			/*iterator = individuals.iterator();
			while(iterator.hasNext()) {
				String hotelName = null;
				String tripDate = null;
				String duration = null;
				String city = null;
				OWLNamedIndividual ind = iterator.next();
				Map<OWLObjectPropertyExpression, Set<OWLIndividual>> prop = pr.getObjectProperties(ind);
				Iterator<OWLObjectPropertyExpression> indKeys = prop.keySet().iterator();
				System.out.println(ind.toString());
				while(indKeys.hasNext()) {
					OWLObjectPropertyExpression key = indKeys.next();
					if(pr.stripFromIRI(key.toString()).equals("hasTrip")) {
						OWLNamedIndividual indTrip = pr.getIndividual(pr.stripFromIRI(prop.get(key).toString()));
						Map<OWLObjectPropertyExpression, Set<OWLIndividual>> propTrip = pr.getObjectProperties(indTrip);
						System.out.println(propTrip.toString());
						Iterator<OWLObjectPropertyExpression> indTripKeys = propTrip.keySet().iterator();
						while(indTripKeys.hasNext()) {
							OWLObjectPropertyExpression tripKey = indTripKeys.next();
							if(pr.stripFromIRI(tripKey.toString()).equals("hasHotelName")) {
								hotelName = pr.stripFromIRI(propTrip.get(tripKey).toString());
							}
							else if(pr.stripFromIRI(tripKey.toString()).equals("toCity")) {
								city = pr.stripFromIRI(propTrip.get(tripKey).toString());
							}
						}
					}
				}*/
			//}
			//Create the scroll pane and add the table to it.
			JScrollPane scrollPane = new JScrollPane(table);

			//Add the scroll pane to this panel.
			background.add(scrollPane);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					TableModel model = table.getModel();
					table.getSelectedRow();
					Thanks t = new Thanks(model.getValueAt(table.getSelectedRow(), 0));
					t.setOpaque(true);
					frame.setContentPane(t);
					frame.pack();
				}
			});
		}
	}
	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	public void createAndShowGUI() {
		//Create and set up the window.
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame = new JDialog(f, "Trips");
		frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setModal(true);
		//Create and set up the content pane.
		Frame newContentPane = new Frame();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);
		frame.setSize(800, 540);
		frame.setResizable(false);
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}
