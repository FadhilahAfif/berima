const crypto = require("crypto");
const fs = require("fs");
const https = require("https");
const path = require("path");
const zlib = require("zlib");

const ROOT = path.resolve(__dirname, "..");
const GOOGLE_SERVICES = path.join(ROOT, "app", "google-services.json");
const ASSET_DIR = path.join(__dirname, "demo-assets");
const PROJECT_ID = process.env.FIREBASE_PROJECT_ID || "berima-74938";
const ASSETS_ONLY = process.argv.includes("--assets-only");

const googleServices = JSON.parse(fs.readFileSync(GOOGLE_SERVICES, "utf8"));
const STORAGE_BUCKET = process.env.FIREBASE_STORAGE_BUCKET
  || googleServices.project_info.storage_bucket;
const DB_PATH = `projects/${PROJECT_ID}/databases/(default)/documents`;
const ACCESS_TOKEN = resolveAccessToken();

if (!ACCESS_TOKEN && !ASSETS_ONLY) {
  console.error("Set FIREBASE_ACCESS_TOKEN before running this script.");
  console.error("Example: $env:FIREBASE_ACCESS_TOKEN = '<token>'; node scripts/seed-demo.js");
  console.error("Or run `firebase login` first so the script can reuse the local Firebase CLI session.");
  console.error("You can still generate thumbnails with: node scripts/seed-demo.js --assets-only");
  process.exit(1);
}

const buyerId = process.env.SEED_BUYER_UID || "RCHylIvr0ydaDLx0Arqiomy0loo2";
const sellerId = process.env.SEED_SELLER_UID || "23jnaqjsdYb9a3U6FyBMG4dehYj1";
const secondSellerId = "demoSellerNadiaData";

const palette = {
  cream: [242, 239, 233, 255],
  paper: [255, 255, 255, 255],
  green: [45, 106, 79, 255],
  dark: [26, 26, 26, 255],
  muted: [107, 104, 100, 255],
  line: [191, 201, 193, 255],
  mint: [212, 237, 227, 255],
  raised: [247, 245, 240, 255],
  gold: [251, 191, 36, 255],
};

function resolveAccessToken() {
  if (process.env.FIREBASE_ACCESS_TOKEN) return process.env.FIREBASE_ACCESS_TOKEN;
  const configPath = path.join(process.env.USERPROFILE || "", ".config", "configstore", "firebase-tools.json");
  if (!fs.existsSync(configPath)) return null;
  try {
    const config = JSON.parse(fs.readFileSync(configPath, "utf8"));
    const tokens = config.tokens || {};
    if (tokens.access_token && Number(tokens.expires_at || 0) > Date.now() + 60_000) {
      return tokens.access_token;
    }
  } catch (_) {
    return null;
  }
  return null;
}

function crc32(buf) {
  let c = ~0;
  for (let i = 0; i < buf.length; i++) {
    c ^= buf[i];
    for (let k = 0; k < 8; k++) c = (c >>> 1) ^ (0xedb88320 & -(c & 1));
  }
  return ~c >>> 0;
}

function chunk(type, data) {
  const name = Buffer.from(type);
  const len = Buffer.alloc(4);
  const crc = Buffer.alloc(4);
  len.writeUInt32BE(data.length);
  crc.writeUInt32BE(crc32(Buffer.concat([name, data])));
  return Buffer.concat([len, name, data, crc]);
}

