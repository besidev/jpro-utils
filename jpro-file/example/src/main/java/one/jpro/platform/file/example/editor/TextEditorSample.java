package one.jpro.platform.file.example.editor;

import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Styles;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import one.jpro.platform.file.ExtensionFilter;
import one.jpro.platform.file.FileSource;
import one.jpro.platform.file.dropper.FileDropper;
import one.jpro.platform.file.picker.FilePicker;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a sample application for file dropper operations.
 *
 * @author Besmir Beqiri
 */
public class TextEditorSample extends Application {

    private static final Logger logger = LoggerFactory.getLogger(TextEditorSample.class);

    private static final PseudoClass FILES_DRAG_OVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("files-drag-over");
    private static final ExtensionFilter textExtensionFilter = ExtensionFilter.of("Text files", ".txt", ".srt", ".md", ".csv");

    @Override
    public void start(Stage stage) {
        stage.setTitle("JPro File Dropper");
        Scene scene = new Scene(createRoot(stage), 1140, 640);
        Optional.ofNullable(CupertinoLight.class.getResource(new CupertinoLight().getUserAgentStylesheet()))
                .map(URL::toExternalForm)
                .ifPresent(scene::setUserAgentStylesheet);
        Optional.ofNullable(TextEditorSample.class.getResource("/one/jpro/platform/file/example/css/file_dropper.css"))
                .map(URL::toExternalForm)
                .ifPresent(scene.getStylesheets()::add);
        stage.setScene(scene);
        stage.show();
    }

    public Parent createRoot(Stage stage) {
        Label dropLabel = new Label("Drop here text files only!");
        StackPane dropPane = new StackPane(dropLabel);
        dropPane.getStyleClass().add("drop-pane");

        final var fileDropper = FileDropper.create(dropPane);
        fileDropper.setExtensionFilter(textExtensionFilter);
        fileDropper.filesDragOverProperty().addListener(observable ->
                dropPane.pseudoClassStateChanged(FILES_DRAG_OVER_PSEUDO_CLASS,
                        fileDropper.isFilesDragOver()));

        BorderPane rootPane = new BorderPane(dropPane);
        rootPane.getStyleClass().add("root-pane");
        TextArea textArea = new TextArea();
        fileDropper.setOnFilesSelected(fileSources -> {
            appendFilesContent(fileSources, textArea);
            rootPane.setCenter(textArea);
        });

        Button openButton = new Button("Open", new FontIcon(Material2AL.FOLDER_OPEN));
        openButton.setDefaultButton(true);
        final var filePicker = FilePicker.create(openButton);
        filePicker.getExtensionFilters().add(textExtensionFilter);
        filePicker.setSelectedExtensionFilter(textExtensionFilter);
        filePicker.setOnFilesSelected(fileSources -> {
            appendFilesContent(fileSources, textArea);
            rootPane.setCenter(textArea);
        });

        ChoiceBox<SelectionMode> selectionModeComboBox = new ChoiceBox<>();
        selectionModeComboBox.getItems().addAll(SelectionMode.SINGLE, SelectionMode.MULTIPLE);
        selectionModeComboBox.getSelectionModel().select(SelectionMode.SINGLE);
        selectionModeComboBox.setConverter(selectionModeStringConverter);
        fileDropper.selectionModeProperty().bind(selectionModeComboBox.valueProperty());
        filePicker.selectionModeProperty().bind(selectionModeComboBox.valueProperty());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button clearButton = new Button("Clear", new FontIcon(Material2AL.CLEAR));
        clearButton.getStyleClass().add(Styles.DANGER);
        clearButton.setOnAction(event -> rootPane.setCenter(dropPane));

        Label selectionModeLabel = new Label("Selection Mode:");
        HBox controlsBox = new HBox(selectionModeLabel, selectionModeComboBox, spacer, openButton, clearButton);
        controlsBox.getStyleClass().add("controls-box");
        rootPane.setTop(controlsBox);

        return rootPane;
    }

    private void appendFilesContent(List<? extends FileSource> fileSources, TextArea textArea) {
        final StringBuilder content = new StringBuilder();
        fileSources.forEach(fileSource -> fileSource.uploadFileAsync().thenAcceptAsync(file -> {
            try {
                String fileContent = new String(Files.readAllBytes(file.toPath()));
                content.append(fileContent);
                content.append("\n=================================================================================\n");
                Platform.runLater(() -> textArea.setText(content.toString()));
            } catch (IOException ex) {
                logger.error("Error reading file: " + ex.getMessage(), ex);
            }
        }));
    }

    private final StringConverter<SelectionMode> selectionModeStringConverter = new StringConverter<>() {

        @Override
        public String toString(SelectionMode selectionMode) {
            return selectionMode == SelectionMode.MULTIPLE ? "Multiple" : "Single";
        }

        @Override
        public SelectionMode fromString(String string) {
            return "Multiple".equals(string) ? SelectionMode.MULTIPLE : SelectionMode.SINGLE;
        }
    };
}
