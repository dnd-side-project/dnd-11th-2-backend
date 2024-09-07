package com.dnd.runus.domain.scale;

public record Scale(long scaleId, String name, int sizeMeter, int index, String startName, String endName) {
    public Scale(long scaleId) {
        this(scaleId, null, 0, 0, null, null);
    }
}
