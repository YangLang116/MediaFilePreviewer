let animation

function onPageLoad() {
    let scale = 1.0
    const canvasElement = document.getElementById('canvas')
    let animationData = JSON.parse('{file_content_placeholder}')
    const canvasSize = calcInitSize(animationData.w, animationData.h)

    function scaleCanvas(delta) {
        scale += delta
        if(scale < 0.1) scale = 0.1
        if(scale > 10) scale = 10
        canvasElement.style.width = (canvasSize[0] * scale) + 'px'
        canvasElement.style.height = (canvasSize[1] * scale) + 'px'
    }

    scaleCanvas(0)
    fillCanvasInfo(animationData)

    window.addEventListener('wheel', function(event) {
        if (event.ctrlKey || event.metaKey) {
           event.preventDefault()
           scaleCanvas(event.deltaY > 0 ? -0.1 : 0.1)
        }
    }, { passive: false })

    window.addEventListener('keydown', function(event) {
        if (event.keyCode === 32) {
           playOrPauseAnim()
        }
    })

    animation = bodymovin.loadAnimation({
        container: canvasElement,
        animationData: animationData,
        renderer: 'svg',
        loop: true,
        autoplay: true,
    })
}

function calcInitSize(width, height) {
    const maxSize = 360
    if (width < maxSize && height < maxSize) return [width, height]
    if (height === 0) return [0, 0]
    let rate = width / height
    return width > height ? [maxSize, maxSize / rate] : [maxSize * rate, maxSize]
}

function fillCanvasInfo(animationData) {
    const versionDiv = document.getElementById('version_info') //version
    versionDiv.innerText = 'Version: ' + animationData.v
    const frameDiv = document.getElementById('frame_info') //frame
    frameDiv.innerText = 'Frames: ' + animationData.fr
    const sizeDiv = document.getElementById('size_info') //size
    sizeDiv.innerText = 'Size: ' + animationData.w + ' x ' + animationData.h
}

function playOrPauseAnim() {
    if(animation === null) return
    if(animation.isPaused) {
      animation.play()
      updateStatusText('Pause')
    }else {
      animation.pause()
      updateStatusText('Play')
    }
}

function stopAnim() {
    if (animation !== null) {
      animation.stop()
      updateStatusText('Play')
    }
}

function updateStatusText(statusText) {
    const el = document.getElementsByClassName('controller-item')[0]
    el.textContent = statusText
}