import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.AclEntry.Builder;
import java.util.*;

public class Granssnitt extends JFrame {

	private boolean forandratState = false;
	private ArrayList<Plats> markedPlaces = new ArrayList<>();
	private Map<String, ArrayList<Plats>> placesByName = new HashMap<>();
	private Map<Position, Plats> placesByPos = new HashMap<>();
	private Map<String, ArrayList<Plats>> placesByCategory = new HashMap<>();
	private Karta karta = new Karta();
	private JButton east1 = new JButton("Hide category");
	private JLabel eastLabel = new JLabel("Categories");
	private DefaultListModel eastJListDefaultListModel = new DefaultListModel();
	private JList<String> eastJList = new JList<>();
	private JMenuBar northMenuBar = new JMenuBar();
	private JMenu northArkiv = new JMenu("Archive");
	private JTextField northText = new JTextField("Search", 10);
	private JButton northLabel = new JButton("New:");
	private JButton north2 = new JButton("Search");
	private JButton north3 = new JButton("Hide");
	private JButton north4 = new JButton("Remove");
	private JButton north5 = new JButton("Coordinates");
	private JRadioButton north1 = new JRadioButton("Described");
	private JRadioButton north6 = new JRadioButton("Named");
	private JScrollPane centerScrollPane;
	Cursor cursor = Cursor.getDefaultCursor();



