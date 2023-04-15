<script>
  import { userStore } from '$lib/stores/user';
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <h1 class="text-5xl font-bold">{$userStore?.username}</h1>
    </div>
  </div>
</div>
