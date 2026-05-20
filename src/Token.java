/**
 * Token.java — TürkDil Dili Token Sınıfı
 *
 * Lexer'ın ürettiği ve Parser'ın tükettiği token nesnesi.
 * Her token; tipi (TokenType), değeri (lexeme string) ve
 * kaynak kodundaki satır numarasını taşır.
 *
 * Based on: Sebesta, "Concepts of Programming Languages", 10th Ed., Ch. 4
 */
public class Token {

    /** Token'ın kategorisi (keyword, identifier, literal, operator, vb.) */
    private final TokenType type;

    /** Token'ın kaynak kodundaki ham karakter dizisi (lexeme) */
    private final String value;

    /** Token'ın kaynak kodundaki satır numarası (hata raporlama için) */
    private final int line;

    // ─── Yapılandırıcı / Constructor ──────────────────────────────────────────

    /**
     * @param type  Token kategorisi
     * @param value Kaynak koddaki lexeme değeri
     * @param line  Kaynak koddaki satır numarası
     */
    public Token(TokenType type, String value, int line) {
        this.type  = type;
        this.value = value;
        this.line  = line;
    }

    // ─── Erişimciler / Getters ────────────────────────────────────────────────

    public TokenType getType()  { return type;  }
    public String    getValue() { return value; }
    public int       getLine()  { return line;  }

    // ─── String Gösterimi / String Representation ────────────────────────────

    /**
     * Hata ayıklama ve loglama için okunabilir token formatı.
     * Örnek: [DEGISKEN, "degisken", satir:1]
     */
    @Override
    public String toString() {
        return String.format("[%-14s | %-12s | satir: %d]", type, "\"" + value + "\"", line);
    }
}