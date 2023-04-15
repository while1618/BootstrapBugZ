<script lang="ts">
  import { userStore } from '$lib/stores/user';
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      {#if $userStore}
        <h1 class="text-2xl font-bold">Profile page of: {$userStore.username}</h1>
      {/if}
    </div>
  </div>
</div>
