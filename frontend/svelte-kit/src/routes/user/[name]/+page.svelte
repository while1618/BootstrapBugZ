<script lang="ts">
  import avatar from '$lib/images/avatar.jpg';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
</script>

<div class="flex h-screen flex-col items-center justify-center">
  <div class="card mx-auto w-full max-w-xl bg-base-100 p-8 shadow-xl">
    <div class="flex justify-center">
      <img class="mt-6 h-32 w-32 rounded-full" src={avatar} alt="Avatar" />
    </div>
    <div class="mb-4 mt-6 text-center">
      <h1 class="text-2xl font-bold">{data.user.username}</h1>
      <p class="text-sm">{`${data.user.firstName} ${data.user.lastName}`}</p>
    </div>
    <div class="px-6 py-4">
      <p class="text-base">
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed pellentesque semper est, in
        finibus turpis commodo ut.
      </p>
    </div>
  </div>
</div>
