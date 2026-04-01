package devmind.coding.terravision;

public record Vector3(double x, double y, double z) {
	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3 normalize() {
		double length = length();
		if (length == 0) {
			return new Vector3(0, 0, 0);
		}
		return new Vector3(x / length, y / length, z / length);
	}
}
