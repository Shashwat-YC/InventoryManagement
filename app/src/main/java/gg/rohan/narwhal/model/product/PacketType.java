package gg.rohan.narwhal.model.product;

import androidx.annotation.NonNull;

import java.util.Locale;

public enum PacketType {

    RECONDITIONED,
    NEW,
    USED;


    @NonNull
    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static PacketType fromString(String text) {
        return valueOf(text.toUpperCase(Locale.ROOT));
    }
}
