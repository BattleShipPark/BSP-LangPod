package com.battleshippark.bsp_langpod;

/**
 */

public class AppPhase {
    private final Phase phase;

    public AppPhase(boolean debug) {
        phase = debug ? Phase.DEBUG : Phase.RELEASE;
    }

    public String getServerDomain() {
        return phase.serverDomain;
    }

    private enum Phase {
        DEBUG("langpod-beta.appspot.com"),
        RELEASE("langpod-165807.appspot.com");

        private String serverDomain;

        Phase(String serverDomain) {
            this.serverDomain = serverDomain;
        }
    }
}
