# Berima Product Definition Document (PDD)

**Version:** 1.0
**Status:** Draft
**Last Updated:** June 2026
**Product Owner:** Tim Berima
**Platform:** Android (MVP)

---

# Document Information

| Item          | Value                                                    |
| ------------- | -------------------------------------------------------- |
| Product       | Berima                                                   |
| Tagline       | *Beri Jasa, Terima Hasil*                                |
| Platform      | Android                                                  |
| Document Type | Product Definition Document                              |
| Version       | 1.0                                                      |
| Status        | Draft                                                    |
| Audience      | Product Team, Development Team, UI/UX Team, Coding Agent |

---

# Table of Contents

1. Product Overview
2. Product Philosophy
3. Vision & Mission
4. Product Principles
5. Product Goals
6. Product Non-Goals
7. Success Metrics

---

# 1. Product Overview

## 1.1 Introduction

Berima adalah platform **Marketplace Micro-Service berbasis komunitas kampus** yang mempertemukan mahasiswa yang membutuhkan bantuan pada pekerjaan berbasis keterampilan dengan mahasiswa lain yang memiliki kompetensi untuk menyelesaikannya.

Berima berfokus pada transaksi jasa berskala kecil (micro-services) yang umum dilakukan oleh mahasiswa, seperti desain presentasi, proofreading, desain grafis, pengolahan data, pemrograman, serta layanan digital lainnya yang tidak melanggar integritas akademik.

Platform menyediakan sistem transaksi yang aman melalui mekanisme escrow, sistem ulasan, verifikasi identitas, serta verifikasi kompetensi untuk meningkatkan kepercayaan antar pengguna.

---

## 1.2 Background

Mahasiswa sering membutuhkan bantuan terhadap pekerjaan yang bersifat teknis maupun kreatif dalam waktu yang relatif singkat. Di sisi lain, banyak mahasiswa memiliki keterampilan yang dapat dimanfaatkan untuk memperoleh penghasilan tambahan.

Saat ini proses tersebut umumnya dilakukan melalui media sosial, grup percakapan, atau komunikasi pribadi yang memiliki berbagai keterbatasan, antara lain:

* Sulit menemukan penyedia jasa yang terpercaya.
* Tidak terdapat sistem reputasi yang jelas.
* Tidak ada perlindungan transaksi.
* Tidak tersedia mekanisme penyelesaian sengketa.
* Tidak terdapat validasi kompetensi penyedia jasa.

Berima hadir untuk menyediakan sebuah platform yang lebih aman, transparan, dan terstruktur.

---

## 1.3 Problem Statement

Berima dikembangkan untuk menyelesaikan tiga permasalahan utama.

### P1. Trust

Mahasiswa kesulitan menentukan apakah penyedia jasa benar-benar kompeten dan dapat dipercaya.

### P2. Transaction Security

Tidak terdapat mekanisme yang menjamin keamanan pembayaran antara pembeli dan penyedia jasa.

### P3. Discovery

Belum tersedia media yang secara khusus mempertemukan mahasiswa berdasarkan kebutuhan dan keterampilan mereka dalam satu platform.

---

## 1.4 Product Vision

> Menjadi platform marketplace jasa mahasiswa yang terpercaya, aman, dan menjadi ekosistem kolaborasi keterampilan terbesar di lingkungan perguruan tinggi Indonesia.

---

## 1.5 Product Mission

Berima memiliki lima misi utama.

1. Mempermudah mahasiswa menemukan penyedia jasa yang sesuai.
2. Memberikan peluang penghasilan tambahan melalui pemanfaatan keterampilan.
3. Menyediakan sistem transaksi yang aman dan transparan.
4. Membangun ekosistem mahasiswa yang saling membantu secara profesional.
5. Menjunjung tinggi integritas akademik melalui kebijakan layanan yang jelas.

---

# 2. Product Philosophy

Seluruh pengembangan Berima harus mengikuti filosofi berikut.

## Student First

Setiap keputusan produk harus memberikan manfaat nyata bagi mahasiswa.

---

## Trust Before Transaction

Kepercayaan merupakan fondasi utama seluruh transaksi yang terjadi pada platform.

---

## Ethical Marketplace

Berima hanya memfasilitasi layanan yang sesuai dengan etika akademik dan profesional.

---

## Simple Experience

Seluruh proses transaksi harus dapat dilakukan dengan langkah sesedikit mungkin.

---

## Community Growth

Pertumbuhan platform harus memberikan manfaat bagi komunitas kampus, bukan hanya bagi perusahaan.

---

# 3. Product Principles

Seluruh fitur yang dikembangkan harus memenuhi prinsip berikut.

## Transparency

Status transaksi harus dapat dipantau secara real-time oleh seluruh pihak yang terlibat.

---

## Fairness

Platform tidak memihak pembeli maupun penyedia jasa.

---

