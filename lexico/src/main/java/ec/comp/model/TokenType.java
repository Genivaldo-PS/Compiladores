package ec.comp.model;

public enum TokenType {
    // Tipos Primitivos e Estruturados
    INT, REAL, LOG, CAR, TXT,
    VETOR, MATRIZ, REGISTRO, FIM_REGISTRO,

    // Estrutura de Programa e Módulos
    INICIO, FIM, MODULO, FIM_MODULO, RETORNA, CHAMAR,

    // Entrada e Saída
    LER, ESCREVER,

    // Operadores Lógicos (Palavras-chave)
    E, OU, NAO, XOU,

    // Controle de Fluxo (Símbolos e Palavras híbridas)
    SE,             // ?
    SENAO,          // !?
    FIM_SE,         // FIM_?
    ENQUANTO,       // @
    FIM_ENQUANTO,   // FIM_@
    PARA,           // #
    FIM_PARA,       // FIM_#

    // Identificadores e Literais
    ID, NUMERO_INT, NUMERO_REAL, TEXTO, CARACTERE,

    // Atribuição e Operadores Aritméticos
    ATRIBUICAO,     // <-
    SOMA,           // +
    SUBTRACAO,      // -
    MULTIPLICACAO,  // *
    DIVISAO_REAL,   // /
    DIVISAO_INTEIRA,// \
    MODULO_OP,      // %
    EXPONENCIACAO,  // ^
    INCREMENTO,     // ++
    DECREMENTO,     // --

    // Concatenação
    CONCATENACAO,   // &

    // Operadores Relacionais
    IGUAL,          // =
    DIFERENTE,      // <>
    MENOR,          // <
    MAIOR,          // >
    MENOR_IGUAL,    // <=
    MAIOR_IGUAL,    // >=

    // Delimitadores e Pontuação
    ABRE_PARENTESES,    // (
    FECHA_PARENTESES,   // )
    ABRE_COLCHETES,     // [
    FECHA_COLCHETES,    // ]
    ABRE_CHAVES,        // {
    FECHA_CHAVES,       // }
    VIRGULA,            // ,

    // Controle e Erros
    EOF, ERRO
}
