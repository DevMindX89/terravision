package devmind.coding.terravision;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TerraVision extends Application {
	private static final int POPUP_WIDTH = 800;
	private static final int POPUP_HEIGHT = 640;
	private static final int TOTAL_IMAGES = 5;
	private static final int ICON_SIZE = 56;

	private final CountryLocator countryLocator = new CountryLocator();

	private EarthController earthController;
	private ImageView infoIcon;
	private ImageView countryImageView;
	private VBox popupWindowCountryInfo;
	private VBox popupWindowCountryImages;
	private Label statusLabel;
	private String currentCountry;
	private String currentCountryKey;
	private Timeline imageTimeline;
	private Media media;
	private MediaPlayer player;
	private ListView<String> suggestionList;

	@Override
	public void start(Stage stage) {

		EarthSphere earthSphere = new EarthSphere(200, 512);
		earthController = new EarthController(earthSphere);

		infoIcon = createInfoIcon();
		popupWindowCountryInfo = createPopupWindowCountryInfo();
		popupWindowCountryImages = createPopupWindowCountryImages();
		statusLabel = createStatusLabel();

		TextField searchField = createSearchField();
		suggestionList = createSuggestionList();
		wireSearchAutocomplete(searchField);
		HBox topPanel = createTopPanel(searchField, suggestionList, stage);

		var earthScene = earthSphere.createSubScene(Screen.getPrimary().getVisualBounds().getWidth(),
				Screen.getPrimary().getVisualBounds().getHeight());

		StackPane centerStack = new StackPane(earthScene, infoIcon, popupWindowCountryInfo, popupWindowCountryImages);
		centerStack.setStyle("-fx-background-color: black;");
		StackPane.setAlignment(infoIcon, Pos.CENTER);
		StackPane.setAlignment(popupWindowCountryInfo, Pos.CENTER_LEFT);
		StackPane.setAlignment(popupWindowCountryImages, Pos.CENTER_RIGHT);
		popupWindowCountryInfo.setTranslateX(+40);
		popupWindowCountryImages.setTranslateX(-40);
		infoIcon.setTranslateY(-18);

		BorderPane root = new BorderPane();
		root.setTop(topPanel);
		root.setCenter(centerStack);
		root.setBottom(statusLabel);

		Scene scene = createMainScene(root);
		configureStage(stage, scene);
		startMediaPlayer();
		stage.show();
	}

	private void wireSearchAutocomplete(TextField searchField) {
		searchField.textProperty().addListener((obs, oldValue, newValue) -> updateSuggestions(searchField));
		searchField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (!isFocused && (suggestionList == null || !suggestionList.isFocused())) {
				hideSuggestions();
			}
		});

		suggestionList.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (!isFocused && !searchField.isFocused()) {
				hideSuggestions();
			}
		});

		suggestionList.setOnMouseClicked(event -> {
			String selected = suggestionList.getSelectionModel().getSelectedItem();
			if (selected != null && !selected.isBlank()) {
				searchField.setText(selected);
				searchCountry(searchField);
			}
		});

		suggestionList.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				String selected = suggestionList.getSelectionModel().getSelectedItem();
				if (selected != null && !selected.isBlank()) {
					searchField.setText(selected);
					searchCountry(searchField);
				}
			}
		});
	}

	private ListView<String> createSuggestionList() {
		ListView<String> list = new ListView<>();
		list.setVisible(false);
		list.setManaged(false);
		list.setMaxHeight(180);
		list.setStyle("""
				-fx-background-color: rgba(15,15,15,0.95);
				-fx-control-inner-background: rgba(15,15,15,0.95);
				-fx-text-fill: white;
				-fx-border-color: #3a3a3a;
				-fx-border-radius: 6;
				""");
		return list;
	}

	private void updateSuggestions(TextField field) {
		if (suggestionList == null) {
			return;
		}

		List<String> suggestions = countryLocator.getCountrySuggestions(field.getText(), 8);
		suggestionList.getItems().setAll(suggestions);
		boolean show = !suggestions.isEmpty() && !field.getText().isBlank();
		suggestionList.setVisible(show);
		suggestionList.setManaged(show);
	}

	private void hideSuggestions() {
		if (suggestionList != null) {
			suggestionList.getItems().clear();
			suggestionList.setVisible(false);
			suggestionList.setManaged(false);
		}
	}

	private TextField createSearchField() {
		TextField field = new TextField();
		field.setPromptText("Search for a country...");
		field.setPrefColumnCount(28);
		field.setStyle("""
				-fx-background-color: rgba(20,20,20,0.92);
				-fx-text-fill: white;
				-fx-prompt-text-fill: #8d8d8d;
				-fx-background-radius: 8;
				-fx-border-radius: 8;
				-fx-border-color: #3a3a3a;
				-fx-padding: 10 14 10 14;
				""");
		field.setOnAction(event -> searchCountry(field));
		return field;
	}

	private void searchCountry(TextField field) {
		clearImageTimeline();
		hidePopupAndIcon();
		hideSuggestions();

		currentCountry = field.getText().trim();
		currentCountryKey = countryLocator.normalizeCountryName(currentCountry);

		if (currentCountry.isBlank()) {
			statusLabel.setText("Escribe un país para enfocar el globo.");
			field.clear();
			return;
		}

		boolean found = earthController.searchAndFocusCountry(currentCountry, () -> {
			Platform.runLater(() -> {
				if (infoIcon != null) {
					infoIcon.setVisible(true);
				}
				togglePopup();
				openPopupCountryInfo(currentCountry);
			});
		});

		if (found) {
			statusLabel.setText("Enfocado: " + currentCountry);
		} else {
			currentCountry = null;
			currentCountryKey = null;
			statusLabel.setText("No encuentro ese país en countries.csv.");
		}

		field.clear();
	}

	private HBox createTopPanel(TextField searchField, ListView<String> suggestionList, Stage stage) {

		Button searchButton = new Button("Search");
		searchButton.setStyle(buttonStyleGreen());
		searchButton.setOnAction(event -> searchCountry(searchField));

		Button resetButton = new Button("Reset sphere");
		resetButton.setOnAction(event -> {
			clearImageTimeline();
			hidePopupAndIcon();
			hideSuggestions();
			currentCountry = null;
			currentCountryKey = null;
			earthController.resetView(() -> statusLabel.setText("Vista reiniciada."));
		});
		resetButton.setStyle(buttonStyleGreen());

		Button playPauseMusicButton = new Button("Play music");
		playPauseMusicButton.setStyle(buttonStyleGreen());

		playPauseMusicButton.setOnAction(event -> {
			if (player.getStatus() == Status.PLAYING) {
				player.pause();
				playPauseMusicButton.setText("Play music");
				playPauseMusicButton.setStyle(buttonStyleGreen());
			} else {
				player.play();
				playPauseMusicButton.setText("Pause music");
				playPauseMusicButton.setStyle(buttonStyleRed());
			}
		});

		Button exitButton = new Button("Exit");
		exitButton.setStyle(buttonStyleRed());
		exitButton.setOnAction(event -> {
			stage.close();
		});

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		suggestionList.prefWidthProperty().bind(searchField.widthProperty());
		VBox searchBox = new VBox(6, searchField, suggestionList);
		searchBox.setAlignment(Pos.CENTER_LEFT);

		HBox topPanel = new HBox(12, searchBox, searchButton, resetButton, playPauseMusicButton, spacer, exitButton);
		topPanel.setPadding(new Insets(14));
		topPanel.setAlignment(Pos.CENTER);
		topPanel.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(0,0,0,0.88), rgba(0,0,0,0.25));");
		return topPanel;
	}

	private ImageView createInfoIcon() {
		ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/terravision/red-locator.png")));
		icon.setFitWidth(ICON_SIZE);
		icon.setFitHeight(ICON_SIZE);
		icon.setPreserveRatio(true);
		icon.setVisible(false);
		icon.setPickOnBounds(true);
		icon.setOnMouseClicked(event -> togglePopup());
		return icon;
	}

	private VBox createPopupWindowCountryImages() {
		Label titleLabel = createTitleLabel();
		countryImageView = createCountryImageView();
		Label infoLabel = createInfoLabel();
		Button closeButton = createCloseButton();

		VBox popup = new VBox(12, titleLabel, countryImageView, infoLabel, closeButton);
		popup.setPrefSize(POPUP_WIDTH, POPUP_HEIGHT);
		popup.setMaxSize(POPUP_WIDTH, POPUP_HEIGHT);
		popup.setAlignment(Pos.TOP_CENTER);
		popup.setPadding(new Insets(16));
		popup.setVisible(false);
		popup.setStyle("""
				-fx-background-color: rgba(20, 20, 20, 0.96);
				-fx-background-radius: 10;
				-fx-border-color: #4CAF50;
				-fx-border-width: 2;
				-fx-border-radius: 10;
				""");
		closeButton.setOnAction(event -> closePopupCountryImages());
		popup.getProperties().put("titleLabel", titleLabel);
		return popup;
	}

	// ******** INICIO POPUP COUNTRY INFO ********//
	private VBox createPopupWindowCountryInfo() {
		Label titleLabel = createTitleLabel();
		Button closeButton = createCloseButton();

		TextFlow textFlow = new TextFlow();
		textFlow.setPrefSize(POPUP_WIDTH - 32, POPUP_HEIGHT - 100);
		textFlow.setStyle("-fx-background-color: transparent;");

		ScrollPane scrollPane = new ScrollPane(textFlow);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("""
				-fx-background: transparent;
				-fx-background-color: transparent;
				-fx-border-color: transparent;
				""");

		VBox popup = new VBox(12, titleLabel, scrollPane, closeButton);
		popup.setPrefSize(POPUP_WIDTH, POPUP_HEIGHT);
		popup.setMaxSize(POPUP_WIDTH, POPUP_HEIGHT);
		popup.setAlignment(Pos.TOP_CENTER);
		popup.setPadding(new Insets(16));
		popup.setVisible(false);
		popup.setStyle("""
				-fx-background-color: rgba(20, 20, 20, 0.96);
				-fx-background-radius: 10;
				-fx-border-color: #4CAF50;
				-fx-border-width: 2;
				-fx-border-radius: 10;
				""");

		closeButton.setOnAction(event -> closePopupCountryInfo());
		popup.getProperties().put("titleLabel", titleLabel);
		popup.getProperties().put("textFlow", textFlow);
		return popup;
	}

	private void openPopupCountryInfo(String country) {
		String path = "/terravision/countries/" + country + "/" + country + ".md";
		try {
			InputStream is = getClass().getResourceAsStream(path);

			if (is == null) {
				loadMarkdownIntoPopup(popupWindowCountryInfo, "# Error\nNO INFO AT THE MOMENT");
				return;
			}

			String md = new String(is.readAllBytes(), StandardCharsets.UTF_8);

			if (md == null || md.strip().isEmpty()) {
				loadMarkdownIntoPopup(popupWindowCountryInfo, "# Error\nNO INFO AT THE MOMENT");
				return;
			}

			loadMarkdownIntoPopup(popupWindowCountryInfo, md);
		} catch (IOException e) {
			loadMarkdownIntoPopup(popupWindowCountryInfo, "# Error\nNO INFO AT THE MOMENT");
		}

		popupWindowCountryInfo.setVisible(true);

		Platform.runLater(() -> {
			ScrollPane scrollPane = (ScrollPane) popupWindowCountryInfo.getChildren().get(1);
			scrollPane.setVvalue(0);
		});
	}

	private void loadMarkdownIntoPopup(VBox popup, String markdown) {
		TextFlow textFlow = (TextFlow) popup.getProperties().get("textFlow");
		textFlow.getChildren().clear();

		for (String line : markdown.split("\n")) {
			if (line.startsWith("# ")) {
				textFlow.getChildren().add(createText(line.substring(2) + "\n", "#4CAF50", 20, true));
			} else if (line.startsWith("## ")) {
				textFlow.getChildren().add(createText(line.substring(3) + "\n", "#4CAF50", 15, true));
			} else if (line.startsWith("### ")) {
				textFlow.getChildren().add(createText(line.substring(4) + "\n", "#81C784", 13, true));
			} else if (line.startsWith("- **")) {

				String content = line.substring(2); // quita "- "
				textFlow.getChildren().add(createText("• ", "#ffffff", 13, false));
				parseBoldLine(textFlow, content);
				textFlow.getChildren().add(createText("\n", "#ffffff", 13, false));
			} else if (line.startsWith("- ")) {
				textFlow.getChildren().add(createText("• " + line.substring(2) + "\n", "#e0e0e0", 13, false));
			} else if (!line.isBlank()) {
				textFlow.getChildren().add(createText(line + "\n", "#e0e0e0", 13, false));
			} else {
				textFlow.getChildren().add(createText("\n", "#ffffff", 13, false));
			}
		}
	}

	private void parseBoldLine(TextFlow textFlow, String line) {

		String[] parts = line.split("\\*\\*");
		boolean bold = false;
		for (String part : parts) {
			if (!part.isEmpty()) {
				textFlow.getChildren().add(createText(part, "#ffffff", 13, bold));
			}
			bold = !bold;
		}
	}

	private Text createText(String content, String color, double size, boolean bold) {
		Text text = new Text(content);
		text.setFill(Color.web(color));
		text.setFont(Font.font("Segoe UI", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
		return text;
	}

	// ******** FIN POPUP COUNTRY INFO ********//

	private void togglePopup() {
		if (currentCountry == null || currentCountry.isBlank()) {
			return;
		}

		if (popupWindowCountryInfo.isVisible() && popupWindowCountryImages.isVisible()) {
			closePopupCountryInfo();
			closePopupCountryImages();
			return;
		}

		Label titleLabel = (Label) popupWindowCountryImages.getProperties().get("titleLabel");
		titleLabel.setText(currentCountry.toUpperCase());
		popupWindowCountryInfo.setVisible(true);
		popupWindowCountryImages.setVisible(true);
		startImageSlideshow();
		statusLabel.setText("Mostrando imágenes de " + currentCountry);
	}

	private Label createStatusLabel() {
		Label label = new Label("La Tierra arranca centrada; busca un país para enfocarlo.");
		label.setTextFill(Color.web("#d0d0d0"));
		label.setPadding(new Insets(10, 16, 14, 16));
		label.setStyle("-fx-background-color: rgba(0,0,0,0.75);");
		return label;
	}

	private Scene createMainScene(BorderPane root) {
		Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(),
				Screen.getPrimary().getVisualBounds().getHeight());
		scene.setFill(Color.BLACK);
		return scene;
	}

	private void configureStage(Stage stage, Scene scene) {
		stage.setTitle("TerraVision");
		stage.setMaximized(true);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.setScene(scene);
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/terravision/terra.png")));
	}

	private void startImageSlideshow() {
		clearImageTimeline();
		imageTimeline = new Timeline();

		for (int i = 1; i <= TOTAL_IMAGES; i++) {
			int imageIndex = i;
			imageTimeline.getKeyFrames()
					.add(new KeyFrame(Duration.seconds(i), event -> loadCountryImage(imageIndex), new KeyValue[0]));
		}

		imageTimeline.setCycleCount(Timeline.INDEFINITE);
		imageTimeline.play();
	}

	private void loadCountryImage(int imageNumber) {

		LinkedHashSet<String> candidates = new LinkedHashSet<>();

		if (currentCountry != null && !currentCountry.isBlank()) {
			candidates.add(currentCountry);
			candidates.add(currentCountry.toLowerCase());
			candidates.add(currentCountry.toUpperCase());

			String cap = currentCountry.substring(0, 1).toUpperCase()
					+ (currentCountry.length() > 1 ? currentCountry.substring(1) : "");
			candidates.add(cap);
		}

		candidates.add(currentCountryKey);
		candidates.add(currentCountryKey.toLowerCase());
		candidates.add(currentCountryKey.toUpperCase());
		String capNorm = currentCountryKey.substring(0, 1).toUpperCase()
				+ (currentCountryKey.length() > 1 ? currentCountryKey.substring(1) : "");
		candidates.add(capNorm);

		InputStream imageStream = null;
		for (String candidate : candidates) {
			if (candidate == null || candidate.isBlank())
				continue;
			String imagePath = "/terravision/countries/" + candidate + "/" + imageNumber + ".jpg";
			imageStream = getClass().getResourceAsStream(imagePath);
			if (imageStream != null) {
				break;
			}
		}

		countryImageView.setImage(imageStream == null ? null : new Image(imageStream));
	}

	private void clearImageTimeline() {
		if (imageTimeline != null) {
			imageTimeline.stop();
			imageTimeline.getKeyFrames().clear();
			imageTimeline = null;
		}
		if (countryImageView != null) {
			countryImageView.setImage(null);
		}
	}

	private void closePopupCountryInfo() {
		if (popupWindowCountryInfo != null) {
			popupWindowCountryInfo.setVisible(false);
		}
	}

	private void closePopupCountryImages() {
		clearImageTimeline();
		if (popupWindowCountryImages != null) {
			popupWindowCountryImages.setVisible(false);
		}
	}

	private void hidePopupAndIcon() {
		closePopupCountryInfo();
		closePopupCountryImages();
		if (infoIcon != null) {
			infoIcon.setVisible(false);
		}
	}

	private Label createTitleLabel() {
		Label label = new Label();
		label.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
		return label;
	}

	private Label createInfoLabel() {
		Label label = new Label("");
		label.setWrapText(true);
		label.setStyle("-fx-text-fill: #c0c0c0; -fx-font-size: 12px;");
		return label;
	}

	private ImageView createCountryImageView() {
		ImageView imageView = new ImageView();
		imageView.setFitWidth(640);
		imageView.setFitHeight(420);
		imageView.setPreserveRatio(false);
		imageView.setSmooth(true);
		imageView.setCache(true);
		return imageView;
	}

	private Button createCloseButton() {
		Button button = new Button("Cerrar");
		button.setStyle(buttonStyleGreen());
		return button;
	}

	private String buttonStyleGreen() {
		return """
				-fx-background-color: #4CAF50;
				-fx-text-fill: white;
				-fx-background-radius: 8;
				-fx-padding: 10 16 10 16;
				-fx-cursor: hand;
				""";
	}

	private String buttonStyleRed() {
		return """
				-fx-background-color: #eb4034;
				-fx-text-fill: white;
				-fx-background-radius: 8;
				-fx-padding: 10 16 10 16;
				-fx-cursor: hand;
				""";
	}

	private void startMediaPlayer() {
		try {
			media = new Media(getClass().getResource("/terravision/space92.wav").toURI().toString());
			player = new MediaPlayer(media);
			player.setCycleCount(MediaPlayer.INDEFINITE);
		} catch (Exception e) {
			System.err.println("Música no encontrada o error de reproducción.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
