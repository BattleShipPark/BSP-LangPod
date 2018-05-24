package io.reactivex;

//https://realm.io/docs/java/latest#gson-troubleshooting
public enum BackpressureStrategy {
    MISSING,
    ERROR,
    BUFFER,
    DROP,
    LATEST
}
