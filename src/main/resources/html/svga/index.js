function onPageLoad() {
    const player = new SVGA.Player('#canvas')
    const parser = new SVGA.Parser('#canvas')
    parser.load('{file_content_placeholder}', function (videoItem) {
        fillCanvasInfo(videoItem)
        resizeCanvas(videoItem)
        player.setVideoItem(videoItem);
        player.startAnimation()
    })
}

function resizeCanvas(videoItem) {
    let videoSize = videoItem.videoSize
    let size = getFitSize(videoSize.width, videoSize.height)
    let canvasStyle = document.getElementById('canvas').style
    canvasStyle.width = size[0].toString().concat('px')
    canvasStyle.height = size[1].toString().concat('px')
}

const MAX_SIZE = 320

function getFitSize(width, height) {
    if (width < MAX_SIZE && height < MAX_SIZE) return [width, height];
    if (height === 0) return [0, 0];
    let rate = width / height;
    return width > height ? [MAX_SIZE, MAX_SIZE / rate] : [MAX_SIZE * rate, MAX_SIZE];
}

function fillCanvasInfo(videoItem) {
    let versionDiv = document.getElementById('version_info') //version
    versionDiv.innerText = 'Version: '.concat(videoItem.version);
    let fpsDiv = document.getElementById('fps_info') //fps
    fpsDiv.innerText = 'FPS: '.concat(videoItem.FPS);
    let frameDiv = document.getElementById('frame_info') //frame
    frameDiv.innerText = 'Frames: '.concat(videoItem.frames);
    let sizeDiv = document.getElementById('size_info') //size
    let videoSize = videoItem.videoSize
    sizeDiv.innerText = 'Size: '.concat(videoSize.width, ' x ', videoSize.height);
    let imageInfo = getImageInfo(videoItem) //memory
    let memoryDiv = document.getElementById('memory_info')
    memoryDiv.innerText = 'Memory: '.concat(imageInfo[0]);
    //images
    let imageListDiv = document.getElementById('image-info-list')
    let innerHTML = ''
    for (let key in imageInfo[1]) {
        if (imageInfo[1].hasOwnProperty(key)) {
            let line = imageInfo[1][key];
            innerHTML = innerHTML.concat('<li class="image-info-list-item">', line, '</li>')
        }
    }
    imageListDiv.innerHTML = innerHTML
}

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

function switchColor(target) {
    let canvasStyle = document.getElementById('canvas').style
    canvasStyle.backgroundColor = getComputedStyle(target, null).backgroundColor
}

let currentZoom = 1;

function zoomIn() {
    let bodyStyle = document.getElementById('root').style
    currentZoom -= 0.1
    if (currentZoom <= 0) currentZoom = 0.1
    bodyStyle.zoom = currentZoom.toString()
}

function zoomOut() {
    let bodyStyle = document.getElementById('root').style
    currentZoom += 0.1
    if (currentZoom > 3) currentZoom = 3
    bodyStyle.zoom = currentZoom.toString()
}