package dev.peti.excavator.tools;

public enum ExcavatorToolType {
    EXCAVATOR_2X2(2, "2x2x2 Excavator Pickaxe"),
    EXCAVATOR_3X3(3, "3x3x3 Excavator Pickaxe"),
    EXCAVATOR_5X5(5, "5x5x5 Excavator Pickaxe");

    private final int size;
    private final String displayName;

    ExcavatorToolType(int size, String displayName) {
        this.size = size;
        this.displayName = displayName;
    }

    public int getSize() {
        return size;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ExcavatorToolType fromSize(int size) {
        for (ExcavatorToolType type : values()) {
            if (type.size == size) return type;
        }
        return null;
    }
}