function png(width, height, draw) {
  const pixels = Buffer.alloc(width * height * 4);
  const canvas = {
    width,
    height,
    set(x, y, color) {
      if (x < 0 || y < 0 || x >= width || y >= height) return;
      const i = (Math.floor(y) * width + Math.floor(x)) * 4;
      pixels[i] = color[0]; pixels[i + 1] = color[1]; pixels[i + 2] = color[2]; pixels[i + 3] = color[3];
    },
    fill(color) {
      for (let y = 0; y < height; y++) for (let x = 0; x < width; x++) this.set(x, y, color);
    },
    rect(x, y, w, h, color) {
      for (let yy = y; yy < y + h; yy++) for (let xx = x; xx < x + w; xx++) this.set(xx, yy, color);
    },
    circle(cx, cy, r, color) {
      for (let y = cy - r; y <= cy + r; y++) {
        for (let x = cx - r; x <= cx + r; x++) {
          if ((x - cx) ** 2 + (y - cy) ** 2 <= r ** 2) this.set(x, y, color);
        }
      }
    },
    line(x1, y1, x2, y2, thickness, color) {
      const steps = Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
      for (let i = 0; i <= steps; i++) {
        const t = steps === 0 ? 0 : i / steps;
        const x = x1 + (x2 - x1) * t;
        const y = y1 + (y2 - y1) * t;
        this.circle(Math.round(x), Math.round(y), Math.max(1, Math.floor(thickness / 2)), color);
      }
    },
  };
  canvas.fill(palette.cream);
  draw(canvas);

  const rows = [];
  for (let y = 0; y < height; y++) {
    rows.push(Buffer.from([0]));
    rows.push(pixels.subarray(y * width * 4, (y + 1) * width * 4));
  }
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(width, 0);
  ihdr.writeUInt32BE(height, 4);
  ihdr[8] = 8; ihdr[9] = 6; ihdr[10] = 0; ihdr[11] = 0; ihdr[12] = 0;
  return Buffer.concat([
    Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]),
    chunk("IHDR", ihdr),
    chunk("IDAT", zlib.deflateSync(Buffer.concat(rows), { level: 9 })),
    chunk("IEND", Buffer.alloc(0)),
  ]);
}

function drawShell(c, accent) {
  c.rect(80, 70, 1040, 660, palette.raised);
  c.rect(128, 118, 944, 564, palette.paper);
  c.rect(128, 118, 944, 10, accent);
  c.circle(1000, 176, 56, palette.mint);
  c.circle(1000, 176, 24, accent);
}

const thumbnails = [
  ["demo-ppt-sidang.png", (c) => {
    drawShell(c, palette.green);
    c.rect(198, 188, 420, 300, palette.mint);
    c.rect(230, 226, 210, 26, palette.green);
    c.rect(230, 286, 320, 18, palette.line);
    c.rect(230, 330, 270, 18, palette.line);
    c.rect(650, 228, 78, 260, palette.green);
    c.rect(758, 308, 78, 180, palette.mint);
    c.rect(866, 260, 78, 228, palette.gold);
    c.line(190, 552, 980, 552, 8, palette.dark);
  }],
  ["demo-poster-ukm.png", (c) => {
    drawShell(c, [0, 82, 54, 255]);
    c.rect(230, 178, 330, 430, palette.green);
    c.rect(270, 228, 250, 42, palette.paper);
    c.circle(395, 374, 80, palette.gold);
    c.rect(640, 220, 310, 48, palette.dark);
    c.rect(640, 310, 360, 18, palette.line);
    c.rect(640, 358, 260, 18, palette.line);
    c.rect(640, 500, 190, 60, palette.mint);
  }],
  ["demo-excel-dashboard.png", (c) => {
    drawShell(c, [15, 82, 56, 255]);
    for (let x = 210; x <= 650; x += 88) c.line(x, 180, x, 600, 3, palette.line);
    for (let y = 180; y <= 600; y += 70) c.line(210, y, 650, y, 3, palette.line);
    c.rect(210, 180, 440, 70, palette.mint);
    c.line(720, 560, 790, 430, 12, palette.green);
    c.line(790, 430, 870, 470, 12, palette.green);
    c.line(870, 470, 1000, 290, 12, palette.green);
    c.circle(1000, 290, 22, palette.gold);
  }],
  ["demo-proofread-abstrak.png", (c) => {
    drawShell(c, palette.green);
    c.rect(235, 170, 470, 450, palette.paper);
    c.line(285, 250, 650, 250, 8, palette.line);
    c.line(285, 310, 610, 310, 8, palette.line);
    c.line(285, 370, 660, 370, 8, palette.line);
    c.line(285, 430, 560, 430, 8, palette.line);
    c.line(730, 510, 930, 310, 18, palette.green);
    c.line(930, 310, 985, 365, 18, palette.green);
    c.circle(730, 510, 26, palette.gold);
  }],
  ["demo-reference-format.png", (c) => {
    drawShell(c, [0, 82, 54, 255]);
    c.rect(240, 190, 250, 360, palette.mint);
    c.rect(310, 190, 250, 360, palette.paper);
    c.rect(380, 190, 250, 360, palette.raised);
    c.line(680, 255, 955, 255, 8, palette.dark);
    c.line(680, 325, 900, 325, 8, palette.line);
    c.line(680, 395, 940, 395, 8, palette.line);
    c.line(680, 465, 870, 465, 8, palette.line);
  }],
  ["demo-cv-design.png", (c) => {
    drawShell(c, palette.green);
    c.rect(260, 170, 300, 430, palette.paper);
    c.circle(410, 275, 58, palette.mint);
    c.rect(330, 370, 160, 24, palette.green);
    c.line(315, 450, 505, 450, 6, palette.line);
    c.line(315, 498, 470, 498, 6, palette.line);
    c.rect(660, 220, 300, 70, palette.mint);
    c.rect(660, 340, 240, 70, palette.green);
    c.rect(660, 460, 280, 70, palette.gold);
  }],
];

