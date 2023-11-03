package one.jpro.platform.file.picker;

import com.jpro.webapi.WebAPI;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import one.jpro.platform.file.ExtensionFilter;
import one.jpro.platform.file.util.SaveUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents a {@link FileSavePicker} implementation for JavaFX applications
 * running on the web via JPro server. This class specializes for downloading
 * a file and save it in the native file system.
 *
 * @author Besmir Beqiri
 */
public class WebFileSavePicker extends BaseFileSavePicker {

    private static final Logger logger = LoggerFactory.getLogger(WebFileSavePicker.class);

    public WebFileSavePicker(Node node) {
        super(node);
    }

    // title property
    private StringProperty title;

    @Override
    public final String getTitle() {
        return (title != null) ? title.get() : null;
    }

    @Override
    public final void setTitle(String value) {
        titleProperty().set(value);
    }

    @Override
    public final StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty(this, "title");
        }
        return title;
    }

    // initial file name property
    @Override
    public final StringProperty initialFileNameProperty() {
        if (initialFileName == null) {
            initialFileName = new SimpleStringProperty(this, "initialFileName");
        }
        return initialFileName;
    }

    // initial directory property
    private ObjectProperty<File> initialDirectory;

    @Override
    public final File getInitialDirectory() {
        return (initialDirectory != null) ? initialDirectory.get() : null;
    }

    @Override
    public final void setInitialDirectory(final File value) {
        initialDirectoryProperty().set(value);
    }

    @Override
    public final ObjectProperty<File> initialDirectoryProperty() {
        if (initialDirectory == null) {
            initialDirectory = new SimpleObjectProperty<>(this, "initialDirectory");
        }
        return initialDirectory;
    }

    @Override
    public final ObjectProperty<ExtensionFilter> selectedExtensionFilterProperty() {
        if (selectedExtensionFilter == null) {
            selectedExtensionFilter = new SimpleObjectProperty<>(this, "selectedExtensionFilter");
        }
        return selectedExtensionFilter;
    }

    @Override
    final void showDialog() {
        final String fileName = getInitialFileName() == null ? "filename" : getInitialFileName();
        final ExtensionFilter fileExtension = getSelectedExtensionFilter();
        final String fileType = fileExtension == null ? "" : fileExtension.extensions().get(0);
        final Function<File, CompletableFuture<File>> onFileSelected = getOnFileSelected();
        if (onFileSelected != null) {
            final File tempFile = SaveUtils.createTempFile(fileName, fileType);
            onFileSelected.apply(tempFile)
                    .thenCompose(file -> {
                        try {
                            final URL fileUrl = file.toURI().toURL();
                            final WebAPI webAPI = WebAPI.getWebAPI(getNode().getScene().getWindow());
                            Platform.runLater(() -> webAPI.downloadURL(fileUrl, file::delete));
                            return CompletableFuture.completedFuture(file);
                        } catch (IOException ex) {
                            return CompletableFuture.failedFuture(ex);
                        }
                    }).exceptionallyCompose(ex -> {
                        if (!tempFile.delete()) {
                            logger.warn("Could not delete temporary file {}", tempFile.getAbsolutePath());
                        }
                        logger.error("Error while downloading file", ex);
                        return CompletableFuture.failedFuture(ex);
                    });
        }
    }
}