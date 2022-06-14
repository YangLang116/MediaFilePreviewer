function onPageLoad() {
    let intArray = new Int8Array({data})
    const u8Blob = new Blob([intArray], {type: "application/octet-stream"})
    let url = URL.createObjectURL(u8Blob)
    const player = videojs('video', {
        autoplay: true,
        preload: "auto",
        width: "720",
        height: "480",
        controls: true,
        sources: [{
            type: "video/mp4",
            src: url
        }]
    });
}