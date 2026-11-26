package ec.comp.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LeitorArquivo {
    public static String ler(String caminhoArquivo) throws IOException {
        return Files.readString(Path.of(caminhoArquivo), StandardCharsets.UTF_8);
    }
}
