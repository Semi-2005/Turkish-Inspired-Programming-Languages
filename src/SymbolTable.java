import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * SymbolTable.java — TürkDil Dili Sembol / Arama Tablosu
 *
 * ─── TASARIM AÇIKLAMASI ────────────────────────────────────────────────────
 *
 * Bu sınıf Sebesta Ch.4'teki "symbol/lookup table" konseptini uygular.
 * Tablo iki ayrı bölümden oluşur:
 *
 *  1) KEYWORDS tablosu  (keywordTable):
 *       Lexer oluşturulduğunda önceden (statically) yüklenen anahtar
 *       sözcükler. Bu tablo değişmez; yeni giriş eklenemez.
 *       String → TokenType eşlemesi içerir.
 *
 *  2) IDENTIFIERS tablosu (identifierTable):
 *       Kaynak kod analizi sırasında karşılaşılan kullanıcı tanımlı
 *       isimler burada tutulur. install() metodu ile eklenir.
 *       String → TokenType (her zaman IDENTIFIER) eşlemesi içerir.
 *
 * lookup(name) metodu:
 *   → Önce keywordTable'a bakar. Varsa keyword TokenType'ı döner.
 *   → Sonra identifierTable'a bakar. Varsa IDENTIFIER döner.
 *   → Bulunamazsa null döner (lexer IDENTIFIER olarak ekleyecek).
 *
 * Bu iki-tablolu yaklaşım, Sebesta'nın Ch.4'te tarif ettiği "reserved
 * word lookup" mekanizmasının nesne-yönelimli Java uygulamasıdır.
 *
 * Based on: Sebesta, "Concepts of Programming Languages", 10th Ed., Ch. 4
 */
public class SymbolTable {

    // ─── Tablolar / Tables ────────────────────────────────────────────────────

    /**
     * Anahtar sözcük tablosu — yapılandırıcıda önceden yüklenir, salt-okunur.
     * Key: keyword lexeme (küçük harf)
     * Value: karşılık gelen TokenType enum sabiti
     */
    private final Map<String, TokenType> keywordTable;

    /**
     * Tanımlayıcı tablosu — kaynak kod taranırken doldurulur.
     * Key: identifier lexeme
     * Value: her zaman TokenType.IDENTIFIER
     */
    private final Map<String, TokenType> identifierTable;

    // ─── Yapılandırıcı / Constructor ──────────────────────────────────────────

    public SymbolTable() {
        keywordTable     = new LinkedHashMap<>();
        identifierTable  = new LinkedHashMap<>();
        loadKeywords();
    }

    /**
     * TürkDil dilinin tüm anahtar sözcüklerini keywordTable'a yükler.
     * Bu metod yalnızca bir kez, yapılandırıcı içinde çağrılır.
     */
    private void loadKeywords() {
        keywordTable.put("degisken", TokenType.DEGISKEN);
        keywordTable.put("eger",     TokenType.EGER);
        keywordTable.put("yoksa",    TokenType.YOKSA);
        keywordTable.put("dongu",    TokenType.DONGU);
        keywordTable.put("icin",     TokenType.ICIN);
        keywordTable.put("yazdir",   TokenType.YAZDIR);
        keywordTable.put("dogru",    TokenType.DOGRU);
        keywordTable.put("yanlis",   TokenType.YANLIS);
        keywordTable.put("ve",       TokenType.VE);
        keywordTable.put("veya",     TokenType.VEYA);
        keywordTable.put("degil",    TokenType.DEGIL);
    }

    // ─── Temel İşlemler / Core Operations ────────────────────────────────────

    /**
     * Verilen isme karşılık gelen TokenType'ı arar.
     *
     * Arama sırası:
     *   1. keywordTable  → anahtar sözcük mü?
     *   2. identifierTable → daha önce görülmüş tanımlayıcı mı?
     *   3. null          → tabloda yok
     *
     * @param name Aranacak lexeme
     * @return TokenType veya null (bulunamazsa)
     */
    public TokenType lookup(String name) {
        // Anahtar sözcük tablosunu önce kontrol et
        if (keywordTable.containsKey(name)) {
            return keywordTable.get(name);
        }
        // Sonra tanımlayıcı tablosunu kontrol et
        if (identifierTable.containsKey(name)) {
            return identifierTable.get(name);
        }
        return null; // tabloda yok
    }

    /**
     * Yeni bir tanımlayıcıyı identifierTable'a ekler.
     * Eğer isim zaten keyword ise ekleme yapılmaz.
     * Eğer isim zaten tablodaysa (duplicate), sessizce yok sayılır.
     *
     * @param name Eklenecek identifier lexeme
     * @return eklendiyse true, keyword olduğu için atlandıysa false
     */
    public boolean install(String name) {
        if (keywordTable.containsKey(name)) {
            return false; // keyword'ü identifier olarak ekleyemezsin
        }
        identifierTable.put(name, TokenType.IDENTIFIER);
        return true;
    }

    /**
     * Verilen ismin bir anahtar sözcük olup olmadığını döner.
     *
     * @param name Kontrol edilecek lexeme
     * @return keywordTable'da varsa true
     */
    public boolean isKeyword(String name) {
        return keywordTable.containsKey(name);
    }

    /**
     * Verilen ismin tanımlayıcı tablosunda kayıtlı olup olmadığını döner.
     *
     * @param name Kontrol edilecek lexeme
     * @return identifierTable'da varsa true
     */
    public boolean isIdentifier(String name) {
        return identifierTable.containsKey(name);
    }

    // ─── Tablo İçeriği / Table Content ───────────────────────────────────────

    /** Kayıtlı tüm anahtar sözcükleri döner (debug / raporlama). */
    public Set<String> getKeywords() {
        return keywordTable.keySet();
    }

    /** Kayıtlı tüm tanımlayıcıları döner (debug / raporlama). */
    public Set<String> getIdentifiers() {
        return identifierTable.keySet();
    }

    /**
     * Sembol tablosunun içeriğini okunabilir formatta yazdırır.
     * Hem keyword hem identifier bölümünü gösterir.
     */
    public void printTable() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         SEMBOL TABLOSU / SYMBOL TABLE    ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  [ANAHTAR SÖZCÜKLER / KEYWORDS]          ║");
        System.out.println("╠══════════════════════════════════════════╣");
        for (Map.Entry<String, TokenType> entry : keywordTable.entrySet()) {
            System.out.printf("║  %-20s → %-16s ║%n", entry.getKey(), entry.getValue());
        }
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  [TANIMLAYICILAR / IDENTIFIERS]          ║");
        System.out.println("╠══════════════════════════════════════════╣");
        if (identifierTable.isEmpty()) {
            System.out.println("║  (henüz tanımlayıcı bulunamadı)          ║");
        } else {
            for (Map.Entry<String, TokenType> entry : identifierTable.entrySet()) {
                System.out.printf("║  %-20s → %-16s ║%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("╚══════════════════════════════════════════╝\n");
    }
}