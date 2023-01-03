package one.jpro.media.player.impl;

import com.jpro.webapi.HTMLView;
import com.jpro.webapi.WebAPI;
import javafx.beans.property.*;
import one.jpro.media.MediaEngine;
import one.jpro.media.MediaView;
import one.jpro.media.player.MediaPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link MediaView} implementation for a web {@link MediaPlayer}.
 *
 * @author Besmir Beqiri
 */
public class WebMediaPlayerView extends MediaView {

    private final Logger log = LoggerFactory.getLogger(WebMediaPlayerView.class);

    private final WebAPI webAPI;

    public WebMediaPlayerView(WebAPI webAPI) {
        this.webAPI = webAPI;
        initialize();
    }

    public WebMediaPlayerView(WebMediaPlayer webMediaPlayer) {
        this.webAPI = webMediaPlayer.getWebAPI();
        initialize();
        setMediaEngine(webMediaPlayer);
    }

    private void initialize() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);

        sceneProperty().addListener(observable -> {
            if (getScene() != null && webAPI != null && getMediaEngine() instanceof WebMediaPlayer webMediaPlayer) {
                webAPI.executeScript("""
                                let elem = document.getElementById("%s");
                                elem.width = "%s";
                                """.formatted(webMediaPlayer.getMediaPlayerId(), getFitWidth()));
                webAPI.executeScript("""
                                let elem = document.getElementById("%s");
                                elem.height = "%s";
                                """.formatted(webMediaPlayer.getMediaPlayerId(), getFitHeight()));
            }
        });
    }

    @Override
    public final ObjectProperty<MediaEngine> mediaEngineProperty() {
        if (mediaEngine == null) {
            mediaEngine = new SimpleObjectProperty<>(this, "mediaEngine") {

                @Override
                protected void invalidated() {
                    final MediaEngine mediaPlayer = getMediaEngine();
                    if (mediaPlayer instanceof WebMediaPlayer webMediaPlayer) {
                        HTMLView htmlView = new HTMLView("""
                                <video id="%s" width="%spx" height="%spx"></video>
                                """.formatted(webMediaPlayer.getMediaPlayerId(), getFitWidth(), getFitHeight()));
                        getChildren().setAll(htmlView);
                    }
                }
            };
        }
        return mediaEngine;
    }

    // disable controls property
    private BooleanProperty showControls;

    public final boolean getShowControls() {
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
                    if (webAPI != null && getMediaEngine() instanceof WebMediaPlayer webMediaPlayer) {
                        webAPI.executeScript("""
                                    let elem = document.getElementById('$mediaPlayerId');
                                    if ($showControls) {
                                        elem.setAttribute("controls","controls")
                                    } else if (elem.hasAttribute("controls")) {
                                        elem.removeAttribute("controls")
                                    }
                                    """.replace("$mediaPlayerId", webMediaPlayer.getMediaPlayerId())
                                .replace("$showControls", String.valueOf(get())));
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
                    if (webAPI != null && getMediaEngine() instanceof WebMediaPlayer webMediaPlayer) {
                        webAPI.executeScript("""
                                let elem = document.getElementById("%s");
                                elem.style.width = "%spx";
                                """.formatted(webMediaPlayer.getMediaPlayerId(), getFitWidth()));
                        log.debug("video width: " + getFitWidth());
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
                    if (webAPI != null && getMediaEngine() instanceof WebMediaPlayer webMediaPlayer) {
                        webAPI.executeScript("""
                                let elem = document.getElementById("%s");
                                elem.style.height = "%spx";
                                """.formatted(webMediaPlayer.getMediaPlayerId(), getFitHeight()));
                        log.debug("video height: " + get());
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
                    if (webAPI != null && getMediaEngine() instanceof WebMediaPlayer webMediaPlayer) {
                        if (preserveRatio) {
                            webAPI.executeScript("""
                                    let elem = document.getElementById('$mediaPlayerId');
                                    elem.style.objectFit = 'contain';
                                    console.log('$mediaPlayerId => preserve ratio: true');
                                    """.replace("$mediaPlayerId", webMediaPlayer.getMediaPlayerId()));
                        } else {
                            webAPI.executeScript("""
                                    let elem = document.getElementById('$mediaPlayerId');
                                    elem.style.objectFit = 'fill';
                                    console.log('$mediaPlayerId => preserve ratio: false');
                                    """.replace("$mediaPlayerId", webMediaPlayer.getMediaPlayerId()));
                        }
                        log.debug("preserve ratio: " + preserveRatio);
                    }
                }
            };
        }
        return preserveRatio;
    }
}