import java.util.HashMap;
import java.util.Map;

/**
 * Class Diskon menangani semua jenis diskon di TR (tanpa diskon, persen,
 * nominal, dan member) dalam satu class saja, dibedakan lewat property
 * "jenis". Cara membuat objeknya lewat static method (factory method)
 * supaya lebih jelas maksudnya, misalnya Diskon.persen(10, 20000).
 */
class Diskon {
    private String jenis;        // "TANPA", "PERSEN", "NOMINAL", atau "MEMBER"
    private double persen;       // dipakai untuk jenis PERSEN dan MEMBER
    private double maxPotongan;  // dipakai untuk jenis PERSEN, 0 = tidak dibatasi
    private double nominal;      // dipakai untuk jenis NOMINAL
    private double minimalBelanja; // dipakai untuk jenis NOMINAL
    private boolean isMember;    // dipakai untuk jenis MEMBER

    // Constructor dibuat private, supaya objek Diskon hanya bisa dibuat
    // lewat static method di bawah (tidak sembarang isi property).
    private Diskon(String jenis, double persen, double maxPotongan,
                    double nominal, double minimalBelanja, boolean isMember) {
        this.jenis = jenis;
        this.persen = persen;
        this.maxPotongan = maxPotongan;
        this.nominal = nominal;
        this.minimalBelanja = minimalBelanja;
        this.isMember = isMember;
    }

    // ===== Static method untuk membuat tiap jenis diskon =====

    public static Diskon tanpaDiskon() {
        return new Diskon("TANPA", 0, 0, 0, 0, false);
    }

    public static Diskon persen(double persen, double maxPotongan) {
        if (persen < 0 || persen > 100) {
            throw new IllegalArgumentException("Persen diskon harus di antara 0 - 100");
        }
        return new Diskon("PERSEN", persen, maxPotongan, 0, 0, false);
    }

    public static Diskon nominal(double nominal, double minimalBelanja) {
        return new Diskon("NOMINAL", 0, 0, nominal, minimalBelanja, false);
    }

    public static Diskon member(boolean isMember, double persenMember) {
        return new Diskon("MEMBER", persenMember, 0, 0, 0, isMember);
    }

    // ===== Method utama: hitung potongan sesuai jenis diskon =====

    public double hitungPotongan(double subtotal) {
        switch (jenis) {
            case "PERSEN":
                double potongan = subtotal * (persen / 100.0);
                if (maxPotongan > 0 && potongan > maxPotongan) {
                    potongan = maxPotongan;
                }
                return potongan;

            case "NOMINAL":
                if (subtotal < minimalBelanja) {
                    return 0;
                }
                return Math.min(nominal, subtotal);

            case "MEMBER":
                if (!isMember) {
                    return 0;
                }
                return subtotal * (persen / 100.0);

            default: // TANPA
                return 0;
        }
    }

    public String getKeterangan() {
        switch (jenis) {
            case "PERSEN":
                return "Diskon " + persen + "%";
            case "NOMINAL":
                return "Diskon Rp" + String.format("%,.0f", nominal)
                        + " (min. belanja Rp" + String.format("%,.0f", minimalBelanja) + ")";
            case "MEMBER":
                return isMember ? "Diskon Member " + persen + "%" : "Bukan Member (tidak ada diskon)";
            default:
                return "Tanpa Diskon";
        }
    }

    public boolean isDiskonMemberAktif() {
        return jenis.equals("MEMBER") && isMember;
    }

    // ===== Poin member sederhana: 1 poin per Rp10.000 belanja =====
    private static final Map<String, Integer> poinMember = new HashMap<>();

    public static void tambahPoin(String idMember, double totalBelanja) {
        int poinBaru = (int) (totalBelanja / 10000);
        poinMember.merge(idMember, poinBaru, Integer::sum);
    }

    public static int cekPoin(String idMember) {
        return poinMember.getOrDefault(idMember, 0);
    }
}
