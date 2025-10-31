import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import Dashboard from './views/Dashboard.vue';
import History from './views/History.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Dashboard },
    { path: '/history', component: History }
  ]
});

createApp(App).use(router).mount('#app');



