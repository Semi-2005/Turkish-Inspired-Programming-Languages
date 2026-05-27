import java.util.ArrayList;
import java.util.List;

public class Lexer {

    // ─── Özel İstisna Sınıfı / Custom Exception ───────────────────────────────

    public static class LexerException extends RuntimeException {
        public LexerException(String message) {
            super(message);
        }
    }

    // ─── Durum Alanları / State Fields ───────────────────────────────────────

    private final String source;

    private int pos;

    private int lineNumber;

    private final SymbolTable symbolTable;

    // ─── Yapılandırıcı / Constructor ──────────────────────────────────────────

    public Lexer(String source, SymbolTable symbolTable) {
        this.source      = source;
        this.symbolTable = symbolTable;
        this.pos         = 0;
        this.lineNumber  = 1;
    }

    // ─── Yardımcı Metotlar / Helper Methods ───────────────────────────────────

    private char peek() {
        if (pos >= source.length()) return '\0';
        return source.charAt(pos);
    }

    private char peekNext() {
        if (pos + 1 >= source.length()) return '\0';
        return source.charAt(pos + 1);
    }

    private char advance() {
        char c = source.charAt(pos++);
        if (c == '\n') lineNumber++;
        return c;
    }

    private boolean isAtEnd() {
        return pos >= source.length();
    }

    private boolean isLetter(char c) {
        if (Character.isLetter(c)) return true;
        // Türkçe özel karakterler (ASCII dışı)
        return "çğışüöÇĞİŞÜÖ".indexOf(c) >= 0;
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isIdentChar(char c) {
        return isLetter(c) || isDigit(c) || c == '_';
    }

    // ─── Yorum Atlama / Comment Skipping ─────────────────────────────────────


    private void skipWhitespaceAndComments() {
        while (!isAtEnd()) {
            char c = peek();

            // Boşluk karakterlerini atla (Sebesta: skip whitespace)
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                advance();
            }
            // Tek satır yorum: // ... satır sonu
            else if (c == '/' && peekNext() == '/') {
                advance(); advance(); // '//' yi tüket
                while (!isAtEnd() && peek() != '\n') {
                    advance();
                }
            }
            // Çok satır yorum: /* ... */
            else if (c == '/' && peekNext() == '*') {
                advance(); advance(); // '/*' yi tüket
                while (!isAtEnd()) {
                    if (peek() == '*' && peekNext() == '/') {
                        advance(); advance(); // '*/' yi tüket
                        break;
                    }
                    advance();
                }
            }
            else {
                break; // Boşluk veya yorum değil, asıl karaktere ulaşıldı
            }
        }
    }

    // ─── Ana Lexer Metodu / Core Lexer Method ────────────────────────────────

    public Token nextToken() {
        // ── ADIM 1: Boşluk ve yorumları atla (Sebesta: skip whitespace) ───────
        skipWhitespaceAndComments();

        // ── ADIM 2: Dosya sonu kontrolü (Sebesta: check for EOF) ──────────────
        if (isAtEnd()) {
            return new Token(TokenType.EOF, "EOF", lineNumber);
        }

        int tokenLine = lineNumber; // Token'ın başladığı satırı kaydet
        char c = peek();

        // ── ADIM 3: Harf → Identifier veya Keyword (Sebesta: isalpha() branch) ─
        if (isLetter(c)) {
            return readIdentifierOrKeyword(tokenLine);
        }

        // ── ADIM 4: Rakam → Integer Literal (Sebesta: isdigit() branch) ────────
        if (isDigit(c)) {
            return readIntLiteral(tokenLine);
        }

        // ── ADIM 5: Operatörler ve Ayırıcılar (Sebesta: switch(nextChar)) ───────
        advance(); // Karakteri tüket

        switch (c) {
            // Aritmetik operatörler
            case '+':
                if (!isAtEnd() && peek() == '+') { advance(); return new Token(TokenType.INCREMENT, "++", tokenLine); }
                return new Token(TokenType.PLUS,  "+", tokenLine);
            case '-':
                if (!isAtEnd() && peek() == '-') { advance(); return new Token(TokenType.DECREMENT, "--", tokenLine); }
                return new Token(TokenType.MINUS, "-", tokenLine);
            case '*':
                return new Token(TokenType.MULT,  "*", tokenLine);
            case '/':
                return new Token(TokenType.DIV,   "/", tokenLine);

            // Atama ve ilişkisel operatörler
            case '=':
                if (!isAtEnd() && peek() == '=') { advance(); return new Token(TokenType.EQ,     "==", tokenLine); }
                return new Token(TokenType.ASSIGN, "=", tokenLine);
            case '!':
                if (!isAtEnd() && peek() == '=') { advance(); return new Token(TokenType.NEQ,    "!=", tokenLine); }
                // Tek '!' tek başına geçerli değil → UNKNOWN
                throw new LexerException(
                    "[SÖZCÜKSEL HATA] Satir " + tokenLine + ": '!' sonrasi '=' beklendi.");
            case '<':
                if (!isAtEnd() && peek() == '=') { advance(); return new Token(TokenType.LTE,    "<=", tokenLine); }
                return new Token(TokenType.LT,    "<",  tokenLine);
            case '>':
                if (!isAtEnd() && peek() == '=') { advance(); return new Token(TokenType.GTE,    ">=", tokenLine); }
                return new Token(TokenType.GT,    ">",  tokenLine);

            // Ayırıcılar
            case '(':  return new Token(TokenType.LPAREN,    "(", tokenLine);
            case ')':  return new Token(TokenType.RPAREN,    ")", tokenLine);
            case '{':  return new Token(TokenType.LBRACE,    "{", tokenLine);
            case '}':  return new Token(TokenType.RBRACE,    "}", tokenLine);
            case ';':  return new Token(TokenType.SEMICOLON, ";", tokenLine);
            case ',':  return new Token(TokenType.COMMA,     ",", tokenLine);

            // ── ADIM 6: Bilinmeyen Karakter (Sebesta: error case) ─────────────
            default:
                // Sözcüksel hata: karakteri bildir ve UNKNOWN döndür
                System.err.printf("[SÖZCÜKSEL HATA] Satir %d: Tanimsiz karakter '%c' (Unicode: U+%04X)%n",
                                  tokenLine, c, (int) c);
                return new Token(TokenType.UNKNOWN, String.valueOf(c), tokenLine);
        }
    }

    // ─── Identifier / Keyword Okuma ───────────────────────────────────────────

    private Token readIdentifierOrKeyword(int tokenLine) {
        StringBuilder sb = new StringBuilder();

        // Harf veya rakam veya alt çizgi geldiği sürece biriktir
        while (!isAtEnd() && isIdentChar(peek())) {
            sb.append(advance());
        }

        String lexeme = sb.toString();

        // Sembol tablosunda ara: keyword mi, identifier mi?
        TokenType type = symbolTable.lookup(lexeme);

        if (type == null) {
            // Tabloda yok → yeni identifier; tabloya ekle
            symbolTable.install(lexeme);
            type = TokenType.IDENTIFIER;
        }
        // type != null → ya keyword (önceden yüklü) ya da daha önce görülmüş identifier

        return new Token(type, lexeme, tokenLine);
    }

    // ─── Integer Literal Okuma ────────────────────────────────────────────────

    private Token readIntLiteral(int tokenLine) {
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && isDigit(peek())) {
            sb.append(advance());
        }

        return new Token(TokenType.INT_LITERAL, sb.toString(), tokenLine);
    }

    // ─── Toplu Tokenize Etme / Batch Tokenization ────────────────────────────

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);
        return tokens;
    }
}