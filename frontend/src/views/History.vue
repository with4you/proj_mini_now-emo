<template>
  <div>
    <h1>히스토리</h1>
    <div class="card">
      <div class="controls">
        <label>FROM <input type="date" v-model="from"/></label>
        <label>TO <input type="date" v-model="to"/></label>
        <button class="btn" @click="load">검색</button>
      </div>
      <table>
        <thead>
          <tr><th>ID</th><th>Level</th><th>Created At</th><th>Actions</th></tr>
        </thead>
        <tbody>
          <tr v-for="row in rows" :key="row.id">
            <td><span class="tag">#{{ row.id }}</span></td>
            <td class="level">{{ row.level }}</td>
            <td>{{ fmt(row.createdAt) }}</td>
            <td>
              <button class="btn secondary" @click="startEdit(row)">수정</button>
              <button class="btn danger" @click="removeRow(row.id)">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <dialog ref="dlg">
      <div class="modal">
        <header>레벨 수정</header>
        <main>
          <label>Level (1~5)
            <input class="input" type="number" min="1" max="5" v-model.number="editLevel" />
          </label>
        </main>
        <footer>
          <button class="btn secondary" @click="closeEdit">취소</button>
          <button class="btn" @click="saveEdit">저장</button>
        </footer>
      </div>
    </dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const rows = ref([]);
const from = ref('');
const to = ref('');
const dlg = ref(null);
const editing = ref(null);
const editLevel = ref(3);

function fmt(ts){
  const d = new Date(ts);
  return new Intl.DateTimeFormat('ko-KR', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit', second:'2-digit'}).format(d);
}
function toStartOfDayInstant(dateStr){
  if (!dateStr) return null;
  const d = new Date(dateStr + 'T00:00:00');
  return d.toISOString();
}
function toEndOfDayInstant(dateStr){
  if (!dateStr) return null;
  const d = new Date(dateStr + 'T23:59:59.999');
  return d.toISOString();
}

function defaultRange(){
  const t = new Date();
  const f = new Date(t.getTime() - 29*24*3600*1000);
  const toStr = t.toISOString().slice(0,10);
  const fromStr = new Date(f).toISOString().slice(0,10);
  from.value = fromStr; to.value = toStr;
}

async function load(){
  const q = new URLSearchParams();
  if (from.value) q.set('from', toStartOfDayInstant(from.value));
  if (to.value) q.set('to', toEndOfDayInstant(to.value));
  const res = await fetch(`/api/emotions?${q.toString()}`);
  if (!res.ok) { alert('조회 실패'); return; }
  rows.value = await res.json();
}

function startEdit(row){
  editing.value = row;
  editLevel.value = row.level;
  dlg.value.showModal();
}
function closeEdit(){ editing.value = null; dlg.value.close(); }

async function saveEdit(){
  if (!editing.value) return;
  const level = editLevel.value|0; if (level<1 || level>5){ alert('레벨은 1~5'); return; }
  const res = await fetch(`/api/emotions/${editing.value.id}`, { method:'PATCH', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ level }) });
  if (!res.ok){ const e = await res.json().catch(()=>({})); alert(e.message||'수정 실패'); return; }
  closeEdit();
  await load();
}

async function removeRow(id){
  if(!confirm(`#${id} 항목을 삭제할까요?`)) return;
  const res = await fetch(`/api/emotions/${id}`, { method:'DELETE' });
  if (!res.ok){ const e = await res.json().catch(()=>({})); alert(e.message||'삭제 실패'); return; }
  await load();
}

function onStorage(e){ if (e.key === 'emotion_saved') load(); }
function onVisibility(){ if (document.visibilityState === 'visible') load(); }

onMounted(()=>{ defaultRange(); load(); window.addEventListener('storage', onStorage); document.addEventListener('visibilitychange', onVisibility); });
onBeforeUnmount(()=>{ window.removeEventListener('storage', onStorage); document.removeEventListener('visibilitychange', onVisibility); });
</script>

<style scoped>
.controls { display:flex; gap:10px; align-items:center; margin-bottom:10px; }
.btn { background:#7c9aff; color:#0b1020; border:none; border-radius:10px; padding:8px 12px; cursor:pointer; }
.btn.secondary { background: transparent; color:#e6ecff; border:1px solid #2a3557; }
.btn.danger { background:#ff6b6b; color:#fff; }
table { width:100%; border-collapse: collapse; }
th, td { padding: 10px; border-bottom:1px solid #2a3557; }
.tag { background:#0f1426; border:1px solid #2a3557; color:#aab2c8; padding:2px 8px; border-radius:8px; }
dialog { border:none; border-radius:12px; padding:0; }
.modal { background:#0f1426; color:#e6ecff; border:1px solid #2a3557; border-radius:12px; min-width: 320px; }
.modal header, .modal footer { padding:12px 14px; }
.modal main { padding: 0 14px 14px; }
.modal header { border-bottom:1px solid #2a3557; font-weight:700; }
.modal footer { border-top:1px solid #2a3557; display:flex; justify-content:flex-end; gap:8px; }
.input { width:100%; border:1px solid #2a3557; border-radius:10px; padding:8px 10px; background:#0b1020; color:#e6ecff; }
</style>



