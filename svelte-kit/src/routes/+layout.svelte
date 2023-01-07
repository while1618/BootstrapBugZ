<script>
  import Navbar from '$lib/Navbar.svelte';
  import '../app.css';
</script>

<div class="flex h-screen flex-col bg-white dark:bg-gray-800">
  <Navbar />
  <slot />
</div>
