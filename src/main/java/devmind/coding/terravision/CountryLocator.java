package devmind.coding.terravision;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CountryLocator {
	private final Map<String, double[]> countryCoordinates;

	public CountryLocator() {
		countryCoordinates = new HashMap<>();
		loadCountryData();
	}

	private void loadCountryData() {
		InputStream csvFile = getClass().getResourceAsStream("/terravision/countries.csv");

		if (csvFile == null) {
			throw new RuntimeException("No se encontró countries.csv");
		}

		try (InputStreamReader reader = new InputStreamReader(csvFile, StandardCharsets.UTF_8);
				CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true)
						.setIgnoreEmptyLines(true).setTrim(true).build().parse(reader)) {

			Map<String, Integer> headerMap = parser.getHeaderMap();
			if (headerMap == null || headerMap.isEmpty()) {
				throw new RuntimeException("countries.csv está vacío o no tiene cabecera");
			}

			String countryHeader = findHeader(headerMap, "country", "pais", "paises");
			String latitudeHeader = findHeader(headerMap, "latitude", "latitud");
			String longitudeHeader = findHeader(headerMap, "longitude", "longitud");

			if (countryHeader == null || latitudeHeader == null || longitudeHeader == null) {
				throw new RuntimeException("Cabeceras inválidas en countries.csv: " + headerMap.keySet());
			}

			for (CSVRecord record : parser) {
				String country = record.get(countryHeader).trim();
				String latitudeText = record.get(latitudeHeader).trim();
				String longitudeText = record.get(longitudeHeader).trim();

				if (country.isBlank() || latitudeText.isBlank() || longitudeText.isBlank()) {
					continue;
				}

				try {
					countryCoordinates.put(normalizeCountryName(country),
							new double[] { Double.parseDouble(latitudeText), Double.parseDouble(longitudeText) });
				} catch (NumberFormatException e) {
					System.err.println("Línea CSV inválida, ignorada: " + record);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public double[] getCoordinates(String country) {
		return countryCoordinates.get(normalizeCountryName(country));
	}

	public String normalizeCountryName(String country) {
		if (country == null) {
			return "";
		}

		if (country.equalsIgnoreCase("españa")) {
			return "españa";
		}

		String normalized = Normalizer.normalize(country.trim().toLowerCase(), Normalizer.Form.NFD);
		return normalized.replaceAll("\\p{M}+", "");
	}

	private String findHeader(Map<String, Integer> headerMap, String... acceptedNames) {
		for (String header : headerMap.keySet()) {
			String normalizedHeader = normalizeHeader(header);
			for (String acceptedName : acceptedNames) {
				if (normalizedHeader.equals(acceptedName)) {
					return header;
				}
			}
		}
		return null;
	}

	private String normalizeHeader(String header) {
		return normalizeCountryName(header).replaceAll("[^a-z]", "");
	}
}