const listings = [
  {
    id: "demoListingPptSidang",
    sellerId,
    sellerName: "Andi Pratama",
    sellerRating: 4.9,
    title: "Desain PPT Sidang yang Rapi",
    description: "Merapikan dan mendesain slide presentasi sidang agar alur cerita, hierarki visual, dan grafik lebih mudah dipahami. Layanan ini membantu tampilan presentasi, bukan membuat isi akademik atau menggantikan pekerjaan penulis.",
    category: "visual",
    price: 45000,
    deliveryTimeHours: 24,
    tags: ["presentasi", "slide", "desain"],
    totalOrders: 18,
    averageRating: 4.9,
    reviewCount: 12,
    asset: "demo-ppt-sidang.png",
    daysBack: 2,
  },
  {
    id: "demoListingPosterUkm",
    sellerId,
    sellerName: "Andi Pratama",
    sellerRating: 4.9,
    title: "Poster Kegiatan UKM",
    description: "Desain poster digital untuk seminar, rekrutmen, lomba, atau acara kampus. Pemesan menyediakan informasi acara dan aset logo; hasil dikirim sebagai PNG dan PDF siap unggah.",
    category: "visual",
    price: 55000,
    deliveryTimeHours: 36,
    tags: ["poster", "ukm", "kampus"],
    totalOrders: 11,
    averageRating: 4.8,
    reviewCount: 8,
    asset: "demo-poster-ukm.png",
    daysBack: 5,
  },
  {
    id: "demoListingExcelDashboard",
    sellerId: secondSellerId,
    sellerName: "Nadia Putri",
    sellerRating: 4.7,
    title: "Dashboard Excel Ringkas",
    description: "Merapikan data mentah menjadi tabel, pivot, grafik, dan dashboard Excel sederhana. Cocok untuk rekap kegiatan, survei organisasi, atau laporan operasional kampus.",
    category: "data",
    price: 65000,
    deliveryTimeHours: 48,
    tags: ["excel", "dashboard", "data"],
    totalOrders: 14,
    averageRating: 4.7,
    reviewCount: 9,
    asset: "demo-excel-dashboard.png",
    daysBack: 3,
  },
  {
    id: "demoListingProofreadAbstrak",
    sellerId: secondSellerId,
    sellerName: "Nadia Putri",
    sellerRating: 4.7,
    title: "Proofreading Abstrak",
    description: "Koreksi ejaan, tata bahasa, konsistensi istilah, dan keterbacaan abstrak Bahasa Indonesia atau Inggris. Layanan ini tidak menulis ulang substansi penelitian dan tidak menerima joki tugas.",
    category: "academic",
    price: 35000,
    deliveryTimeHours: 24,
    tags: ["proofreading", "abstrak", "bahasa"],
    totalOrders: 16,
    averageRating: 4.8,
    reviewCount: 10,
    asset: "demo-proofread-abstrak.png",
    daysBack: 1,
  },
  {
    id: "demoListingReferenceFormat",
    sellerId: secondSellerId,
    sellerName: "Nadia Putri",
    sellerRating: 4.7,
    title: "Format Referensi APA/IEEE",
    description: "Merapikan daftar pustaka sesuai format APA 7 atau IEEE dari sumber yang sudah diberikan pemesan. Fokus pada format, konsistensi, dan kerapian dokumen.",
    category: "academic",
    price: 30000,
    deliveryTimeHours: 12,
    tags: ["referensi", "apa", "ieee"],
    totalOrders: 9,
    averageRating: 4.9,
    reviewCount: 7,
    asset: "demo-reference-format.png",
    daysBack: 7,
  },
  {
    id: "demoListingCvDesign",
    sellerId,
    sellerName: "Andi Pratama",
    sellerRating: 4.9,
    title: "Desain CV Magang",
    description: "Mendesain CV satu halaman yang bersih, mudah dibaca, dan cocok untuk pendaftaran magang. Pemesan tetap menyediakan data diri dan pengalaman sendiri.",
    category: "visual",
    price: 40000,
    deliveryTimeHours: 24,
    tags: ["cv", "magang", "desain"],
    totalOrders: 13,
    averageRating: 4.8,
    reviewCount: 9,
    asset: "demo-cv-design.png",
    daysBack: 4,
  },
];

