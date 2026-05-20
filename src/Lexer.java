import java.util.ArrayList;
import java.util.List;

/**
 * Lexer.java — TürkDil Dili Sözcüksel Çözümleyici (Lexical Analyzer)
 *
 * ─── SEBesta CH.4 BAĞLANTISI ───────────────────────────────────────────────
 *
 * Bu sınıf, Sebesta "Concepts of Programming Languages" 10th Ed., Chapter 4'te
 * sunulan lex() fonksiyonunun nesne-yönelimli Java uyarlamasıdır.
 *
 * Sebesta'nın C tabanlı lex() fonksiyonundaki yapı:
 *   - Karakter karakter okuma (nextChar() eşdeğeri: getNextChar())
 *   - Boşluk atlama (whitespace skipping)
 *   - Harf → identifier veya keyword
 *   - Rakam → integer literal
 *   - Operatör / ayırıcı → tek veya çift karakter token
 *   - Bilinmeyen → lexical error
 *
 * Bu implementasyonda Sebesta'nın yaklaşımı şu şekilde genişletilmiştir:
 *   - Türkçe karakterler (ç, ğ, ı, ö, ş, ü, Ç, Ğ, İ, Ö, Ş, Ü) tanımlayıcı
 *     ve anahtar sözcüklerde geçerli sayılır.
 *   - SymbolTable entegrasyonu: lookup ile keyword/identifier ayrımı yapılır.
 *   - Satır sayacı (lineNumber) hata raporlama için tutulur.
 *   - Tek satır (//) ve çok satır (/* ... &#42;/) yorum desteği eklendi.
 *
 * Based on: Sebesta, "Concepts of Programming Languages", 10th Ed., Ch. 4
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class Lexer {

    // ─── Özel İstisna Sınıfı / Custom Exception ───────────────────────────────

    /**
     * Sözcüksel hata (lexical error) oluştuğunda fırlatılır.
     * Hata mesajı satır numarasını ve sorunlu karakteri içerir.
     */
    public static class LexerException extends RuntimeException {
        public LexerException(String message) {
            super(message);
        }
    }

    // ─── Durum Alanları / State Fields ───────────────────────────────────────

    /** Tüm kaynak kod tek bir String olarak tutulur. */
    private final String source;

    /** Şu an incelenen karakterin kaynak koddaki indeksi (Sebesta'daki charIndex). */
    private int pos;

    /** O an işlenen satırın numarası (hata raporlaması için). */
    private int lineNumber;

    /** Keyword ve identifier ayrımı için kullanılan sembol tablosu. */
    private final SymbolTable symbolTable;

    // ─── Yapılandırıcı / Constructor ──────────────────────────────────────────

    /**
     * @param source      Analiz edilecek kaynak kod metni
     * @param symbolTable Önceden hazırlanmış sembol tablosu
     */
    public Lexer(String source, SymbolTable symbolTable) {
        this.source      = source;
        this.symbolTable = symbolTable;
        this.pos         = 0;
        this.lineNumber  = 1;
    }

    // ─── Yardımcı Metotlar / Helper Methods ───────────────────────────────────

    /**
     * Geçerli karakteri döner, ilerlemez.
     * Sebesta'daki peek() eşdeğeri.
     */
    private char peek() {
        if (pos >= source.length()) return '\0';
        return source.charAt(pos);
    }

    /**
     * Bir sonraki karakteri döner (lookahead), ilerlemez.
     * Çift karakterli operatörler (==, !=, <=, >=, ++, --) için kullanılır.
     */
    private char peekNext() {
        if (pos + 1 >= source.length()) return '\0';
        return source.charAt(pos + 1);
    }

    /**
     * Geçerli karakteri tüketir ve sonraki konuma ilerler.
     * Sebesta'daki nextChar() eşdeğeri.
     * Yeni satır karakterini gördüğünde lineNumber'ı artırır.
     *
     * @return Tüketilen karakter
     */
    private char advance() {
        char c = source.charAt(pos++);
        if (c == '\n') lineNumber++;
        return c;
    }

    /**
     * Kaynak kod sona erdiyse true döner.
     */
    private boolean isAtEnd() {
        return pos >= source.length();
    }

    /**
     * Verilen karakterin ASCII veya Türkçe harf olup olmadığını kontrol eder.
     * Sebesta'nın isalpha() eşdeğeri, Türkçe karakterler eklenerek genişletildi.
     *
     * Desteklenen Türkçe karakterler:
     *   Küçük: ç ğ ı ö ş ü
     *   Büyük: Ç Ğ İ Ö Ş Ü
     */
    private boolean isLetter(char c) {
        if (Character.isLetter(c)) return true;
        // Türkçe özel karakterler (ASCII dışı)
        return "çğışüöÇĞİŞÜÖ".indexOf(c) >= 0;
    }

    /**
     * Verilen karakterin rakam olup olmadığını kontrol eder.
     * Sebesta'nın isdigit() eşdeğeri.
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Verilen karakterin tanımlayıcı gövdesinde (identifier body) geçerli
     * olup olmadığını kontrol eder: harf, rakam veya alt çizgi.
     */
    private boolean isIdentChar(char c) {
        return isLetter(c) || isDigit(c) || c == '_';
    }

    // ─── Yorum Atlama / Comment Skipping ─────────────────────────────────────

    /**
     * Tek satır (//) ve çok satır (/* ... *\/) yorumları atlar.
     * Boşluk ve yorum atlama, Sebesta Ch.4 lex() başlangıcındaki
     * "skip whitespace" bloğunun genişletilmiş halidir.
     */
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

    /**
     * Kaynak koddan bir sonraki token'ı okur ve döner.
     *
     * Bu metod Sebesta Ch.4'teki lex() fonksiyonunun doğrudan Java uyarlamasıdır.
     * Sebesta'nın C kodu yapısı:
     *   1. Boşluk atla
     *   2. EOF → DONE döndür
     *   3. isalpha() → identifier veya keyword oku
     *   4. isdigit() → integer literal oku
     *   5. Operatör/ayırıcı → switch ile eşleştir
     *   6. Bilinmeyen → error
     *
     * @return Bir sonraki Token nesnesi
     * @throws LexerException Tanınmayan karakter bulunursa
     */
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

    /**
     * Mevcut konumdan itibaren bir identifier veya keyword okur.
     *
     * Sebesta Ch.4 algoritması:
     *   while (isalpha(nextChar) || isdigit(nextChar))
     *       identBuffer += nextChar
     *   lookup(identBuffer) → keyword mi yoksa identifier mi?
     *
     * @param tokenLine Token'ın başladığı satır
     * @return Keyword veya IDENTIFIER token'ı
     */
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

    /**
     * Mevcut konumdan itibaren bir tam sayı literali okur.
     *
     * Sebesta Ch.4 algoritması:
     *   while (isdigit(nextChar))
     *       intBuffer += nextChar
     *   return INT_LITERAL token
     *
     * @param tokenLine Token'ın başladığı satır
     * @return INT_LITERAL token'ı
     */
    private Token readIntLiteral(int tokenLine) {
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && isDigit(peek())) {
            sb.append(advance());
        }

        return new Token(TokenType.INT_LITERAL, sb.toString(), tokenLine);
    }

    // ─── Toplu Tokenize Etme / Batch Tokenization ────────────────────────────

    /**
     * Kaynak kodun tamamını tokenize eder ve token listesini döner.
     * nextToken() metodunu EOF token'ına ulaşana kadar döngü içinde çağırır.
     *
     * Bu metod Main.java'nın token listesini tek seferde almasını sağlar.
     *
     * @return Tüm token'ların listesi (son eleman her zaman EOF)
     */
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