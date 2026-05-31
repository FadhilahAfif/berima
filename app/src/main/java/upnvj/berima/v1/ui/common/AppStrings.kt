package upnvj.berima.v1.ui.common

object AppStrings {
    const val SEARCH_PLACEHOLDER = "Cari layanan..."
    const val SEARCH_HINT = "Ketik untuk mencari layanan."
    const val SEARCH_NO_RESULTS_TITLE = "Tidak ada hasil"
    const val SEARCH_NO_RESULTS_BODY = "Coba kata kunci lain."

    const val SPLASH_TAGLINE = "Tugas Tuntas, Kantong Pas."
    const val SPLASH_FOOTER = "BERIMA · 2026"

    const val NAV_HOME = "Beranda"
    const val NAV_ORDERS = "Pesanan"
    const val NAV_PROFILE = "Profil"

    const val ORDERS_TITLE = "Pesanan"
    const val ORDERS_TAB_BUYER = "Pembeli"
    const val ORDERS_TAB_SELLER = "Penjual"

    // Orders list
    const val ORDERS_ROLE_SELLER_PREFIX = "Penjual"
    const val ORDERS_ROLE_BUYER_PREFIX = "Pembeli"
    const val ORDERS_EMPTY_TITLE = "Belum ada pesanan"
    const val ORDERS_EMPTY_BODY_BUYER = "Mulai pesan layanan dari teman kampusmu."
    const val ORDERS_EMPTY_BODY_SELLER = "Listing kamu belum ada yang dipesan."

    // Order detail
    const val ORDER_DETAIL_TITLE = "Detail Pesanan"
    const val ORDER_DETAIL_NOT_FOUND = "Pesanan tidak ditemukan"
    const val ORDER_DETAIL_STATUS_LABEL = "STATUS PESANAN"
    const val ORDER_DETAIL_NOTE_LABEL = "CATATAN DARI PEMBELI"
    const val ORDER_DETAIL_COUNTERPARTY_SELLER = "PENJUAL"
    const val ORDER_DETAIL_COUNTERPARTY_BUYER = "PEMBELI"
    const val ORDER_DETAIL_ATTACHMENT_TITLE = "Hasil pekerjaan"
    const val ORDER_DETAIL_ATTACHMENT_ACTION = "Buka file"

    // Order status contextual sentences (status x role)
    const val ORDER_STATUS_PENDING_BUYER = "Menunggu penjual menerima pesananmu."
    const val ORDER_STATUS_PENDING_SELLER = "Pesanan baru masuk. Terima untuk mulai mengerjakan."
    const val ORDER_STATUS_IN_PROGRESS_BUYER = "Penjual sedang mengerjakan pesananmu."
    const val ORDER_STATUS_IN_PROGRESS_SELLER = "Kerjakan lalu unggah hasilnya untuk pembeli."
    const val ORDER_STATUS_DELIVERED_BUYER = "Hasil sudah dikirim. Periksa dan konfirmasi bila sesuai."
    const val ORDER_STATUS_DELIVERED_SELLER = "Menunggu pembeli mengonfirmasi hasil."
    const val ORDER_STATUS_COMPLETED_BUYER = "Pesanan selesai. Lanjut ke pembayaran."
    const val ORDER_STATUS_COMPLETED_SELLER = "Menunggu pembayaran dari pembeli."
    const val ORDER_STATUS_PAID_BUYER = "Pesanan tuntas dan sudah dibayar. Terima kasih."
    const val ORDER_STATUS_PAID_SELLER = "Pembayaran diterima. Pesanan tuntas."
    const val ORDER_STATUS_CANCELLED = "Pesanan ini dibatalkan."
    const val ORDER_STATUS_REJECTED = "Pesanan ini ditolak penjual."

    // Chat section
    const val ORDER_CHAT_TITLE_PREFIX = "Chat dengan"
    const val ORDER_CHAT_EMPTY_TITLE = "Belum ada pesan"
    const val ORDER_CHAT_EMPTY_BODY_PREFIX = "Mulai percakapan dengan"
    const val ORDER_CHAT_INPUT_PLACEHOLDER = "Tulis pesan..."

    const val CREATE_ORDER_TITLE = "Konfirmasi Pesanan"

    const val LOGIN_HEADLINE = "Halo,\nselamat datang."
    const val LOGIN_SUBHEADLINE = "Masuk untuk lanjut beri jasa, terima hasil."

    const val REGISTER_HEADLINE = "Buat\nakun baru."
    const val REGISTER_SUBHEADLINE = "Daftar dan mulai tawarkan jasamu."

    const val BACK_CONTENT_DESCRIPTION = "Kembali"

    const val REVIEW_TITLE = "Tulis Ulasan"

