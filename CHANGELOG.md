# 📝 Changelog

All notable changes to TerraVision will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Planned Features
- [ ] Height map support for terrain visualization
- [ ] Cloud layer rendering for atmospheric effects
- [ ] Satellite imagery layers
- [ ] Custom marker/pin system for locations
- [ ] GeoJSON polygon support
- [ ] Day/night cycle simulation
- [ ] Keyboard shortcuts for common actions
- [ ] Accessibility improvements (screen reader support)
- [ ] Internationalization (i18n) support
- [ ] Unit test coverage expansion

### Performance Improvements
- [ ] Asynchronous texture loading with progress indicator
- [ ] Tile-based streaming for ultra-high resolution textures
- [ ] Level-of-detail (LOD) system for mesh optimization
- [ ] Viewport culling for occluded geometry
- [ ] GPU instancing for multiple markers

---

## [1.0] - 2026-04-02

### Added
- 🌐 **3D Globe Rendering**: Full spherical Earth visualization with JavaFX 3D
  - Configurable sphere resolution and divisions
  - PhongMaterial with specular highlights
  - Multi-source lighting system
  
- 🗺️ **Cartographic Projection**: Accurate map-to-sphere coordinate mapping
  - Equirectangular projection support
  - Conversion between geographic (lat/lng) and 3D coordinates
  - Focus target system for animated navigation
  
- 📍 **Country Detection & Highlighting**: 
  - CSV-based country coordinate database
  - Country lookup by name with Unicode normalization
  - Interactive popup with country information
  - Country name fuzzy matching
  
- 🖱️ **Interactive Controls**:
  - Mouse drag for globe rotation
  - Mouse wheel for smooth zooming
  - Constrained rotation ranges (-80° to +80° pitch)
  - Wrapping yaw rotation (±180°)
  
- 🎬 **Smooth Animations**:
  - Focus animation (2.4 seconds, EASE_BOTH interpolation)
  - Reset animation (1.4 seconds)
  - Camera transition callbacks
  
- ⚡ **Hardware-Aware Optimization**:
  - GPU detection via OSHI library
  - Adaptive texture subsampling based on VRAM:
    - RTX (≥8GB): 4x subsampling → 10,800×5,400 px
    - GTX (<8GB): 5x subsampling → 8,640×4,320 px
    - Integrated: 6x subsampling → 7,200×3,600 px
  - Memory-efficient ImageReader usage
  
- 🎨 **Advanced Lighting System**:
  - Ambient light for general illumination
  - Sun light for primary shading
  - Fill light for shadow softening
  - Top light for specular highlights
  - Configurable light colors and intensities
  
- 🎮 **User Interface**:
  - BorderPane-based layout
  - ScrollPane for interactive country list
  - Popup information windows
  - Country image display
  - Background music/media playback
  
- 📚 **3D Mathematics**:
  - Custom Vector3 class with operations
  - Rotation matrix calculations
  - Perspective projection for UI overlays
  - Angle normalization (±180°)
  - Value clamping with min/max bounds
  
- 📖 **Documentation**:
  - Comprehensive README with features and getting started
  - Architecture documentation (ARCHITECTURE.md)
  - Contribution guidelines (CONTRIBUTING.md)
  - MIT License

### Technical Stack
- **Java 21** with latest language features
- **JavaFX 21** for 3D rendering and UI
- **Maven 3.6+** for dependency management
- **Apache Commons CSV 1.9.0** for data parsing
- **OSHI 6.4.0** for hardware detection
- **Flexmark 0.64.8** for markdown processing

### Known Limitations
- Texture loading is synchronous (blocks UI briefly on load)
- Single-threaded rendering (JavaFX constraint)
- Limited to equirectangular texture format
- No height map or 3D terrain support
- Fixed country database (CSV-based)

---

## Release Notes

### Installation
```bash
git clone https://github.com/DevMindX89/terravision.git
cd terravision
mvn clean package install
mvn javafx:run
```

### System Requirements
- **Java**: 21 or higher
- **OS**: Windows, macOS, or Linux
- **GPU VRAM**: 2GB minimum (8GB+ recommended)
- **Maven**: 3.6 or higher

### Breaking Changes
None (initial release)

---

## Version History Reference

| Version | Date | Status |
|:---:|:---:|:---:|
| **1.0** | 2026-04-02 | ✅ Current |
| **0.9** | TBD | Alpha |
| **0.5** | TBD | Early Dev |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on:
- Reporting bugs
- Suggesting features
- Submitting pull requests
- Code style standards

---

## Versioning Strategy

TerraVision follows **Semantic Versioning** (MAJOR.MINOR.PATCH):

- **MAJOR**: Breaking API changes or major feature additions
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes and hotpatches

---

## Archive

### Version 0.x (Pre-Release)
Documentation for older versions is available in [releases](https://github.com/DevMindX89/terravision/releases).

---

<div align="center">

**Last Updated**: 2026-04-02

</div>
