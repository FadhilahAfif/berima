// Creates Firebase Auth accounts for the two .maestro test users.
// Returns their UIDs so seed.js can map them onto user docs.
//
// Idempotent: if an account already exists, looks up the existing UID instead.
//
// Usage:
//   $env:FIREBASE_ACCESS_TOKEN = <token>
//   node scripts/create-auth-users.js
const https = require("https");

const PROJECT_ID = process.env.FIREBASE_PROJECT_ID || "berima-74938";
const ACCESS_TOKEN = process.env.FIREBASE_ACCESS_TOKEN;

if (!ACCESS_TOKEN) {
  console.error("Set FIREBASE_ACCESS_TOKEN env var before running.");
  process.exit(1);
}

const BUYER_PASSWORD = process.env.TEST_BUYER_PASSWORD;
const SELLER_PASSWORD = process.env.TEST_SELLER_PASSWORD;

if (!BUYER_PASSWORD || !SELLER_PASSWORD) {
  console.error("Set TEST_BUYER_PASSWORD and TEST_SELLER_PASSWORD env vars before running.");
  process.exit(1);
}

const ACCOUNTS = [
  { email: "test+buyer@berima.dev", password: BUYER_PASSWORD, label: "buyer" },
  { email: "test+seller@berima.dev", password: SELLER_PASSWORD, label: "seller" },
];

function request(method, path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const options = {
      hostname: "identitytoolkit.googleapis.com",
      path,
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
          resolve(raw ? JSON.parse(raw) : {});
        } else {
          reject(Object.assign(new Error(`HTTP ${res.statusCode}: ${raw}`), { statusCode: res.statusCode, body: raw }));
        }
      });
    });
    req.on("error", reject);
    if (data) req.write(data);
    req.end();
  });
}

async function lookupByEmail(email) {
  // Identity Toolkit admin API: lookup users by email
  const result = await request(
    "POST",
    `/v1/projects/${PROJECT_ID}/accounts:lookup`,
    { email: [email] }
  );
  return result.users?.[0]?.localId || null;
}

async function createAccount(email, password) {
  const result = await request(
    "POST",
    `/v1/projects/${PROJECT_ID}/accounts`,
    { email, password, emailVerified: false }
  );
  return result.localId;
}

async function ensureAccount({ email, password, label }) {
  console.log(`\n[${label}] ${email}`);
  let uid = await lookupByEmail(email);
  if (uid) {
    console.log(`  ↻ already exists, uid=${uid}`);
    return { label, email, uid };
  }
  uid = await createAccount(email, password);
  console.log(`  ✓ created, uid=${uid}`);
  return { label, email, uid };
}

async function main() {
  console.log("Ensuring Firebase Auth accounts for project:", PROJECT_ID);
  const out = {};
  for (const a of ACCOUNTS) {
    const r = await ensureAccount(a);
    out[r.label] = r.uid;
  }
  console.log("\n" + JSON.stringify(out));
}

main().catch((err) => {
  console.error("\n❌ Error:", err.message);
  process.exit(1);
});
