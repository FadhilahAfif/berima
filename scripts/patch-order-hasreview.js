// Patches hasReview=false on the paid "Desain PPT Presentasi Sidang" order.
// Run from repo root: node scripts/patch-order-hasreview.js
// Requires FIREBASE_ACCESS_TOKEN env var (get via: firebase login:ci)

const https = require("https");

const PROJECT_ID = "berima-74938";
const BASE = "firestore.googleapis.com";
const DB = `projects/${PROJECT_ID}/databases/(default)/documents`;
const TOKEN = process.env.FIREBASE_ACCESS_TOKEN;

if (!TOKEN) {
  console.error("Set FIREBASE_ACCESS_TOKEN before running.");
  console.error("  $env:FIREBASE_ACCESS_TOKEN = firebase login:ci");
  process.exit(1);
}

function req(method, path, body) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null;
    const opts = {
      hostname: BASE,
      path: `/v1/${path}`,
      method,
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        "Content-Type": "application/json",
        ...(data ? { "Content-Length": Buffer.byteLength(data) } : {}),
      },
    };
    const r = https.request(opts, (res) => {
      let raw = "";
      res.on("data", (c) => (raw += c));
      res.on("end", () => {
        if (res.statusCode >= 200 && res.statusCode < 300) resolve(JSON.parse(raw));
        else reject(new Error(`HTTP ${res.statusCode}: ${raw}`));
      });
    });
    r.on("error", reject);
    if (data) r.write(data);
    r.end();
  });
}

async function main() {
  // 1. Find the paid order
  const queryBody = {
    structuredQuery: {
      from: [{ collectionId: "orders" }],
      where: {
        compositeFilter: {
          op: "AND",
          filters: [
            { fieldFilter: { field: { fieldPath: "listingTitle" }, op: "EQUAL", value: { stringValue: "Desain PPT Presentasi Sidang" } } },
            { fieldFilter: { field: { fieldPath: "status" }, op: "EQUAL", value: { stringValue: "paid" } } },
          ],
        },
      },
      orderBy: [{ field: { fieldPath: "createTime" }, direction: "ASCENDING" }],
      limit: 1,
    },
  };

  console.log("Querying for paid order...");
  const results = await req("POST", `${DB}:runQuery`, queryBody);
  if (!results || results.length === 0 || !results[0]?.document) {
    console.error("No results returned from query.");
    process.exit(1);
  }
  const doc = results[0]?.document;
  if (!doc) {
    console.error("No paid order found for 'Desain PPT Presentasi Sidang'.");
    process.exit(1);
  }

  const docName = doc.name;
  const docId = docName.split("/").pop();
  const hasReview = doc.fields?.hasReview?.booleanValue;
  console.log(`Found: orders/${docId}  hasReview=${hasReview}`);

  if (hasReview === false) {
    console.log("Already hasReview=false, nothing to do.");
    return;
  }

  // 2. Patch only the hasReview field
  const patchPath = `${docName}?updateMask.fieldPaths=hasReview`;
  await req("PATCH", patchPath.replace(`/v1/`, ""), {
    fields: { hasReview: { booleanValue: false } },
  });

  console.log(`✓ orders/${docId} patched: hasReview=false`);
}

main().catch((e) => { console.error("Error:", e.message); process.exit(1); });
