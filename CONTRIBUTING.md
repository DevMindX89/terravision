# 🤝 Contributing to TerraVision

Thank you for your interest in contributing to TerraVision! This document provides guidelines and instructions for contributing to the project.

---

## 📋 Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Commit Convention](#commit-convention)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Feature Requests](#feature-requests)

---

## Getting Started

### Fork & Clone
```bash
# Fork the repository on GitHub
git clone https://github.com/YOUR_USERNAME/terravision.git
cd terravision
git remote add upstream https://github.com/DevMindX89/terravision.git
```

### Create a Feature Branch
```bash
git checkout -b feature/your-feature-name
# or for bug fixes:
git checkout -b fix/bug-description
```

---

## Development Setup

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

### Build & Test
```bash
# Clean build
mvn clean package

# Run the application
mvn javafx:run

# Run tests (if applicable)
mvn test
```

### Project Structure Review
- **`src/main/java`** — Main source code
- **`src/main/resources`** — Assets and configuration files
- **`src/test/java`** — Unit tests (to be expanded)
- **`pom.xml`** — Maven configuration

---

## Code Style

### Java Conventions
- **Naming**: Use camelCase for variables/methods, PascalCase for classes
- **Line Length**: Keep lines under 120 characters
- **Indentation**: Use 1 tab (4 spaces equivalent)
- **Comments**: Use meaningful comments for complex logic
- **Javadoc**: Document public methods and classes

### Example
```java
/**
 * Rotates the Earth sphere based on mouse input.
 * 
 * @param deltaX Change in X position
 * @param deltaY Change in Y position
 * @param sensitivity Rotation sensitivity multiplier
 */
public void rotateGlobe(double deltaX, double deltaY, double sensitivity) {
    // Implementation
}
```

### Import Organization
```java
// Standard library imports
import java.io.*;
import java.util.*;

// Third-party imports
import org.apache.commons.*;
import oshi.*;

// Project imports
import devmind.coding.terravision.*;
```

---

## Commit Convention

Follow conventional commits for clear commit history:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- **feat**: New feature
- **fix**: Bug fix
- **refactor**: Code refactoring
- **perf**: Performance improvements
- **docs**: Documentation changes
- **test**: Test additions/modifications
- **chore**: Build, dependencies, configuration

### Examples
```bash
git commit -m "feat(renderer): add support for custom texture resolution"
git commit -m "fix(camera): prevent camera clipping through sphere"
git commit -m "perf(projection): optimize cartographic calculations"
git commit -m "docs(readme): add GPU detection documentation"
```

---

## Pull Request Process

### Before Submitting
1. **Update your branch** with latest upstream changes:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Test thoroughly**:
   ```bash
   mvn clean package
   mvn javafx:run
   ```

3. **Run code checks**:
   - Ensure no compiler warnings
   - Verify code follows style guidelines
   - Test all modified features

### PR Description Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Performance improvement
- [ ] Documentation update

## Related Issues
Closes #(issue number)

## Testing
How to test these changes:
1. Step 1
2. Step 2

## Screenshots (if applicable)
Include relevant screenshots or GIFs

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] No new warnings generated
- [ ] Tests added/updated
```

### Merge Criteria
- ✅ All tests pass
- ✅ Code review approved
- ✅ No merge conflicts
- ✅ Documentation updated (if needed)
- ✅ Commits are meaningful

---

## Reporting Bugs

### Bug Report Template
**Title**: Brief, descriptive title

**Environment**:
```
OS: [e.g., Ubuntu 22.04, Windows 11]
Java: [e.g., 21.0.1]
GPU: [e.g., RTX 3080]
VRAM: [e.g., 10GB]
```

**Description**:
Clear description of the bug

**Steps to Reproduce**:
1. Step 1
2. Step 2
3. ...

**Expected Behavior**:
What should happen

**Actual Behavior**:
What actually happens

**Logs/Stack Trace**:
```
Paste relevant error messages
```

**Screenshots**:
If applicable, add visual evidence

---

## Feature Requests

### Feature Request Template
**Title**: Brief description of feature

**Use Case**:
Why is this feature needed?

**Proposed Solution**:
How should this be implemented?

**Alternative Solutions**:
Other possible approaches

**Example**:
```java
// Example of how the feature might be used
earthSphere.enableHeightMap(heightMapTexture);
```

---

## Areas for Contribution

### High-Priority
- [ ] Unit test coverage (tests directory)
- [ ] Performance optimizations
- [ ] Bug fixes
- [ ] Documentation improvements

### Welcome Contributions
- [ ] Additional country data (CSV enhancements)
- [ ] Custom texture support
- [ ] Enhanced lighting models
- [ ] Keyboard shortcuts/input mapping
- [ ] Accessibility features
- [ ] Internationalization (i18n)

### Not Accepting (At This Time)
- Massive architecture rewrites
- Integration of external 3D engines
- Non-Java language ports

---

## Questions?

- 📧 Open an Issue with the `question` label
- 💬 Join discussions in the repository
- 🐛 Report bugs with proper detail

---

## Code of Conduct

### Our Pledge
We are committed to providing a welcoming and inspiring community. Please be respectful of all contributors.

### Expected Behavior
- Use welcoming and inclusive language
- Be respectful of differing opinions
- Accept constructive criticism gracefully
- Focus on what is best for the community

### Unacceptable Behavior
- Harassment or discrimination
- Offensive comments
- Disruptive behavior

---

## License

By contributing to TerraVision, you agree that your contributions will be licensed under its MIT License.

---

<div align="center">

**Thank you for contributing to TerraVision! 🌍**

</div>
