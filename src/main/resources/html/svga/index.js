function convertSize(size) {
    if (size < 1024) {
        return size + ' B'
    } else if (size < 1024 * 1024) {
        return (size * 1.0 / 1024).toFixed(2) + ' KB'
    } else {
        return (size * 1.0 / 1024 / 1024).toFixed(2) + ' MB'
    }
}

function getImageSize(image) {
    let dec = window.atob(image)
    let length = dec.length
    let array = new Uint8Array(new ArrayBuffer(length))
    for (let i = 0; i < length; i++) array[i] = dec.charCodeAt(i)
    return {width: 256 * array[18] + array[19], height: 256 * array[22] + array[23]}
}

function getImageInfo(videoItem) {
    let size = 0
    const infoList = []
    for (let key in videoItem.images) {
        const n = getImageSize(videoItem.images[key])
        size += n.width * n.height * 4
        infoList.push(key + " --- { width: " + n.width + ", height: " + n.height + " }")
    }
    return [convertSize(size), infoList]
}

function onPageLoad() {
    let canvasSize
    let scale = 1.0
    let isRunning = false
    const canvasElement = document.getElementById('canvas')
    const canvasStyle = canvasElement.style
    const player = new SVGA.Player('#canvas')

    function resizeCanvas() {
        canvasStyle.width = (canvasSize[0] * scale) + 'px'
        canvasStyle.height = (canvasSize[1] * scale) + 'px'
    }

    function toggleAnim() {
        if (isRunning) {
            player.pauseAnimation()
        } else {
            player.startAnimation()
        }
        isRunning = !isRunning
    }

    function bindEvent() {
        function scaleCanvas(delta) {
            scale += delta
            if (scale < 0.1) scale = 0.1
            if (scale > 10) scale = 10
            resizeCanvas()
        }

        window.addEventListener('wheel', function (event) {
            if (event.ctrlKey || event.metaKey) {
                event.preventDefault()
                scaleCanvas(event.deltaY > 0 ? -0.1 : 0.1)
            }
        }, {passive: false})
        window.addEventListener('keydown', function (event) {
            if (event.keyCode === 32) {
                event.preventDefault()
                toggleAnim()
            }
        })
        canvasElement.addEventListener('click', function (event) {
            toggleAnim()
        })
    }

    const parser = new SVGA.Parser('#canvas')
    parser.load('{svga_data}', function (videoItem) {
        canvasSize = calcInitSize(videoItem.videoSize)
        resizeCanvas()
        bindEvent()
        fillCanvasInfo(videoItem)
        player.setVideoItem(videoItem)
        toggleAnim()
    })
}

function calcInitSize(videoSize) {
    const maxSize = 360
    const width = videoSize.width
    const height = videoSize.height
    if (width < maxSize && height < maxSize) return [width, height]
    if (height === 0) return [0, 0]
    let rate = width / height
    return width > height ? [maxSize, maxSize / rate] : [maxSize * rate, maxSize]
}

function fillCanvasInfo(videoItem) {
    document.getElementById('version_info').innerText = 'Version: ' + videoItem.version
    document.getElementById('fps_info').innerText = 'FPS: ' + videoItem.FPS
    document.getElementById('frame_info').innerText = 'Frames: ' + videoItem.frames
    document.getElementById('duration_info').innerText = 'Duration: ' + (videoItem.frames / videoItem.FPS).toFixed(2) + ' s'

    const videoSize = videoItem.videoSize
    document.getElementById('dimension_info').innerText = 'Dimension: ' + videoSize.width + ' x ' + videoSize.height

    const imageInfo = getImageInfo(videoItem)
    document.getElementById('memory_info').innerText = 'Memory: ' + imageInfo[0]
    document.getElementById('image-info-list').innerHTML = imageInfo[1].map((item) => {
        return '<li class="info-item">' + item + '</li>';
    }).join('\n');
}

function switchColor(target) {
    document.body.style.backgroundColor = getComputedStyle(target, null).backgroundColor
}

function showMoreInfo(target) {
    const infoContainer = document.getElementById('info-container')
    if (infoContainer.style.visibility === 'visible') {
        infoContainer.style.visibility = 'hidden'
        target.innerHTML = 'Dev: Off'
    } else {
        infoContainer.style.visibility = 'visible'
        target.innerHTML = 'Dev: On'
    }
}