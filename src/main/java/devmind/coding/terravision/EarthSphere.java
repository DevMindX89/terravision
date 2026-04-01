package devmind.coding.terravision;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

public class EarthSphere {
	private static final double DRAG_SENSITIVITY = 0.35;
	private static final double MIN_TILT = -80;
	private static final double MAX_TILT = 80;

	private static final double DEFAULT_CAMERA_Z = -900;
	private static final double MIN_CAMERA_Z = -1200;
	private static final double MAX_CAMERA_Z = -300;
	private static final double FOCUS_CAMERA_Z = MAX_CAMERA_Z;
	private static final double ZOOM_SENSITIVITY = 35;

	private static final double CAMERA_FOV = 30;
	private static final double DIFFUSE_REQUESTED_WIDTH = 43200;
	private static final double DIFFUSE_REQUESTED_HEIGHT = 21600;

	private static final Duration FOCUS_DURATION = Duration.seconds(2.4);

	private final Sphere earth;
	private final Group pitchGroup;
	private final Group yawGroup;
	private final Group world;
	private final PerspectiveCamera camera;
	private final Rotate rotateX;
	private final Rotate rotateY;

	private SubScene subScene;

	private Timeline activeAnimation;
	private double anchorX;
	private double anchorY;
	private double anchorAngleX;
	private double anchorAngleY;

	public EarthSphere(double radius, int divisions) {
		earth = new Sphere(radius, divisions);
		earth.setCullFace(CullFace.BACK);
		earth.setDrawMode(DrawMode.FILL);
		earth.setMaterial(createEarthMaterial());

		rotateX = new Rotate(0, Rotate.X_AXIS);
		rotateY = new Rotate(0, Rotate.Y_AXIS);

		pitchGroup = new Group(earth);
		pitchGroup.getTransforms().add(rotateX);

		yawGroup = new Group(pitchGroup);
		yawGroup.getTransforms().add(rotateY);

		world = new Group(yawGroup);
		addLights(world);

		camera = new PerspectiveCamera(true);
		camera.setNearClip(0.1);
		camera.setFarClip(5000);
		camera.setFieldOfView(CAMERA_FOV);
		camera.setTranslateZ(DEFAULT_CAMERA_Z);

		setupInteraction();
	}

	private PhongMaterial createEarthMaterial() {
		PhongMaterial material = new PhongMaterial();
		material.setDiffuseMap(loadBestDiffuseTexture());

		material.setDiffuseColor(Color.rgb(255, 255, 255));
		material.setSpecularColor(Color.rgb(165, 175, 190));
		material.setSpecularPower(28);
		return material;
	}

	private Image loadBestDiffuseTexture() {
		Image texture = loadOptionalTexture("/terravision/world_shaded_43k.jpg", DIFFUSE_REQUESTED_WIDTH,
				DIFFUSE_REQUESTED_HEIGHT);

		if (texture != null) {
			return texture;
		}

		throw new IllegalStateException("No se encontró ninguna textura válida de la Tierra");
	}

	private Image loadOptionalTexture(String textureCandidates, double requestedWidth, double requestedHeight) {

		InputStream textureStream = getClass().getResourceAsStream(textureCandidates);

		try {
			WritableImage img = loadLargeTexture(textureStream, textureCandidates);
			if (img != null) {
				System.out.println("Textura cargada: " + textureCandidates + " → " + (int) img.getWidth() + "x"
						+ (int) img.getHeight());
				return img;
			}
		} catch (Exception e) {
			System.err.println("Error cargando textura " + textureCandidates + ": " + e.getMessage());
		}
		return null;
	}

