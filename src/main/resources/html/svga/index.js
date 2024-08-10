
function onPageLoad() {
    let scale = 1.0
    let canvasSize
    const canvasStyle = document.getElementById('canvas').style

    function resizeCanvas() {
        canvasStyle.width = (canvasSize[0] * scale) + 'px'
        canvasStyle.height = (canvasSize[1] * scale) + 'px'
    }

    function calcInitSize(videoSize) {
        const maxSize = 360
        const width = videoSize.width
        const height = videoSize.height
        if (width < maxSize && height < maxSize) return [width, height];
        if (height === 0) return [0, 0];
        let rate = width / height;
        return width > height ? [maxSize, maxSize / rate] : [maxSize * rate, maxSize];
    }

    function bindScaleEvent() {
        function scaleCanvas(delta) {
            scale += delta
            if(scale < 0.1) scale = 0.1
            if(scale > 10) scale = 10
            resizeCanvas()
        }
        window.addEventListener('wheel', function(event) {
            if (event.ctrlKey || event.metaKey) {
               event.preventDefault()
               scaleCanvas(event.deltaY > 0 ? -0.1 : 0.1)
            }
        }, { passive: false });
    }

    const player = new SVGA.Player('#canvas')
    const parser = new SVGA.Parser('#canvas')
    parser.load('{file_content_placeholder}', function (videoItem) {
            canvasSize = calcInitSize(videoItem.videoSize)
            resizeCanvas()
            fillCanvasInfo(videoItem)
            bindScaleEvent()
            player.setVideoItem(videoItem);
            player.startAnimation()
    })
}

function fillCanvasInfo(videoItem) {
    function getImageInfo(videoItem) {
        let size = 0;
        let infoList = []
        for (let key in videoItem.images) {
            if (videoItem.images.hasOwnProperty(key)) {
                let n = getImageSize(videoItem.images[key]);
                size += n.width * n.height * 4;
                infoList.push(''.concat(key, ' --- ', "{width: ", n.width.toString(), ", height: ", n.height.toString(), '}'))
            }
        }
        return [convertSize(size), infoList];
    }
    function getImageSize(image) {
        let dec = window.atob(image)
        let length = dec.length
        let array = new Uint8Array(new ArrayBuffer(length));
        let i;
        for (i = 0; i < length; i++) array[i] = dec.charCodeAt(i);
        return {width: 256 * array[18] + array[19], height: 256 * array[22] + array[23]}
    }
    function convertSize(size) {
        if (size < 1024) {
            return size + 'B';
        } else if (size < 1024 * 1024) {
            return Math.round(size * 1.0 / 1024) + 'K';
        } else {
            return Math.round(size * 1.0 / 1024 / 1024) + 'M';
        }
    }
    const versionDiv = document.getElementById('version_info') //version
    versionDiv.innerText = 'Version: '.concat(videoItem.version);
    const fpsDiv = document.getElementById('fps_info') //fps
    fpsDiv.innerText = 'FPS: '.concat(videoItem.FPS);
    const frameDiv = document.getElementById('frame_info') //frame
    frameDiv.innerText = 'Frames: '.concat(videoItem.frames);
    const sizeDiv = document.getElementById('size_info') //size
    let videoSize = videoItem.videoSize
    sizeDiv.innerText = 'Size: '.concat(videoSize.width, ' x ', videoSize.height);
    let imageInfo = getImageInfo(videoItem) //memory
    const memoryDiv = document.getElementById('memory_info')
    memoryDiv.innerText = 'Memory: '.concat(imageInfo[0]);
    //images
    const imageListDiv = document.getElementById('image-info-list')
    let innerHTML = ''
    for (let key in imageInfo[1]) {
        if (imageInfo[1].hasOwnProperty(key)) {
            let line = imageInfo[1][key];
            innerHTML = innerHTML.concat('<li class="image-info-list-item">', line, '</li>')
        }
    }
    imageListDiv.innerHTML = innerHTML
}

function switchColor(target) {
    const canvasStyle = document.getElementById('canvas').style
    canvasStyle.backgroundColor = getComputedStyle(target, null).backgroundColor
}