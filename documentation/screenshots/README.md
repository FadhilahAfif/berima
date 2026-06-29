# Dokumentasi Screenshot Prototype Berima

Tanggal pengambilan: 2026-06-29  
Perangkat: Samsung SM-A235F, Android 14, serial ADB `RR8T303EZAK`  
Resolusi screenshot: 1080 x 2408 piksel, portrait, PNG  
Build variant: Debug  
Package aplikasi: `upnvj.berima.v1`  

## Akun Demo

| Peran | Nama Tampilan | Akun Demo | Catatan |
|---|---|---|---|
| Pengguna jasa | Dian Rahayu | Akun pengguna jasa dari seed demo | Password tidak dicantumkan dalam dokumentasi. |
| Penyedia jasa | Andi Pratama | Akun penyedia jasa dari seed demo | Password tidak dicantumkan dalam dokumentasi. |

## Command Utama

| Kebutuhan | Command |
|---|---|
| Seed data demo | `node scripts/seed-demo.js` |
| Build dan install debug | `./gradlew.bat :app:installDebug --console=plain` |
| Jalankan aplikasi | `adb -s RR8T303EZAK shell am start -n upnvj.berima.v1/.MainActivity` |
| Ambil screenshot | `adb -s RR8T303EZAK exec-out screencap -p > documentation/screenshots/<folder>/<file>.png` |
| Baca struktur UI | `adb -s RR8T303EZAK exec-out uiautomator dump /dev/tty` |

## Daftar Folder

| Alur | Folder | Jumlah |
|---|---|---:|
| Autentikasi dan Beranda | `01_autentikasi_dan_beranda` | 5 |
| Pencarian dan Pemesanan Jasa | `02_pencarian_dan_pemesanan_jasa` | 6 |
| Pesanan dan Penyelesaian | `03_pesanan_dan_penyelesaian` | 10 |
| Profil, Verifikasi, dan Reputasi | `04_profil_verifikasi_dan_reputasi` | 7 |
| Pengelolaan Jasa oleh Penyedia Jasa | `05_pengelolaan_jasa_penyedia_jasa` | 10 |

## Daftar Screenshot

