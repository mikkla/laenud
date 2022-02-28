import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "isik-list",
      component: () => import("../views/IsikList.vue"),
    },
    {
      path: "/isik/:id",
      name: "isik-details",
      component: () => import("../views/TaotlusList.vue"),
    },
    {
      path: "/taotlus",
      name: "isik-add",
      component: () => import("../views/TaotlusList.vue"),
    },
  ],
});

export default router;