## Accountability

Seluruh aktivitas penting harus memiliki jejak aktivitas (audit trail).

---

## Security

Data pengguna dan transaksi harus dilindungi dengan baik.

---

## Scalability

Arsitektur harus memungkinkan ekspansi ke universitas lain tanpa perubahan besar.

---

# 4. Product Goals

## Short-Term Goal (MVP)

* Meluncurkan aplikasi Android.
* Digunakan oleh mahasiswa UPN Veteran Jakarta.
* Mendukung transaksi jasa sederhana.
* Memvalidasi kebutuhan pasar.

---

## Mid-Term Goal

* Menambah universitas lain.
* Mengimplementasikan payment gateway.
* Mengembangkan escrow otomatis.
* Menambah sistem dispute.

---

## Long-Term Goal

* Menjadi marketplace jasa mahasiswa tingkat nasional.
* Mendukung kerja sama universitas.
* Menjadi ekosistem pengembangan keterampilan mahasiswa.

---

# 5. Product Non-Goals

Agar ruang lingkup produk tetap jelas, Berima **tidak** dirancang sebagai:

* Platform media sosial.
* Platform e-learning.
* Marketplace barang fisik.
* Platform freelance internasional.
* Platform pencarian kerja penuh waktu.
* Platform joki tugas atau jasa yang melanggar integritas akademik.

Fitur-fitur tersebut berada di luar cakupan pengembangan produk.

---

# 6. Product Goals (Success Criteria)

Keberhasilan MVP diukur menggunakan indikator berikut.

| Metric                | Target MVP |
| --------------------- | ---------- |
| Registered Users      | ≥ 100      |
| Verified Users        | ≥ 30       |
| Active Listings       | ≥ 50       |
| Completed Orders      | ≥ 50       |
| Order Completion Rate | ≥ 90%      |
| Average Rating        | ≥ 4.5      |
| Repeat Customer Rate  | ≥ 20%      |

---

# 7. North Star Metric

North Star Metric Berima adalah:

> **Jumlah transaksi jasa yang berhasil diselesaikan dengan tingkat kepuasan pengguna yang tinggi.**

Metrik ini dipilih karena secara langsung merepresentasikan nilai utama yang diberikan Berima kepada pengguna.

---

# 8. Product Identity

| Item               | Value                                      |
| ------------------ | ------------------------------------------ |
| Product Name       | Berima                                     |
| Tagline            | Beri Jasa, Terima Hasil                    |
| Product Type       | Mobile Marketplace                         |
| Category           | Campus Micro-Service Marketplace           |
| Primary Platform   | Android                                    |
| Initial Target     | Mahasiswa UPN Veteran Jakarta              |
| Business Model     | Transaction Fee                            |
| Transaction System | Escrow                                     |
| Verification       | Identity Verification & Skill Verification |

---

# 9. Product Scope Summary

## Included in MVP

* Authentication
* User Profile
* Listing Management
* Search
* Order Management
* Escrow (Simulation)
* Review & Rating
* Chat
* Identity Verification
* Skill Verification

---

## Excluded from MVP

* AI Recommendation
* Automatic Escrow
* Real Payment Gateway
* Push Notification
* Multi Campus Support
* Business Account
* Featured Listing
* Seller Analytics

---

# Berima Product Definition Document (PDD)

# Part 2 — Product Strategy

---

# 10. Problem Definition

## 10.1 Current Situation

Mahasiswa saat ini sering memanfaatkan media informal seperti grup WhatsApp, Instagram, Telegram, atau komunikasi langsung untuk mencari maupun menawarkan jasa. Cara tersebut memang mudah, tetapi memiliki berbagai keterbatasan.

Permasalahan yang paling sering muncul meliputi:

* Sulit menemukan penyedia jasa yang benar-benar kompeten.
* Tidak tersedia sistem reputasi yang dapat dipercaya.
* Tidak ada perlindungan terhadap pembayaran.
* Sulit menemukan jasa berdasarkan kategori tertentu.
* Tidak terdapat standar kualitas layanan.
* Tidak tersedia media penyelesaian sengketa.

Akibatnya, baik pembeli maupun penyedia jasa sama-sama menghadapi risiko selama proses transaksi.

---

## 10.2 Root Cause Analysis

| Masalah                    | Penyebab                                 |
| -------------------------- | ---------------------------------------- |
| Sulit mencari jasa         | Tidak ada marketplace khusus mahasiswa   |
| Risiko penipuan            | Pembayaran dilakukan secara langsung     |
| Sulit menilai kualitas     | Tidak ada sistem reputasi                |
| Tidak ada standar layanan  | Setiap transaksi memiliki aturan berbeda |
| Sulit memperoleh pelanggan | Promosi hanya mengandalkan media sosial  |

---

## 10.3 Opportunity

