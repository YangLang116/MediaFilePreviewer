let animation

function onPageLoad() {
    let animationData = JSON.parse('{data_placeholder}')
    animation = bodymovin.loadAnimation({
        container: document.getElementById('canvas'),
        animationData: animationData,
        renderer: 'canvas',
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