import java.util.Scanner;

/**
 * Demo alur kasir: hitung subtotal belanja, pilih jenis diskon,
 * lalu proses pembayaran tunai atau transfer.
 * Data produk di sini disederhanakan jadi input manual subtotal,
 * fokus contoh ini ada di bagian Diskon dan Pembayaran.
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Masukkan subtotal belanja (Rp): ");
        double subtotal = sc.nextDouble();

        System.out.println("\nPilih jenis diskon:");
        System.out.println("1. Tanpa diskon");
        System.out.println("2. Diskon persen (contoh: 10%, maks potongan Rp20.000)");
        System.out.println("3. Diskon nominal (contoh: potongan Rp10.000 min. belanja Rp50.000)");
        System.out.println("4. Diskon member (10% khusus member)");
        System.out.print("Pilihan: ");
        int pilihanDiskon = sc.nextInt();

        Diskon diskon = null;
        switch (pilihanDiskon) {
            case 2:
                diskon = new DiskonPersen(10, 20000);
                break;
            case 3:
                diskon = new DiskonNominal(10000, 50000);
                break;
            case 4:
                System.out.print("Apakah pelanggan member? (y/n): ");
                boolean isMember = sc.next().equalsIgnoreCase("y");
                diskon = new DiskonMember(isMember, 10);
                break;
            default:
                // tidak ada diskon
                break;
        }

        System.out.println("\nPilih metode pembayaran:");
        System.out.println("1. Tunai");
        System.out.println("2. Transfer");
        System.out.print("Pilihan: ");
        int pilihanMetode = sc.nextInt();

        MetodePembayaran metode = (pilihanMetode == 2)
                ? MetodePembayaran.TRANSFER
                : MetodePembayaran.TUNAI;

        Pembayaran pembayaran = new Pembayaran(subtotal, diskon, metode);
        System.out.printf("\nTotal yang harus dibayar: Rp%,.0f%n", pembayaran.getTotalBayar());

        try {
            if (metode == MetodePembayaran.TUNAI) {
                System.out.print("Masukkan jumlah uang tunai yang diterima: Rp");
                double uangDiterima = sc.nextDouble();
                pembayaran.bayarTunai(uangDiterima);
            } else {
                System.out.print("Masukkan nominal transfer yang masuk: Rp");
                double nominalTransfer = sc.nextDouble();
                System.out.print("Masukkan nomor referensi/bukti transfer: ");
                String noReferensi = sc.next();
                pembayaran.bayarTransfer(nominalTransfer, noReferensi);
            }

            System.out.println();
            pembayaran.cetakStruk();

            if (diskon instanceof DiskonMember) {
                DiskonMember dm = (DiskonMember) diskon;
                if (dm.getKeterangan().contains("Diskon Member")) {
                    DiskonMember.tambahPoin("MEMBER001", pembayaran.getTotalBayar());
                    System.out.println("Poin member sekarang: " + DiskonMember.cekPoin("MEMBER001"));
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Transaksi gagal: " + e.getMessage());
        }

        sc.close();
    }
}