Pertumbuhan ekonomi digital dan meningkatnya penggunaan smartphone membuka peluang bagi platform yang mampu menghubungkan mahasiswa berdasarkan keterampilan mereka.

Selain itu, semakin banyak mahasiswa yang memiliki kemampuan pada bidang desain, pemrograman, analisis data, penulisan, hingga multimedia yang dapat dikembangkan menjadi sumber pendapatan tambahan.

Berima hadir untuk menghubungkan kebutuhan tersebut dalam satu ekosistem yang aman dan terstruktur.

---

# 11. Target User

Berima menggunakan pendekatan bertahap (phased market expansion).

## MVP

Mahasiswa aktif UPN Veteran Jakarta.

---

## Future Expansion

* Mahasiswa universitas lain.
* Organisasi kampus.
* UKM kampus.
* Alumni.
* UMKM sebagai pengguna jasa.

---

# 12. User Persona

## Persona 1 — Buyer

### Nama

Andi

### Umur

20 Tahun

### Status

Mahasiswa

### Kebutuhan

* Membuat presentasi.
* Mendesain poster.
* Mengolah data.
* Proofreading.

### Pain Points

* Tidak tahu harus meminta bantuan kepada siapa.
* Khawatir hasil tidak sesuai.
* Takut ditipu.

### Goals

Menemukan penyedia jasa yang terpercaya dengan proses cepat.

---

## Persona 2 — Seller

### Nama

Rina

### Umur

21 Tahun

### Status

Mahasiswa

### Skill

* UI Design
* Canva
* Figma
* Poster

### Pain Points

* Sulit memperoleh pelanggan.
* Promosi hanya melalui Instagram.
* Tidak memiliki sistem pembayaran yang aman.

### Goals

Memperoleh penghasilan tambahan dari keterampilan yang dimiliki.

---

# 13. User Needs

## Buyer Needs

* Mudah menemukan jasa.
* Harga transparan.
* Penyedia jasa terpercaya.
* Pembayaran aman.
* Komunikasi mudah.

---

## Seller Needs

* Mudah menawarkan jasa.
* Mendapat pelanggan.
* Pembayaran terjamin.
* Membangun reputasi.
* Menunjukkan kompetensi.

---

# 14. Value Proposition

## Untuk Buyer

Berima menyediakan platform yang memudahkan mahasiswa menemukan penyedia jasa terpercaya melalui sistem pencarian, ulasan, escrow, serta verifikasi identitas dan kompetensi.

---

## Untuk Seller

Berima membantu mahasiswa memperoleh penghasilan tambahan dengan menyediakan media promosi, sistem transaksi yang aman, serta mekanisme membangun reputasi profesional.

---

# 15. Product Positioning

## Positioning Statement

> Berima adalah marketplace micro-service berbasis komunitas kampus yang membantu mahasiswa menemukan maupun menawarkan jasa berbasis keterampilan melalui sistem transaksi yang aman, transparan, dan terpercaya.

---

# 16. Unique Selling Proposition (USP)

Berima memiliki beberapa pembeda utama dibanding marketplace jasa pada umumnya.

## Campus-Oriented Marketplace

Seluruh fitur dirancang khusus untuk kebutuhan mahasiswa.

---

## Skill Verification

Penyedia jasa dapat mengajukan verifikasi kompetensi berdasarkan portofolio maupun sertifikat sehingga meningkatkan kepercayaan pengguna.

---

## Escrow Transaction

Dana ditahan sementara hingga pekerjaan disetujui pembeli.

---

## Ethical Marketplace

Platform memiliki kebijakan yang melarang layanan yang melanggar integritas akademik.

---

## Community Driven

Berima dibangun untuk mendorong kolaborasi dan pertumbuhan komunitas mahasiswa.

---

# 17. Competitive Analysis

| Feature                   | Berima | Fiverr   | Fastwork | Sribulancer |
| ------------------------- | ------ | -------- | -------- | ----------- |
| Fokus Mahasiswa           | ✅      | ❌        | ❌        | ❌           |
| Skill Verification        | ✅      | Sebagian | Sebagian | Sebagian    |
| Escrow                    | ✅      | ✅        | ✅        | ✅           |
| Campus Community          | ✅      | ❌        | ❌        | ❌           |
| Academic Integrity Policy | ✅      | ❌        | ❌        | ❌           |
| Student Marketplace       | ✅      | ❌        | ❌        | ❌           |

---

# 18. Business Model

Berima menggunakan model bisnis berbasis komisi transaksi.

## Revenue Stream

### Transaction Fee

Platform memperoleh pendapatan sebesar **10%** dari setiap transaksi yang berhasil diselesaikan.

Contoh:

Order : Rp100.000

Seller menerima : Rp90.000

Platform memperoleh : Rp10.000

---

## Future Revenue

* Featured Listing.
* Premium Seller.
* Campus Partnership.
* Business Account.
* Advertising.

