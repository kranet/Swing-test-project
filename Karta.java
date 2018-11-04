import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Karta extends JPanel {
	private ImageIcon bild;
	// private String filnamn;
	private int w;
	private int h;

	public Karta() {
		
	}

	public void setBild(String namn) {
		bild = new ImageIcon(namn);
		int w = bild.getIconWidth();
		int h = bild.getIconHeight();
		this.w = w;
		this.h = h;

		Dimension d = new Dimension(h, w);
		setPreferredSize(d);
		setMaximumSize(d);
		setMinimumSize(d);
		setLayout(null);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bild == null) {
			return;
		}
		g.drawImage(bild.getImage(), 0, 0, w, h, this);
	}
}