	private WritableImage loadLargeTexture(InputStream stream, String pathHint) throws Exception {
		try (ImageInputStream iis = ImageIO.createImageInputStream(stream)) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			if (!readers.hasNext()) {
				throw new IOException("No ImageReader disponible para: " + pathHint);
			}

			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			List<GraphicsCard> gpus = hal.getGraphicsCards();

			System.out.println(gpus.get(0).getName() + "----" + gpus.get(0).getVRam());

			ImageReader reader = readers.next();
			try {
				reader.setInput(iis);

				int originalWidth = reader.getWidth(0);
				int originalHeight = reader.getHeight(0);
				System.out.println("Textura original: " + originalWidth + "x" + originalHeight);

				ImageReadParam param = reader.getDefaultReadParam();

				// Aplicar subsampling dependiendo de la cantidad de memoria grafica
				int factor;
				if (gpus.get(0).getVRam() >= 8623489024L) {
					factor = 4; // rtx
				} else if (gpus.get(0).getVRam() < 8623489024L) {
					factor = 5; // gtx
				}else {
					factor = 6; // integradas
				}

				if (factor > 1) {
					param.setSourceSubsampling(factor, factor, 0, 0);
					System.out.println("Subsampling aplicado x" + factor + " → " + (originalWidth / factor) + "x"
							+ (originalHeight / factor));
				}

				BufferedImage buffered = reader.read(0, param);
				return SwingFXUtils.toFXImage(buffered, null);

			} finally {
				reader.dispose();
			}
		}
	}

	private void addLights(Group root) {
		AmbientLight ambient = new AmbientLight(Color.color(0.56, 0.58, 0.62));

		PointLight sunLight = new PointLight(Color.color(1.0, 0.99, 0.97));
		sunLight.setTranslateX(-1380);
		sunLight.setTranslateY(-220);
		sunLight.setTranslateZ(-1520);

		PointLight fillLight = new PointLight(Color.color(0.34, 0.38, 0.46));
		fillLight.setTranslateX(980);
		fillLight.setTranslateY(120);
		fillLight.setTranslateZ(-760);

		PointLight topLight = new PointLight(Color.color(0.22, 0.28, 0.36));
		topLight.setTranslateX(-120);
		topLight.setTranslateY(-980);
		topLight.setTranslateZ(-320);

		root.getChildren().addAll(ambient, sunLight, fillLight, topLight);
	}

	private void setupInteraction() {
		earth.setOnMousePressed(event -> {
			anchorX = event.getSceneX();
			anchorY = event.getSceneY();
			anchorAngleX = rotateX.getAngle();
			anchorAngleY = rotateY.getAngle();
			stopAnimation();
		});

		earth.setOnMouseDragged(event -> {
			double nextRotateX = anchorAngleX + (event.getSceneY() - anchorY) * DRAG_SENSITIVITY;
			double nextRotateY = anchorAngleY - (event.getSceneX() - anchorX) * DRAG_SENSITIVITY;

			rotateX.setAngle(clamp(nextRotateX, MIN_TILT, MAX_TILT));
			rotateY.setAngle(normalizeAngle(nextRotateY));
		});
	}

	public SubScene createSubScene(double width, double height) {
		SubScene subScene = new SubScene(world, width, height, true, SceneAntialiasing.BALANCED);
		subScene.setFill(Color.rgb(3, 5, 10));
		subScene.setCamera(camera);

		subScene.setOnScroll(event -> {
			stopAnimation();
			double nextCameraZ = camera.getTranslateZ()
					+ (event.getDeltaY() > 0 ? ZOOM_SENSITIVITY : -ZOOM_SENSITIVITY);
			camera.setTranslateZ(clamp(nextCameraZ, MIN_CAMERA_Z, MAX_CAMERA_Z));
		});

		this.subScene = subScene;
		return subScene;
	}

	public void focusOn(EarthProjection.FocusTarget focusTarget, Runnable onFinished) {
		stopAnimation();

		double targetRotateX = clamp(focusTarget.rotateX(), MIN_TILT, MAX_TILT);
		double targetRotateY = normalizeAngle(focusTarget.rotateY());

		double currentRotateX = rotateX.getAngle();
		double currentRotateY = normalizeAngle(rotateY.getAngle());
		double currentCameraZ = camera.getTranslateZ();

		double resolvedRotateY = currentRotateY + shortestAngleDelta(currentRotateY, targetRotateY);

		activeAnimation = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(rotateX.angleProperty(), currentRotateX),
						new KeyValue(rotateY.angleProperty(), currentRotateY),
						new KeyValue(camera.translateZProperty(), currentCameraZ)),
				new KeyFrame(FOCUS_DURATION,
						new KeyValue(rotateX.angleProperty(), targetRotateX, Interpolator.EASE_BOTH),
						new KeyValue(rotateY.angleProperty(), resolvedRotateY, Interpolator.EASE_BOTH),
						new KeyValue(camera.translateZProperty(), FOCUS_CAMERA_Z, Interpolator.EASE_BOTH)));

		activeAnimation.setOnFinished(event -> {
			rotateY.setAngle(normalizeAngle(rotateY.getAngle()));
			camera.setTranslateZ(clamp(camera.getTranslateZ(), MIN_CAMERA_Z, MAX_CAMERA_Z));
			activeAnimation = null;
			if (onFinished != null) {
				onFinished.run();
			}
		});

		activeAnimation.play();
	}

	public void resetView(Runnable onFinished) {
		stopAnimation();

		activeAnimation = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(rotateX.angleProperty(), rotateX.getAngle()),
						new KeyValue(rotateY.angleProperty(), rotateY.getAngle()),
						new KeyValue(camera.translateZProperty(), camera.getTranslateZ())),
				new KeyFrame(Duration.seconds(1.4), new KeyValue(rotateX.angleProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(rotateY.angleProperty(), 0, Interpolator.EASE_BOTH),
						new KeyValue(camera.translateZProperty(), DEFAULT_CAMERA_Z, Interpolator.EASE_BOTH)));

		activeAnimation.setOnFinished(event -> {
			rotateY.setAngle(normalizeAngle(rotateY.getAngle()));
			activeAnimation = null;
			if (onFinished != null) {
				onFinished.run();
			}
		});

		activeAnimation.play();
	}

	private void stopAnimation() {
		if (activeAnimation != null) {
			activeAnimation.stop();
			activeAnimation = null;
		}
	}

	private double shortestAngleDelta(double from, double to) {
		return normalizeAngle(to - from);
	}

	private double normalizeAngle(double angle) {
		double normalized = angle % 360;
		if (normalized > 180) {
			normalized -= 360;
		}
		if (normalized < -180) {
			normalized += 360;
		}
		return normalized;
	}

	private double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}

	public double[] projectVectorToSubScene(Vector3 v) {
		if (subScene == null) {
			return null;
		}

		double rx = Math.toRadians(rotateX.getAngle());
		double ry = Math.toRadians(rotateY.getAngle());

		double cosRx = Math.cos(rx);
		double sinRx = Math.sin(rx);
		double y1 = v.y() * cosRx - v.z() * sinRx;
		double z1 = v.y() * sinRx + v.z() * cosRx;
		double x1 = v.x();

		double cosRy = Math.cos(ry);
		double sinRy = Math.sin(ry);
		double x2 = x1 * cosRy + z1 * sinRy;
		double z2 = -x1 * sinRy + z1 * cosRy;
		double y2 = y1;

		double cameraZ = camera.getTranslateZ();

		double px = x2;
		double py = y2;
		double pz = z2 - cameraZ;

		if (pz <= 0) {
			return null;
		}

		double fov = Math.toRadians(camera.getFieldOfView());
		double h = subScene.getHeight();
		double focal = (h / 2.0) / Math.tan(fov / 2.0);

		double screenX = (px * focal) / pz + subScene.getWidth() / 2.0;
		double screenY = (-py * focal) / pz + subScene.getHeight() / 2.0;

		return new double[] { screenX, screenY };
	}

	public double getSubSceneWidth() {
		return subScene == null ? 0 : subScene.getWidth();
	}

	public double getSubSceneHeight() {
		return subScene == null ? 0 : subScene.getHeight();
	}
}