---

# 19. Stakeholders

| Stakeholder    | Peran                         |
| -------------- | ----------------------------- |
| Buyer          | Membeli jasa                  |
| Seller         | Menawarkan jasa               |
| Admin          | Mengelola platform            |
| Product Team   | Mengembangkan produk          |
| Developer      | Mengimplementasikan fitur     |
| UI/UX Designer | Mendesain pengalaman pengguna |
| Universitas    | Mitra potensial               |

---

# 20. Product Ecosystem

```text
                +----------------+
                |     Admin      |
                +--------+-------+
                         |
                         |
        +----------------+----------------+
        |                                 |
+-------v-------+                 +-------v-------+
|     Buyer     |                 |     Seller    |
+-------+-------+                 +-------+-------+
        |                                 |
        +---------------+-----------------+
                        |
                +-------v-------+
                |    BERIMA     |
                +-------+-------+
                        |
              Escrow • Chat • Review
              Verification • Search
```

---

# 21. Product Expansion Strategy

### Phase 1

UPN Veteran Jakarta

↓

### Phase 2

Universitas Jabodetabek

↓

### Phase 3

Perguruan Tinggi Indonesia

↓

### Phase 4

Business Account & UMKM

---

**End of Part 2**

# Berima Product Definition Document (PDD)

# Part 3 — Product Scope & Business Rules

---

# 22. Product Scope

## 22.1 MVP Features

Versi pertama Berima (MVP) berfokus pada penyediaan fitur-fitur inti yang diperlukan agar proses transaksi jasa dapat berjalan secara lengkap dari awal hingga akhir.

### Authentication

* Register
* Login
* Logout
* Forgot Password

---

### User Profile

* Edit Profile
* Portfolio
* Skill
* Bio
* Profile Picture

---

### Marketplace

* Browse Service
* Search Service
* Filter
* Category

---

### Service Management

* Create Service
* Edit Service
* Delete Service
* Service Gallery
* Pricing

---

### Transaction

* Order
* Escrow (Simulation)
* Order Status
* Revision
* Completion

---

### Chat

* Buyer ↔ Seller Communication

---

### Review

* Rating
* Review
* Seller Reputation

---

### Verification Center

* Identity Verification
* Skill Verification
* Verification Status

---

## 22.2 Out of Scope

Fitur berikut tidak termasuk dalam MVP.

* AI Recommendation
* AI Chatbot
* Automatic Escrow
* Push Notification
* Multi Campus
* Business Account
* Featured Listing
* Seller Analytics
* Referral Program

---

# 23. User Roles

Berima memiliki tiga jenis pengguna utama.

---

## Buyer

Hak akses:

* Melihat jasa
* Melakukan pencarian
* Membuat order
* Membayar
* Chat
* Review

---

## Seller

Hak akses:

* Membuat jasa
* Mengelola jasa
* Menerima order
* Mengirim hasil
* Mengajukan verifikasi
* Melihat statistik sederhana

---

## Administrator

Hak akses:

* Mengelola pengguna
* Memverifikasi identitas
* Memverifikasi kompetensi
* Menangani dispute
* Menghapus layanan yang melanggar kebijakan

---

# 24. Business Rules

Seluruh transaksi pada Berima harus mengikuti aturan berikut.

---

## Rule 1

Satu order hanya dapat memiliki satu buyer dan satu seller.

---

## Rule 2

Pembayaran harus dilakukan sebelum seller mulai mengerjakan pesanan.

---

## Rule 3

Dana disimpan sementara oleh sistem (Escrow Simulation).

---

## Rule 4

Dana hanya dapat diteruskan kepada seller setelah buyer menyetujui hasil pekerjaan.

---

## Rule 5

Seller dapat menerima atau menolak order.

---

## Rule 6

Buyer memiliki hak mengajukan revisi maksimal satu kali pada MVP.

---

## Rule 7

Setelah status berubah menjadi Completed, transaksi tidak dapat diubah.

---

## Rule 8

Setiap transaksi hanya dapat diberi satu ulasan.

---

# 25. Order Lifecycle

Seluruh transaksi mengikuti siklus berikut.

```text
Draft

↓

Pending Payment

↓

Waiting Seller

↓

Accepted

↓

In Progress

↓

Delivered

↓

Revision Requested (Optional)

↓

Delivered Again

↓

Completed

↓

Reviewed
```

---

# 26. Escrow Workflow

Escrow digunakan untuk meningkatkan keamanan transaksi.

```text
Buyer membuat order

↓

Buyer melakukan pembayaran

↓

Dana masuk ke Escrow

↓

Seller menerima order

↓

Seller mengerjakan pesanan

↓

Seller mengirim hasil

↓

Buyer melakukan review

↓

Disetujui

↓

Dana diteruskan kepada Seller

↓

Order selesai
```