| Alur | File | Halaman | Peran | Status |
|---|---|---|---|---|
| Autentikasi dan Beranda | `01_splash_screen.png` | Splash screen | Umum | Berhasil |
| Autentikasi dan Beranda | `02_login.png` | Login | Pengguna jasa | Berhasil |
| Autentikasi dan Beranda | `03_registrasi.png` | Registrasi | Pengguna jasa | Berhasil |
| Autentikasi dan Beranda | `04_beranda.png` | Beranda | Pengguna jasa | Berhasil |
| Autentikasi dan Beranda | `05_kategori_jasa.png` | Kategori jasa pada beranda | Pengguna jasa | Berhasil |
| Pencarian dan Pemesanan Jasa | `01_daftar_jasa.png` | Daftar jasa | Pengguna jasa | Berhasil |
| Pencarian dan Pemesanan Jasa | `02_pencarian_jasa.png` | Pencarian jasa | Pengguna jasa | Berhasil |
| Pencarian dan Pemesanan Jasa | `03_hasil_pencarian.png` | Hasil pencarian | Pengguna jasa | Berhasil |
| Pencarian dan Pemesanan Jasa | `04_detail_jasa.png` | Detail jasa | Pengguna jasa | Berhasil |
| Pencarian dan Pemesanan Jasa | `05_formulir_pemesanan.png` | Formulir pemesanan | Pengguna jasa | Berhasil |
| Pencarian dan Pemesanan Jasa | `06_ringkasan_pesanan.png` | Ringkasan pesanan | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `01_ringkasan_pesanan.png` | Ringkasan pesanan | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `02_simulasi_pembayaran.png` | Simulasi pembayaran | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `03_status_escrow.png` | Status escrow simulasi | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `04_chat_pesanan.png` | Chat pesanan | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `05_pesanan_diproses.png` | Pesanan diproses | Penyedia jasa | Berhasil |
| Pesanan dan Penyelesaian | `06_hasil_pekerjaan.png` | Hasil pekerjaan | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `07_permohonan_revisi.png` | Permohonan revisi | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `08_hasil_revisi.png` | Hasil dan catatan revisi | Penyedia jasa | Berhasil |
| Pesanan dan Penyelesaian | `09_pesanan_selesai.png` | Pesanan selesai | Pengguna jasa | Berhasil |
| Pesanan dan Penyelesaian | `10_riwayat_pesanan.png` | Riwayat pesanan | Pengguna jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `01_profil_penyedia_jasa.png` | Profil penyedia jasa | Pengguna jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `02_portofolio.png` | Portofolio | Pengguna jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `03_pusat_verifikasi.png` | Pusat verifikasi | Penyedia jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `04_verifikasi_identitas.png` | Verifikasi identitas | Penyedia jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `05_verifikasi_keterampilan.png` | Verifikasi keterampilan | Penyedia jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `06_rating_dan_ulasan.png` | Rating dan ringkasan ulasan | Pengguna jasa | Berhasil |
| Profil, Verifikasi, dan Reputasi | `07_reputasi_penyedia_jasa.png` | Reputasi penyedia jasa | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `01_dashboard_penyedia_jasa.png` | Dashboard penyedia jasa | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `02_daftar_jasa_saya.png` | Daftar jasa saya | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `03_tambah_jasa.png` | Tambah jasa | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `04_edit_jasa.png` | Edit jasa | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `05_detail_jasa_milik_saya.png` | Detail jasa milik saya | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `06_pesanan_masuk.png` | Pesanan masuk | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `07_detail_pesanan_masuk.png` | Detail pesanan masuk | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `08_pesanan_diterima.png` | Pesanan diterima | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `09_pengerjaan_pesanan.png` | Pengerjaan pesanan | Penyedia jasa | Berhasil |
| Pengelolaan Jasa oleh Penyedia Jasa | `10_kirim_hasil_pekerjaan.png` | Kirim hasil pekerjaan | Penyedia jasa | Berhasil |

## Halaman Tidak Tersedia atau Tidak Terpisah

| Halaman | Catatan |
|---|---|
| Daftar kategori jasa terpisah | Aplikasi menampilkan kategori sebagai rail/filter pada beranda, bukan halaman mandiri. Screenshot diambil dari rail kategori. |
| Rating dan ulasan terpisah | Tidak ada halaman ulasan mandiri. Screenshot diambil dari area rating/reputasi dan detail penyedia jasa yang tersedia. |
| Pembayaran nyata atau escrow nyata | Fitur yang tersedia adalah simulasi alur pembayaran. Tidak ada transaksi uang nyata. |
| Form sistem untuk unggah hasil | Screenshot hanya mengambil entry point `Unggah Hasil` atau `Kirim Revisi` di aplikasi, bukan file picker sistem. |

## Kendala dan Catatan Pengulangan

| Topik | Catatan |
|---|---|
| Data awal | Data dokumentasi memakai seed `scripts/seed-demo.js`, termasuk profil Dian Rahayu, Andi Pratama, jasa desain PPT, portofolio, verifikasi, dan beberapa status pesanan. |
| Konsistensi perangkat | Semua screenshot diambil dari perangkat fisik yang sama dengan orientasi portrait dan resolusi 1080 x 2408. |
| Simulasi pembayaran | Tombol `Simulasi Bayar` tampil dan teks aplikasi menjelaskan bahwa tidak ada uang asli yang diproses. |
| Mutasi data | Proses login, pembuatan pesanan, dan pembacaan seed dilakukan pada data demo. Tidak ada penghapusan data produksi. |
| Pemeriksaan kualitas | Seluruh PNG diperiksa ukuran/resolusinya, dicek tidak kosong, dan hash duplikat sudah dibersihkan. |
