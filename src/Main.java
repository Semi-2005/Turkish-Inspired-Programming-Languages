import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Main {

    private static final String DEFAULT_TEST_FILE = "tests/VariableDeclarationExample.txt";

    public static void main(String[] args) {

        printBanner();

        // ── Dosya Yolu Belirleme ──────────────────────────────────────────────
        String filePath = (args.length > 0) ? args[0] : DEFAULT_TEST_FILE;
        System.out.println("📂 Kaynak dosya: " + filePath);

        // ── Kaynak Kodu Oku ───────────────────────────────────────────────────
        String source;
        try {
            source = new String(
                Files.readAllBytes(Paths.get(filePath)),
                StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            System.err.println("❌ HATA: Dosya okunamadı: " + filePath);
            System.err.println("   → " + e.getMessage());
            System.exit(1);
            return;
        }

        printSection("KAYNAK KOD");
        System.out.println(source);

        // ── Sembol Tablosu Oluştur ────────────────────────────────────────────
        SymbolTable symbolTable = new SymbolTable();

        // ── Lexical Analysis ──────────────────────────────────────────────────
        printSection("SÖZCÜKSEL ANALİZ (LEXICAL ANALYSIS)");
        System.out.println("Lexer başlatılıyor... [Sebesta Ch.4 lex() temelli]");

        List<Token> tokens;
        try {
            Lexer lexer = new Lexer(source, symbolTable);
            tokens = lexer.tokenize();
        } catch (Lexer.LexerException e) {
            System.err.println("\n❌ SÖZCÜKSEL HATA: " + e.getMessage());
            System.exit(2);
            return;
        }

        // Token listesini yazdır
        System.out.println("\nToken Listesi:");
        System.out.println("┌──────────────────────────────────────────────────────┐");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.printf("│  %3d. %s%n", i + 1, tokens.get(i));
        }
        System.out.println("└──────────────────────────────────────────────────────┘");
        System.out.printf("Toplam %d token üretildi.%n", tokens.size());

        // ── Sembol Tablosunu Yazdır ───────────────────────────────────────────
        printSection("SEMBOL TABLOSU (SYMBOL TABLE)");
        symbolTable.printTable();

        // ── Syntax Analysis ───────────────────────────────────────────────────
        printSection("SÖZDİZİMİ ANALİZİ (SYNTAX ANALYSIS)");
        System.out.println("Parser başlatılıyor... [Sebesta Ch.4 Recursive-Descent temelli]");

        try {
            Parser parser = new Parser(tokens);
            parser.parse();
        } catch (Parser.ParseException e) {
            System.err.println("\n❌ SÖZDIZIMI HATASI: " + e.getMessage());
            System.exit(3);
            return;
        }

        // ── Özet ─────────────────────────────────────────────────────────────
        printSection("ÖZET");
        System.out.println("✔ Sözcüksel Analiz : BAŞARILI (" + tokens.size() + " token)");
        System.out.println("✔ Sözdizimi Analizi: BAŞARILI");
        System.out.println("✔ Sembol Tablosu   : " +
            symbolTable.getIdentifiers().size() + " tanımlayıcı kayıtlı");
    }

    // ─── Yardımcı Yazdırma Metotları ─────────────────────────────────────────

    private static void printBanner() {

        System.out.println("      TürkDil — Türkçe İlhamlı Programlama Dili       ");
        System.out.println(" Sözcüksel Çözümleyici & Özyinelemeli İniş Ayrıştırıcı ");
        System.out.println(" Kaynak: Sebesta, Concepts of PLs, 10th Ed., Ch. 4    ");


        System.out.println();
    }

    private static void printSection(String title) {
        System.out.println();
        System.out.println("");
        System.out.println("  " + title);
        System.out.println("");
    }
}