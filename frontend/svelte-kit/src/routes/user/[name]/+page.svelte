<script lang="ts">
  import type { PageServerData } from './$types';

  export let data: PageServerData;
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <p class="py-6">{data?.user?.firstName}</p>
      <p class="py-6">{data?.user?.lastName}</p>
      <p class="py-6">{data?.user?.username}</p>
      {#if data?.user?.email}
        <p class="py-6">{data.user.email}</p>
      {/if}
    </div>
  </div>
</div>
