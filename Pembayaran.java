/**
 * Metode pembayaran yang didukung TR: tunai atau transfer bank.
 */
enum MetodePembayaran {
    TUNAI, TRANSFER
}

/**
 * Menangani perhitungan total setelah diskon, proses pembayaran
 * (tunai maupun transfer), dan pencetakan struk sederhana.
 */
class Pembayaran {
    private double subtotal;
    private Diskon diskon;          // boleh null kalau tidak ada diskon
    private MetodePembayaran metode;
    private double jumlahDibayar;
    private double kembalian;       // hanya berlaku untuk TUNAI
    private double kelebihanTransfer; // hanya berlaku untuk TRANSFER
    private String noReferensiTransfer; // bukti/kode referensi transfer
    private boolean sudahLunas = false;

    public Pembayaran(double subtotal, Diskon diskon, MetodePembayaran metode) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal tidak boleh negatif");
        }
        this.subtotal = subtotal;
        this.diskon = diskon;
        this.metode = metode;
    }

    // ===== Bagian penerapan diskon =====

    public double getPotonganDiskon() {
        return diskon != null ? diskon.hitungPotongan(subtotal) : 0;
    }

    public double getTotalBayar() {
        double total = subtotal - getPotonganDiskon();
        return Math.max(total, 0); // jaga-jaga total tidak minus
    }

    // ===== Bagian proses pembayaran =====

    /**
     * Proses pembayaran TUNAI. Uang yang diterima kasir dari pelanggan
     * wajib cukup, kelebihannya dikembalikan sebagai uang fisik (kembalian).
     */
    public void bayarTunai(double uangDiterima) {
        if (metode != MetodePembayaran.TUNAI) {
            throw new IllegalStateException("Metode pembayaran ini bukan TUNAI");
        }
        double total = getTotalBayar();
        if (uangDiterima < total) {
            throw new IllegalArgumentException(
                    "Uang tunai kurang. Kurang Rp" + String.format("%,.0f", total - uangDiterima));
        }
        this.jumlahDibayar = uangDiterima;
        this.kembalian = uangDiterima - total;
        this.sudahLunas = true;
    }

    /**
     * Proses pembayaran TRANSFER. Nominal yang masuk ke rekening toko
     * wajib minimal sama dengan total tagihan. Beda dengan tunai, kalau
     * nominal transfer lebih besar dari total, uangnya tidak langsung
     * "dikembalikan" secara fisik, jadi dicatat sebagai kelebihan yang
     * perlu ditindaklanjuti (refund/konfirmasi ke pelanggan).
     */
    public void bayarTransfer(double nominalTransfer, String noReferensi) {
        if (metode != MetodePembayaran.TRANSFER) {
            throw new IllegalStateException("Metode pembayaran ini bukan TRANSFER");
        }
        double total = getTotalBayar();
        if (nominalTransfer < total) {
            throw new IllegalArgumentException(
                    "Nominal transfer kurang. Kurang Rp" + String.format("%,.0f", total - nominalTransfer));
        }
        this.jumlahDibayar = nominalTransfer;
        this.kelebihanTransfer = nominalTransfer - total;
        this.noReferensiTransfer = noReferensi;
        this.sudahLunas = true;
    }

    public double getKembalian() {
        return kembalian;
    }

    public double getKelebihanTransfer() {
        return kelebihanTransfer;
    }

    public boolean isLunas() {
        return sudahLunas;
    }

    public void cetakStruk() {
        if (!sudahLunas) {
            System.out.println("Transaksi belum dibayar, struk tidak bisa dicetak.");
            return;
        }
        System.out.println("========== STRUK PEMBAYARAN ==========");
        System.out.printf("Subtotal        : Rp%,.0f%n", subtotal);
        if (diskon != null) {
            System.out.println("Keterangan      : " + diskon.getKeterangan());
            System.out.printf("Potongan Diskon : Rp%,.0f%n", getPotonganDiskon());
        }
        System.out.printf("Total Bayar     : Rp%,.0f%n", getTotalBayar());
        System.out.println("Metode Bayar    : " + metode);

        if (metode == MetodePembayaran.TUNAI) {
            System.out.printf("Uang Diterima   : Rp%,.0f%n", jumlahDibayar);
            System.out.printf("Kembalian       : Rp%,.0f%n", kembalian);
        } else {
            System.out.printf("Nominal Transfer: Rp%,.0f%n", jumlahDibayar);
            System.out.println("No. Referensi   : " + noReferensiTransfer);
            if (kelebihanTransfer > 0) {
                System.out.printf("Kelebihan Bayar : Rp%,.0f (perlu ditindaklanjuti/refund)%n", kelebihanTransfer);
            }
        }
        System.out.println("=======================================");
    }
}
