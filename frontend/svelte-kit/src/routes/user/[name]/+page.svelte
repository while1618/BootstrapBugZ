<script lang="ts">
  import avatar from '$lib/images/avatar.jpg';
  import * as m from '$lib/paraglide/messages.js';
  import type { PageServerData } from './$types';

  export let data: PageServerData;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="flex justify-center">
        <img class="mt-6 h-32 w-32 rounded-full" src={avatar} alt="Avatar" />
      </div>
      <div class="mb-4 mt-6 text-center">
        <h1 class="text-2xl font-bold">{data.user.username}</h1>
        <p class="text-sm">{`${data.user.firstName} ${data.user.lastName}`}</p>
      </div>
      <div class="px-6 py-4">
        <p class="text-base">{m.user_bio()}</p>
      </div>
    </div>
  </div>
</section>
