
public class DescribedPlace extends Plats{

	private String beskrivning;
	
	public DescribedPlace(Position position, String namn, String kategori, String beskrivning) {
		super(position, namn, kategori);
		this.beskrivning = beskrivning;
	}
	public String getBeskrivning(){
		return this.beskrivning;
	}
	
	public String nuSkaViSpara() {
		return String.format(
				"%s,%s,%d,%d,%s,%s",
				"Described",
				this.getKategori() == null ? "None" : this.getKategori(),
						this.getPosition().getX(),
						this.getPosition().getY(),
						this.getNamn(),
						this.getBeskrivning());
	}
}
