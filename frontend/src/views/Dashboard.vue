<template>
  <div>
    <h1>대시보드</h1>
    <div class="card" style="margin-top:12px;">
      <div class="controls" style="display:flex;gap:10px;align-items:center;">
        <span>지금 기분:</span>
        <label v-for="n in 5" :key="n"><input type="radio" name="level" :value="n" v-model="level" /> {{ n }}</label>
        <button class="btn" @click="save" :disabled="saving">저장</button>
      </div>
    </div>
    <div class="grid">
      <div class="card dark"><canvas ref="d1" height="200"></canvas></div>
      <div class="card dark"><canvas ref="d2" height="200"></canvas></div>
      <div class="card dark"><canvas ref="d3" height="200"></canvas></div>
      <div class="card dark"><canvas ref="d4" height="200"></canvas></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Chart } from 'chart.js/auto';

const d1 = ref(null), d2 = ref(null), d3 = ref(null), d4 = ref(null);
const chartMap = new WeakMap();
const level = ref(3);
const saving = ref(false);

function toDataset(stats){
  return {
    labels: stats.map(s=>s.label),
    counts: stats.map(s=>s.count),
    avgs: stats.map(s=>s.avg)
  }
}

function renderChart(canvas, title, data){
  const existing = chartMap.get(canvas);
  if (existing) existing.destroy();

  // gradient for line
  const ctx = canvas.getContext('2d');
  const grad = ctx.createLinearGradient(0, 0, 0, canvas.height);
  grad.addColorStop(0, 'rgba(255,99,132,0.35)');
  grad.addColorStop(1, 'rgba(255,99,132,0.02)');

  const chart = new Chart(canvas, {
    type: 'bar',
    data: { labels: data.labels, datasets: [
      { type:'bar', label:'count', data:data.counts, backgroundColor: 'rgba(99, 179, 237, 0.55)', borderColor:'rgba(99,179,237,0.9)', borderWidth:1, borderRadius:6 },
      { type:'line', label:'avg', data:data.avgs, yAxisID:'y1', borderColor:'#ff6b81', backgroundColor: grad, fill:true, tension:0.25, pointRadius:2 }
    ]},
    options: {
      responsive:true,
      plugins:{
        title:{ display:true, text:title, color:'#e5e7eb', font:{ weight:'700' } },
        legend:{ labels:{ color:'#cbd5e1' } },
        tooltip:{ backgroundColor:'#0b1020', titleColor:'#fff', bodyColor:'#e5e7eb', borderColor:'#1f2937', borderWidth:1 }
      },
      scales:{
        x:{ grid:{ color:'rgba(148,163,184,0.15)' }, ticks:{ color:'#94a3b8' } },
        y:{ beginAtZero:true, grid:{ color:'rgba(148,163,184,0.15)' }, ticks:{ color:'#94a3b8' } },
        y1:{ beginAtZero:true, position:'right', min:1, max:5, grid:{ drawOnChartArea:false }, ticks:{ color:'#94a3b8' } }
      }
    }
  });
  chartMap.set(canvas, chart);
}

async function fetchStats(range){
  const res = await fetch(`/api/stats?range=${range}`);
  if (!res.ok) throw new Error('stats 실패');
  return res.json();
}

async function refreshAll(){
  const [a,b,c,d] = await Promise.all([
    fetchStats('daily'), fetchStats('weekly'), fetchStats('monthly'), fetchStats('yearly')
  ]);
  renderChart(d1.value, '일별', toDataset(a));
  renderChart(d2.value, '주별', toDataset(b));
  renderChart(d3.value, '월별', toDataset(c));
  renderChart(d4.value, '연도별', toDataset(d));
}

async function save(){
  saving.value = true;
  try {
    const res = await fetch('/api/emotions', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ level: level.value })});
    if (!res.ok){ const e = await res.json().catch(()=>({})); throw new Error(e.message||'저장 실패'); }
    await refreshAll();
    try { localStorage.setItem('emotion_saved', String(Date.now())); } catch {}
  } finally { saving.value = false; }
}

onMounted(refreshAll);
</script>

<style scoped>
.grid { display:grid; grid-template-columns: 1fr; gap: 16px; margin-top: 16px; }
.btn { background:#3b82f6; color:#fff; border:none; border-radius:10px; padding:8px 12px; cursor:pointer; }
.card.dark { background: linear-gradient(180deg, #12182b 0%, #10162a 100%); border-color:#2a3557; }
</style>



