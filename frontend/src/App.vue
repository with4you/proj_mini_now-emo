<template>
  <div class="layout">
    <nav class="nav">
      <div class="brand">Emotion Snapshot</div>
      <div class="links">
        <RouterLink to="/">대시보드</RouterLink>
        <RouterLink to="/history">히스토리</RouterLink>
      </div>
    </nav>
    <main>
      <RouterView />
    </main>
  </div>
  </template>

<script setup>
</script>

<style>
:root {
  --bg: #0b1020;
  --panel: #12182b;
  --muted: #aab2c8;
  --text: #e6ecff;
  --brand: #7c9aff;
  --border: #2a3557;
}
.layout { font-family: Inter, system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; background: var(--bg); color: var(--text); min-height:100vh; }
.nav { display:flex; align-items:center; justify-content:space-between; padding:12px 16px; border-bottom:1px solid var(--border); background: #0d1326; }
.brand { font-weight:800; letter-spacing:0.2px; }
.links a { margin-left:12px; color:var(--muted); text-decoration:none; }
.links a.router-link-active, .links a:hover { color: var(--text); }
main { max-width:1100px; margin: 16px auto; padding: 0 16px; }
.card { border:1px solid var(--border); border-radius:12px; padding:16px; box-shadow:0 10px 24px rgba(0,0,0,0.25); background: linear-gradient(180deg, #12182b 0%, #10162a 100%); }
</style>



