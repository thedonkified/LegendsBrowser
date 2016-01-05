package legends.model;

public class UndergroundRegion {
	private int id;
	private int depth;
	private String type;

	private String coords;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCoords() {
		return coords;
	}

	public void setCoords(String coords) {
		this.coords = coords;
	}

	@Override
	public String toString() {
		return "[" + id + "] " + depth + " (" + type + ")";
	}

}
