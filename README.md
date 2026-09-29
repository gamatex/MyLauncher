# Alur Launcher — prototipe Android

Versi sumber 0.5: tombol ▣ menampilkan daftar aplikasi terbaru Alur untuk berpindah cepat. Tekan lama tombol atau pilih **Overview sistem** di panel untuk mencoba Overview Android. Daftar ini menampilkan ikon dan nama aplikasi yang baru dipakai, tanpa kartu pratinjau dari sistem.

Launcher minimalis dengan daftar aplikasi vertikal, favorit, pencarian, indeks alfabet, dan tombol Recent Apps. Tampilan terinspirasi pola navigasi daftar cepat, dengan nama dan identitas sendiri.

## Membangun APK

Impor folder ini sebagai proyek Gradle di Android Studio. Gunakan JDK 17 dan Android SDK 35, lalu pilih **Build > Build APK(s)**. Berkas hasil: `app/build/outputs/apk/debug/app-debug.apk`.

Alternatif: unggah **isi folder ini** (bukan folder pembungkusnya) ke akar repositori GitHub. Pastikan `.github/workflows/build-apk.yml` juga terunggah. Ganti workflow lama sepenuhnya, termasuk langkah `setup-android@v3`, `sdkmanager`, `chmod +x gradlew`, atau JDK 11, lalu jalankan **Actions > Build APK > Run workflow**. Unduh artefak `alur-launcher-debug-apk`. Alur ini memakai Ubuntu 24.04 dengan Android SDK 35 yang sudah terpasang, JDK 17, dan Gradle 8.12; proyek ini memang tidak menyertakan `gradlew`.

## Memakai

1. Instal APK. Saat diminta, pilih **Alur Launcher** sebagai aplikasi layar utama. Bila tidak muncul: **Pengaturan > Aplikasi > Aplikasi default > Aplikasi layar utama**.
2. Ketuk ☆ atau tekan lama baris aplikasi untuk menambah favorit. Ketuk huruf di kanan untuk melompat ke aplikasi dengan awalan tersebut.
3. Ketuk tombol ▣ di samping pencarian. Pertama kali, aktifkan **Alur Launcher** pada **Akses Penggunaan** di pengaturan untuk menampilkan aplikasi terbaru. Ketuk aplikasi untuk berpindah. Data pemakaian tidak dikirim ke server.
4. Untuk mencoba Overview bawaan Android, tekan lama tombol ▣ atau pilih **Overview sistem** di panel. Pertama kali, aktifkan **Tombol Recent Apps Alur** di Aksesibilitas. Keberhasilan Overview sistem bergantung pada ponsel.
5. Untuk kembali ke launcher sebelumnya, ubah lagi aplikasi layar utama di pengaturan Android.

## Batas prototipe

Overview asli tetap milik sistem, sehingga tampilannya mengikuti ponsel. Daftar aplikasi terbaru Alur menggunakan riwayat aplikasi yang dibuka selama tiga hari terakhir, bukan tumpukan tugas dan bukan tangkapan layar; membuka aplikasi bisa memulai ulang aktivitas tertentu bergantung pada aplikasinya. Launcher ini belum memuat widget, paket ikon, notifikasi, atau dukungan profil kerja. Hasilnya dapat berbeda pada ROM vendor. Kode sumber ini belum dikompilasi di lingkungan pembuatan ini karena Android SDK dan Gradle tidak tersedia; build GitHub Actions disediakan untuk menghasilkan APK dan memeriksa kompilasi.
