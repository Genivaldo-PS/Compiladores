package ec.comp.analisador;

import ec.comp.model.Token;
import ec.comp.model.TokenType;
import java.util.HashMap;
import java.util.Map;

public class LexerSingular {
    private final String codigoFonte;
    private int pos = 0;
    private int linha = 1;
    private int coluna = 1;

    private static final Map<String, TokenType> palavrasReservadas = new HashMap<>();

    static {
        // Tipos e Estruturas
        palavrasReservadas.put("INT", TokenType.INT);
        palavrasReservadas.put("REAL", TokenType.REAL);
        palavrasReservadas.put("LOG", TokenType.LOG);
        palavrasReservadas.put("CAR", TokenType.CAR);
        palavrasReservadas.put("TXT", TokenType.TXT);
        palavrasReservadas.put("VETOR", TokenType.VETOR);
        palavrasReservadas.put("MATRIZ", TokenType.MATRIZ);
        palavrasReservadas.put("REGISTRO", TokenType.REGISTRO);
        palavrasReservadas.put("FIM_REGISTRO", TokenType.FIM_REGISTRO);

        // Programa e Módulos
        palavrasReservadas.put("INICIO", TokenType.INICIO);
        palavrasReservadas.put("FIM", TokenType.FIM);
        palavrasReservadas.put("MODULO", TokenType.MODULO);
        palavrasReservadas.put("FIM_MODULO", TokenType.FIM_MODULO);
        palavrasReservadas.put("RETORNA", TokenType.RETORNA);
        palavrasReservadas.put("chamar", TokenType.CHAMAR);

        // Entrada e Saída
        palavrasReservadas.put("LER", TokenType.LER);
        palavrasReservadas.put("ESCREVER", TokenType.ESCREVER);

        // Operadores Lógicos
        palavrasReservadas.put("E", TokenType.E);
        palavrasReservadas.put("OU", TokenType.OU);
        palavrasReservadas.put("NAO", TokenType.NAO);
        palavrasReservadas.put("XOU", TokenType.XOU);

        // Fechamentos de Controle de Fluxo
        palavrasReservadas.put("FIM_?", TokenType.FIM_SE);
        palavrasReservadas.put("FIM_@", TokenType.FIM_ENQUANTO);
        palavrasReservadas.put("FIM_#", TokenType.FIM_PARA);
    }

