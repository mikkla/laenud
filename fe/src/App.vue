<script setup lang="ts">
import { RouterLink, RouterView } from "vue-router";

const items = [
    {
        label: 'Isikud',
        icon: 'pi pi-user',
        to: '/'
    },
    {
        label: 'Taotlused',
        icon: 'pi pi-dollar',
        to: '/taotlus'
    },
];
</script>

<template>
  <div id="app">
    <Toast />
    <Menubar :model="items" />
    <div class="container mt-3">
      <RouterView />
    </div>
  </div>
</template>

<style>

</style>