function ts(daysBack) {
  const d = new Date();
  d.setDate(d.getDate() - daysBack);
  return { __type: "timestamp", value: d.toISOString() };
}

function tsMinutesAgo(minutesBack) {
  return {
    __type: "timestamp",
    value: new Date(Date.now() - minutesBack * 60 * 1000).toISOString(),
  };
}

function firestoreValue(value) {
  if (value === null || value === undefined) return { nullValue: null };
  if (value && value.__type === "timestamp") return { timestampValue: value.value };
  if (typeof value === "boolean") return { booleanValue: value };
  if (typeof value === "string") return { stringValue: value };
  if (typeof value === "number" && Number.isInteger(value)) return { integerValue: String(value) };
  if (typeof value === "number") return { doubleValue: value };
  if (Array.isArray(value)) return { arrayValue: { values: value.map(firestoreValue) } };
  const fields = {};
  for (const [key, child] of Object.entries(value)) fields[key] = firestoreValue(child);
  return { mapValue: { fields } };
}

function docBody(data) {
  const fields = {};
  for (const [key, value] of Object.entries(data)) fields[key] = firestoreValue(value);
  return { fields };
}

function requestJson(hostname, requestPath, method, body, headers = {}) {
  return new Promise((resolve, reject) => {
    const payload = body ? (Buffer.isBuffer(body) ? body : Buffer.from(JSON.stringify(body))) : null;
    const req = https.request({
      hostname,
      path: requestPath,
      method,
      headers: {
        Authorization: `Bearer ${ACCESS_TOKEN}`,
        ...(body && !Buffer.isBuffer(body) ? { "Content-Type": "application/json" } : {}),
        ...(payload ? { "Content-Length": payload.length } : {}),
        ...headers,
      },
    }, (res) => {
      let raw = "";
      res.on("data", (chunk) => raw += chunk);
      res.on("end", () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(raw ? JSON.parse(raw) : {});
        } else {
          reject(new Error(`${method} ${requestPath} -> HTTP ${res.statusCode}: ${raw}`));
        }
      });
    });
    req.on("error", reject);
    if (payload) req.write(payload);
    req.end();
  });
}