	Granssnitt() {

		super("Inlupp 2");
		setLayout(new BorderLayout());

		JPanel east = new JPanel();
		add(east, BorderLayout.EAST);
		east.setLayout(new BoxLayout(east, BoxLayout.PAGE_AXIS));
		east.add(Box.createVerticalGlue());
		east.add(eastLabel, BorderLayout.CENTER);
		east.add(east1, BorderLayout.CENTER);
		north4.setEnabled(false);
		north5.setEnabled(false);
		eastJListDefaultListModel = new DefaultListModel();
		eastJListDefaultListModel.addElement("Bus");
		eastJListDefaultListModel.addElement("Underground");
		eastJListDefaultListModel.addElement("Train");
		eastJList = new JList(eastJListDefaultListModel);
		eastJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll = new JScrollPane(eastJList);
		scroll.setPreferredSize(new Dimension(10, 10));
		east.add(scroll);
		east.add(Box.createVerticalGlue());

		JScrollPane centerScrollPane = new JScrollPane(karta);
		add(centerScrollPane, BorderLayout.CENTER);

		JPanel north = new JPanel();
		add(north, BorderLayout.NORTH);
		north.add(northMenuBar);
		setJMenuBar(northMenuBar);
		northMenuBar.add(northArkiv);
		JMenuItem nyVal = new JMenuItem("New Map");
		northArkiv.add(nyVal);
		nyVal.addActionListener(new AL1());
		JMenuItem oppnaVal = new JMenuItem("Load Places");
		northArkiv.add(oppnaVal);
		JMenuItem sparaVal = new JMenuItem("Save");
		northArkiv.add(sparaVal);
		JMenuItem stangVal = new JMenuItem("Exit");
		northArkiv.add(stangVal);
		stangVal.addActionListener(event -> {
			if (forandratState == true) {
				int result = JOptionPane.showConfirmDialog(null,
						"Det finns osparade ändringar. Vill du fortsätta?",
						"Osparade ändringar", JOptionPane.OK_CANCEL_OPTION);
				if (result != 0) {
					return;
				}

			}
			setVisible(false);
			dispose();
		});
		sparaVal.addActionListener(event -> {

			JFileChooser saverOfFile = new JFileChooser(".");
			int svar = saverOfFile.showSaveDialog(Granssnitt.this);

			if (svar == 0) {

				File kartFil = saverOfFile.getSelectedFile();

				ArrayList<String> linjerAttSpara = new ArrayList<>();

				for (Entry<String, ArrayList<Plats>> entry : placesByName
						.entrySet()) {
					for (Plats p : entry.getValue()) {
						linjerAttSpara.add(p.nuSkaViSpara());

					}
				}

				try {

					Files.write(kartFil.toPath(),
							String.join("\r\n", linjerAttSpara).getBytes());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Files.write(kartFil.toPath(), );
				forandratState = false;
			}
		});
		//
		oppnaVal.addActionListener(event -> {

			JFileChooser bildVÃ¤ljare = new JFileChooser(".");
			int svar = bildVÃ¤ljare.showOpenDialog(Granssnitt.this);
			if (svar == JFileChooser.CANCEL_OPTION) {
				return;
			}
			if (svar == JFileChooser.APPROVE_OPTION) {

				if (forandratState == true) {

					int result = JOptionPane.showConfirmDialog(null,
							"Det finns osparade ändringar. Vill du fortsätta?",
							"Osparade ändringar", JOptionPane.OK_CANCEL_OPTION);
					if (result == 0) {
						placesByName.clear();
						placesByPos.clear();
						markedPlaces.clear();
						karta.removeAll();
						repaint();
					}

				}
			}
			File kartFil = bildVÃ¤ljare.getSelectedFile();
			try {
				java.util.List<String> lines = Files.readAllLines(
						kartFil.toPath(), StandardCharsets.ISO_8859_1);

				for (String line : lines) {
					Plats parsedPlats = Plats.parse(line);

					if (parsedPlats != null) {
						if (!placesByName.containsKey(parsedPlats.getNamn())) {
							placesByName.put(parsedPlats.getNamn(),
									new ArrayList<>());
						}
						placesByName.get(parsedPlats.getNamn())
								.add(parsedPlats);
						placesByPos.put(parsedPlats.getPosition(), parsedPlats);
						if (!placesByCategory.containsKey(parsedPlats
								.getKategori())) {
							placesByCategory.put(parsedPlats.getKategori(),
									new ArrayList<>());

						}
						placesByCategory.get(parsedPlats.getKategori()).add(
								parsedPlats);

						karta.add(parsedPlats);

						parsedPlats.setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
						parsedPlats.addMouseListener(new PlatsSelector());

						if (parsedPlats instanceof DescribedPlace) {
							parsedPlats
									.addMouseListener(new PlatsSelectorRightClickBeskPlats());
						} else {
							parsedPlats
									.addMouseListener(new PlatsSelectorRightClick());
						}

					}
				}
				repaint();

			} catch (Exception e1) {
				e1.printStackTrace();
			}

		});
		//
		north.add(northLabel);
		northLabel.addActionListener(new NyKnappLyssnare());
		north2.addActionListener(new SearchButtonListener());
		Box verticalBox = Box.createVerticalBox();
		north.add(verticalBox);
		verticalBox.add(north6);
		verticalBox.add(north1);
		north.add(northText);
		north.add(north2);
		north.add(north3);

		north5.addActionListener(new KoordinatVal());

		north3.addActionListener(event -> {
			for (Plats x : markedPlaces) {
				x.setSynlig(false);
				x.setMarkerad(false);
			}
			repaint();
		});
		east1.addActionListener(event -> {
			String str = eastJList.getSelectedValue();

			if (placesByCategory.containsKey(str)) {
				ArrayList<Plats> kategoriPlatser = placesByCategory.get(str);
				for (Plats looper : kategoriPlatser) {
					looper.setMarkerad(false);
					looper.setSynlig(false);
					markedPlaces.remove(looper);
				}
			}
			repaint();
		});
		north4.addActionListener(event -> {
			ArrayList<Plats> clonedList = new ArrayList<>(markedPlaces);
			for (Plats y : clonedList) {
				if (y.getMarkerad() != null && y.getMarkerad()) {
					placesByName.get(y.getNamn()).remove(y);
					placesByPos.remove(y.getPosition());
					placesByCategory.get(y.getKategori()).remove(y);
					markedPlaces.remove(y);
					karta.remove(y);

					if (placesByName.get(y.getNamn()).isEmpty()) {
						placesByName.remove(y.getNamn());
					}
					if (placesByCategory.get(y.getKategori()).isEmpty()) {
						placesByCategory.remove(y.getKategori());
					}
				}
			}
			{
			}
			repaint();
		});

		eastJList.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {

				String str = eastJList.getSelectedValue();

				if (placesByCategory.containsKey(str)) {
					ArrayList<Plats> kategoriPlatser = placesByCategory
							.get(str);
					for (Plats looper : kategoriPlatser) {
						looper.setSynlig(true);
					}
				}
				repaint();
			}

		});

