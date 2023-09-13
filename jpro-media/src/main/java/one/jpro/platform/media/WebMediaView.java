package one.jpro.platform.media;

import com.jpro.webapi.HTMLView;
import com.jpro.webapi.JSVariable;
import com.jpro.webapi.WebAPI;
import javafx.beans.property.*;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import one.jpro.platform.media.recorder.impl.WebMediaRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * {@link MediaView} base implementation for the web.
 *
 * @author Besmir Beqiri
 */
public abstract class WebMediaView extends MediaView {

    private static final Logger log = LoggerFactory.getLogger(WebMediaView.class);

    private final WebAPI webAPI;
    private JSVariable mediaContainerElement;

    public WebMediaView(WebAPI webAPI) {
        this.webAPI = Objects.requireNonNull(webAPI, "WebAPI must not be null.");
        initialize();
    }

    protected void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        final HTMLView viewContainer = new HTMLView();
        mediaContainerElement = webAPI.getHTMLViewElement(viewContainer);
        getChildren().setAll(viewContainer);
    }

    @Override
    public final ObjectProperty<MediaEngine> mediaEngineProperty() {
        if (mediaEngine == null) {
            mediaEngine = new SimpleObjectProperty<>(this, "mediaEngine") {

                @Override
                protected void invalidated() {
                    if (getMediaEngine() instanceof WebMediaEngine webMediaEngine) {
                        webAPI.executeScript("""
                                // clear all elements
                                while ($mediaContainer.firstChild) {
                                    elem.removeChild(elem.firstChild);
                                }
                                // add new element
                                $mediaContainer.appendChild(%s);
                                """.replace("$mediaContainer", mediaContainerElement.getName())
                                .formatted(webMediaEngine.getVideoElement().getName()));
                        if (webMediaEngine instanceof WebMediaRecorder) {
                            webAPI.executeScript("""
                                    %s.play();
                                    """.formatted(webMediaEngine.getVideoElement().getName()));
                        }
                        webAPI.executeScript("""
                                %s.width = "%s";
                                """.formatted(webMediaEngine.getVideoElement().getName(), getFitWidth()));
                        webAPI.executeScript("""
                                %s.height = "%s";
                                """.formatted(webMediaEngine.getVideoElement().getName(), getFitHeight()));
                        webAPI.executeScript("""
                                %s.controls = $controls;
                                """.formatted(webMediaEngine.getVideoElement().getName())
                                .replace("$controls", String.valueOf(isShowControls())));
                    }
                }
            };
        }
        return mediaEngine;
    }

    // disable controls property
    private BooleanProperty showControls;

    public final boolean isShowControls() {
        return showControls != null && showControls.get();
    }

    public final void setShowControls(boolean showControls) {
        showControlsProperty().set(showControls);
    }

    public final BooleanProperty showControlsProperty() {
        if (showControls == null) {
            showControls = new SimpleBooleanProperty(this, "showControls") {
                @Override
                protected void invalidated() {
                    if (getMediaEngine() instanceof WebMediaEngine webMediaEngine) {
                        webAPI.executeScript("""
                                %s.controls = $controls;
                                """.formatted(webMediaEngine.getVideoElement().getName())
                                .replace("$controls", String.valueOf(get())));
                    }
                }
            };
        }
        return showControls;
    }

    @Override
    public final DoubleProperty fitWidthProperty() {
        if (fitWidth == null) {
            fitWidth = new SimpleDoubleProperty(this, "fitWidth") {

                @Override
                protected void invalidated() {
                    if (getMediaEngine() instanceof WebMediaEngine webMediaEngine) {
                        final double fitWidth = get();
                        webAPI.executeScript("""
                                %s.width = "%s";
                                """.formatted(webMediaEngine.getVideoElement().getName(), fitWidth));
                        log.trace("video width: {}", fitWidth);
                    }
                }
            };
        }
        return fitWidth;
    }

    @Override
    public final DoubleProperty fitHeightProperty() {
        if (fitHeight == null) {
            fitHeight = new SimpleDoubleProperty(this, "fitHeight") {
                @Override
                protected void invalidated() {
                    if (getMediaEngine() instanceof WebMediaEngine webMediaEngine) {
                        final double fitHeight = get();
                        webAPI.executeScript("""
                                %s.height = "%s";
                                """.formatted(webMediaEngine.getVideoElement().getName(), fitHeight));
                        log.trace("video height: " + fitHeight);
                    }
                }
            };
        }
        return fitHeight;
    }

    @Override
    public final BooleanProperty preserveRatioProperty() {
        if (preserveRatio == null) {
            preserveRatio = new SimpleBooleanProperty(this, "preserveRatio", true) {

                @Override
                protected void invalidated() {
                    final boolean preserveRatio = get();
                    if (getMediaEngine() instanceof WebMediaEngine webMediaEngine) {
                        if (preserveRatio) {
                            webAPI.executeScript("""
                                    %s.style.objectFit = 'contain';
                                    """.formatted(webMediaEngine.getVideoElement().getName()));
                        } else {
                            webAPI.executeScript("""
                                    %s.style.objectFit = 'fill';
                                    """.formatted(webMediaEngine.getVideoElement().getName()));
                        }
                        log.trace("preserve ratio: " + preserveRatio);
                    }
                }
            };
        }
        return preserveRatio;
    }

    @Override
    protected void layoutChildren() {
        for (Node child : getManagedChildren()) {
            if (getMediaEngine() instanceof WebMediaEngine webMediaEngine) {
                if (getFitWidth() < 0) {
                    webAPI.executeScript("""
                            %s.width = "%s";
                            """.formatted(webMediaEngine.getVideoElement().getName(), getWidth()));
                }
                if (getFitHeight() < 0) {
                    webAPI.executeScript("""
                            %s.height = "%s";
                            """.formatted(webMediaEngine.getVideoElement().getName(), getHeight()));
                }
            }
            layoutInArea(child, 0.0, 0.0, getWidth(), getHeight(),
                    0.0, HPos.CENTER, VPos.CENTER);
        }
    }
}