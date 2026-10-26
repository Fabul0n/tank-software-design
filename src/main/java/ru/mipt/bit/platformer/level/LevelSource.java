package ru.mipt.bit.platformer.level;

import java.io.IOException;

@FunctionalInterface
public interface LevelSource {
    LevelLayout load() throws IOException;
}
