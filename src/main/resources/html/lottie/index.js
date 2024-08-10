let animation

function onPageLoad() {

    let animationData = JSON.parse('{file_content_placeholder}')

    let scale = 1.0
    const canvasSize = calcInitSize(animationData.w, animationData.h)

    const canvasElement = document.getElementById('canvas')
    scaleCanvas(0)
    fillCanvasInfo(animationData);

    function calcInitSize(width, height) {
        const maxSize = 360
        if (width < maxSize && height < maxSize) return [width, height];
        if (height === 0) return [0, 0];
        let rate = width / height;
        return width > height ? [maxSize, maxSize / rate] : [maxSize * rate, maxSize];
    }

    function scaleCanvas(delta) {
        scale += delta
        if(scale < 0.1) scale = 0.1
        if(scale > 10) scale = 10
        canvasElement.style.width = (canvasSize[0] * scale) + 'px';
        canvasElement.style.height = (canvasSize[1] * scale) + 'px';
    }
    window.addEventListener('wheel', function(event) {
        if (event.ctrlKey || event.metaKey) {
           event.preventDefault()
           scaleCanvas(event.deltaY > 0 ? -0.1 : 0.1)
        }
    }, { passive: false });

    function fillCanvasInfo(animationData) {
        const versionDiv = document.getElementById('version_info') //version
        versionDiv.innerText = 'Version: ' + animationData.v;
        const frameDiv = document.getElementById('frame_info') //frame
        frameDiv.innerText = 'Frames: ' + animationData.fr;
        const sizeDiv = document.getElementById('size_info') //size
        sizeDiv.innerText = 'Size: ' + animationData.w + ' x ' + animationData.h;
    }

    animation = bodymovin.loadAnimation({
        container: canvasElement,
        animationData: animationData,
        renderer: 'svg',
        loop: true,
        autoplay: true,
    })
}

function playAnim() {
    if (animation == null) return
    animation.play()
}

function pauseAnim() {
    if (animation == null) return
    animation.pause()
}

function stopAnim() {
    if (animation == null) return
    animation.stop()
}