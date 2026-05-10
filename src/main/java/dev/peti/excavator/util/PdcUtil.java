package dev.peti.excavator.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PdcUtil {
    public static boolean hasInt(PersistentDataContainer pdc, NamespacedKey key) {
        return pdc.has(key, PersistentDataType.INTEGER);
    }

    public static Integer getInt(PersistentDataContainer pdc, NamespacedKey key) {
        return pdc.get(key, PersistentDataType.INTEGER);
    }

    public static void setInt(PersistentDataContainer pdc, NamespacedKey key, int value) {
        pdc.set(key, PersistentDataType.INTEGER, value);
    }
}