Apabila terjadi sengketa, dana tetap berada pada Escrow hingga terdapat keputusan administrator.

---

# 27. Verification Center

Verification Center merupakan pusat verifikasi akun pengguna.

Verifikasi bersifat **opsional**, namun memberikan tingkat kepercayaan yang lebih tinggi kepada pengguna lain.

---

## 27.1 Identity Verification

### Tujuan

Memastikan identitas pengguna telah diverifikasi.

---

### Dokumen yang Didukung

Contoh dokumen yang dapat digunakan:

* KTM
* Surat Keterangan Mahasiswa Aktif
* KTP (untuk ekspansi di masa depan)
* Dokumen lain yang didukung platform

---

### Alur

```text
User

↓

Verification Center

↓

Upload Document

↓

Admin Review

↓

Approved / Rejected
```

---

### Status

* Not Submitted
* Pending Review
* Approved
* Rejected

---

### Badge

```
✓ Identity Verified
```

---

## 27.2 Skill Verification

Skill Verification bertujuan meningkatkan kepercayaan terhadap kompetensi penyedia jasa.

Verifikasi dilakukan berdasarkan portofolio atau bukti kompetensi.

---

### Kategori

* UI/UX Design
* Graphic Design
* Programming
* Data Analysis
* Video Editing
* Writing
* Academic Support
* Digital Service

---

### Bukti yang Dapat Digunakan

* Portfolio
* GitHub
* Behance
* Dribbble
* Figma
* Kaggle
* Sertifikat
* Hasil Lomba

---

### Workflow

```text
Seller

↓

Pilih Skill

↓

Upload Portfolio

↓

Admin Review

↓

Approved

↓

Badge Aktif
```

---

### Badge

Contoh badge:

* ✓ Verified Developer
* ✓ Verified Designer
* ✓ Verified Data Analyst
* ✓ Verified Video Editor

Satu pengguna dapat memiliki lebih dari satu badge.

---

# 28. Trust System

Kepercayaan pengguna dibangun dari kombinasi beberapa indikator.

## Reputation Score

Dihitung berdasarkan:

* Rating
* Total Order
* Completion Rate
* Response Rate
* Response Time

---

## Verification Badge

* Identity Verified
* Skill Verified

---

## Profile Completion

Semakin lengkap profil, semakin tinggi tingkat kepercayaan pengguna.

Komponen:

* Foto Profil
* Bio
* Skill
* Portfolio
* Identity Verification
* Skill Verification

---

# 29. Service Policy

Seluruh layanan yang tersedia harus mematuhi kebijakan platform.

---

## Allowed Services

Contoh layanan yang diperbolehkan.

### Academic Support

* Formatting
* Proofreading
* Reference Formatting
* Template Presentation

---

### Creative

* Poster
* UI Design
* Logo
* Social Media Design

---

### Digital

* Data Entry
* Excel
* Spreadsheet
* Dashboard
* OCR
* PDF Conversion

---

### Programming

* Website
* Mobile App
* Automation
* API Integration

---

# 30. Prohibited Services

Layanan berikut dilarang dipublikasikan.

* Joki Tugas
* Joki Ujian
* Pembuatan Skripsi
* Plagiarisme
* Pembuatan Karya Ilmiah atas nama orang lain
* Pemalsuan Dokumen
* Aktivitas yang melanggar hukum

Pelanggaran terhadap kebijakan ini dapat menyebabkan penghapusan layanan maupun penangguhan akun.

---

# 31. Dispute Resolution

Apabila terjadi perselisihan:

1. Buyer mengajukan dispute.
2. Dana tetap berada pada escrow.
3. Seller memberikan klarifikasi.
4. Admin melakukan peninjauan bukti.
5. Admin memutuskan hasil akhir.

Keputusan administrator bersifat final untuk transaksi tersebut.

---

# 32. Refund Policy

Refund dapat dilakukan apabila:

* Seller membatalkan order.
* Seller tidak merespons dalam batas waktu yang ditentukan.
* Seller gagal mengirim hasil pekerjaan.
* Admin memutuskan buyer memenangkan dispute.

---

# 33. Service Level Agreement (SLA)

Untuk menjaga kualitas layanan, diterapkan target waktu berikut.

| Aktivitas                     | Target         |
| ----------------------------- | -------------- |
| Seller merespons order baru   | ≤ 6 Jam        |
| Seller menerima/menolak order | ≤ 24 Jam       |
| Buyer memberikan review hasil | ≤ 48 Jam       |
| Admin memverifikasi akun      | ≤ 3 Hari Kerja |
| Penyelesaian dispute          | ≤ 5 Hari Kerja |

---

# 34. Security Principles

Seluruh pengembangan Berima harus memperhatikan prinsip berikut.

* Data pengguna dilindungi.
* Dokumen verifikasi hanya dapat diakses administrator.
* Seluruh transaksi memiliki audit trail.
* Password disimpan dalam bentuk terenkripsi.
* Komunikasi menggunakan koneksi aman.

