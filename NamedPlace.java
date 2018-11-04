
public class NamedPlace extends Plats {

	public NamedPlace(Position position, String namn, String kategori) {
		super(position, namn, kategori);
	}
	
	public String nuSkaViSpara() {
		return String.format(
				"%s,%s,%d,%d,%s",
				"Named",
				this.getKategori() == null ? "None" : this.getKategori(),
						this.getPosition().getX(),
						this.getPosition().getY(),
						this.getNamn());
	}
	
}
