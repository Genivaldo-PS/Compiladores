package ec.comp;

import ec.comp.analisador.LexerSingular;
import ec.comp.model.Token;
import ec.comp.model.TokenType;
import ec.comp.util.LeitorArquivo;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        String codigoFonte;

        try {
            if (args.length > 0) {
                codigoFonte = LeitorArquivo.ler(args[0]);
            } else {
                codigoFonte = """
                    REGISTRO Aluno
                        TXT nome
                        REAL nota
                    FIM_REGISTRO

                    MODULO calcular_media <REAL n1, REAL n2> RETORNA REAL
                        REAL resultado <- (n1 + n2) / 2.0
                        RETORNA resultado
                    FIM_MODULO

                    INICIO
                        Aluno estudante
                        REAL nota_final, nota1, nota2

                        ESCREVER "Digite o nome do aluno:"
                        LER estudante{nome}

                        nota_final <- chamar calcular_media <nota1, nota2>
                        estudante{nota} <- nota_final

                        ESCREVER "A média de " & estudante{nome} & " é " & estudante{nota}

                        ? estudante{nota} >= 7.0
                            ESCREVER "Aprovado!"
                        !?
                            ESCREVER "Reprovado!"
                        FIM_?
                    FIM
                    """;
            }

            LexerSingular lexer = new LexerSingular(codigoFonte);
            Token token;

            System.out.println("=== TOKENS DA LINGUAGEM SINGULAR ===");
            do {
                token = lexer.proximoToken();
                System.out.println(token);
            } while (token.getTipo() != TokenType.EOF);

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }
}
