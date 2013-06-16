package projekt.agents;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public class SimpleTableDemo extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2988259647989202553L;
	private boolean DEBUG = false;
    static Set<OWLNamedIndividual> individuals;
    ProcessRequests pr = new ProcessRequests();
    public SimpleTableDemo(Set<OWLNamedIndividual> individuals) {
    	super(new GridLayout(1,0));
    	SimpleTableDemo.individuals = individuals;
       
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
		JButton b = new JButton("Send");
		b.setBounds(318, 143, 98, 27);
		background.add(b);
        String[] columnNames = {"Country",
        		"City",
        		"Hotel",
        		"Price",
        "Yes/No"};

        Object[][] data = {};

        final JTable table = new JTable();
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        table.setModel(model);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        if (DEBUG) {
            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    printDebugData(table);
                }
            });
        }
        Iterator<OWLNamedIndividual> iterator = individuals.iterator();
		while(iterator.hasNext()) {
			OWLNamedIndividual ind = iterator.next();
			System.out.println(ind.toString());
			Object[] data1 = {pr.stripFromIRI(ind.toString())};
			model.addRow(data1);
		}
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        background.add(scrollPane);
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
        JDialog frame = new JDialog(f, "SimpleTableDemo");
        frame.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setModal(true);
        //Create and set up the content pane.
        SimpleTableDemo newContentPane = new SimpleTableDemo(individuals);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.setSize(600, 450);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