async function upsertDoc(collection, docId, data) {
  await requestJson(
    "firestore.googleapis.com",
    `/v1/${DB_PATH}/${collection}/${docId}`,
    "PATCH",
    docBody(data),
  );
  console.log(`  ok ${collection}/${docId}`);
}

async function uploadFile(storagePath, filePath, contentType) {
  const bytes = fs.readFileSync(filePath);
  const encodedPath = encodeURIComponent(storagePath);
  const token = crypto.randomUUID();
  await requestJson(
    "storage.googleapis.com",
    `/upload/storage/v1/b/${STORAGE_BUCKET}/o?uploadType=media&name=${encodedPath}`,
    "POST",
    bytes,
    { "Content-Type": contentType },
  );
  await requestJson(
    "storage.googleapis.com",
    `/storage/v1/b/${STORAGE_BUCKET}/o/${encodedPath}`,
    "PATCH",
    { metadata: { firebaseStorageDownloadTokens: token } },
  );
  return {
    url: `https://firebasestorage.googleapis.com/v0/b/${STORAGE_BUCKET}/o/${encodedPath}?alt=media&token=${token}`,
    storagePath,
  };
}

async function uploadPng(storagePath, filePath) {
  return uploadFile(storagePath, filePath, "image/png");
}

function generateAssets() {
  fs.mkdirSync(ASSET_DIR, { recursive: true });
  for (const [fileName, draw] of thumbnails) {
    fs.writeFileSync(path.join(ASSET_DIR, fileName), png(1200, 800, draw));
  }
  fs.writeFileSync(
    path.join(ASSET_DIR, "demo-brief-ppt.txt"),
    [
      "Brief PPT Sidang - Berima Demo",
      "Tema: visual rapi, profesional, warna hijau kampus.",
      "Isi utama sudah disiapkan pemesan. Penyedia jasa hanya merapikan tampilan.",
    ].join("\n"),
  );
  fs.writeFileSync(
    path.join(ASSET_DIR, "demo-hasil-ppt.txt"),
    [
      "Hasil Demo - Desain PPT Sidang",
      "File dummy untuk screenshot alur pesanan Berima.",
      "Pada demo asli, penyedia jasa mengunggah PPT/PDF final di sini.",
    ].join("\n"),
  );
}

