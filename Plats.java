import javax.swing.JComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JFrame;
import javax.swing.JOptionPane;



public abstract class Plats extends JComponent{
	private Polygon poly;
	private boolean synlig = true;
	private Position position;
	private String namn;
	private Color färg;
	private String kategori;
	private boolean markerad = false;
	
	public Plats(Position position, String namn, String kategori){
		this.namn=namn;
		this.position=position;
		this.kategori=kategori;
		this.färg = Color.BLACK;

		if (kategori != null){
			
		if (kategori.equals("Bus")){
			this.färg= Color.RED;
		}
		if (kategori.equals("Underground")){
			this.färg= Color.BLUE;
		}
		if (kategori.equals("Train")){
			this.färg= Color.GREEN;
		}
		}
		setBounds(position.getX()-15,position.getY()-25,30,25);
	}

public Position getPosition(){
	return position;
}
public String getNamn(){
	return namn;
}
public String getKategori(){
	return kategori;
}

@Override
protected void paintComponent(Graphics g){
	if(!synlig){
		return;
	}
int[]xes = {0,15,30};
int[]yes = {0,25,0};
poly= new Polygon(xes,yes,3);
g.setColor(färg);
if(markerad){
	g.setColor(Color.WHITE);
}
g.fillPolygon(poly);

}

public void toggleMarkerad(){
	markerad = !markerad;
}

public Boolean getMarkerad() {
	return markerad;
}3
public void setMarkerad(boolean a){
	markerad=a;
}
public void setSynlig(boolean a){
	synlig = a;
}
public Boolean getSynlig(){
	return synlig;
}
public Color getFärg(){
	return färg;
}
public void infoRuta(){
	JOptionPane.showMessageDialog(this, this.namn, "Info", JOptionPane.INFORMATION_MESSAGE);
}

public abstract String nuSkaViSpara();

public static Plats parse(String saved) {
	String[] parts = saved.split(",");
	
	if (parts.length < 5) {
		return null;
	}
	int x = Integer.parseInt(parts[2]);
	int y = Integer.parseInt(parts[3]);
	Position tempPosition = new Position (x, y);
	switch (parts[0]) {
	case "Named":
		return new NamedPlace(tempPosition, parts[4], parts[1]);
	case "Described":
		return new DescribedPlace(tempPosition, parts[4], parts[1], parts.length == 6 ? parts[5] : "");
	}
	return null;
}

}
