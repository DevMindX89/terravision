# 🏗️ TerraVision Architecture

This document provides an in-depth overview of TerraVision's architecture, design patterns, and component interactions.

---

## 📊 System Architecture

```
┌─────────────────────────────────────────────────────────┐
│          TerraVision Application Layer                   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────┐         ┌──────────────────┐     │
│  │   UI Layer       │         │  Event Bus       │     │
│  │  (TerraVision)   │◄────────┤  Controller      │     │
│  └────────┬─────────┘         └──────────────────┘     │
│           │                                              │
│  ┌────────▼──────────────────────────────────────┐     │
│  │      Rendering Engine Layer                    │     │
│  │                                                │     │
│  │  ┌─────────────────┐  ┌─────────────────┐    │     │
│  │  │  EarthSphere    │  │  Projection     │    │     │
│  │  │  - 3D Rendering │  │  - Cartography  │    │     │
│  │  │  - Camera Ctrl  │  │  - Coordinates  │    │     │
│  │  └─────────────────┘  └─────────────────┘    │     │
│  │                                                │     │
│  └────────┬────────────────────────────────────┬─┘     │
│           │                                      │       │
│  ┌────────▼──────────┐            ┌─────────────▼──┐   │
│  │  Data Layer       │            │  GPU Layer     │   │
│  │                   │            │                │   │
│  │ - Vector3        │            │ - Texture Mgmt │   │
│  │ - GeoCoordinates │            │ - GPU Detect   │   │
│  │ - CountryLocator │            │ - Memory Opt   │   │
│  └───────────────────┘            └────────────────┘   │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow

### Interaction Flow
```
User Input (Mouse)
        ↓
   EarthController
   - Captures mouse events
   - Calculates deltas
        ↓
   EarthSphere
   - Updates rotations
   - Applies constraints
        ↓
   JavaFX Rendering
   - Updates transforms
   - Re-renders frame
```

### Projection Flow
```
Geographic Coordinates (lat, lng)
        ↓
   EarthProjection
   - Convert to 3D space
   - Apply rotations
        ↓
   3D Sphere Coordinates (x, y, z)
        ↓
   Camera Projection
   - Perspective transform
   - Screen coordinates
        ↓
   2D Screen Position (x, y)
```

---

## 📦 Component Details

### 1. **LauncherTerraVision** (Entry Point)
```java
Purpose: Application bootstrap
Responsibilities:
  - JVM startup configuration
  - Application initialization
  - Delegates to TerraVision.main()
```

### 2. **TerraVision** (Main Application)
**Inheritance**: `Application` (JavaFX)

**Key Features**:
- UI layout management (BorderPane)
- Country information popup display
- Media/music playback
- Event routing

**Key Methods**:
```java
public void start(Stage primaryStage)
  - Initializes 3D scene
  - Builds UI components
  - Sets up event handlers

public void showCountryInfo(String countryName)
  - Fetches country coordinates
  - Displays popup information
  - Manages country highlighting

private void setupMediaPlayer()
  - Configures background music
  - Handles playback state
```

**Dependencies**:
- CountryLocator
- EarthController
- EarthSphere

---

### 3. **EarthSphere** (Rendering Engine)
**Core Responsibility**: 3D sphere rendering and interaction

**Architecture**:
```
Transform Hierarchy:
  world (Group)
    └── yawGroup (Group) - Y-axis rotation
        └── pitchGroup (Group) - X-axis rotation
            └── earth (Sphere) - Actual 3D geometry
```

**Key Components**:

#### Geometry Setup
```java
private Sphere earth;                    // 3D sphere mesh
private PhongMaterial material;          // Lighting model
private Image textureMap;                // Earth texture
```

#### Camera System
```java
private PerspectiveCamera camera;        // View frustum
private double cameraZ;                  // Zoom position
  - Range: -1200 (zoomed out) to -300 (zoomed in)
  - Default: -900
  - Focus: -300 (closest)
```

#### Rotation Controls
```java
private Rotate rotateX;                  // Pitch rotation
private Rotate rotateY;                  // Yaw rotation
  - Pitch Range: -80° to +80°
  - Yaw Range: -180° to +180° (wraps)
```

#### Animation System
```java
private Timeline activeAnimation;        // Current animation
- Used for smooth camera transitions
- Supports concurrent multiple KeyFrames
- 2.4 second focus duration
```

**Key Methods**:

```java
public SubScene createSubScene(double width, double height)
  - Creates 3D rendering surface
  - Sets up mouse interaction handlers
  
public void focusOn(EarthProjection.FocusTarget target, Runnable callback)
  - Animates camera to focus on location
  - Smooth interpolation using EASE_BOTH
  - Calls callback on completion