async function main() {
  console.log("Generating demo thumbnails...");
  generateAssets();
  if (ASSETS_ONLY) {
    console.log(`Assets saved locally in ${ASSET_DIR}`);
    return;
  }

  console.log(`Uploading thumbnails to ${STORAGE_BUCKET}...`);
  const uploaded = {};
  for (const listing of listings) {
    uploaded[listing.asset] = await uploadPng(
      `users/${listing.sellerId}/listings/${listing.id}/${listing.asset}`,
      path.join(ASSET_DIR, listing.asset),
    );
    console.log(`  ok ${listing.asset}`);
  }

  const shotOrderIds = [
    "demoShotOrderPendingAndi",
    "demoShotOrderInProgressAndi",
    "demoShotOrderDeliveredAndi",
    "demoShotOrderRevisionAndi",
    "demoShotOrderCompletedAndi",
    "demoShotOrderPaidAndi",
  ];
  const orderFiles = {};
  for (const orderId of shotOrderIds) {
    orderFiles[`${orderId}:brief`] = await uploadFile(
      `orders/${orderId}/requirements/brief-ppt-sidang.txt`,
      path.join(ASSET_DIR, "demo-brief-ppt.txt"),
      "text/plain",
    );
    orderFiles[`${orderId}:result`] = await uploadFile(
      `orders/${orderId}/result/hasil-ppt-sidang.txt`,
      path.join(ASSET_DIR, "demo-hasil-ppt.txt"),
      "text/plain",
    );
    console.log(`  ok ${orderId} order files`);
  }

  console.log("Writing demo users...");
  await upsertDoc("users", sellerId, {
    name: "Andi Pratama",
    email: "test+seller@berima.dev",
    photoUrl: null,
    bio: "Penyedia jasa desain kampus yang fokus pada presentasi, poster, dan CV. Semua layanan mengikuti kebijakan integritas akademik Berima.",
    faculty: "Ilmu Komputer",
    role: "both",
    identityVerificationStatus: "approved",
    isIdentityVerified: true,
    verifiedSkillBadges: ["visual"],
    verificationUpdatedAt: ts(1),
    averageRating: 4.9,
    totalReviews: 21,
    totalOrdersAsBuyer: 1,
    totalOrdersAsSeller: 42,
    createdAt: ts(80),
  });
  await upsertDoc("users", buyerId, {
    name: "Dian Rahayu",
    email: "test+buyer@berima.dev",
    photoUrl: null,
    bio: "Mahasiswa yang sering memesan bantuan teknis untuk kebutuhan presentasi dan rekap data.",
    faculty: "Manajemen",
    role: "buyer",
    identityVerificationStatus: "not_submitted",
    isIdentityVerified: false,
    verifiedSkillBadges: [],
    verificationUpdatedAt: null,
    averageRating: 0.0,
    totalReviews: 0,
    totalOrdersAsBuyer: 4,
    totalOrdersAsSeller: 0,
    createdAt: ts(50),
  });
  await upsertDoc("users", secondSellerId, {
    name: "Nadia Putri",
    email: "nadia.demo@berima.dev",
    photoUrl: null,
    bio: "Membantu proofreading ringan, format referensi, dan dashboard Excel untuk kebutuhan organisasi serta kelas.",
    faculty: "Ekonomi dan Bisnis",
    role: "seller",
    identityVerificationStatus: "approved",
    isIdentityVerified: true,
    verifiedSkillBadges: ["academic", "data"],
    verificationUpdatedAt: ts(2),
    averageRating: 4.7,
    totalReviews: 17,
    totalOrdersAsBuyer: 0,
    totalOrdersAsSeller: 31,
    createdAt: ts(70),
  });

  console.log("Writing demo listings...");
  for (const listing of listings) {
    const image = uploaded[listing.asset];
    await upsertDoc("listings", listing.id, {
      sellerId: listing.sellerId,
      sellerName: listing.sellerName,
      sellerPhotoUrl: null,
      sellerRating: listing.sellerRating,
      title: listing.title,
      description: listing.description,
      category: listing.category,
      price: listing.price,
      deliveryTimeHours: listing.deliveryTimeHours,
      thumbnailUrl: image.url,
      thumbnailStoragePath: image.storagePath,
      tags: listing.tags,
      isActive: true,
      sellerIdentityVerified: true,
      sellerVerifiedSkillBadges: listing.sellerId === sellerId ? ["visual"] : ["academic", "data"],
      policyAcceptedAt: ts(listing.daysBack),
      averageRating: listing.averageRating,
      reviewCount: listing.reviewCount,
      totalOrders: listing.totalOrders,
      createdAt: ts(listing.daysBack),
    });
  }

  console.log("Writing portfolio, verification, orders, and reviews...");
  await upsertDoc("portfolioItems", "demoPortfolioPpt", {
    userId: sellerId,
    title: "Deck Presentasi Sidang",
    description: "Contoh perapian struktur slide, visualisasi data, dan konsistensi layout untuk presentasi akademik.",
    category: "visual",
    externalLink: "https://example.com/berima-demo-ppt",
    imageUrl: uploaded["demo-ppt-sidang.png"].url,
    imageStoragePath: uploaded["demo-ppt-sidang.png"].storagePath,
    createdAt: ts(16),
    updatedAt: ts(16),
  });
  await upsertDoc("portfolioItems", "demoPortfolioDashboard", {
    userId: secondSellerId,
    title: "Dashboard Rekap Kegiatan",
    description: "Contoh dashboard Excel untuk memantau peserta, anggaran, dan progres acara organisasi kampus.",
    category: "data",
    externalLink: "https://example.com/berima-demo-dashboard",
    imageUrl: uploaded["demo-excel-dashboard.png"].url,
    imageStoragePath: uploaded["demo-excel-dashboard.png"].storagePath,
    createdAt: ts(14),
    updatedAt: ts(14),
  });
  await upsertDoc("verificationSubmissions", "demoIdentityApprovedAndi", {
    userId: sellerId,
    type: "identity",
    status: "approved",
    documentType: "ktm",
    skillCategory: null,
    portfolioItemId: null,
    externalLink: null,
    storagePath: "users/demo/private/identity-redacted",
    fileName: "ktm-redacted.pdf",
    contentType: "application/pdf",
    note: "Data demo: status disetujui untuk kebutuhan presentasi.",
    rejectionReason: null,
    reviewedBy: "Firebase Console",
    reviewedAt: ts(1),
    createdAt: ts(12),
    updatedAt: ts(1),
  });
  await upsertDoc("verificationSubmissions", "demoSkillApprovedAndiVisual", {
    userId: sellerId,
    type: "skill",
    status: "approved",
    documentType: null,
    skillCategory: "visual",
    portfolioItemId: "demoPortfolioPpt",
    externalLink: "https://example.com/berima-demo-ppt",
    storagePath: null,
    fileName: null,
    contentType: null,
    note: "Data demo: badge visual disetujui manual.",
    rejectionReason: null,
    reviewedBy: "Firebase Console",
    reviewedAt: ts(1),
    createdAt: ts(10),
    updatedAt: ts(1),
  });
  await upsertDoc("orders", "demoOrderPending", {
    listingId: "demoListingProofreadAbstrak",
    listingTitle: "Proofreading Abstrak",
    buyerId,
    buyerName: "Dian Rahayu",
    sellerId: secondSellerId,
    sellerName: "Nadia Putri",
    price: 35000,
    note: "Tolong fokus pada ejaan dan konsistensi istilah, bukan mengubah isi penelitian.",
    status: "pending",
    attachmentUrl: null,
    hasReview: false,
    createdAt: ts(1),
    updatedAt: ts(1),
  });
  await upsertDoc("orders", "demoOrderInProgress", {
    listingId: "demoListingExcelDashboard",
    listingTitle: "Dashboard Excel Ringkas",
    buyerId,
    buyerName: "Dian Rahayu",
    sellerId: secondSellerId,
    sellerName: "Nadia Putri",
    price: 65000,
    note: "Data survei organisasi sudah rapi di satu file Excel.",
    status: "in_progress",
    attachmentUrl: null,
    hasReview: false,
    createdAt: ts(4),
    updatedAt: ts(3),
  });
  await upsertDoc("orders", "demoOrderPaidReviewReady", {
    listingId: "demoListingPptSidang",
    listingTitle: "Desain PPT Sidang yang Rapi",
    buyerId,
    buyerName: "Dian Rahayu",
    sellerId,
    sellerName: "Andi Pratama",
    price: 45000,
    note: "Bantu rapikan visual dan grafik, isi sudah saya siapkan sendiri.",
    status: "paid",
    attachmentUrl: null,
    hasReview: false,
    createdAt: ts(9),
    updatedAt: ts(6),
  });
  await upsertDoc("orders", "demoOrderReviewed", {
    listingId: "demoListingCvDesign",
    listingTitle: "Desain CV Magang",
    buyerId,
    buyerName: "Dian Rahayu",
    sellerId,
    sellerName: "Andi Pratama",
    price: 40000,
    note: "CV untuk pendaftaran magang, data diri dan pengalaman sudah saya siapkan.",
    status: "paid",
    attachmentUrl: null,
    hasReview: true,
    createdAt: ts(12),
    updatedAt: ts(8),
  });

  const shotOrders = [
    ["demoShotOrderPendingAndi", "pending", 0, false, null, 0],
    ["demoShotOrderInProgressAndi", "in_progress", 1, false, null, 0],
    ["demoShotOrderDeliveredAndi", "delivered", 2, true, null, 0],
    [
      "demoShotOrderRevisionAndi",
      "revision_requested",
      3,
      true,
      "Tolong bagian grafik dibuat lebih kontras dan judul tiap slide dibuat lebih ringkas.",
      1,
    ],
    ["demoShotOrderCompletedAndi", "completed", 4, true, null, 0],
    ["demoShotOrderPaidAndi", "paid", 5, true, null, 0],
  ];
  for (const [orderId, status, daysBack, hasResult, revisionNote, revisionCount] of shotOrders) {
    const brief = orderFiles[`${orderId}:brief`];
    const result = orderFiles[`${orderId}:result`];
    await upsertDoc("orders", orderId, {
      listingId: "demoListingPptSidang",
      listingTitle: "Desain PPT Sidang yang Rapi",
      buyerId,
      buyerName: "Dian Rahayu",
      sellerId,
      sellerName: "Andi Pratama",
      price: 45000,
      note: "Rapikan visual slide sidang, pertahankan isi utama, dan buat grafik lebih mudah dibaca.",
      status,
      attachmentUrl: hasResult ? result.url : null,
      requirementFileUrl: brief.url,
      requirementFileName: "brief-ppt-sidang.txt",
      requirementStoragePath: brief.storagePath,
      resultFileName: hasResult ? "hasil-ppt-sidang.txt" : null,
      resultStoragePath: hasResult ? result.storagePath : null,
      revisionNote,
      revisionCount,
      hasReview: false,
      demoScenario: "screenshot-order-flow",
      createdAt: ts(daysBack),
      updatedAt: ts(daysBack),
    });
  }

  const chatThreads = {
    demoShotOrderInProgressAndi: [
      [buyerId, "Dian Rahayu", "Halo Kak Andi, brief sudah saya lampirkan di pesanan ya.", 90],
      [sellerId, "Andi Pratama", "Siap, saya cek dulu. Saya fokus rapikan layout dan grafiknya.", 80],
      [buyerId, "Dian Rahayu", "Terima kasih, isi slide jangan diubah ya Kak.", 70],
    ],
    demoShotOrderDeliveredAndi: [
      [sellerId, "Andi Pratama", "Hasil pertama sudah saya unggah. Mohon dicek bagian grafiknya.", 60],
      [buyerId, "Dian Rahayu", "Saya cek dulu ya Kak.", 50],
    ],
    demoShotOrderRevisionAndi: [
      [buyerId, "Dian Rahayu", "Sudah bagus, tapi grafik slide 8 bisa dibuat lebih kontras?", 45],
      [sellerId, "Andi Pratama", "Bisa, saya revisi dan kirim ulang sebentar lagi.", 35],
    ],
    demoShotOrderCompletedAndi: [
      [sellerId, "Andi Pratama", "Revisi sudah saya kirim ulang.", 30],
      [buyerId, "Dian Rahayu", "Sudah sesuai. Saya konfirmasi selesai.", 20],
    ],
  };
  for (const [orderId, messages] of Object.entries(chatThreads)) {
    for (const [index, [senderId, senderName, text, minutesBack]] of messages.entries()) {
      await upsertDoc(`messages/${orderId}/chats`, `demoMsg${index + 1}`, {
        senderId,
        senderName,
        text,
        isRead: true,
        createdAt: tsMinutesAgo(minutesBack),
      });
    }
  }

  await upsertDoc("reviews", "demoReviewPpt", {
    orderId: "demoOrderReviewed",
    listingId: "demoListingCvDesign",
    buyerId,
    buyerName: "Dian Rahayu",
    buyerPhotoUrl: null,
    sellerId,
    rating: 5,
    comment: "Desainnya rapi dan mudah dibaca. Cocok untuk kebutuhan magang.",
    createdAt: ts(8),
  });

  console.log("\nDemo dataset ready.");
  console.log("Buyer login:  test+buyer@berima.dev");
  console.log("Seller login: test+seller@berima.dev");
  console.log(`Assets saved locally in ${ASSET_DIR}`);
}

main().catch((error) => {
  console.error("\nSeed failed:", error.message);
  process.exit(1);
});
