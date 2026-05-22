const https = require("https");

const PROJECT_ID = "berima-74938";
const BASE_URL = `firestore.googleapis.com`;
const DB_PATH = `projects/${PROJECT_ID}/databases/(default)/documents`;

// Access token from firebase-tools config — refresh if expired
const ACCESS_TOKEN = process.env.FIREBASE_ACCESS_TOKEN;

if (!ACCESS_TOKEN) {
  console.error("Set FIREBASE_ACCESS_TOKEN env var before running.");
  process.exit(1);
}

function randomId() {
  const chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  let id = "";
  for (let i = 0; i < 20; i++) id += chars[Math.floor(Math.random() * chars.length)];
  return id;
}

function daysAgo(n) {
  const d = new Date();
  d.setDate(d.getDate() - n);
  return { seconds: Math.floor(d.getTime() / 1000), nanos: 0 };
}

function firestoreValue(val) {
  if (val === null || val === undefined) return { nullValue: null };
  if (typeof val === "boolean") return { booleanValue: val };
  if (typeof val === "string") return { stringValue: val };
  if (typeof val === "number" && Number.isInteger(val)) return { integerValue: String(val) };
  if (typeof val === "number") return { doubleValue: val };
  if (val && val.__type === "timestamp") return { timestampValue: new Date(val.seconds * 1000).toISOString() };
  if (Array.isArray(val)) return { arrayValue: { values: val.map(firestoreValue) } };
  if (typeof val === "object") {
    const fields = {};
    for (const [k, v] of Object.entries(val)) fields[k] = firestoreValue(v);
    return { mapValue: { fields } };
  }
  return { nullValue: null };
}

function toFirestoreDoc(obj) {
  const fields = {};
  for (const [k, v] of Object.entries(obj)) fields[k] = firestoreValue(v);
  return { fields };
}

function ts(daysBack) {
  return { __type: "timestamp", seconds: daysAgo(daysBack).seconds };
}

function request(method, path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = {
      hostname: BASE_URL,
      path: `/v1/${path}`,
      method,
      headers: {
        Authorization: `Bearer ${ACCESS_TOKEN}`,
        "Content-Type": "application/json",
        ...(data ? { "Content-Length": Buffer.byteLength(data) } : {}),
      },
    };
    const req = https.request(options, (res) => {
      let raw = "";
      res.on("data", (c) => (raw += c));
      res.on("end", () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(JSON.parse(raw));
        } else {
          reject(new Error(`HTTP ${res.statusCode}: ${raw}`));
        }
      });
    });
    req.on("error", reject);
    if (data) req.write(data);
    req.end();
  });
}

async function upsertDoc(collection, docId, obj) {
  const path = `${DB_PATH}/${collection}/${docId}`;
  // Full replace: PATCH without updateMask wipes any existing fields not in `obj`.
  // This matters because @DocumentId fields (listingId, orderId, uid) must NOT
  // exist in the document body at all.
  const result = await request("PATCH", path, toFirestoreDoc(obj));
  console.log(`  ✓ ${collection}/${docId}`);
  return result;
}

