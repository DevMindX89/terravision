# 🌍 TerraVision

> A Google Earth-inspired 3D globe visualization built entirely in Java.

![TerraVision Demo](assets/demo.gif)

---

## ✨ Features

- 🌐 **3D Globe Rendering** — Full spherical Earth visualization from scratch
- 🗺️ **Cartographic Projection** — Accurate map-to-sphere coordinate mapping
- 📍 **Country Location** — Detect and highlight countries by coordinates
- 🖱️ **Interactive Controls** — Rotate and zoom the globe in real time

---

## 🚀 Getting Started

### Prerequisites
- Java 8+
- Maven 3+

### Run the project
```bash
mvn clean packgae install
mvn clean javafx:run
```

---

## 🏗️ Project Structure
```
terravision/
├── CountryLocator.java       # Country detection by coordinates
├── EarthController.java      # Input & interaction controller
├── EarthProjection.java      # Map-to-sphere projection logic
├── EarthSphere.java          # 3D sphere rendering engine
├── GeoCoordinates.java       # Geo coordinate model
├── LauncherTerraVision.java  # Entry point
├── TerraVision.java          # Main application
└── Vector3.java              # 3D vector math utilities
```

---

## 🛠️ Built With

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

**DevMindX89**  
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/DevMindX89)