---

**End of Part 3**

# Berima Product Definition Document (PDD)

# Part 4 — Product Specification & Roadmap

---

# 35. Product Architecture Overview

Berima dikembangkan menggunakan arsitektur modern yang memisahkan tanggung jawab setiap komponen agar mudah dikembangkan dan dipelihara.

```text
                   Android Application
                           │
               Presentation Layer (UI)
                           │
                    ViewModel (MVVM)
                           │
                     Repository Layer
                           │
          Firebase / Backend Service Layer
                           │
          Authentication • Database • Storage
```

## Design Principles

* Clean Architecture
* MVVM Pattern
* Repository Pattern
* Offline-first (Future)
* Modular Development

---

# 36. Core Modules

## Authentication Module

### Purpose

Mengelola autentikasi pengguna.

### Features

* Register
* Login
* Logout
* Reset Password

---

## Marketplace Module

### Purpose

Menghubungkan buyer dengan seller.

### Features

* Browse Services
* Search
* Category
* Detail Service

---

## Order Module

### Purpose

Mengelola seluruh siklus transaksi.

### Features

* Order Creation
* Order Tracking
* Escrow Status
* Revision
* Completion

---

## Verification Module

### Purpose

Meningkatkan tingkat kepercayaan pengguna.

### Features

* Identity Verification
* Skill Verification
* Verification Status

---

## Review Module

### Purpose

Membangun reputasi penyedia jasa.

### Features

* Rating
* Review
* Reputation Summary

---

## Chat Module

### Purpose

Memfasilitasi komunikasi buyer dan seller.

### Features

* Conversation
* Image Attachment (Future)
* Read Status

---

# 37. User Journey

## Buyer Journey

```text
Register

↓

Complete Profile

↓

Browse Service

↓

Search

↓

View Detail

↓

Order

↓

Payment

↓

Seller Works

↓

Receive Result

↓

Review

↓

Completed
```

---

## Seller Journey

```text
Register

↓

Complete Profile

↓

Create Service

↓

(Optional) Verification

↓

Receive Order

↓

Accept

↓

Complete Work

↓

Upload Result

↓

Receive Payment

↓

Gain Reputation
```

---

# 38. User Flow

## Buyer Flow

```text
Home

↓

Search

↓

Service Detail

↓

Order

↓

Checkout

↓

Payment

↓

Order Tracking

↓

Review
```

---

## Seller Flow

```text
Dashboard

↓

Manage Services

↓

Incoming Orders

↓

Order Detail

↓

Upload Deliverables

↓

Complete
```

---

# 39. MVP Release Plan

## MVP Version 1.0

Target:

Membuktikan bahwa mahasiswa bersedia menggunakan marketplace jasa khusus kampus.

### Features

* Authentication
* Marketplace
* Order
* Escrow Simulation
* Review
* Chat
* Identity Verification
* Skill Verification

---

## Version 1.5

Target:

Meningkatkan kualitas transaksi.

### Features

* Dispute Center
* Push Notification
* Seller Dashboard
* Featured Listing

---

## Version 2.0

Target:

Meningkatkan otomatisasi.

### Features

* Payment Gateway
* Automatic Escrow
* Analytics
* Campus Partnership

---

## Version 3.0

Target:

Ekspansi nasional.

### Features

* Multi Campus
* Business Account
* Internship Marketplace
* Organization Marketplace

---

# 40. Success Metrics

## Business KPI

| KPI                      | Target |
| ------------------------ | ------ |
| Registered Users         | 100+   |
| Verified Users           | 30+    |
| Monthly Orders           | 50+    |
| Transaction Success Rate | >90%   |
| Average Rating           | >4.5   |

---

## Product KPI

* Daily Active Users
* Weekly Active Users
* Search Success Rate
* Order Completion Time
* Verification Approval Rate

---

## Trust KPI

* Identity Verification Rate
* Skill Verification Rate
* Dispute Rate
* Refund Rate
* Fraud Cases

---

# 41. Risk Analysis

## Product Risk

### Low Adoption

Mitigation:

Peluncuran terbatas di lingkungan UPN Veteran Jakarta untuk memperoleh umpan balik awal.

---

### Low Trust

Mitigation:

Implementasi Identity Verification, Skill Verification, Review, dan Escrow.

---

### Fake Services

Mitigation:

Moderasi oleh admin dan mekanisme pelaporan pengguna.

---

### Academic Misuse

Mitigation:

Service Policy dan moderasi konten.

---

### Technical Risk

Mitigation:

Arsitektur modular dan penggunaan layanan backend yang mudah dikembangkan.

---

# 42. Future Expansion

## Geographic Expansion

* UPN Veteran Jakarta
* Jabodetabek
* Jawa
* Indonesia

