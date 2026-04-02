# 🌍 TerraVision (In progress...)

> **A Google Earth-inspired 3D globe visualization built entirely in Java.** Experience interactive, hardware-optimized Earth rendering with real-time cartographic projections.

<div align="center">
  
**[Features](#-features) • [Demo](#-demo) • [Installation](#-installation) • [Architecture](#-architecture) • [Performance](#-performance-optimization)**

</div>

---

## 📚 Documentation

| Document | Purpose | Read Time |
|:---|:---|:---:|
| **[QUICK_START.md](QUICK_START.md)** | 5-minute setup guide | 5 min |
| **[FEATURES.md](FEATURES.md)** | Complete feature documentation | 20 min |
| **[ARCHITECTURE.md](ARCHITECTURE.md)** | System design & components | 30 min |
| **[CONTRIBUTING.md](CONTRIBUTING.md)** | How to contribute | 10 min |
| **[CHANGELOG.md](CHANGELOG.md)** | Version history & roadmap | 5 min |
| **[DOCS.md](DOCS.md)** | Documentation index | 10 min |

👉 **First time here?** Start with [QUICK_START.md](QUICK_START.md) for a 5-minute setup!

---

## 📺 Demo

https://github.com/user-attachments/assets/d82f2b89-4377-4fa8-aef4-5ce4cf3b0b74

---

## ✨ Features

- 🌐 **3D Globe Rendering** — Full spherical Earth visualization from scratch using JavaFX 3D
- 🗺️ **Cartographic Projection** — Accurate map-to-sphere coordinate mapping with support for equirectangular textures
- 📍 **Country Detection** — Identify and highlight countries by geographic coordinates using CSV-based location data
- 🖱️ **Interactive Controls** — Smooth mouse-based rotation, zoom controls, and animated focus transitions
- 🎬 **Smooth Animations** — JavaFX Timeline-based camera animations for seamless transitions
- 🔍 **Advanced Lighting** — Multi-source lighting system (ambient, sun, fill, top lights) for realistic shading
- ⚡ **Hardware Optimization** — Automatic GPU detection and dynamic texture subsampling based on VRAM availability
- 🎨 **High-Resolution Textures** — Support for 43K resolution Earth textures with intelligent memory management

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Version | Notes |
|:---|:---:|:---|
| **Java** | 21+ | Required for latest JavaFX modules |
| **Maven** | 3.6+ | For dependency management and building |
| **GPU VRAM** | 2GB+ | Recommended for smooth 43K texture rendering |
| **OS** | Linux/Windows/macOS | Cross-platform support |

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/DevMindX89/terravision.git
cd terravision
```

#### 2. Build the Project
```bash
mvn clean package install
```

#### 3. Run the Application
```bash
mvn javafx:run
```

Or run directly:
```bash
java -jar target/terravision-1.0.jar
```

### Quick Start

Once the application launches:
- **Rotate Globe**: Click and drag the Earth with your mouse
- **Zoom In/Out**: Use your mouse wheel to zoom
- **Focus on Countries**: Click on interactive buttons to focus on specific locations
- **Reset View**: Click the reset button to return to the default view

---

## 🏗️ Project Architecture

### Class Structure

```
terravision/
├── LauncherTerraVision.java  # Application entry point
├── TerraVision.java          # Main UI controller & event dispatcher
├── EarthSphere.java          # Core 3D sphere rendering engine
├── EarthController.java      # Input handling & interaction logic
├── EarthProjection.java      # Cartographic map-to-sphere projection
├── CountryLocator.java       # CSV-based country coordinate detection
├── GeoCoordinates.java       # Geographic coordinate data model
└── Vector3.java              # 3D vector mathematics & transformations
```

### Key Components

#### **EarthSphere.java** (Engine)
- Manages 3D sphere geometry and material properties
- Handles camera positioning and perspective projection
- Implements mouse interaction handlers for rotation/zoom
- Optimized subsampling based on GPU capabilities

#### **EarthProjection.java** (Cartography)
- Converts 2D map coordinates to 3D sphere coordinates
- Manages country focus targets and projection logic

#### **CountryLocator.java** (Geolocation)
- Loads country data from embedded CSV file
- Provides coordinate lookup by country name
- Normalized country name matching for robustness

#### **TerraVision.java** (UI)
- Builds JavaFX UI with BorderPane layout
- Manages popup windows with country information
- Integrates music/media playback
- Handles dynamic country highlighting

---

## 🛠️ Built With

<div align="center">

![Java](https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX%2021-0078D4?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![OSHI](https://img.shields.io/badge/OSHI-FFB300?style=for-the-badge&logo=java&logoColor=white)

</div>

### Dependencies

| Library | Version | Purpose |
|:---|:---:|:---|
| **JavaFX** | 21 | 3D graphics rendering & UI |
| **Apache Commons CSV** | 1.9.0 | Country data CSV parsing |
| **OSHI (OS & Hardware Info)** | 6.4.0 | GPU detection & VRAM querying |
| **Flexmark** | 0.64.8 | Markdown processing |

---

## ⚡ Performance Optimization

TerraVision includes **intelligent hardware-aware optimization**:

### GPU Detection & Adaptive Texturing

The application automatically detects your GPU and optimizes texture loading:

```
RTX Series (≥8GB VRAM)  → 4x subsampling → Full 10,800x5,400 resolution
GTX Series (<8GB VRAM)  → 5x subsampling → 8,640x4,320 resolution
Integrated Graphics    → 6x subsampling → 7,200x3,600 resolution
```

### Lighting System
- **Ambient Light** — Overall scene illumination
- **Sun Light** — Primary directional light source
- **Fill Light** — Secondary fill illumination
- **Top Light** — Additional detail enhancement

---

## 🎮 Usage Examples

### Basic Interaction
```
Mouse Drag     → Rotate the globe freely
Scroll Wheel   → Zoom in/out with smooth animation
Button Click   → Focus on country with smooth camera transition
Reset Button   → Restore default view with 1.4s animation
```

### Camera Positioning
The camera system supports:
- **Zoom Range**: -1200 to -300 (Z-axis units)
- **Rotation Range**: -80° to +80° (pitch), -180° to +180° (yaw)
- **Smooth Transitions**: 2.4 second animated focus transitions

---

## 📊 Technical Highlights

### 3D Mathematics
- **Vector3 Transformations** — Custom 3D vector math for coordinate conversions
- **Rotation Matrices** — Real-time rotation calculations for camera orientation
- **Perspective Projection** — Accurate screen-space projection for UI overlays

### Cartography
- **Equirectangular Projection** — Maps 2D textures to 3D spheres accurately
- **Coordinate Mapping** — Converts geographic coordinates (lat/lng) to 3D space
- **Country Polygons** — Pre-computed country boundary detection

### Memory Management
- **Streaming Texture Loading** — Large textures loaded with ImageIO
- **Adaptive Subsampling** — Reduces resolution based on available VRAM
- **Resource Cleanup** — Proper disposal of ImageReader objects

---

## 🔧 Troubleshooting

### Issue: "No ImageReader available"
**Solution**: Ensure your JDK includes image codec support. Update Java to version 21+.

### Issue: "No se encontró countries.csv"
**Solution**: Verify the CSV file exists in `src/main/resources/terravision/countries.csv`

### Issue: Low FPS or stuttering
**Solution**: 
- Check GPU VRAM (see Performance section)
- Close other GPU-intensive applications
- Verify JavaFX drivers are updated

### Issue: "Module not found" errors
**Solution**: Run `mvn clean package` before `mvn javafx:run`

---

## 📚 Resources & References

- [JavaFX 3D Documentation](https://openjfx.io/)
- [OSHI Hardware Detection](https://github.com/oshi/oshi)
- [Apache Commons CSV](https://commons.apache.org/proper/commons-csv/)
- [Equirectangular Projection](https://en.wikipedia.org/wiki/Equirectangular_projection)

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👤 Author

<div align="center">

**DevMindX89**

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/DevMindX89)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/mario-guiberteau-12009639b/)

</div>

---

<div align="center">

⭐ **If you find this project useful, please consider giving it a star!**

</div>

