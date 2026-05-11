package com.vitorvidal.criptomoedas;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {
    private static final Path MAIN_PACKAGE = Path.of("src/main/java/com/vitorvidal/criptomoedas");

    @Test
    void domainDoesNotDependOnFrameworksApplicationOrAdapters() throws IOException {
        for (Path file : javaFiles(MAIN_PACKAGE.resolve("domain"))) {
            String source = Files.readString(file);

            assertThat(source)
                    .as(file.toString())
                    .doesNotContain("org.springframework")
                    .doesNotContain("jakarta.persistence")
                    .doesNotContain("com.vitorvidal.criptomoedas.application")
                    .doesNotContain("com.vitorvidal.criptomoedas.adapter");
        }
    }

    @Test
    void applicationDoesNotDependOnFrameworksOrAdapters() throws IOException {
        for (Path file : javaFiles(MAIN_PACKAGE.resolve("application"))) {
            String source = Files.readString(file);

            assertThat(source)
                    .as(file.toString())
                    .doesNotContain("org.springframework")
                    .doesNotContain("jakarta.persistence")
                    .doesNotContain("com.vitorvidal.criptomoedas.adapter");
        }
    }

    private List<Path> javaFiles(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList();
        }
    }
}