public double[] projectVectorToSubScene(Vector3 v)
  - Converts 3D world coordinates to 2D screen
  - Applies camera transforms
  - Returns [x, y] screen position
```

**Lighting Model**:
```
┌─ Ambient Light
│  └─ Illuminates entire scene evenly
│     Color: rgb(0.56, 0.58, 0.62) - Bluish-white
│
├─ Sun Light (Primary)
│  └─ Main directional light
│     Position: (-1380, -220, -1520)
│     Color: rgb(1.0, 0.99, 0.97) - Warm white
│
├─ Fill Light (Secondary)
│  └─ Softens shadows, adds depth
│     Position: (980, 120, -760)
│     Color: rgb(0.34, 0.38, 0.46) - Cool blue-gray
│
└─ Top Light (Detail)
   └─ Adds specular highlights
      Position: (-120, -980, -320)
      Color: rgb(0.22, 0.28, 0.36) - Very dark
```

---

### 4. **EarthController** (Input Handler)
**Responsibility**: Transform user input into camera/sphere updates

**State Management**:
```java
private double anchorX, anchorY;         // Initial mouse position
private double anchorAngleX, anchorAngleY; // Initial rotation angles
private EarthSphere earthSphere;         // Reference to sphere
```

**Event Handlers**:
```
onMousePressed   → Capture initial state
                → Stop ongoing animation

onMouseDragged   → Calculate delta movement
                → Update rotations
                → Apply clamp constraints

onScroll         → Calculate zoom delta
                → Update camera Z position
                → Clamp to zoom range
```

---

### 5. **EarthProjection** (Cartography Engine)
**Responsibility**: Convert between 2D map and 3D sphere coordinates

**Key Concepts**:

#### Equirectangular Projection
```
The Earth texture uses equirectangular projection:
- Horizontal: longitude (-180° to +180°)
- Vertical: latitude (-90° to +90°)
- Linear pixel mapping

Map Coordinates → Geographic Coordinates → 3D Sphere
```

#### FocusTarget (Data Class)
```java
public record FocusTarget(
    String countryName,
    double rotateX,              // Pitch angle
    double rotateY,              // Yaw angle
    double zoomLevel            // Camera Z position
)
```

**Key Methods**:
```java
public FocusTarget locate(String countryName)
  - Finds country in database
  - Calculates center coordinates
  - Returns animation target

public Vector3 projectToSphere(double latitude, double longitude, double radius)
  - Uses geographic to 3D conversion:
    x = r * cos(lat) * cos(lng)
    y = r * sin(lat)
    z = r * cos(lat) * sin(lng)
```

---

### 6. **CountryLocator** (Geolocation Database)
**Responsibility**: Country data lookup and coordinate detection

**Data Structure**:
```java
private Map<String, double[]> countryCoordinates;
  - Key: Country name (normalized)
  - Value: [latitude, longitude, approximate_radius]
```

**CSV Parsing**:
- Format: Standard CSV with headers
- Path: `/terravision/countries.csv`
- Encoding: UTF-8
- Uses Apache Commons CSV for robust parsing

**Methods**:
```java
public double[] locateCountry(String name)
  - Normalizes country name (NFD decomposition)
  - Case-insensitive lookup
  - Returns [lat, lng] or null

private void loadCountryData()
  - Reads countries.csv from classpath
  - Handles Unicode normalization
  - Builds lookup map
```

---

### 7. **Vector3** (Math Utilities)
**Responsibility**: 3D vector operations and transformations

**Implemented Operations**:
```java
- Addition: v1 + v2
- Subtraction: v1 - v2
- Scalar multiplication: v * scalar
- Normalization: v.normalize()
- Magnitude: v.length()
- Dot product: v1.dot(v2)
- Cross product: v1.cross(v2)
```

---

### 8. **GeoCoordinates** (Data Model)
**Responsibility**: Represent geographic coordinate data

**Properties**:
```java
private double latitude;      // -90 to +90
private double longitude;     // -180 to +180
```

**Operations**:
```java
- Validation: ensureValid()
- Conversion: toVector3()
- Formatting: toString()
```

---

## 🔍 Texture Loading Pipeline

```
1. Resource Loading
   └─ getClass().getResourceAsStream("/terravision/world_shaded_43k.jpg")

2. Image Reader Setup
   └─ ImageIO.createImageInputStream()
   └─ ImageIO.getImageReaders()

3. GPU Detection (OSHI)
   └─ SystemInfo.getHardware().getGraphicsCards()
   └─ Calculate subsampling factor:
      - RTX (≥8GB): factor = 4
      - GTX (<8GB): factor = 5
      - Integrated: factor = 6

4. Adaptive Downsampling
   └─ ImageReadParam.setSourceSubsampling(factor, factor, 0, 0)
   └─ Reduces resolution during read

