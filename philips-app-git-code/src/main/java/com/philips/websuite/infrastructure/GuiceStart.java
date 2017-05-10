package com.philips.websuite.infrastructure;

import com.philips.app.boot.PhilipsBoot;
import java.net.URL;

/**
 * @author crhobus
 */
public final class GuiceStart {

    private GuiceStart() {}

    public static void main(final String... values) {
        final URL resource = GuiceStart.class.getResource("/configuration.yml");
        final String file = resource.getFile();
        PhilipsBoot.main("-f", file);
    }
}
