# ⚡ Quick Start Guide

Get TerraVision up and running in 5 minutes!

---

## 📋 Prerequisites Checklist

- [ ] Java 21+ installed (`java -version`)
- [ ] Maven 3.6+ installed (`mvn -version`)
- [ ] Git installed (`git --version`)
- [ ] At least 2GB GPU VRAM

---

## 🚀 5-Minute Setup

### Step 1: Clone the Repository (1 min)
```bash
git clone https://github.com/DevMindX89/terravision.git
cd terravision
```

### Step 2: Build the Project (2 min)
```bash
mvn clean package install
```

**Expected Output:**
```
BUILD SUCCESS
```

### Step 3: Run the Application (1 min)
```bash
mvn javafx:run
```

**Wait for window to appear** (takes 5-10 seconds on first run while loading textures)

---

## 🎮 First Interaction (1 min)

Once the window opens:

| Action | How |
|:---|:---|
| **Rotate Earth** | Click and drag with mouse |
| **Zoom In** | Scroll wheel up |
| **Zoom Out** | Scroll wheel down |
| **Reset View** | Click "Reset" button |
| **Focus on Country** | Click a country button |

---

## 🔧 Troubleshooting

### Problem: "Module not found" error
**Solution:**
```bash
mvn clean install
mvn javafx:run
```

### Problem: Window takes forever to open
**Solution:** First run loads 43K texture (~60MB). Be patient (30-60 seconds).
- Subsequent runs will be faster
- Check GPU VRAM with `nvidia-smi` or system monitor

### Problem: Low FPS or stuttering
**Solution:** 
1. Close other GPU-intensive apps
2. Check VRAM: `nvidia-smi` (Windows) or `glxinfo` (Linux)
3. Application auto-optimizes; may need restart

### Problem: Build fails with Java version error
**Solution:**
```bash
java -version
# Ensure output shows Java 21+
# If not, update Java:
# Ubuntu: sudo apt install openjdk-21-jdk
# macOS: brew install java21
# Windows: download from oracle.com
```

### Problem: "SLF4J: Failed to load" warning
**Solution:** Safe to ignore (logging configuration). Not an error.

---

## 📦 Build Artifacts

After `mvn clean package`:

```
target/
├── terravision-1.0.jar          # Compiled JAR
├── classes/                      # Compiled classes
└── maven-archiver/               # Build metadata
```

Run JAR directly:
```bash
java -jar target/terravision-1.0.jar
```

---

## 🎨 UI Layout

Once the app launches:

```
┌─────────────────────────────────────────────────┐
│  🌍 TerraVision                         [📋]    │
├─────────────────────────────────────────────────┤
│                                                 │
│                                                 │
│          🌎 3D Earth Visualization             │
│                                                 │
│  [◄] [Reset] [Focus]  ← Control Buttons       │
│                                                 │
├─────────────────────────────────────────────────┤
│ Country List (Scroll):                         │
│ ☑ Spain      ☑ France   ☑ Germany             │
│ ☑ Italy      ☑ Poland   ☑ UK                  │
│ ... more countries ...                         │
└─────────────────────────────────────────────────┘
```

---

## 🎓 Key Controls

### Mouse Controls
```
Left Click + Drag     → Rotate globe
Scroll Up            → Zoom in
Scroll Down          → Zoom out
```

### Keyboard Controls
```
[Reset]  → Return to default view
[Focus]  → Animate to selected country
```

---

## 📊 Performance Expectations

### First Run
- **Load Time**: 30-60 seconds (texture loading)
- **Texture Size**: Depends on GPU
  - RTX (8GB+): 10,800×5,400 px (~175MB VRAM)
  - GTX (<8GB): 8,640×4,320 px (~112MB VRAM)
  - iGPU: 7,200×3,600 px (~78MB VRAM)

### Subsequent Runs
- **Load Time**: 5-10 seconds
- **Frame Rate**: 60 FPS (with smooth dragging)
- **Memory Usage**: 500-800 MB RAM

---

## 🔍 Verify Installation

### Check Build Success
```bash
ls -la target/terravision-1.0.jar
# Should show file with size > 1MB
```

### Check Dependencies
```bash
mvn dependency:tree
# Should show:
# - javafx-controls
# - javafx-fxml
# - commons-csv
# - oshi-core
# - flexmark-all
```

---

## 💡 Tips & Tricks

### Slow Interaction?
- Check background apps consuming GPU
- Reduce other screen resolutions
- Ensure JavaFX drivers are current

### Want to Explore Code?
```bash
# Open in IDE
code .                           # VS Code
idea .                           # IntelliJ IDEA
eclipse -data . &               # Eclipse

# Or explore structure
tree -L 2 src/
```

### Looking for Country Coordinates?
- Edit `src/main/resources/terravision/countries.csv`
- Format: `country,latitude,longitude`
- Restart application to reload

---

## 🚀 Next Steps

1. **Explore the Code**: See [ARCHITECTURE.md](ARCHITECTURE.md)
2. **Contribute**: See [CONTRIBUTING.md](CONTRIBUTING.md)
3. **Report Issues**: Use GitHub Issues
4. **Request Features**: Open a Discussion

---

## ⚠️ Common Issues & Solutions

| Issue | Cause | Fix |
|:---|:---|:---|
| `ClassNotFoundException` | Missing dependencies | `mvn clean install` |
| `IOException: stream is closed` | Resource loading error | Check classpath, rebuild |
| Black screen | GPU issue | Restart, check drivers |
| High CPU usage | FPS unlocked | Application auto-limits to 60 FPS |
| Memory leak | Long session | Restart application |

---

## 📞 Getting Help

1. **Check FAQ**: Search [Issues](https://github.com/DevMindX89/terravision/issues)
2. **Read Docs**: [ARCHITECTURE.md](ARCHITECTURE.md), [README.md](README.md)
3. **Open Issue**: Provide system info + steps to reproduce
4. **Ask Question**: Use Discussions tab

---

## 🎉 Success!

If you see a rotating 3D Earth with smooth interactions, you're all set! 🌍✨

---

<div align="center">

**Questions?** Open an Issue on GitHub

**Enjoying TerraVision?** Star the repository ⭐

</div>
