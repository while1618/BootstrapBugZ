<script lang="ts">
  import { page } from '$app/stores';
  import * as m from '$lib/paraglide/messages.js';
  import { LockKeyholeIcon, UserIcon } from 'lucide-svelte';

  interface Props {
    children?: import('svelte').Snippet;
  }

  const { children }: Props = $props();
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="flex flex-col gap-2">
      <div class="tabs-boxed tabs flex justify-center bg-base-100">
        <a
          href="/profile/settings/personal"
          class="tab gap-2"
          class:tab-active={$page.url.pathname.includes('/personal')}
        >
          <UserIcon />
          {m.profile_personalInformation()}
        </a>
        <a
          href="/profile/settings/security"
          class="tab gap-2"
          class:tab-active={$page.url.pathname.includes('/security')}
        >
          <LockKeyholeIcon />
          {m.profile_security()}
        </a>
      </div>
      {@render children?.()}
    </div>
  </div>
</section>
