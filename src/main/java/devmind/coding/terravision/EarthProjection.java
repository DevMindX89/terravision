package devmind.coding.terravision;

public final class EarthProjection {
	
	private static final double LATITUDE_CENTER_SHIFT = 14.0;
	private static final double LONGITUDE_CENTER_SHIFT = 0.0;

	private EarthProjection() {
	}

	public static FocusTarget project(GeoCoordinates coordinates) {
		double latitude = clampLatitude(coordinates.latitude());
		double longitude = normalizeLongitude(coordinates.longitude());

		Vector3 vector = toTextureAlignedVector(latitude, longitude);

		double rotateX = computeRotateX(latitude, longitude);
		double rotateY = normalizeLongitude(longitude + LONGITUDE_CENTER_SHIFT);

		return new FocusTarget(latitude, longitude, vector, rotateX, rotateY);
	}

	private static double computeRotateX(double latitude, double longitude) {
		double baseRotateX = latitude - LATITUDE_CENTER_SHIFT;
		return clampLatitude(baseRotateX);
	}

	private static Vector3 toTextureAlignedVector(double latitude, double longitude) {
		double latitudeRad = Math.toRadians(latitude);
		double longitudeRad = Math.toRadians(longitude);

		double x = Math.cos(latitudeRad) * Math.sin(longitudeRad);
		double y = Math.sin(latitudeRad);
		double z = Math.cos(latitudeRad) * Math.cos(longitudeRad);

		return new Vector3(x, y, z).normalize();
	}

	private static double clampLatitude(double latitude) {
		return Math.max(-90, Math.min(90, latitude));
	}

	private static double normalizeLongitude(double longitude) {
		while (longitude > 180) {
			longitude -= 360;
		}
		while (longitude < -180) {
			longitude += 360;
		}
		return longitude;
	}

	public record FocusTarget(double latitude, double longitude, Vector3 vector, double rotateX, double rotateY) {
	}
}