package file;

import java.io.Closeable;

public interface FileListener extends Closeable {
    void start() throws InterruptedException;
}
