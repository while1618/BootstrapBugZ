<script>
  import Navbar from '$lib/Navbar.svelte';
  import '../app.css';
</script>

<div class="flex min-h-screen flex-col bg-gray-50 dark:bg-gray-900">
  <Navbar />
  <slot />
</div>
