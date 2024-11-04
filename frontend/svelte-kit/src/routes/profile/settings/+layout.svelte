<script lang="ts">
  import { page } from '$app/stores';
  import LockCloseIcon from '$lib/icons/lock-close.svelte';
  import ProfileIcon from '$lib/icons/profile.svelte';
  import * as m from '$lib/paraglide/messages.js';

  interface Props {
    children?: import('svelte').Snippet;
  }

  let { children }: Props = $props();
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="flex flex-col gap-2">
      <div class="tabs-boxed tabs bg-base-100 flex justify-center">
        <a
          href="/profile/settings/personal"
          class="tab"
          class:tab-active={$page.url.pathname.includes('/personal')}
        >
          <ProfileIcon />
          {m.profile_personalInformation()}
        </a>
        <a
          href="/profile/settings/security"
          class="tab"
          class:tab-active={$page.url.pathname.includes('/security')}
        >
          <LockCloseIcon />
          {m.profile_security()}
        </a>
      </div>
      {@render children?.()}
    </div>
  </div>
</section>
