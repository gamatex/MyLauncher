# Alur Launcher — prototipe Android

Launcher minimalis dengan daftar aplikasi vertikal, favorit, pencarian, indeks alfabet, dan tombol Recent Apps. Tampilan terinspirasi pola navigasi daftar cepat, dengan nama dan identitas sendiri.

## Membangun APK

Impor folder ini sebagai proyek Gradle di Android Studio. Gunakan JDK 17 dan Android SDK 35, lalu pilih **Build > Build APK(s)**. Berkas hasil: `app/build/outputs/apk/debug/app-debug.apk`.

Alternatif: unggah **isi folder ini** (bukan folder pembungkusnya) ke akar repositori GitHub. Pastikan `.github/workflows/build-apk.yml` juga terunggah. Hapus atau nonaktifkan workflow lama yang menjalankan `chmod +x gradlew` atau JDK 11, lalu jalankan **Actions > Build APK > Run workflow**. Unduh artefak `alur-launcher-debug-apk`. Alur ini menggunakan JDK 17, Gradle 8.12, dan SDK Android 35 dari runner; proyek ini memang tidak menyertakan `gradlew`.

## Memakai

1. Instal APK. Saat diminta, pilih **Alur Launcher** sebagai aplikasi layar utama. Bila tidak muncul: **Pengaturan > Aplikasi > Aplikasi default > Aplikasi layar utama**.
2. Ketuk ☆ atau tekan lama baris aplikasi untuk menambah favorit. Ketuk huruf di kanan untuk melompat ke aplikasi dengan awalan tersebut.
3. Ketuk tombol ▣ di samping pencarian. Pertama kali, aktifkan **Tombol Recent Apps Alur** di Aksesibilitas. Layanan itu hanya memanggil aksi Overview dan tidak mengambil isi jendela.
4. Untuk kembali ke launcher sebelumnya, ubah lagi aplikasi layar utama di pengaturan Android.

## Batas prototipe

Overview tetap milik sistem, sehingga tampilannya mengikuti ponsel. Launcher ini belum memuat widget, paket ikon, notifikasi, atau dukungan profil kerja. Hasilnya dapat berbeda pada ROM vendor. Kode sumber ini belum dikompilasi di lingkungan pembuatan ini karena Android SDK dan Gradle tidak tersedia; build GitHub Actions disediakan untuk menghasilkan APK dan memeriksa kompilasi.
