<script lang="ts">
  import * as m from '$lib/paraglide/messages.js';
  import type { PageServerData } from './$types';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="mb-4 mt-6 text-center">
        <h1 class="text-2xl font-bold">{data.user?.username}</h1>
      </div>
      <div class="px-6 py-4">
        <p class="text-base">
          {m.profile_bio()}
        </p>
      </div>
    </div>
  </div>
</section>
