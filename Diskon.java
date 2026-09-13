import java.util.HashMap;
import java.util.Map;

/**
 * Kontrak untuk semua jenis diskon di TR.
 * Setiap jenis diskon wajib bisa menghitung potongan harga
 * dan memberi keterangan singkat untuk dicetak di struk.
 */
interface Diskon {
    double hitungPotongan(double subtotal);
    String getKeterangan();
}

/**
 * Diskon berbasis persentase, misalnya diskon 10% untuk promo tertentu.
 * Bisa diberi batas maksimal potongan (maxPotongan) agar tidak kebablasan,
 * isi 0 kalau tidak ingin ada batas.
 */
class DiskonPersen implements Diskon {
    private double persen;       // contoh: 10 artinya 10%
    private double maxPotongan;  // 0 = tidak dibatasi

    public DiskonPersen(double persen, double maxPotongan) {
        if (persen < 0 || persen > 100) {
            throw new IllegalArgumentException("Persen diskon harus di antara 0 - 100");
        }
        this.persen = persen;
        this.maxPotongan = maxPotongan;
    }

    @Override
    public double hitungPotongan(double subtotal) {
        double potongan = subtotal * (persen / 100.0);
        if (maxPotongan > 0 && potongan > maxPotongan) {
            potongan = maxPotongan;
        }
        return potongan;
    }

    @Override
    public String getKeterangan() {
        return "Diskon " + persen + "%";
    }
}

/**
 * Diskon nominal tetap, misalnya potongan Rp10.000 untuk minimal belanja tertentu.
 * Kalau subtotal belum memenuhi minimalBelanja, diskon tidak berlaku (potongan 0).
 */
class DiskonNominal implements Diskon {
    private double nominal;
    private double minimalBelanja;

    public DiskonNominal(double nominal, double minimalBelanja) {
        this.nominal = nominal;
        this.minimalBelanja = minimalBelanja;
    }

    @Override
    public double hitungPotongan(double subtotal) {
        if (subtotal < minimalBelanja) {
            return 0;
        }
        // Potongan tidak boleh melebihi subtotal, jaga-jaga kalau nominal terlalu besar
        return Math.min(nominal, subtotal);
    }

    @Override
    public String getKeterangan() {
        return "Diskon Rp" + String.format("%,.0f", nominal)
                + " (min. belanja Rp" + String.format("%,.0f", minimalBelanja) + ")";
    }
}

/**
 * Diskon khusus member. Kalau pelanggan bukan member, potongan otomatis 0.
 * Sistem juga menyimpan poin member sederhana: setiap Rp10.000 belanja = 1 poin.
 */
class DiskonMember implements Diskon {
    private boolean isMember;
    private double persenMember;
    private static final Map<String, Integer> poinMember = new HashMap<>();

    public DiskonMember(boolean isMember, double persenMember) {
        this.isMember = isMember;
        this.persenMember = persenMember;
    }

    @Override
    public double hitungPotongan(double subtotal) {
        if (!isMember) {
            return 0;
        }
        return subtotal * (persenMember / 100.0);
    }

    @Override
    public String getKeterangan() {
        return isMember ? "Diskon Member " + persenMember + "%" : "Bukan Member (tidak ada diskon)";
    }

    // Menambah poin member berdasarkan total belanja setelah diskon
    public static void tambahPoin(String idMember, double totalBelanja) {
        int poinBaru = (int) (totalBelanja / 10000);
        poinMember.merge(idMember, poinBaru, Integer::sum);
    }

    public static int cekPoin(String idMember) {
        return poinMember.getOrDefault(idMember, 0);
    }
}