5. JavaFX Conversion
   └─ SwingFXUtils.toFXImage(buffered, null)
   └─ Returns WritableImage

6. Material Assignment
   └─ PhongMaterial.setDiffuseMap(image)
```

---

## 🎬 Animation System

### Timeline Architecture
```java
Timeline activeAnimation = new Timeline(
    new KeyFrame(Duration.ZERO, keyValues_initial),
    new KeyFrame(Duration.seconds(2.4), keyValues_final)
);
```

### Interpolation
- **Interpolator**: EASE_BOTH
- **Duration**: Configurable per animation
- **Focus Duration**: 2.4 seconds
- **Reset Duration**: 1.4 seconds

### Animation Types

#### Focus Animation
```
Properties animated:
  - rotateX.angleProperty() → target pitch
  - rotateY.angleProperty() → target yaw
  - camera.translateZProperty() → focus zoom

Special handling:
  - Shortest angle path calculation
  - Angle wrapping (±180°)
```

#### Reset Animation
```
Returns to default state:
  - Pitch: 0°
  - Yaw: 0°
  - Zoom: -900 (default)
```

---

## 💾 Memory Management

### Texture Memory Optimization

| GPU Type | VRAM | Factor | Texture Size | Memory |
|:---|:---:|:---:|:---:|:---:|
| RTX | ≥8GB | 4x | 10,800×5,400 | ~175 MB |
| GTX | <8GB | 5x | 8,640×4,320 | ~112 MB |
| iGPU | any | 6x | 7,200×3,600 | ~78 MB |

### Resource Cleanup
```java
// Proper disposal of ImageReader
finally {
    reader.dispose();
}

// Stream closing
try (ImageInputStream iis = ...) {
    // Auto-closed after use
}
```

---

## 🔄 Event Flow Diagram

```
┌──────────────┐
│  Mouse Event │
└──────┬───────┘
       │
       ▼
┌─────────────────────┐
│  EarthSphere        │
│  (Mouse Handler)    │
└──────────┬──────────┘
           │
    ┌──────┴──────┐
    │             │
    ▼             ▼
onPressed    onDragged
    │             │
    ├─ Capture    ├─ Calculate delta
    │  state      │
    │             ├─ Apply transform
    └─────┬───────┤
          │       ├─ Clamp values
          │       │
          ▼       ▼
      rotateX.setAngle()
      rotateY.setAngle()
          │       │
          └───┬───┘
              │
              ▼
         JavaFX Render
              │
              ▼
         Screen Update
```

---

## 📈 Performance Considerations

### Critical Paths
1. **Mouse Drag Rendering** (60 FPS target)
   - Direct property updates
   - No timeline overhead
   - Minimal garbage generation

2. **Focus Animation** (24 fps, 2.4 seconds)
   - Timeline-driven
   - Smooth interpolation
   - Callback on completion

3. **Texture Streaming**
   - One-time load
   - Adaptive resolution
   - GPU VRAM aware

### Optimization Strategies
- **Lazy Loading**: Texture loaded on demand
- **Subsampling**: Resolution reduced per GPU capabilities
- **Stateless Math**: Vector3 operations without allocation
- **Direct Properties**: Camera updates via JavaFX properties

---

## 🔐 Thread Safety

- **JavaFX UI Thread**: All rendering operations
- **Resource Loading**: Blocking (design trade-off)
- **Event Handlers**: UI thread (implicit in JavaFX)

---

## 🎯 Design Patterns

| Pattern | Usage | Class |
|:---|:---:|:---|
| **Singleton** | CountryLocator instance | TerraVision |
| **Observer** | JavaFX properties | Camera, Rotation |
| **Strategy** | Animation interpolators | Timeline |
| **Adapter** | SwingFXUtils conversion | EarthSphere |
| **Builder** | SubScene creation | EarthSphere |

---

## 📚 Dependencies Map

```
LauncherTerraVision
    ↓
TerraVision
    ├─ EarthController
    ├─ EarthSphere
    │   ├─ Vector3
    │   └─ PhongMaterial
    ├─ EarthProjection
    │   └─ GeoCoordinates
    │       └─ Vector3
    └─ CountryLocator
        └─ GeoCoordinates
```

---

## 🚀 Future Architecture Improvements

### Potential Enhancements
- [ ] Asynchronous texture loading with ProgressIndicator
- [ ] Height map support for terrain visualization
- [ ] Cloud layer rendering
- [ ] Day/night cycle simulation
- [ ] Satellite imagery layers
- [ ] Custom marker system
- [ ] GeoJSON support for polygon rendering
- [ ] Particle system for atmospheric effects

### Scalability
- Multi-threading for data loading
- Tile-based texture streaming
- Level-of-detail (LOD) system
- Viewport culling

---

<div align="center">

**Architecture Document v1.0**

</div>
