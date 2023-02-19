package listener;

import com.intellij.ide.AppLifecycleListener;
import com.twelvemonkeys.imageio.plugins.svg.SVGImageReaderSpi;
import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import org.jetbrains.annotations.NotNull;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import java.util.List;

public class AppLifecycleListenerImpl implements com.intellij.ide.AppLifecycleListener {

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        AppLifecycleListener.super.appFrameCreated(commandLineArgs);
        IIORegistry.getDefaultInstance().registerServiceProvider(new WebPImageReaderSpi(), ImageReaderSpi.class);
        IIORegistry.getDefaultInstance().registerServiceProvider(new SVGImageReaderSpi(), ImageReaderSpi.class);
    }

}
