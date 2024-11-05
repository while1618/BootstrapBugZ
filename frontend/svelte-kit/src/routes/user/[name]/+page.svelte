<script lang="ts">
  import * as Card from '$lib/components/ui/card';
  import avatar from '$lib/images/avatar.jpg';
  import * as m from '$lib/paraglide/messages.js';
  import type { PageServerData } from './$types';

  interface Props {
    data: PageServerData;
  }

  let { data }: Props = $props();
</script>

<Card.Root>
  <Card.Content>
    <div class="mx-auto w-full max-w-xl p-8 shadow-xl">
      <div class="flex justify-center">
        <img class="mt-6 h-32 w-32 rounded-full" src={avatar} alt="Avatar" />
      </div>
      <div class="mb-4 mt-6 text-center">
        <h1 class="text-2xl font-bold">{data.user.username}</h1>
      </div>
      <div class="px-6 py-4">
        <p class="text-base">{m.user_bio()}</p>
      </div>
    </div>
  </Card.Content>
</Card.Root>
