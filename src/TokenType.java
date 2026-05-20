/**
 * TokenType.java — TürkDil Dili Token Kategorileri
 *
 * Sebesta Ch.4 yaklaşımına göre: her token kategorisi bir enum sabiti ile temsil edilir.
 * Lexer bu enum değerlerini döndürür; Parser bu değerleri eşleştirir.
 *
 * Based on: Sebesta, "Concepts of Programming Languages", 10th Ed., Ch. 4
 */
public enum TokenType {

    // ─── Anahtar Sözcükler (Keywords) ─────────────────────────────────────────
    DEGISKEN,   // degisken  → değişken bildirimi (variable declaration)
    EGER,       // eger      → koşul ifadesi (if)
    YOKSA,      // yoksa     → aksi takdirde (else)
    DONGU,      // dongu     → döngü (while)
    ICIN,       // icin      → için (for)
    YAZDIR,     // yazdir    → çıktı (print)
    DOGRU,      // dogru     → boolean true
    YANLIS,     // yanlis    → boolean false
    VE,         // ve        → mantıksal VE (logical AND)
    VEYA,       // veya      → mantıksal VEYA (logical OR)
    DEGIL,      // degil     → mantıksal DEĞİL (logical NOT)

    // ─── Değişmezler / Literals ───────────────────────────────────────────────
    INT_LITERAL,    // 0, 1, 42, 100, ...

    // ─── Tanımlayıcı / Identifier ─────────────────────────────────────────────
    IDENTIFIER,     // kullanıcı tanımlı isimler (user-defined names)

    // ─── Aritmetik Operatörler / Arithmetic Operators ────────────────────────
    PLUS,       // +
    MINUS,      // -
    MULT,       // *
    DIV,        // /

    // ─── İlişkisel Operatörler / Relational Operators ────────────────────────
    EQ,         // ==
    NEQ,        // !=
    LT,         // <
    GT,         // >
    LTE,        // <=
    GTE,        // >=

    // ─── Atama / Assignment ───────────────────────────────────────────────────
    ASSIGN,     // =

    // ─── Artırma / Azaltma ────────────────────────────────────────────────────
    INCREMENT,  // ++
    DECREMENT,  // --

    // ─── Ayırıcılar / Separators ─────────────────────────────────────────────
    LPAREN,     // (
    RPAREN,     // )
    LBRACE,     // {
    RBRACE,     // }
    SEMICOLON,  // ;
    COMMA,      // ,

    // ─── Özel / Special ───────────────────────────────────────────────────────
    EOF,        // dosya sonu (end of file)
    UNKNOWN     // tanınmayan karakter (unrecognized character → lexical error)
}