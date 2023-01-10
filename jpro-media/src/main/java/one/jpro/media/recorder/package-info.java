/**
 * <h3>Media recorder via {@link one.jpro.media.recorder.MediaRecorder}</h3>
 * <p>
 * The basic steps required to record media are:
 * </p>
 * <ol>
 *     <li>Create a {@link one.jpro.media.recorder.MediaRecorder} object object by calling
 *     {@link one.jpro.media.recorder.MediaRecorder#create(javafx.stage.Stage)}.</li>
 *     <li>Create a {@link one.jpro.media.MediaView} object by calling
 *     {@link one.jpro.media.MediaView#create(one.jpro.media.recorder.MediaRecorder)}.</li>
 *     <li>Add the <code>MediaView</code> to the scene graph.</li>
 *     <li>Enable the <code>MediaRecorder</code> by calling {@link one.jpro.media.recorder.MediaRecorder#enable()}</li>
 *     <li>Invoke {@link one.jpro.media.recorder.MediaRecorder#start()}.</li>
 *     <li>Retrieve the <code>MediaSource</code> of the recorded media via
 *     {@link one.jpro.media.recorder.MediaRecorder#getMediaSource()}</li>
 * </ol>
 * These steps are illustrated by the sample code in the {@link one.jpro.media.MediaView} class documentation.
 * Some things which should be noted are:
 * <ul>
 *     <li>One <code>MediaSource</code> object may be shared among multiple <code>MediaRecorder</code>s.
 *     <li>One <code>MediaRecorder</code> may be shared among multiple <code>MediaView</code>s.
 *     <li>Media may be recorded directly by a <code>MediaRecorder</code>
 *         without creating a <code>MediaView</code> although a view is required for display.</li>
 *     <li>Enable camera recording on a device by calling {@link one.jpro.media.recorder.MediaRecorder#enable()}.
 *         This is required to acquire the permission to access the camera on the device</li>
 *     <li><code>MediaPlayer</code> has few operational states defined by
 *         {@link one.jpro.media.recorder.MediaRecorder.Status}.
 * </ul>
 *
 * @author Besmir Beqiri
 */
package one.jpro.media.recorder;