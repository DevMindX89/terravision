package devmind.coding.terravision;

public class EarthController {
	private static final GeoCoordinates NO_FOCUS_OFFSET = new GeoCoordinates(0, 0);


	private final EarthSphere earthSphere;
	private final CountryLocator countryLocator;

	public EarthController(EarthSphere earthSphere) {
		this.earthSphere = earthSphere;
		this.countryLocator = new CountryLocator();
	}

	public boolean searchAndFocusCountry(String country, Runnable onFinished) {
		double[] coordinates = countryLocator.getCoordinates(country);
		if (coordinates == null) {
			System.out.println("País no encontrado: " + country);
			return false;
		}

		GeoCoordinates geoCoordinates = new GeoCoordinates(coordinates[0], coordinates[1]);
		geoCoordinates = applyFocusOverrides(country, geoCoordinates);

		EarthProjection.FocusTarget focusTarget = EarthProjection.project(geoCoordinates);

		earthSphere.resetView(() -> earthSphere.focusOn(focusTarget, onFinished));
		return true;
	}

	public void resetView(Runnable onFinished) {
		earthSphere.resetView(onFinished);
	}

	private GeoCoordinates applyFocusOverrides(String country, GeoCoordinates coordinates) {
		return new GeoCoordinates(coordinates.latitude() + NO_FOCUS_OFFSET.latitude(),
				coordinates.longitude() + NO_FOCUS_OFFSET.longitude());
	}
}
