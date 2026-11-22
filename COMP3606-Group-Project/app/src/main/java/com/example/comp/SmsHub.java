package com.example.comp;

/**
 * Holds the SmsDataProvider so the receiver can reach it. Register it in a custom Application class
 * (android:name=".App" in the manifest) so it exists even when a text arrives while the app is closed.
 */
public final class SmsHub {
    private static SmsDataProvider provider;

    private SmsHub() {}

    public static void setProvider(SmsDataProvider p) { provider = p; }

    public static SmsDataProvider getProvider() { return provider; }
}