    const val HOME_GREETING_NAMED = "Halo, %s"
    const val HOME_GREETING_GENERIC = "Halo"
    const val HOME_SUBTITLE = "Mau tugas apa yang dituntaskan hari ini?"
    const val HOME_SECTION_FEATURED = "Sedang ramai"
    const val HOME_SECTION_FEATURED_SUB = "Paling banyak dipesan minggu ini"
    const val HOME_SECTION_LATEST = "Terbaru"
    const val HOME_EMPTY_TITLE = "Belum ada listing"
    const val HOME_EMPTY_BODY = "Coba kategori lain atau kembali nanti."

    const val SEARCH_NO_RESULTS_TITLE_QUERY = "Tidak ada hasil untuk"

    // Profile
    const val PROFILE_TITLE = "Profil"
    const val PROFILE_LOGOUT = "Keluar"
    const val PROFILE_EDIT = "Edit Profil"
    const val PROFILE_ADD_LISTING = "Tambah Listing Baru"
    const val PROFILE_MY_LISTINGS = "Listing Saya"
    const val PROFILE_NOT_FOUND = "Profil tidak ditemukan"
    const val PROFILE_STAT_RATING = "RATING"
    const val PROFILE_STAT_AS_SELLER = "JADI PENJUAL"
    const val PROFILE_STAT_AS_BUYER = "JADI PEMBELI"
    const val PROFILE_LISTING_COUNT_LABEL = "LISTING"
    const val PROFILE_EMPTY_LISTINGS_TITLE = "Belum ada listing"
    const val PROFILE_EMPTY_LISTINGS_BODY = "Tawarkan jasamu, mulai dari listing pertama."
    const val ROLE_BUYER = "Pembeli"
    const val ROLE_SELLER = "Penjual"
    const val ROLE_BOTH = "Keduanya"

    // Edit profile
    const val EDIT_PROFILE_TITLE = "Edit Profil"
    const val EDIT_PROFILE_CHANGE_PHOTO = "Ubah Foto"
    const val EDIT_PROFILE_FIELD_NAME = "Nama"
    const val EDIT_PROFILE_FIELD_BIO = "Bio"
    const val EDIT_PROFILE_BIO_PLACEHOLDER = "Ceritakan sedikit tentang dirimu"
    const val EDIT_PROFILE_FIELD_FACULTY = "Fakultas"
    const val EDIT_PROFILE_FACULTY_PLACEHOLDER = "Contoh: Teknik Informatika"
    const val EDIT_PROFILE_ROLE_LABEL = "Peran"
    const val EDIT_PROFILE_ROLE_HELP = "Pilih bagaimana kamu memakai Berima."
    const val EDIT_PROFILE_SAVE = "Simpan Perubahan"

    // Listing form (Create + Edit)
    const val LISTING_CREATE_TITLE = "Buat Listing"
    const val LISTING_EDIT_TITLE = "Edit Listing"
    const val LISTING_SECTION_DETAIL = "Detail Listing"
    const val LISTING_SECTION_PRICING = "Harga & Waktu"
    const val LISTING_SECTION_EXTRA = "Tambahan"
    const val LISTING_FIELD_TITLE = "Judul"
    const val LISTING_TITLE_PLACEHOLDER = "Contoh: Desain poster acara kampus"
    const val LISTING_FIELD_CATEGORY = "Kategori"
    const val LISTING_FIELD_DESCRIPTION = "Deskripsi"
    const val LISTING_DESCRIPTION_PLACEHOLDER = "Jelaskan layanan yang kamu tawarkan"
    const val LISTING_FIELD_PRICE = "Harga"
    const val LISTING_PRICE_PLACEHOLDER = "0"
    const val LISTING_FIELD_DELIVERY = "Waktu pengerjaan"
    const val LISTING_DELIVERY_PLACEHOLDER = "Maks. 48"
    const val LISTING_DELIVERY_UNIT = "jam"
    const val LISTING_FIELD_TAGS = "Tags (opsional)"
    const val LISTING_TAGS_PLACEHOLDER = "Pisahkan dengan koma: desain, logo"
    const val LISTING_SAVE_CREATE = "Simpan Listing"
    const val LISTING_SAVE_EDIT = "Simpan Perubahan"
    const val LISTING_PRICE_PREVIEW_PREFIX = "Pembeli membayar"
    const val LISTING_DELIVERY_HINT_PREFIX = "Maksimal"
    const val LISTING_DELIVERY_HINT_SUFFIX = "jam"

    const val PROFILE_PHOTO_DESCRIPTION = "Foto profil"

    // Category picker (shared)
    const val CATEGORY_SHEET_TITLE = "Pilih Kategori"
    const val CATEGORY_ACADEMIC = "Dukungan Akademik"
    const val CATEGORY_VISUAL = "Branding Visual"
    const val CATEGORY_DATA = "Pengolahan Data"
}