async function main() {
  console.log("Seeding Firestore for project:", PROJECT_ID);

  // ── Users ──────────────────────────────────────────────────────────────────
  console.log("\n[users]");

  // Real Firebase Auth UIDs from create-auth-users.js. These two accounts can
  // log in via the app. The other two sellers stay as random Firestore-only
  // docs (no auth) — they exist purely to make Home + ListingDetail look full.
  const seller1Id = process.env.SEED_SELLER_UID || "23jnaqjsdYb9a3U6FyBMG4dehYj1";
  const buyer1Id = process.env.SEED_BUYER_UID || "RCHylIvr0ydaDLx0Arqiomy0loo2";

  const seller2Id = randomId();
  const seller3Id = randomId();

  await upsertDoc("users", seller1Id, {
    name: "Andi Pratama",
    email: "test+seller@berima.dev",
    photoUrl: null,
    bio: "Jasa desain PPT dan poster UKM profesional.",
    faculty: "Teknik Informatika",
    role: "seller",
    averageRating: 4.8,
    totalReviews: 12,
    totalOrdersAsBuyer: 0,
    totalOrdersAsSeller: 15,
    createdAt: ts(60),
  });

  await upsertDoc("users", seller2Id, {
    name: "Budi Santoso",
    email: "budi@demo.com",
    photoUrl: null,
    bio: "Spesialis olah data SPSS dan Excel untuk skripsi.",
    faculty: "Ilmu Komputer",
    role: "seller",
    averageRating: 4.6,
    totalReviews: 8,
    totalOrdersAsBuyer: 0,
    totalOrdersAsSeller: 10,
    createdAt: ts(55),
  });

  await upsertDoc("users", seller3Id, {
    name: "Citra Dewi",
    email: "citra@demo.com",
    photoUrl: null,
    bio: "Proofreading abstrak dan format daftar pustaka.",
    faculty: "Sastra Indonesia",
    role: "seller",
    averageRating: 4.9,
    totalReviews: 20,
    totalOrdersAsBuyer: 0,
    totalOrdersAsSeller: 22,
    createdAt: ts(50),
  });

  await upsertDoc("users", buyer1Id, {
    name: "Dian Rahayu",
    email: "test+buyer@berima.dev",
    photoUrl: null,
    bio: null,
    faculty: "Manajemen",
    role: "buyer",
    averageRating: 0.0,
    totalReviews: 0,
    totalOrdersAsBuyer: 3,
    totalOrdersAsSeller: 0,
    createdAt: ts(30),
  });

  // ── Listings ───────────────────────────────────────────────────────────────
  console.log("\n[listings]");

  const listings = [
    {
      id: randomId(),
      sellerId: seller1Id, sellerName: "Andi Pratama", sellerRating: 4.8,
      title: "Desain PPT Presentasi Sidang",
      description: "Desain slide presentasi sidang skripsi/tugas akhir yang profesional dan menarik. Termasuk cover, isi, dan penutup. Revisi 2x.",
      category: "visual", price: 35000, deliveryTimeHours: 24,
      tags: ["presentasi", "sidang", "powerpoint"], totalOrders: 15, averageRating: 4.8, daysBack: 45,
    },
    {
      id: randomId(),
      sellerId: seller1Id, sellerName: "Andi Pratama", sellerRating: 4.8,
      title: "Desain Poster UKM A3",
      description: "Desain poster kegiatan UKM ukuran A3 full color. Cocok untuk acara seminar, lomba, atau rekrutmen anggota baru. File dikirim dalam format PNG dan PDF.",
      category: "visual", price: 50000, deliveryTimeHours: 48,
      tags: ["poster", "ukm", "desain grafis"], totalOrders: 10, averageRating: 4.7, daysBack: 40,
    },
    {
      id: randomId(),
      sellerId: seller1Id, sellerName: "Andi Pratama", sellerRating: 4.8,
      title: "Desain CV Profesional",
      description: "Desain CV satu halaman yang bersih dan profesional untuk melamar kerja atau magang. Template modern, ATS-friendly. Kirim data diri, langsung jadi.",
      category: "visual", price: 25000, deliveryTimeHours: 24,
      tags: ["cv", "lamaran kerja", "desain"], totalOrders: 12, averageRating: 4.9, daysBack: 35,
    },
    {
      id: randomId(),
      sellerId: seller2Id, sellerName: "Budi Santoso", sellerRating: 4.6,
      title: "Olah Data SPSS Skripsi",
      description: "Pengolahan data kuesioner skripsi menggunakan SPSS: uji validitas, reliabilitas, normalitas, dan regresi. Hasil berupa output SPSS + interpretasi singkat.",
      category: "data", price: 75000, deliveryTimeHours: 48,
      tags: ["spss", "skripsi", "statistik"], totalOrders: 8, averageRating: 4.6, daysBack: 30,
    },
    {
      id: randomId(),
      sellerId: seller2Id, sellerName: "Budi Santoso", sellerRating: 4.6,
      title: "Rekapitulasi Data Excel",
      description: "Rekapitulasi dan pengolahan data mentah ke dalam tabel Excel yang rapi. Termasuk pivot table, grafik, dan formula dasar sesuai kebutuhan.",
      category: "data", price: 30000, deliveryTimeHours: 24,
      tags: ["excel", "rekap data", "tabel"], totalOrders: 6, averageRating: 4.5, daysBack: 25,
    },
    {
      id: randomId(),
      sellerId: seller2Id, sellerName: "Budi Santoso", sellerRating: 4.6,
      title: "Ketik Ulang Dokumen PDF",
      description: "Pengetikan ulang dokumen dari PDF ke Word dengan format rapi. Cocok untuk dokumen scan, laporan lama, atau buku catatan. Harga per 10 halaman.",
      category: "data", price: 20000, deliveryTimeHours: 24,
      tags: ["ketik ulang", "pdf ke word", "dokumen"], totalOrders: 9, averageRating: 4.6, daysBack: 20,
    },
    {
      id: randomId(),
      sellerId: seller3Id, sellerName: "Citra Dewi", sellerRating: 4.9,
      title: "Proofreading Abstrak Skripsi",
      description: "Koreksi tata bahasa, ejaan, dan struktur kalimat abstrak skripsi Bahasa Indonesia. Hasil bersih, baku, dan siap dikumpulkan. Maks. 300 kata.",
      category: "academic", price: 40000, deliveryTimeHours: 24,
      tags: ["proofreading", "abstrak", "skripsi"], totalOrders: 14, averageRating: 4.9, daysBack: 28,
    },
    {
      id: randomId(),
      sellerId: seller3Id, sellerName: "Citra Dewi", sellerRating: 4.9,
      title: "Format Daftar Pustaka APA/IEEE",
      description: "Perapian dan pemformatan daftar pustaka sesuai gaya APA edisi 7 atau IEEE. Kirim daftar referensi, langsung diformat dengan benar. Maks. 30 referensi.",
      category: "academic", price: 25000, deliveryTimeHours: 12,
      tags: ["daftar pustaka", "apa", "ieee"], totalOrders: 11, averageRating: 5.0, daysBack: 22,
    },
    {
      id: randomId(),
      sellerId: seller3Id, sellerName: "Citra Dewi", sellerRating: 4.9,
      title: "Koreksi Tata Bahasa Makalah",
      description: "Koreksi menyeluruh tata bahasa, ejaan, dan tanda baca makalah ilmiah. Menggunakan PUEBI sebagai acuan. Cocok untuk tugas kuliah dan jurnal kampus.",
      category: "academic", price: 35000, deliveryTimeHours: 24,
      tags: ["koreksi", "makalah", "tata bahasa"], totalOrders: 7, averageRating: 4.8, daysBack: 18,
    },
    {
      id: randomId(),
      sellerId: seller3Id, sellerName: "Citra Dewi", sellerRating: 4.9,
      title: "Terjemahan Abstrak Inggris-Indonesia",
      description: "Terjemahan abstrak skripsi dari Bahasa Inggris ke Indonesia atau sebaliknya. Hasil natural, akademis, dan tidak kaku. Maks. 300 kata per pesanan.",
      category: "academic", price: 45000, deliveryTimeHours: 48,
      tags: ["terjemahan", "abstrak", "bahasa inggris"], totalOrders: 5, averageRating: 4.9, daysBack: 15,
    },
  ];

  const listingIds = {};
  for (const l of listings) {
    await upsertDoc("listings", l.id, {
      sellerId: l.sellerId,
      sellerName: l.sellerName,
      sellerPhotoUrl: null,
      sellerRating: l.sellerRating,
      title: l.title,
      description: l.description,
      category: l.category,
      price: l.price,
      deliveryTimeHours: l.deliveryTimeHours,
      thumbnailUrl: null,
      tags: l.tags,
      isActive: true,
      averageRating: l.averageRating,
      totalOrders: l.totalOrders,
      createdAt: ts(l.daysBack),
    });
    listingIds[l.title] = { id: l.id, sellerId: l.sellerId, sellerName: l.sellerName, price: l.price };
  }

  // ── Orders ─────────────────────────────────────────────────────────────────
  console.log("\n[orders]");

  const pptListing = listingIds["Desain PPT Presentasi Sidang"];
  const spssListing = listingIds["Olah Data SPSS Skripsi"];
  const proofListing = listingIds["Proofreading Abstrak Skripsi"];

  const order1Id = randomId();
  const order2Id = randomId();
  const order3Id = randomId();

  await upsertDoc("orders", order1Id, {
    listingId: pptListing.id,
    listingTitle: "Desain PPT Presentasi Sidang",
    buyerId: buyer1Id,
    buyerName: "Dian Rahayu",
    sellerId: pptListing.sellerId,
    sellerName: pptListing.sellerName,
    price: pptListing.price,
    note: "Tolong pakai template modern ya kak",
    status: "paid",
    attachmentUrl: null,
    hasReview: true,
    createdAt: ts(20),
    updatedAt: ts(18),
  });

  await upsertDoc("orders", order2Id, {
    listingId: spssListing.id,
    listingTitle: "Olah Data SPSS Skripsi",
    buyerId: buyer1Id,
    buyerName: "Dian Rahayu",
    sellerId: spssListing.sellerId,
    sellerName: spssListing.sellerName,
    price: spssListing.price,
    note: "Data ada 200 responden",
    status: "in_progress",
    attachmentUrl: null,
    hasReview: false,
    createdAt: ts(5),
    updatedAt: ts(4),
  });

  await upsertDoc("orders", order3Id, {
    listingId: proofListing.id,
    listingTitle: "Proofreading Abstrak Skripsi",
    buyerId: buyer1Id,
    buyerName: "Dian Rahayu",
    sellerId: proofListing.sellerId,
    sellerName: proofListing.sellerName,
    price: proofListing.price,
    note: null,
    status: "pending",
    attachmentUrl: null,
    hasReview: false,
    createdAt: ts(1),
    updatedAt: ts(1),
  });

  console.log("\n✅ Seeding complete!");
  console.log("\nSummary:");
  console.log(`  Users   : 4 (seller1=${seller1Id}, seller2=${seller2Id}, seller3=${seller3Id}, buyer1=${buyer1Id})`);
  console.log(`  Listings: ${listings.length}`);
  console.log(`  Orders  : 3 (${order1Id}, ${order2Id}, ${order3Id})`);
}

main().catch((err) => {
  console.error("\n❌ Error:", err.message);
  process.exit(1);
});