---

## Feature Expansion

* AI Recommendation
* Smart Search
* Auto Pricing Suggestion
* Video Portfolio
* Team Services
* Subscription

---

## Partnership Expansion

* Universitas
* Organisasi Mahasiswa
* UKM
* Startup
* UMKM

---

# 43. Technical Constraints

## Platform

Android

---

## MVP Limitation

* Belum menggunakan payment gateway.
* Escrow masih berupa simulasi.
* Moderasi dilakukan manual.
* Verifikasi dilakukan manual.
* Belum tersedia notifikasi push.

---

# 44. Product Glossary

| Istilah      | Definisi                                            |
| ------------ | --------------------------------------------------- |
| Buyer        | Pengguna yang membeli jasa.                         |
| Seller       | Pengguna yang menawarkan jasa.                      |
| Service      | Layanan yang ditawarkan seller.                     |
| Escrow       | Mekanisme penahanan dana hingga transaksi selesai.  |
| Verification | Proses validasi identitas atau kompetensi pengguna. |
| Dispute      | Sengketa antara buyer dan seller.                   |
| Revision     | Permintaan perbaikan hasil pekerjaan.               |

---

# 45. Product Roadmap

```text
2026

V1.0
Marketplace MVP
Authentication
Order
Review
Escrow Simulation
Verification

│
├─────────────►

V1.5
Dispute
Push Notification
Featured Listing

│
├─────────────►

V2.0
Payment Gateway
Automatic Escrow
Campus Partnership

│
├─────────────►

V3.0
Multi Campus
Business Account
Internship Marketplace
```

---

# 46. Product Vision Statement

Berima tidak hanya bertujuan menjadi marketplace jasa, tetapi membangun ekosistem kolaborasi mahasiswa yang aman, profesional, dan berkelanjutan.

Setiap fitur yang dikembangkan harus mendukung terciptanya kepercayaan, memperluas peluang mahasiswa untuk memanfaatkan keterampilannya, serta menjaga integritas akademik sebagai nilai utama platform.

---

# 47. Revision History

| Version | Date      | Description                         |
| ------- | --------- | ----------------------------------- |
| 1.0     | June 2026 | Initial Product Definition Document |

---

# 48. Architecture Decision Log (ADL)

Bagian ini mendokumentasikan seluruh keputusan penting yang diambil selama proses pengembangan produk. Setiap keputusan harus memiliki alasan yang jelas sehingga anggota tim maupun coding agent memahami konteks di balik implementasi.

| ID      | Decision                                      | Status   | Rationale                                                                                                           |
| ------- | --------------------------------------------- | -------- | ------------------------------------------------------------------------------------------------------------------- |
| DEC-001 | Platform utama menggunakan Android            | Accepted | Berdasarkan mayoritas perangkat yang digunakan oleh target pengguna (mahasiswa) serta keterbatasan sumber daya tim. |
| DEC-002 | Target awal hanya UPN Veteran Jakarta         | Accepted | Mempermudah validasi pasar dan memperoleh umpan balik awal sebelum ekspansi.                                        |
| DEC-003 | Menggunakan konsep Marketplace Micro-Service  | Accepted | Fokus pada jasa berskala kecil yang umum dibutuhkan mahasiswa.                                                      |
| DEC-004 | Sistem pembayaran menggunakan Escrow          | Accepted | Meningkatkan keamanan transaksi antara buyer dan seller.                                                            |
| DEC-005 | Escrow pada MVP masih berupa simulasi         | Accepted | Menghindari kompleksitas integrasi payment gateway pada tahap awal.                                                 |
| DEC-006 | Login menggunakan email apa pun               | Accepted | Mempermudah proses registrasi dan mendukung ekspansi di masa depan.                                                 |
| DEC-007 | Identity Verification bersifat opsional       | Accepted | Menurunkan hambatan registrasi, namun tetap menyediakan mekanisme peningkatan kepercayaan.                          |
| DEC-008 | Skill Verification menjadi fitur MVP          | Accepted | Menjadi pembeda utama Berima dibanding marketplace umum.                                                            |
| DEC-009 | Verifikasi dilakukan secara manual oleh Admin | Accepted | Lebih realistis untuk MVP dengan jumlah pengguna yang masih terbatas.                                               |
| DEC-010 | AI tidak menjadi bagian dari MVP              | Accepted | Fokus pada validasi model bisnis sebelum menambahkan fitur berbasis AI.                                             |
| DEC-011 | Sistem revisi maksimal satu kali              | Accepted | Menjaga keseimbangan hak buyer dan seller pada tahap awal.                                                          |
| DEC-012 | Transaksi mengikuti Service Policy            | Accepted | Menjaga integritas akademik dan kualitas layanan di dalam platform.                                                 |

---

# 49. Open Questions & Product Backlog

