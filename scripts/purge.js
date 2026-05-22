// Deletes all documents in users, listings, orders before re-seeding.
const https = require("https");

const PROJECT_ID = "berima-74938";
const BASE_URL = "firestore.googleapis.com";
const DB_PATH = `projects/${PROJECT_ID}/databases/(default)/documents`;
const ACCESS_TOKEN = process.env.FIREBASE_ACCESS_TOKEN;

if (!ACCESS_TOKEN) {
  console.error("Set FIREBASE_ACCESS_TOKEN env var before running.");
  process.exit(1);
}

function request(method, path) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: BASE_URL,
      path: `/v1/${path}`,
      method,
      headers: { Authorization: `Bearer ${ACCESS_TOKEN}` },
    };
    const req = https.request(options, (res) => {
      let raw = "";
      res.on("data", (c) => (raw += c));
      res.on("end", () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(raw ? JSON.parse(raw) : {});
        } else {
          reject(new Error(`HTTP ${res.statusCode}: ${raw}`));
        }
      });
    });
    req.on("error", reject);
    req.end();
  });
}

async function purgeCollection(name) {
  console.log(`\n[${name}]`);
  const result = await request("GET", `${DB_PATH}/${name}?pageSize=200`);
  const docs = result.documents || [];
  console.log(`  Found ${docs.length} docs`);
  for (const doc of docs) {
    const id = doc.name.split("/").pop();
    await request("DELETE", `${DB_PATH}/${name}/${id}`);
    console.log(`  ✗ deleted ${name}/${id}`);
  }
}

async function main() {
  console.log("Purging Firestore for project:", PROJECT_ID);
  await purgeCollection("orders");
  await purgeCollection("listings");
  await purgeCollection("users");
  console.log("\n✅ Purge complete.");
}

main().catch((err) => {
  console.error("\n❌ Error:", err.message);
  process.exit(1);
});
