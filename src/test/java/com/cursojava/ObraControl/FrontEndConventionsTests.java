package com.cursojava.ObraControl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Guarda a convenção de nomes do front-end: o campo do backend é em português
 * ({@code apartamentoId}) e a variável local correspondente no JS é em inglês
 * ({@code apartmentId}). Misturar as duas grafias do mesmo campo no mesmo
 * arquivo gera {@code ReferenceError} em tempo de execução — e o
 * {@code node --check} não pega, porque só valida sintaxe.
 *
 * <p>Usar só uma das duas grafias é aceito de propósito: o código do front-end
 * tem variáveis locais em português (por exemplo {@code estadoId} e o parâmetro
 * {@code obraId}), e renomear tudo seria um refactor cosmético fora do escopo.
 * O que não passa é o arquivo que mistura as duas.
 */
class FrontEndConventionsTests {

    private static final Path PASTA_JS = Path.of("src", "main", "resources", "static");

    /** Grafia em português do campo no banco -> grafia da variável local em inglês. */
    private static final Map<String, String> GRAFIA_EM_PORTUGUES = new LinkedHashMap<>();

    static {
        GRAFIA_EM_PORTUGUES.put("apartamentoId", "apartmentId");
        GRAFIA_EM_PORTUGUES.put("obraId", "workId");
        GRAFIA_EM_PORTUGUES.put("cidadeId", "cityId");
        GRAFIA_EM_PORTUGUES.put("estadoId", "stateId");
        GRAFIA_EM_PORTUGUES.put("construtoraId", "builderId");
        GRAFIA_EM_PORTUGUES.put("instaladorId", "installerId");
    }

    /** Comentários, strings e template literals: neles a grafia em português é legítima. */
    private static final Pattern BLOCOS_IGNORADOS = Pattern.compile(
            "(?s)/\\*.*?\\*/|//[^\\n]*|\"(?:\\\\.|[^\"\\\\])*\"|'(?:\\\\.|[^'\\\\])*'|`(?:\\\\.|[^`\\\\])*`");

    @Test
    void naoDeveMisturarGrafiasDoMesmoCampoNoJs() throws IOException {
        List<Path> arquivos = scripts();
        assertFalse(arquivos.isEmpty(), "Nenhum script encontrado em " + PASTA_JS.toAbsolutePath());

        List<String> violacoes = new ArrayList<>();
        for (Path arquivo : arquivos) {
            String codigo = semBlocosIgnorados(Files.readString(arquivo, StandardCharsets.UTF_8));
            for (Map.Entry<String, String> par : GRAFIA_EM_PORTUGUES.entrySet()) {
                int linhaPortugues = linhaComoVariavel(codigo, par.getKey());
                int linhaIngles = linhaComoVariavel(codigo, par.getValue());
                if (linhaPortugues > 0 && linhaIngles > 0) {
                    violacoes.add(arquivo.getFileName() + ":" + linhaPortugues
                            + " usa a variável '" + par.getKey() + "' (português) enquanto '"
                            + par.getValue() + "' também é usado no mesmo arquivo;"
                            + " padronize em uma das duas grafias");
                }
            }
        }
        assertTrue(violacoes.isEmpty(),
                "Grafias misturadas no JS do front-end:\n  " + String.join("\n  ", violacoes));
    }

    /**
     * Devolve a linha da primeira ocorrência do identificador como variável, ou
     * -1 se ele não aparecer fora de chave de objeto ({@code campo:}) ou de
     * acesso a propriedade ({@code algo.campo}).
     */
    private int linhaComoVariavel(String codigo, String identificador) {
        Matcher matcher = Pattern.compile("(?<![.\\w$])" + identificador + "\\b").matcher(codigo);
        while (matcher.find()) {
            int fim = matcher.end();
            boolean chaveDeObjeto = fim < codigo.length() && codigo.charAt(fim) == ':';
            if (!chaveDeObjeto) {
                return (int) codigo.substring(0, matcher.start()).lines().count();
            }
        }
        return -1;
    }

    private List<Path> scripts() throws IOException {
        try (Stream<Path> arquivos = Files.list(PASTA_JS)) {
            return arquivos.filter(path -> path.toString().endsWith(".js")).sorted().toList();
        } catch (UncheckedIOException exception) {
            throw exception.getCause();
        }
    }

    private String semBlocosIgnorados(String codigo) {
        return BLOCOS_IGNORADOS.matcher(codigo).replaceAll(" ");
    }
}