Bagian ini berisi keputusan yang belum ditetapkan dan akan dibahas pada iterasi pengembangan berikutnya.

## High Priority

| ID     | Pertanyaan                                          | Prioritas |
| ------ | --------------------------------------------------- | --------- |
| OQ-001 | Apakah seller dapat membeli Featured Listing?       | High      |
| OQ-002 | Berapa besar biaya komisi setelah MVP?              | High      |
| OQ-003 | Bagaimana mekanisme pencarian dan ranking layanan?  | High      |
| OQ-004 | Bagaimana sistem rekomendasi layanan di masa depan? | High      |

---

## Medium Priority

| ID     | Pertanyaan                                                             | Prioritas |
| ------ | ---------------------------------------------------------------------- | --------- |
| OQ-005 | Apakah buyer dapat melakukan pembatalan setelah seller menerima order? | Medium    |
| OQ-006 | Berapa lama masa berlaku verifikasi kompetensi?                        | Medium    |
| OQ-007 | Apakah diperlukan re-verifikasi berkala?                               | Medium    |
| OQ-008 | Apakah admin dapat mencabut status verifikasi?                         | Medium    |

---

## Future Discussion

Topik berikut belum menjadi fokus MVP namun akan dipertimbangkan pada pengembangan berikutnya.

* AI Recommendation
* Smart Search
* Dynamic Pricing Suggestion
* Team Service
* Campus Ambassador
* Organization Marketplace
* Business Account
* Multi-Campus Verification
* Digital Certificate Verification
* API Integration
* Web Platform

---

# 50. Change Management

PDD merupakan **dokumen acuan utama (Single Source of Truth)** bagi seluruh tim pengembang Berima.

## 50.1 Change Policy

Setiap perubahan terhadap produk harus mengikuti urutan berikut:

```text
Ide Baru
    │
    ▼
Diskusi Tim
    │
    ▼
Update Product Definition Document (PDD)
    │
    ▼
Update Product Requirements Document (PRD)
    │
    ▼
Update Prototype
    │
    ▼
Implementasi
    │
    ▼
Testing
    │
    ▼
Release
```

Tidak diperbolehkan melakukan implementasi fitur baru tanpa pembaruan pada PDD.

---

## 50.2 Versioning Rules

Penomoran versi mengikuti aturan Semantic Versioning yang disederhanakan.

### Major Version

Perubahan besar terhadap arah produk.

Contoh:

* Marketplace → Platform Internship
* Android → Multi Platform

Contoh versi:

* v2.0
* v3.0

---

### Minor Version

Penambahan fitur baru yang tidak mengubah konsep utama.

Contoh:

* Verification Center
* Featured Listing
* Push Notification

Contoh versi:

* v1.1
* v1.2
* v1.3

---

### Patch Version

Perbaikan kecil tanpa perubahan fitur.

Contoh:

* Perbaikan typo
* Revisi business rule
* Perbaikan alur transaksi

Contoh:

* v1.0.1
* v1.0.2

---

## 50.3 Document Ownership

| Document                      | Owner                     | Reviewer      |
| ----------------------------- | ------------------------- | ------------- |
| Product Definition Document   | Product Owner             | Seluruh Tim   |
| Product Requirements Document | Product Owner & Developer | Seluruh Tim   |
| UI/UX Specification           | UI/UX Designer            | Product Owner |
| Technical Documentation       | Developer                 | Product Owner |
| Business Documentation        | Product Owner             | Seluruh Tim   |

---

## 50.4 Source of Truth

Urutan prioritas dokumen dalam pengambilan keputusan adalah sebagai berikut:

1. Product Definition Document (PDD)
2. Product Requirements Document (PRD)
3. UI/UX Specification
4. Technical Documentation
5. Source Code

Apabila terjadi inkonsistensi antar dokumen, maka dokumen dengan prioritas lebih tinggi menjadi acuan utama.

---

## 50.5 Change Request Workflow

Setiap perubahan fitur wajib mengikuti alur berikut:

1. Pengusul membuat Change Request.
2. Product Owner melakukan evaluasi dampak.
3. Tim mendiskusikan usulan.
4. Keputusan dicatat pada Architecture Decision Log.
5. PDD diperbarui.
6. PRD disesuaikan.
7. Prototype direvisi (jika diperlukan).
8. Developer mulai implementasi.
9. Perubahan dicatat pada Revision History.

---

## 50.6 Definition of Done (Product)

Sebuah fitur dinyatakan selesai apabila memenuhi seluruh kriteria berikut:

* Seluruh kebutuhan pada PRD telah dipenuhi.
* UI telah sesuai dengan desain yang disepakati.
* Business Rules telah diimplementasikan.
* Tidak terdapat bug kritis.
* Dokumentasi telah diperbarui.
* Pengujian berhasil dilakukan.
* Product Owner memberikan persetujuan.

---

# End of Product Definition Document v1.0