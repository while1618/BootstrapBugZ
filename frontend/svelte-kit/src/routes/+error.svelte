<script>
  import { page } from '$app/stores';
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <h1 class="text-5xl font-bold">{$page?.error?.status}</h1>
      <p class="py-6">{$page?.error?.message}</p>
    </div>
  </div>
</div>