    public LexerSingular(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    private char avancar() {
        if (pos >= codigoFonte.length()) return '\0';
        char c = codigoFonte.charAt(pos++);
        if (c == '\n') {
            linha++;
            coluna = 1;
        } else {
            coluna++;
        }
        return c;
    }

    private char olharAtual() {
        if (pos >= codigoFonte.length()) return '\0';
        return codigoFonte.charAt(pos);
    }

    private char olharProximo() {
        if (pos + 1 >= codigoFonte.length()) return '\0';
        return codigoFonte.charAt(pos + 1);
    }

    public Token proximoToken() {
        pularEspacosEComentarios();

        if (pos >= codigoFonte.length()) {
            return new Token(TokenType.EOF, "EOF", linha, coluna);
        }

        int linhaInicio = linha;
        int colunaInicio = coluna;
        char c = avancar();

        // 1. Identificadores, Palavras Reservadas e Fechamentos (FIM_?, FIM_@, FIM_#)
        if (Character.isLetter(c) || c == '_') {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            while (Character.isLetterOrDigit(olharAtual()) || olharAtual() == '_') {
                sb.append(avancar());
            }

            // Verifica se termina com ?, @ ou # após "FIM_" (ex: FIM_?, FIM_@, FIM_#)
            if (sb.toString().equals("FIM_") && (olharAtual() == '?' || olharAtual() == '@' || olharAtual() == '#')) {
                sb.append(avancar());
            }

            String lexema = sb.toString();
            TokenType tipo = palavrasReservadas.getOrDefault(lexema, TokenType.ID);
            return new Token(tipo, lexema, linhaInicio, colunaInicio);
        }

        // 2. Números Inteiros (INT) e Reais (REAL)
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            boolean isReal = false;

            while (Character.isDigit(olharAtual())) {
                sb.append(avancar());
            }

            if (olharAtual() == '.' && Character.isDigit(olharProximo())) {
                isReal = true;
                sb.append(avancar()); // consome '.'
                while (Character.isDigit(olharAtual())) {
                    sb.append(avancar());
                }
            }

            return new Token(
                isReal ? TokenType.NUMERO_REAL : TokenType.NUMERO_INT,
                sb.toString(),
                linhaInicio,
                colunaInicio
            );
        }

        // 3. Cadeia de Caracteres (TXT): "..."
        if (c == '"') {
            StringBuilder sb = new StringBuilder();
            while (olharAtual() != '"' && olharAtual() != '\0' && olharAtual() != '\n') {
                sb.append(avancar());
            }
            if (olharAtual() == '"') {
                avancar(); // fecha aspas duplas
                return new Token(TokenType.TEXTO, sb.toString(), linhaInicio, colunaInicio);
            } else {
                return new Token(TokenType.ERRO, "Texto (TXT) não fechado", linhaInicio, colunaInicio);
            }
        }

        // 4. Caractere Literal (CAR): 'a'
        if (c == '\'') {
            if (olharAtual() != '\0' && olharAtual() != '\n' && olharProximo() == '\'') {
                char valorChar = avancar();
                avancar(); // fecha aspas simples
                return new Token(TokenType.CARACTERE, String.valueOf(valorChar), linhaInicio, colunaInicio);
            } else {
                return new Token(TokenType.ERRO, "Caractere (CAR) inválido", linhaInicio, colunaInicio);
            }
        }

        // 5. Operadores Compostos, Símbolos de Fluxo e Delimitadores
        switch (c) {
            case '?': return new Token(TokenType.SE, "?", linhaInicio, colunaInicio);
            case '@': return new Token(TokenType.ENQUANTO, "@", linhaInicio, colunaInicio);
            case '#': return new Token(TokenType.PARA, "#", linhaInicio, colunaInicio);

            case '!':
                if (olharAtual() == '?') {
                    avancar();
                    return new Token(TokenType.SENAO, "!?", linhaInicio, colunaInicio);
                }
                return new Token(TokenType.ERRO, "!", linhaInicio, colunaInicio);

            case '<':
                if (olharAtual() == '-') {
                    avancar();
                    return new Token(TokenType.ATRIBUICAO, "<-", linhaInicio, colunaInicio);
                } else if (olharAtual() == '>') {
                    avancar();
                    return new Token(TokenType.DIFERENTE, "<>", linhaInicio, colunaInicio);
                } else if (olharAtual() == '=') {
                    avancar();
                    return new Token(TokenType.MENOR_IGUAL, "<=", linhaInicio, colunaInicio);
                }
                return new Token(TokenType.MENOR, "<", linhaInicio, colunaInicio);

            case '>':
                if (olharAtual() == '=') {
                    avancar();
                    return new Token(TokenType.MAIOR_IGUAL, ">=", linhaInicio, colunaInicio);
                }
                return new Token(TokenType.MAIOR, ">", linhaInicio, colunaInicio);

            case '=':
                return new Token(TokenType.IGUAL, "=", linhaInicio, colunaInicio);

            case '+':
                if (olharAtual() == '+') {
                    avancar();
                    return new Token(TokenType.INCREMENTO, "++", linhaInicio, colunaInicio);
                }
                return new Token(TokenType.SOMA, "+", linhaInicio, colunaInicio);

            case '-':
                if (olharAtual() == '-') {
                    avancar();
                    return new Token(TokenType.DECREMENTO, "--", linhaInicio, colunaInicio);
                }
                return new Token(TokenType.SUBTRACAO, "-", linhaInicio, colunaInicio);

            case '*': return new Token(TokenType.MULTIPLICACAO, "*", linhaInicio, colunaInicio);
            case '/': return new Token(TokenType.DIVISAO_REAL, "/", linhaInicio, colunaInicio);
            case '\\': return new Token(TokenType.DIVISAO_INTEIRA, "\\", linhaInicio, colunaInicio);
            case '%': return new Token(TokenType.MODULO_OP, "%", linhaInicio, colunaInicio);
            case '^': return new Token(TokenType.EXPONENCIACAO, "^", linhaInicio, colunaInicio);
            case '&': return new Token(TokenType.CONCATENACAO, "&", linhaInicio, colunaInicio);

            case '(': return new Token(TokenType.ABRE_PARENTESES, "(", linhaInicio, colunaInicio);
            case ')': return new Token(TokenType.FECHA_PARENTESES, ")", linhaInicio, colunaInicio);
            case '[': return new Token(TokenType.ABRE_COLCHETES, "[", linhaInicio, colunaInicio);
            case ']': return new Token(TokenType.FECHA_COLCHETES, "]", linhaInicio, colunaInicio);
            case '{': return new Token(TokenType.ABRE_CHAVES, "{", linhaInicio, colunaInicio);
            case '}': return new Token(TokenType.FECHA_CHAVES, "}", linhaInicio, colunaInicio);
            case ',': return new Token(TokenType.VIRGULA, ",", linhaInicio, colunaInicio);

            default:
                return new Token(TokenType.ERRO, String.valueOf(c), linhaInicio, colunaInicio);
        }
    }

    private void pularEspacosEComentarios() {
        while (pos < codigoFonte.length()) {
            char atual = olharAtual();

            if (Character.isWhitespace(atual)) {
                avancar();
            } else if (atual == '/' && olharProximo() == '/') {
                while (olharAtual() != '\n' && olharAtual() != '\0') {
                    avancar();
                }
            } else if (atual == '/' && olharProximo() == '*') {
                avancar();
                avancar();
                while (pos < codigoFonte.length() && !(olharAtual() == '*' && olharProximo() == '/')) {
                    avancar();
                }
                if (pos < codigoFonte.length()) {
                    avancar();
                    avancar();
                }
            } else {
                break;
            }
        }
    }
}

