package projekt.agents;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class List extends JPanel {
	private boolean DEBUG = false;
	protected JCheckBox check;
	private static final long serialVersionUID = 1L;
	Set<OWLNamedIndividual> individuals;
	private DefaultTableModel model;
	private ProcessRequests req = new ProcessRequests();
	private static final Object[][] rowData = {};
    private static final Object[] columnNames = {"Country",
		"City",
		"Hotel",
		"Price",
"Yes/No"};
	public List(Set<OWLNamedIndividual> individuals) throws IOException {
		this.individuals = individuals;
		GridLayout grid = new GridLayout(1,0);
		grid.setVgap(50);
		grid.setHgap(50);
		this.setLayout(grid);
		this.setSize(600, 450);
		URL url = this.getClass().getResource("/images/travel1.png");
		BufferedImage bf = ImageIO.read(url);
		setLayout(new BorderLayout());
		JLabel background=new JLabel(new ImageIcon(getClass().getResource("/images/travel1.png")));
		this.add(background);
		background.setLayout(new FlowLayout());
		JButton b = new JButton("Send");
		b.setBounds(318, 143, 98, 27);
		background.add(b);
		// Jtable
		final JTable table = new JTable();
		model = new DefaultTableModel(rowData, columnNames);
		table.setModel(model);
		table.setPreferredScrollableViewportSize(new Dimension(400, 70));
		table.setFillsViewportHeight(true);
		table.setBackground(new Color(0, 0, 0,0));
		table.setBounds(100,100,400,150);
		table.setDragEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
		table.setDefaultRenderer(getClass(), renderer);
		Iterator<OWLNamedIndividual> iterator = individuals.iterator();
		while(iterator.hasNext()) {
			Object[][] data = {{iterator.next().toString()}};
			model.addRow(data);
		}
		final JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setSize(new Dimension(200,200));
		background.add(scrollPane);
		JDialog dialog = new JDialog();
		dialog.setContentPane(this);
		dialog.pack();
		dialog.setVisible(true);
	}
	/*class MyTableModel extends AbstractTableModel {
		 * 
		 *//*
		private static final long serialVersionUID = 6368149819389038731L;
		private String[] columnNames = {"Country",
				"City",
				"Hotel",
				"Price",
		"Yes/No"};
		private Object[][] data = {
				{"Montenegro", "Budva",
					"Hotel**", new Integer(1200), new Boolean(false)},
					{"Italy", "Roma",
						"Hotel***", new Integer(1745), new Boolean(true)},
						{"Italy", "Roma",
							"Hotel***", new Integer(1745), new Boolean(true)}
		};
		private Object[][] data;
		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell.  If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

	}*/


	public static void main(String[] args) throws IOException {

		//List f = new List();

		//f.setVisible(true);
	}



}