		north.add(north4);
		north.add(north5);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(north6);
		buttonGroup.add(north1);
		north6.setSelected(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (forandratState == true) {

					int result = JOptionPane.showConfirmDialog(null,
							"Det finns osparade ändringar. Vill du fortsätta?",
							"Osparade ändringar", JOptionPane.OK_CANCEL_OPTION);
					if (result == 0) {
						System.exit(0);
					}
				} else {
					System.exit(0);
				}
			}
		});

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(850, 750);
		setLocation(600, 100);
		setVisible(true);
	}

	// Här skapar vi en karta
	class AL1 implements ActionListener {
		public void actionPerformed(ActionEvent infogaBild) {
			JFileChooser bildVÃ¤ljare = new JFileChooser(".");
			int svar = bildVÃ¤ljare.showOpenDialog(Granssnitt.this);
			if (svar == JFileChooser.APPROVE_OPTION) {

				if (forandratState == true) {

					int result = JOptionPane.showConfirmDialog(null,
							"Det finns osparade ändringar. Vill du fortsätta?",
							"Osparade ändringar", JOptionPane.OK_CANCEL_OPTION);
					if (result == 0) {
						placesByName.clear();
						placesByPos.clear();
						markedPlaces.clear();
						karta.removeAll();

						repaint();
					} else {
						return;
					}
				}

				File kartFil = bildVÃ¤ljare.getSelectedFile();
				try {
					String filnamn = kartFil.getAbsolutePath();
					karta.setBild(filnamn);
					forandratState = false;
					north4.setEnabled(true);
					north5.setEnabled(true);

					setPreferredSize(new Dimension(750, 750));
					// setPrefferedSize(karta.getX(),karta.getY());
					karta.setLayout(null);
					pack();
					validate();
					repaint();
					BufferedImage kartBild = ImageIO.read(new File(kartFil
							.getPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class KoordinatVal extends JPanel implements ActionListener {
		private JTextField xKo = new JTextField(5);
		private JTextField yKo = new JTextField(5);

		public void actionPerformed(ActionEvent e) {
			JPanel rad1 = new JPanel();
			rad1.add(new JLabel("x:"));
			rad1.add(xKo);
			rad1.add(new JLabel("y:"));
			rad1.add(yKo);
			this.add(rad1);

			int result = JOptionPane.showConfirmDialog(null, rad1,
					"Input coordinates:", JOptionPane.OK_CANCEL_OPTION);
			if (result == 0) {
				try {
					int x = Integer.parseInt(xKo.getText());
					int y = Integer.parseInt(yKo.getText());
					Position test = new Position(x, y);
					if (placesByPos.containsKey(test)) {
						for (Plats tempPlats : markedPlaces) {
							tempPlats.setMarkerad(false);
						}
						markedPlaces = new ArrayList<>();
						markedPlaces.add(placesByPos.get(test));
						placesByPos.get(test).setMarkerad(true);
						placesByPos.get(test).setSynlig(true);
						forandratState = true;

					} else {
						JOptionPane.showMessageDialog(null,
								"Finns inga platser", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

				} catch (NumberFormatException NFE) {
					JOptionPane.showMessageDialog(null, "Endast siffror",
							"Error", JOptionPane.ERROR_MESSAGE);
				}

			}
			karta.invalidate();
			karta.repaint();
		}
	}

	class PlatsSkapande extends JPanel {
		public JTextField platsNamn;

		PlatsSkapande() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			platsNamn = new JTextField(20);

			JPanel rad1 = new JPanel();
			rad1.add(new JLabel("Plats:"));
			rad1.add(platsNamn);
			this.add(rad1);
		}
	}

	class NyKnappLyssnare implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				northLabel.setEnabled(false);
				karta.addMouseListener(new AL2());
				karta.setCursor(Cursor
						.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			} catch (Exception allErrors) {
				JOptionPane.showMessageDialog(null, "Välj en kartfil först.",
						"Error", JOptionPane.ERROR_MESSAGE);
				northLabel.setEnabled(true);
			}

		}
	}

	class SearchButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			String searchQuery = northText.getText();

			if (placesByName.containsKey(searchQuery)) {
				for (Plats tempPlats : markedPlaces) {
					tempPlats.setMarkerad(false);
				}
				markedPlaces.clear();
				ArrayList<Plats> platser = placesByName.get(searchQuery);
				for (Plats looper : platser) {
					looper.setMarkerad(true);
					looper.setSynlig(true);
					markedPlaces.add(looper);
				}
				repaint();
			}
		}
	}

	// Här skapar vi en plats
	class AL2 extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {

			int x = e.getX();
			int y = e.getY();
			Position position = new Position(x, y);
			PlatsSkapande platsSkapande = new PlatsSkapande();
			// Ruta där DescribedPlace skapas men bara om positionen inte är
			// upptagen
			if (!placesByPos.containsKey(position)) {
				if (north1.isSelected()) {
					JTextField platsBeskrivning;
					platsBeskrivning = new JTextField(20);
					JPanel rad2 = new JPanel();
					rad2.add(new JLabel("Platsens beskrivning:"));
					rad2.add(platsBeskrivning);
					platsSkapande.add(rad2);

					int val = JOptionPane.showConfirmDialog(Granssnitt.this,
							platsSkapande, "Ny plats",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (val == JOptionPane.OK_OPTION) {
						String namn = platsSkapande.platsNamn.getText();

						String beskrivning = platsBeskrivning.getText();
						Plats beskPlats = new DescribedPlace(position, namn,
								eastJList.getSelectedValue(), beskrivning);

						karta.add(beskPlats);
						beskPlats.setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
						beskPlats.addMouseListener(new PlatsSelector());
						beskPlats
								.addMouseListener(new PlatsSelectorRightClickBeskPlats());

						if (!placesByName.containsKey(namn)) {
							placesByName.put(namn, new ArrayList<>());

						}
						placesByName.get(namn).add(beskPlats);
						placesByPos.put(position, beskPlats);
						if (!placesByCategory.containsKey(beskPlats
								.getKategori())) {
							placesByCategory.put(beskPlats.getKategori(),
									new ArrayList<>());

						}
						placesByCategory.get(beskPlats.getKategori()).add(
								beskPlats);

					}
				}
				// Skapa NamedPlace
				else {
					int val = JOptionPane.showConfirmDialog(Granssnitt.this,
							platsSkapande, "Ny plats",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (val == JOptionPane.OK_OPTION) {
						String namn = platsSkapande.platsNamn.getText();
						Plats plats = new NamedPlace(position, namn,
								eastJList.getSelectedValue());

						karta.add(plats);
						plats.setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
						plats.addMouseListener(new PlatsSelector());
						plats.addMouseListener(new PlatsSelectorRightClick());
						if (!placesByName.containsKey(namn)) {
							placesByName.put(namn, new ArrayList<>());

						}
						placesByName.get(namn).add(plats);
						placesByPos.put(position, plats);
						if (!placesByCategory.containsKey(plats.getKategori())) {
							placesByCategory.put(plats.getKategori(),
									new ArrayList<>());

						}
						placesByCategory.get(plats.getKategori()).add(plats);
					}
				}

				forandratState = true;
				karta.validate();
				repaint();
			}
			northLabel.setEnabled(true);
			karta.removeMouseListener(this);
			karta.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	class PlatsSelector extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == e.BUTTON1) {

				Plats plats = (Plats) e.getSource();
				plats.toggleMarkerad();
				if (plats.getMarkerad() && !markedPlaces.contains(plats)) {
					markedPlaces.add(plats);
				} else if (!plats.getMarkerad()) {
					markedPlaces.remove(plats);
				}
				repaint();
				forandratState = true;
			}
		}
	}

	// Ta fram popup där info om platsen visas
	class PlatsSelectorRightClick extends MouseAdapter {
		public void mouseClicked(MouseEvent click) {
			if (click.getButton() == click.BUTTON3) {
				Plats plats = (Plats) click.getSource();
				JOptionPane.showMessageDialog(Granssnitt.this,
						"Platsens namn: " + plats.getNamn() + "\nKoordinater: "
								+ plats.getPosition(), "Info",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	// Popup för platser med beskrivning
	class PlatsSelectorRightClickBeskPlats extends MouseAdapter {
		public void mouseClicked(MouseEvent click) {
			if (click.getButton() == click.BUTTON3) {
				DescribedPlace plats = (DescribedPlace) click.getSource();
				JOptionPane.showMessageDialog(Granssnitt.this,
						"Platsens namn: " + plats.getNamn() + "\nKoordinater: "
								+ plats.getPosition() + "\nBeskrivning: "
								+ plats.getBeskrivning(), "Info",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

}