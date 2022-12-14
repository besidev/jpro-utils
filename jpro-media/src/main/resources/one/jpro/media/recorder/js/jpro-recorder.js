let camera_stream = null;
let media_recorder = null;
let blobs_recorded = [];

enableCamera = async function(videoId, options) {
    let preview = document.getElementById(videoId);
    camera_stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
    preview.srcObject = camera_stream;

    // set MIME type of recording as video/webm
    media_recorder = new MediaRecorder(camera_stream, options);

    // event : new recorded video blob available
    media_recorder.addEventListener('dataavailable', function(e) {
        blobs_recorded.push(e.data);
        jpro.mediaRecorderOnDataavailable(e.timecode);
    });

    // event : recording stopped & all blobs sent
    media_recorder.onstop = (event) => {
        // create local object URL from the recorded video blobs
        let recordedBlob = new Blob(blobs_recorded, { type: "video/webm" });
        var videoUrl = URL.createObjectURL(recordedBlob);
        // pas object url and file size as json format
        jpro.mediaRecorderOnStop(JSON.stringify({
            objectUrl: videoUrl,
            fileSize: recordedBlob.size
        }));
    }

    media_recorder.onerror = (event) => {
        // pas error type and message as json format
        jpro.mediaRecorderOnError(JSON.stringify({
            type: event.error.code,
            message: event.error.message
        }));
    }

    media_recorder.onstart = (event) => jpro.mediaRecorderOnStart(media_recorder.state)
    media_recorder.onpause = (event) => jpro.mediaRecorderOnPause(media_recorder.state)
    media_recorder.onresume = (event) => jpro.mediaRecorderOnResume(media_recorder.state)
}

startRecording = function() {
    // clear recorded buffer
    blobs_recorded = [];
    // start recording with a timeslice of 1 sec
    media_recorder.start(1000);
}

pauseRecording = function() {
    media_recorder.pause();
}

resumeRecording = function() {
    media_recorder.resume();
}

stopRecording = function() {
    media_recorder.stop();
}